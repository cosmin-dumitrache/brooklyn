package brooklyn.cli.system;

import brooklyn.cli.commands.CommandExecutionException;
import org.testng.annotations.Test;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;

public class UndeployCommandTest extends SystemTest {

    @Test(dependsOnGroups = {"AppDeployed"}, groups = {"AppUndeployed"})
    public void testUndeployStopsRunningApp() throws Exception {
        try {
            String[] args = {"undeploy","brooklyn.cli.system.ExampleApp"};
            brooklynClient.run(args);
            assertThat(standardOut(), containsString("Application has been undeployed: brooklyn.cli.system.ExampleApp"));
        } catch (Exception e) {
            LOG.error("stdout="+standardOut()+"; stderr="+standardErr(), e);
            throw e;
        }
    }

    @Test(dependsOnMethods = {"testUndeployStopsRunningApp"}, expectedExceptions = {CommandExecutionException.class})
    public void testUndeployFailsGracefulyIfNoAppRunning() throws Exception {
        try {
            String[] args = {"undeploy","brooklyn.cli.system.ExampleApp"};
            brooklynClient.run(args);
            assertThat(standardOut(), containsString("Application 'brooklyn.test.entity.TestApplication' not found"));
        } catch (Exception e) {
            LOG.error("stdout="+standardOut()+"; stderr="+standardErr(), e);
            throw e;
        }
    }

}
