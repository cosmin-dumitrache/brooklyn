package brooklyn.cli.parse;

import brooklyn.cli.Client;
import brooklyn.cli.commands.BrooklynCommand;
import brooklyn.cli.commands.DeployCommand;
import brooklyn.cli.commands.UndeployCommand;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;
import static org.testng.Assert.assertTrue;
import org.iq80.cli.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * This category of tests checks if the client can correctly transform some
 * user provided arguments into the Client's internal representation.
 *
 * The input to a test is a String[] of command line options the user might type
 *
 * The Client will produce a subtype of {@link BrooklynCommand} with attributes already set
 *
 * The test will check to make sure that this is indeed the internal representation that we expect.
 */
@Test(groups = {"ParseTest"})
public abstract class ParseTest {

    protected static Client brooklynClient;
    protected static final Logger LOG = LoggerFactory.getLogger(ParseTest.class);

    @BeforeGroups(groups = {"ParseTest"})
    public void oneTimeSetUp() throws Exception {
        brooklynClient = new Client();
    }

}
