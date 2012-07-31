package brooklyn.cli.parse;

import brooklyn.cli.commands.CatalogEntitiesCommand;
import org.testng.annotations.Test;

import static org.testng.Assert.assertTrue;

public class CatalogEntitiesCommandTest extends ParseTest {

    @Test
    public void testCatalogEntitiesCommandTypicalArgs() throws Exception {
        String[] args = {"catalog-entities"};

        Object command = brooklynClient.getParser().parse(args);

        assertTrue(command instanceof CatalogEntitiesCommand);
    }

}
