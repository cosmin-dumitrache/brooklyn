package brooklyn.cli.system;

import brooklyn.entity.basic.BasicEntity;
import org.testng.annotations.Test;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;

public class CatalogEntitiesCommandTest extends SystemTest {

    @Test
    public void testCatalogEntitiesCommand() throws Exception {
        try {
            String[] args = {"catalog-entities"};
            brooklynClient.run(args);
            // Check list of entity types includes one of the defaults
            assertThat(standardOut(), containsString(BasicEntity.class.getName()));
        } catch (Exception e) {
            LOG.error("\nstdout="+standardOut()+"\nstderr="+standardErr()+"\n", e);
            throw e;
        }
    }

}
