package brooklyn.cli.system;

import brooklyn.entity.basic.BasicEntity;
import com.google.common.collect.Iterables;
import org.testng.annotations.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.testng.Assert.assertEquals;

public class DeployCommandTest extends SystemTest {

    @Test(groups = {"AppDeployed"})
    public void testDeployCreatesApp() throws Exception {
        try {
            String[] args = {"deploy","--format","class", "brooklyn.cli.system.ExampleApp"};
            brooklynClient.run(args);
            // We should only have 1 app in the server's registry
            assertEquals(applicationManager.registry().size(), 1);
            // The name of that app should match what we have provided in the deploy command
            assertEquals(Iterables.getOnlyElement(applicationManager.registry().keySet()), ExampleApp.class.getName());
            // Check that the client reports that appropriately
            assertThat(standardOut(), containsString("The application has been deployed: brooklyn.cli.system.ExampleApp"));
        } catch (Exception e) {
            LOG.error("stdout="+standardOut()+"; stderr="+standardErr(), e);
            throw e;
        }
    }

}
