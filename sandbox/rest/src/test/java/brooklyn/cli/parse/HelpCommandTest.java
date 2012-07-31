package brooklyn.cli.parse;

import brooklyn.cli.commands.HelpCommand;
import org.testng.annotations.Test;

import static org.testng.Assert.assertTrue;

public class HelpCommandTest extends ParseTest {

    @Test
    public void testHelpCommandTypicalArgs() throws Exception {
        String[] args = {"help"};

        Object command = brooklynClient.getParser().parse(args);

        assertTrue(command instanceof HelpCommand);
    }

}
