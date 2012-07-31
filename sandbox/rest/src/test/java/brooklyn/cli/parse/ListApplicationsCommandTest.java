package brooklyn.cli.parse;

import brooklyn.cli.commands.ListApplicationsCommand;
import org.testng.annotations.Test;

import static org.testng.Assert.assertTrue;

public class ListApplicationsCommandTest extends ParseTest {

    @Test
    public void testListApplicationsCommandTypicalArgs() throws Exception {
        String[] args = {"list-applications"};

        Object command = brooklynClient.getParser().parse(args);

        assertTrue(command instanceof ListApplicationsCommand);
    }

}
