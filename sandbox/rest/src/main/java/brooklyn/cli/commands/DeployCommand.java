package brooklyn.cli.commands;

import brooklyn.rest.api.Application;
import brooklyn.rest.api.Application.Status;
import brooklyn.rest.api.ApplicationSpec;
import brooklyn.rest.api.EntitySpec;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Charsets;
import com.google.common.collect.Sets;
import com.google.common.io.Files;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import org.iq80.cli.Command;
import org.iq80.cli.Option;
import org.iq80.cli.Arguments;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

@Command(name = "deploy", description = "Deploys the specified application using given config, classpath, location, etc")
public class DeployCommand extends BrooklynCommand {

    public static final String JSON_FORMAT = "json";
    public static final String GROOVY_FORMAT = "groovy";
    public static final String CLASS_FORMAT = "class";

    @Option(name = "--format",
            allowedValues = {JSON_FORMAT, GROOVY_FORMAT, CLASS_FORMAT},
            description = "Either "+JSON_FORMAT+","+GROOVY_FORMAT+","+CLASS_FORMAT+", to specify the APP type.")
    public String format = null;

    @Option(name = "--no-start",
            description = "Don't invoke `start` on the application")
    public boolean noStart = false;

    @Option(name = { "--location", "--locations" },
            title = "Location list",
            description = "Specifies the locations where the application will be launched. You can specify more than one location like this: \"loc1,loc2,loc3\"")
    public String locations = null;

    @Option(name = "--config",
            title = "Configuration parameters list",
            description = "Pass the config parameters to the application like this: \"A=B,C=D\"")
    public String config = null;

    @Option(name = "--classpath",
            description = "Upload the given classes")
    public String classpath = null;

    @Arguments(title = "APP",
               required = true,
               description = "where APP can be\n" +
                    "    * a fully qualified class-name of something on the classpath\n" +
                    "    * path or URL to a script file (if ends .groovy)\n" +
                    "    * path or URL to a JSON file (if ends .json)")
    public String app = null;

    public void run() throws Exception {

        // Throw exception for unsupported options
        if(noStart)
            throw new UnsupportedOperationException(
                    "The \"--no-start\" option is not supported yet");
        if(locations!=null)
            throw new UnsupportedOperationException(
                    "The \"--location\",\"--locations\" options are not supported yet");

        // Format overrides inference
        if (format == null) {
            format = inferAppFormat(app);
        }

        String objectJsonString;
        if(format.equals(GROOVY_FORMAT)){
            // app is the path of a groovy file
            String appClassName = uploadGroovyFile(app);

            ApplicationSpec applicationSpec = new ApplicationSpec(
                    appClassName, // name
                    Sets.newHashSet(new EntitySpec(
                            appClassName, // name
                            appClassName, // type
                            toMap(config) // config map
                    )), // entities
                    Sets.newHashSet("/v1/locations/1") // locations
            );
            objectJsonString = getJsonParser().writeValueAsString(applicationSpec);

        } else if(format.equals(JSON_FORMAT)) {
            // app is the path of a json file containing a serialized ApplicationSpec
            LOG.info("Loading json request object form file: "+app);
            objectJsonString = Files.toString(new File(app),Charsets.UTF_8);

        } else if (format.equals(CLASS_FORMAT)) { // CLASS_FORMAT or GROOVY_FORMAT
            // app is the fully qualified classname for an app; so create json for app
            ApplicationSpec applicationSpec = new ApplicationSpec(
                    app, // name
                    Sets.newHashSet(new EntitySpec(app)), // entities
                    Sets.newHashSet("/v1/locations/1") // locations
            );
            objectJsonString = getJsonParser().writeValueAsString(applicationSpec);

        } else {
            throw new CommandExecutionException("Unrecognized deploy format '"+format+"'");
        }

        // Request the app instance be created + started
        WebResource webResource = getClient().resource(endpoint+"/v1/applications");
        ClientResponse clientResponse = webResource.type(MediaType.APPLICATION_JSON).post(ClientResponse.class, objectJsonString);

        // Ensure command succeeded
        if (clientResponse.getStatus() != Response.Status.CREATED.getStatusCode()) {
            String err = getErrorMessage(clientResponse);
            throw new CommandExecutionException("Error creating and starting app: "+err);
        }

        // Wait for app to start (i.e. side-effect of above request)
        waitForAppStarted(clientResponse.getLocation());
    }

