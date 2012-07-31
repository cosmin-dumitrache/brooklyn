package brooklyn.cli.http;

import brooklyn.cli.commands.CatalogPoliciesCommand;
import com.google.mockwebserver.MockResponse;
import com.google.mockwebserver.RecordedRequest;
import org.testng.annotations.Test;
import static org.testng.Assert.assertEquals;

public class CatalogPoliciesCommandTest extends HttpTest {

    @Test
    public void testCatalogPoliciesCommand() throws Exception {

        // 1. Enqueue a response.
        server.enqueue(new MockResponse().setBody("[\"brooklyn.policy.basic.GeneralPurposePolicy\"]"));

        // 2. Make a request that retrieves that response.
        CatalogPoliciesCommand catalogPliciesCommand = new CatalogPoliciesCommand();
        catalogPliciesCommand.endpoint = mockServerEndpoint;
        catalogPliciesCommand.call();

        // 3. Validate the request.
        RecordedRequest request = server.takeRequest();
        assertEquals(request.getRequestLine(), "GET /v1/catalog/policies HTTP/1.1");

    }

}
