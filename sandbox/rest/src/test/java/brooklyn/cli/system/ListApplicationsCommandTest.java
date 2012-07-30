package brooklyn.cli.system;

import org.testng.annotations.Test;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.testng.AssertJUnit.assertEquals;

public class ListApplicationsCommandTest extends SystemTest {

    @Test(dependsOnGroups = {"AppDeployed"})
    public void testListApplicationsShowsRunningApp() throws Exception {
        try {
            String[] args = {"list-applications"};
            brooklynClient.run(args);
            assertThat(standardOut(), containsString("brooklyn.cli.system.ExampleApp [RUNNING]"));
        } catch (Exception e) {
            LOG.error("stdout="+standardOut()+"; stderr="+standardErr(), e);
            throw e;
        }
    }

    @Test(dependsOnGroups = {"AppUndeployed"})
    public void testListApplicationsNoRunningApp() throws Exception {
        try {
            String[] args = {"list-applications"};
            brooklynClient.run(args);
            assertEquals(standardOut(), "");
        } catch (Exception e) {
            LOG.error("stdout="+standardOut()+"; stderr="+standardErr(), e);
            throw e;
        }
    }

}
