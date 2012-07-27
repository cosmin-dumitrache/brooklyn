package brooklyn.cli.http;

/**
 * This category of tests checks if the client can generate the correct HTTP requests for a
 * certain subtype of {@link BrooklynCommand} with a certain configuration of its attributes.
 *
 * The input to a test is a pre-configured instance of a {@link BrooklynCommand}
 *
 * The Client will execute the command which will have the effect of generating some
 * HTTP requests. Some of these requests will contain some JSON data.
 *
 * The test will check to make sure that these are indeed the requests we want to make
 * and that they indeed contain the correct JSON string inside.
 *
 * In order to do this, the tests make use a mock web server to simulate the interaction
 * with a real Brooklyn REST server.
 */
public abstract class HttpTest {

    // TODO

}
