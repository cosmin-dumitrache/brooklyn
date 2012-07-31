package brooklyn.cli.http;

import brooklyn.cli.commands.DeployCommand;
import com.google.mockwebserver.MockResponse;
import com.google.mockwebserver.RecordedRequest;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

public class DeployCommandTest extends HttpTest {

    @Test
    public void testDeployCommand() throws Exception {

        // 1. Enqueue a response.
        server.enqueue(new MockResponse()
                .setResponseCode(201) // CREATED
                .addHeader("Location: "+mockServerEndpoint+"/v1/applications/brooklyn.test.App")); // at this location
        server.enqueue(new MockResponse()
                .setBody("{\"spec\":\n" +
                            "{\"name\":\"brooklyn.test.App\"," +
                            " \"entities\":[{" +
                                "\"name\":\"brooklyn.test.App\"," +
                                "\"type\":\"brooklyn.test.App\"," +
                                "\"config\":{}}]," +
                            "\"locations\":[\"/v1/locations/1\"]}," +
                            "\"status\":\"RUNNING\"," +
                            "\"links\":{" +
                                "\"self\":\"/v1/applications/brooklyn.test.App\"," +
                                "\"entities\":\"/v1/applications/brooklyn.test.App/entities\"}}")
                .setResponseCode(200)); // OK

        // 2. Make a request that retrieves that response.
        DeployCommand deployCommand = new DeployCommand();
        deployCommand.endpoint = mockServerEndpoint;
        deployCommand.app = "brooklyn.test.App";
        deployCommand.call();

        // 3. Validate the request.
        RecordedRequest request = server.takeRequest();
        assertEquals(request.getRequestLine(), "POST /v1/applications HTTP/1.1");
        LOG.info(">>> {}", request.getBody().toString());
        request = server.takeRequest();
        assertEquals(request.getRequestLine(), "GET /v1/applications/brooklyn.test.App HTTP/1.1");
    }

}
