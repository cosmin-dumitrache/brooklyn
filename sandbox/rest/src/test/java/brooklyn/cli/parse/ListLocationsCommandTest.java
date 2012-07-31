package brooklyn.cli.parse;

import brooklyn.cli.commands.ListLocationsCommand;
import org.testng.annotations.Test;

import static org.testng.Assert.assertTrue;

public class ListLocationsCommandTest extends ParseTest {

    @Test
    public void testListLocationsCommandTypicalArgs() throws Exception {
        String[] args = {"list-locations"};

        Object command = brooklynClient.getParser().parse(args);

        assertTrue(command instanceof ListLocationsCommand);
    }

}
