package brooklyn.cli.http;

import brooklyn.cli.commands.CatalogEntitiesCommand;
import com.google.mockwebserver.MockResponse;
import com.google.mockwebserver.RecordedRequest;
import org.testng.annotations.Test;
import static org.testng.Assert.assertEquals;

public class CatalogEntitiesCommandTest extends HttpTest {

    @Test
    public void testCatalogEntitiesCommand() throws Exception {

        // 1. Enqueue a response.
        server.enqueue(new MockResponse().setBody("[\"brooklyn.entity.basic.BasicEntity\"]"));

        // 2. Make a request that retrieves that response.
        CatalogEntitiesCommand catalogEntitiesCommand = new CatalogEntitiesCommand();
        catalogEntitiesCommand.endpoint = mockServerEndpoint;
        catalogEntitiesCommand.call();

        // 3. Validate the request.
        RecordedRequest request = server.takeRequest();
        assertEquals(request.getRequestLine(), "GET /v1/catalog/entities HTTP/1.1");

    }

}
