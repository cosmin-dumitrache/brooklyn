package brooklyn.cli.system;

import org.testng.annotations.Test;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;

public class VersionCommandTest extends SystemTest {

    @Test
    public void testVersionCommand() throws Exception {
        try {
            String[] args = {"version"};
            brooklynClient.run(args);
            assertThat(standardOut(), containsString("Brooklyn version:"));
        } catch (Exception e) {
            LOG.error("stdout="+standardOut()+"; stderr="+standardErr(), e);
            throw e;
        }
    }

}
