package brooklyn.cli.parse;

import brooklyn.cli.commands.BrooklynCommand;
import brooklyn.cli.commands.UndeployCommand;
import org.iq80.cli.ParseException;
import org.testng.annotations.Test;

import static org.testng.Assert.assertTrue;

public class UndeployCommandTest extends ParseTest {

    @Test
    public void testUndeployCommandTypicalArgs() throws Exception {
        String[] args = {"undeploy","brooklyn.cli.system.ExampleApp"};

        Object command = brooklynClient.getParser().parse(args);

        assertTrue(command instanceof UndeployCommand);
        UndeployCommand undeployCommand = (UndeployCommand) command;

        assertTrue(undeployCommand.app.equals("brooklyn.cli.system.ExampleApp"));
        assertTrue(undeployCommand.noStop == false);
    }

    @Test
    public void testUndeployCommandAllArgs() throws Exception {
        String[] args = {"undeploy","--no-stop","brooklyn.cli.system.ExampleApp"};

        Object command = brooklynClient.getParser().parse(args);

        assertTrue(command instanceof UndeployCommand);
        UndeployCommand undeployCommand = (UndeployCommand) command;

        assertTrue(undeployCommand.app.equals("brooklyn.cli.system.ExampleApp"));
        assertTrue(undeployCommand.noStop == true);
    }

    @Test(expectedExceptions = {ParseException.class})
    public void testUndeployCommandNoAppArg() throws Exception {
        String[] args = {"undeploy"}; // no APP argument given, expecting exception

        Object command = brooklynClient.getParser().parse(args);
    }

}