    private void waitForAppStarted(URI appUri) throws InterruptedException, CommandExecutionException, IOException {
        // TODO Can use Repeater utility class? or extract to another method
        // TODO Are there other possible end-states? e.g. ends up as "unknown" or "stopped" somehow?
        getOut().println("Waiting for application to start ("+appUri+")...");
        Status status = getApplicationStatus(appUri);
        Status previousStatus = null;
        while (status != Application.Status.RUNNING && status != Application.Status.ERROR) {
            if (status != previousStatus) {
                getOut().println("Application status: "+status);
                previousStatus = status;
            }
            getOut().print(".");
            getOut().flush();
            Thread.sleep(1000);
            status = getApplicationStatus(appUri);
        }
        if (status == Application.Status.RUNNING) {
            String path = appUri.getPath();
            getOut().println("The application has been deployed: "+path.substring(path.lastIndexOf("/")+1));
        } else {
            throw new CommandExecutionException("Application did not start: status="+status+"; "+appUri);
        }
    }

    private String uploadGroovyFile(String path) throws CommandExecutionException, IOException {

        LOG.info("Loading groovy file to the server: {}", path);

        // Get the user's groovy script
        String groovyScript = Files.toString(new File(path), Charsets.UTF_8);

        // Make an HTTP request to the REST server
        String jsonEncodedGroovyScript = getJsonParser().writeValueAsString(groovyScript);
        WebResource webResource = getClient().resource(endpoint + "/v1/catalog");
        ClientResponse clientResponse = webResource.type(MediaType.APPLICATION_JSON).post(ClientResponse.class, jsonEncodedGroovyScript);

        // Make sure we get the correct HTTP response code
        if (clientResponse.getStatus() != Response.Status.CREATED.getStatusCode()) {
            String err = getErrorMessage(clientResponse);
            throw new CommandExecutionException("Error uploading groovy file: "+err);
        }

        // Get the catalog entity name that was just created
        String catalogEntityUri = clientResponse.getLocation().getPath();
        String catalogEntityName = catalogEntityUri.substring(catalogEntityUri.lastIndexOf("/")+1);

        LOG.info("Application has been added to the server's catalog: {}", catalogEntityName);

        return catalogEntityName;
    }

    private Application.Status getApplicationStatus(URI uri) throws IOException, InterruptedException, CommandExecutionException {
        WebResource webResource = getClient().resource(uri.toString());
        ClientResponse clientResponse = webResource.type(MediaType.APPLICATION_JSON).get(ClientResponse.class);

        if (clientResponse.getStatus() != Response.Status.OK.getStatusCode()) {
            String err = getErrorMessage(clientResponse);
            throw new CommandExecutionException("Error querying application status for "+uri+": "+err);
        }

        String response = clientResponse.getEntity(String.class);
        Application application = getJsonParser().readValue(response, Application.class);
        return application.getStatus();
    }

    @VisibleForTesting
    String inferAppFormat(String app) {
        if (app.toLowerCase().endsWith(".groovy")) {
            return GROOVY_FORMAT;
        } else if (app.toLowerCase().endsWith(".json") || app.toLowerCase().endsWith(".jsn")) {
            return JSON_FORMAT;
        } else {
            return CLASS_FORMAT;
        }
    }

    /**
     * Parses a string that looks like k1=v1,k2=v2 into a Map object
     *
     * @param {@link String} that looks like "k1=v1,k2=v2"
     * @return A {@link Map} representation of the string
     */
    private Map<String, String> toMap(String input) {
        Map<String, String> map = new HashMap<String, String>();
        if(input==null) return map;
        String[] array = input.split(",");
        for (String str : array) {
            String[] pair = str.split("=");
            map.put(pair[0], pair[1]);
        }
        return map;
    }

}
