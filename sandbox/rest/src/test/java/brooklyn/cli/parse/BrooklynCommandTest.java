package brooklyn.cli.parse;

import brooklyn.cli.commands.BrooklynCommand;
import org.testng.annotations.Test;

import static org.testng.Assert.assertTrue;

public class BrooklynCommandTest extends ParseTest {

    /**
     * Tests default arguments common to all implementations of
     * {@link brooklyn.cli.commands.BrooklynCommand}
     */
    @Test
    public void testBrooklynCommandDefaultArgs() throws Exception {
        // Just use a very simple command (version) to test the common cli arguments
        String[] args = {"version"};

        Object command = brooklynClient.getParser().parse(args);

        assertTrue(command instanceof BrooklynCommand);
        BrooklynCommand brooklynCommand = (BrooklynCommand) command;

        assertTrue(brooklynCommand.embedded == false);
        assertTrue(brooklynCommand.endpoint.equals(BrooklynCommand.DEFAULT_ENDPOINT));
        assertTrue(brooklynCommand.noRetry == false);
        assertTrue(brooklynCommand.retry == BrooklynCommand.DEFAULT_RETRY_PERIOD);
    }

    /**
     * Tests all arguments common to all implementations of {@link BrooklynCommand}
     */
    @Test
    public void testBrooklynCommandAllArgs() throws Exception {
        // Just use a very simple command (version) to test the common cli arguments
        String[] args = {"--embedded",
                "--endpoint","http://test.endpoint.com:1337",
                "--no-retry",
                "--retry","42",
                "version"};

        Object command = brooklynClient.getParser().parse(args);

        assertTrue(command instanceof BrooklynCommand);
        BrooklynCommand brooklynCommand = (BrooklynCommand) command;

        assertTrue(brooklynCommand.embedded == true);
        assertTrue(brooklynCommand.endpoint.equals("http://test.endpoint.com:1337"));
        assertTrue(brooklynCommand.noRetry == true);
        assertTrue(brooklynCommand.retry == 42);
    }

}
