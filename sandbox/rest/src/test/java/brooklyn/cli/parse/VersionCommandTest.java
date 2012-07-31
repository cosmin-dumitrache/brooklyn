package brooklyn.cli.parse;

import brooklyn.cli.commands.VersionCommand;
import org.testng.annotations.Test;

import static org.testng.Assert.assertTrue;

public class VersionCommandTest extends ParseTest {

    @Test
    public void testVersionCommandTypicalArgs() throws Exception {
        String[] args = {"version"};

        Object command = brooklynClient.getParser().parse(args);

        assertTrue(command instanceof VersionCommand);
    }

}
