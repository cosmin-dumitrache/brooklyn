package brooklyn.cli.commands;

import brooklyn.rest.api.Application;
import brooklyn.rest.api.LocationSummary;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import org.codehaus.jackson.type.TypeReference;
import org.iq80.cli.Command;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Command(name = "list-locations", description = "List all registered locations")
public class ListLocationsCommand extends BrooklynCommand {

    @Override
    public void run() throws Exception {

        // Make an HTTP request to the REST server and get back a JSON encoded response
        WebResource webResource = getClient().resource(endpoint + "/v1/locations");
        ClientResponse clientResponse = webResource.accept(MediaType.APPLICATION_JSON).get(ClientResponse.class);

        // Make sure we get the correct HTTP response code
        if (clientResponse.getStatus() != Response.Status.OK.getStatusCode()) {
            String err = getErrorMessage(clientResponse);
            throw new CommandExecutionException(err);
        }

        // Parse the JSON response
        String jsonResponse = clientResponse.getEntity(String.class);
        List<LocationSummary> locations = getJsonParser().readValue(jsonResponse, new TypeReference<List<LocationSummary>>() {});

        // Display the applications
        for (LocationSummary location : locations) {
            getOut().printf("%s\n",location);
        }

    }
}


