package brooklyn.cli.system;

import brooklyn.policy.basic.GeneralPurposePolicy;
import org.testng.annotations.Test;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;

public class CatalogPoliciesCommandTest extends SystemTest {

    @Test
    public void testCatalogPoliciesCommand() throws Exception {
        try {
            String[] args = {"catalog-policies"};
            brooklynClient.run(args);
            // Check list of entity types includes one of the defaults
            assertThat(standardOut(), containsString(GeneralPurposePolicy.class.getName()));
        } catch (Exception e) {
            LOG.error("\nstdout="+standardOut()+"\nstderr="+standardErr()+"\n", e);
            throw e;
        }
    }

}
