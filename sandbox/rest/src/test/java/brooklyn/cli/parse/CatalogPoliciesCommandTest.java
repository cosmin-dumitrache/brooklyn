package brooklyn.cli.parse;

import brooklyn.cli.commands.CatalogPoliciesCommand;
import org.testng.annotations.Test;

import static org.testng.Assert.assertTrue;

public class CatalogPoliciesCommandTest extends ParseTest {

    @Test
    public void testCatalogPoliciesCommandTypicalArgs() throws Exception {
        String[] args = {"catalog-policies"};

        Object command = brooklynClient.getParser().parse(args);

        assertTrue(command instanceof CatalogPoliciesCommand);
    }

}
