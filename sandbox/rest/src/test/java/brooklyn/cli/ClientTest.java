package brooklyn.cli;

import brooklyn.cli.commands.CommandExecutionException;
import brooklyn.entity.basic.BasicEntity;
import brooklyn.policy.basic.GeneralPurposePolicy;
import brooklyn.rest.BrooklynService;
import brooklyn.rest.core.ApplicationManager;
import com.google.common.collect.Iterables;
import com.google.common.io.ByteStreams;
import com.google.common.io.Files;
import com.yammer.dropwizard.logging.Log;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.io.InputStream;
import static org.testng.Assert.assertEquals;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;

public class ClientTest {

    private final static Log LOG = Log.forClass(ClientTest.class);

    private BrooklynService brooklynServer;
    private ApplicationManager applicationManager;
    private Client brooklynClient;

    private ByteArrayOutputStream outBytes;
    private PrintStream out;
    private ByteArrayOutputStream errBytes;
    private PrintStream err;

    File tempConfigFile;

    protected String standardOut() {
        return outBytes.toString();
    }

    protected String standardErr() {
        return errBytes.toString();
    }

    @BeforeClass
    public void oneTimeSetUp() throws Exception {
        // Create temporary config file
        tempConfigFile = File.createTempFile("server-config",".yml");
        InputStream configInputStream = getClass().getClassLoader().getResourceAsStream("config/config.sample.yml");
        try {
            Files.write(ByteStreams.toByteArray(configInputStream), tempConfigFile);
        } finally {
            configInputStream.close();
        }

        // Start the REST server
        brooklynServer = BrooklynService.newBrooklynService();
        String[] args = {"server",tempConfigFile.getAbsolutePath()};
        brooklynServer.runAsync(args);
        applicationManager = brooklynServer.getApplicationManager();
    }

    @BeforeMethod
    public void setUp() throws Exception {
        // Set i/o streams
        outBytes = new ByteArrayOutputStream();
        out = new PrintStream(outBytes);
        errBytes = new ByteArrayOutputStream();
        err = new PrintStream(errBytes);
        // Create a client instance
        brooklynClient = new Client(out,err);
    }

    @AfterClass
    public void oneTimeTearDown() throws Exception {
        // Kill the REST server and client instance
        brooklynServer.stop();
        // Delete temp file
        tempConfigFile.delete();
    }

    @Test
    public void testListLocationsCommand() throws Exception {
        try {
            String[] args = {"list-locations"};
            brooklynClient.run(args);
            // Check list of locations contains what we provided in out config.sample.yml file
            assertThat(standardOut(), containsString("LocationSummary{provider='localhost', config={}, links={self=/v1/locations/0}}"));
            assertThat(standardOut(), containsString("ocationSummary{provider='aws-ec2', config={identity=ADS45345, location=eu-west-1, userName=, sshPublicKey=}, links={self=/v1/locations/1}}"));
        } catch (Exception e) {
            LOG.error("\nstdout="+standardOut()+"\nstderr="+standardErr()+"\n", e);
            throw e;
        }
    }

    @Test
    public void testCatalogEntitiesCommand() throws Exception {
        try {
            String[] args = {"catalog-entities"};
            brooklynClient.run(args);
            // Check list of entity types includes one of the defaults
            assertThat(standardOut(), containsString(BasicEntity.class.getName()));
        } catch (Exception e) {
            LOG.error("\nstdout="+standardOut()+"\nstderr="+standardErr()+"\n", e);
            throw e;
        }
    }

    @Test
    public void testCatalogPoliciesCommand() throws Exception {
        try {
            String[] args = {"catalog-policies"};
            brooklynClient.run(args);
            // Check list of entity types includes one of the defaults
            assertThat(standardOut(), containsString(GeneralPurposePolicy.class.getName()));
        } catch (Exception e) {
            LOG.error("\nstdout="+standardOut()+"\nstderr="+standardErr()+"\n", e);
            throw e;
        }
    }

    @Test
    public void testDeployCreatesApp() throws Exception {
        try {
            String[] args = {"deploy","--format","class", "brooklyn.cli.ExampleApp"};
            brooklynClient.run(args);
            // We should only have 1 app in the server's registry
            assertEquals(applicationManager.registry().size(), 1);
            // The name of that app should match what we have provided in the deploy command
            assertEquals(Iterables.getOnlyElement(applicationManager.registry().keySet()), ExampleApp.class.getName());
        } catch (Exception e) {
            LOG.error("stdout="+standardOut()+"; stderr="+standardErr(), e);
            throw e;
        }
    }

    @Test(dependsOnMethods = {"testDeployCreatesApp"})
    public void testListApplicationsShowsRunningApp() throws Exception {
        try {
            String[] args = {"list-applications"};
            brooklynClient.run(args);
            assertThat(standardOut(), containsString("brooklyn.cli.ExampleApp [RUNNING]"));
        } catch (Exception e) {
            LOG.error("stdout="+standardOut()+"; stderr="+standardErr(), e);
            throw e;
        }
    }

    @Test(dependsOnMethods = {"testDeployCreatesApp"})
    public void testUndeployStopsRunningApp() throws Exception {
        try {
            String[] args = {"undeploy","brooklyn.cli.ExampleApp"};
            brooklynClient.run(args);
            assertThat(standardOut(), containsString("Application has been undeployed: brooklyn.cli.ExampleApp"));
        } catch (Exception e) {
            LOG.error("stdout="+standardOut()+"; stderr="+standardErr(), e);
            throw e;
        }
    }

    @Test(dependsOnMethods = {"testUndeployStopsRunningApp"}, expectedExceptions = {CommandExecutionException.class})
    public void testUndeployFailsGracefulyIfNoAppRunning() throws Exception {
        try {
            String[] args = {"undeploy","brooklyn.cli.ExampleApp"};
            brooklynClient.run(args);
            assertThat(standardOut(), containsString("Application 'brooklyn.test.entity.TestApplication' not found"));
        } catch (Exception e) {
            LOG.error("stdout="+standardOut()+"; stderr="+standardErr(), e);
            throw e;
        }
    }

    @Test(dependsOnMethods = {"testUndeployStopsRunningApp"})
    public void testListApplicationsNoRunningApp() throws Exception {
        try {
            String[] args = {"list-applications"};
            brooklynClient.run(args);
            assertEquals(standardOut(), "");
        } catch (Exception e) {
            LOG.error("stdout="+standardOut()+"; stderr="+standardErr(), e);
            throw e;
        }
    }

    @Test
    public void testVersionCommand() throws Exception {
        try {
            String[] args = {"version"};
            brooklynClient.run(args);
            assertThat(standardOut(), containsString("Brooklyn version:"));
        } catch (Exception e) {
            LOG.error("stdout="+standardOut()+"; stderr="+standardErr(), e);
            throw e;
        }
    }

}
