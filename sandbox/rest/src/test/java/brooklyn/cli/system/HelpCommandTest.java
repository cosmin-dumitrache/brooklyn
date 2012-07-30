package brooklyn.cli.system;

import org.testng.annotations.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;

public class HelpCommandTest extends SystemTest {

    @Test
    public void testHelpCommand() throws Exception {
        try {
            String[] args = {"help"};
            brooklynClient.run(args);
            // TODO: need to find a way to test this because it's not using my i/o streams
            //assertThat(standardOut(), containsString("usage:"));
            //assertThat(standardErr(), containsString("See 'brooklyn help <command>' for more information on a specific command."));
        } catch (Exception e) {
            LOG.error("stdout="+standardOut()+"; stderr="+standardErr(), e);
            throw e;
        }
    }

}
