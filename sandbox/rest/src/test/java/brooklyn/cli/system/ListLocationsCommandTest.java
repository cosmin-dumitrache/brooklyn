package brooklyn.cli.system;

import org.testng.annotations.Test;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;

public class ListLocationsCommandTest extends SystemTest {

    @Test
    public void testListLocationsCommand() throws Exception {
        try {
            String[] args = {"list-locations"};
            brooklynClient.run(args);
            // Check list of locations contains what we provided in our config.sample.yml file
            assertThat(standardOut(), containsString("LocationSummary{provider='localhost', config={}, links={self=/v1/locations/0}}"));
            assertThat(standardOut(), containsString("ocationSummary{provider='aws-ec2', config={identity=ADS45345, location=eu-west-1, userName=, sshPublicKey=}, links={self=/v1/locations/1}}"));
        } catch (Exception e) {
            LOG.error("\nstdout="+standardOut()+"\nstderr="+standardErr()+"\n", e);
            throw e;
        }
    }

}
