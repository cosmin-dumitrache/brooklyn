package brooklyn.cli.system;

import brooklyn.cli.Client;
import brooklyn.rest.BrooklynService;
import brooklyn.rest.core.ApplicationManager;
import com.google.common.io.ByteStreams;
import com.google.common.io.Files;
import org.testng.annotations.Test;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.AfterGroups;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.io.InputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This category of tests checks the client against the actual REST server.
 *
 * The input to a test is a String[] of command line options the user might type
 *
 * The Client will then make a request to the server and then display some output to the user
 *
 * The test will check that this is indeed the standard out/err message that we expect
 */
@Test(groups = {"SystemTest", "Integration"})
public abstract class SystemTest {

    protected static final Logger LOG = LoggerFactory.getLogger(SystemTest.class);

    protected static BrooklynService brooklynServer;
    protected static ApplicationManager applicationManager;
    protected Client brooklynClient;

    private ByteArrayOutputStream outBytes;
    private PrintStream out;
    private ByteArrayOutputStream errBytes;
    private PrintStream err;

    private static File tempConfigFile;

    protected String standardOut() {
        return outBytes.toString();
    }

    protected String standardErr() {
        return errBytes.toString();
    }

    @BeforeGroups(groups = {"SystemTest"})
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

    @AfterGroups(groups = {"SystemTest"})
    public void oneTimeTearDown() throws Exception {
        // Kill the REST server and client instance
        brooklynServer.stop();
        // Delete temp file
        tempConfigFile.delete();
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

}
