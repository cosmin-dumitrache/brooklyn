package brooklyn.entity.messaging.qpid;

import static brooklyn.test.TestUtils.*
import static java.util.concurrent.TimeUnit.*
import static org.testng.Assert.*

import javax.jms.Connection
import javax.jms.MessageConsumer
import javax.jms.MessageProducer
import javax.jms.Queue
import javax.jms.Session
import javax.jms.TextMessage

import org.apache.qpid.client.AMQConnectionFactory
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.testng.annotations.AfterMethod
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test

import brooklyn.entity.Application
import brooklyn.entity.basic.AbstractApplication
import brooklyn.entity.basic.Attributes
import brooklyn.entity.basic.JavaApp
import brooklyn.entity.trait.Startable
import brooklyn.location.Location
import brooklyn.location.basic.LocalhostMachineProvisioningLocation
import brooklyn.management.Task
import brooklyn.util.internal.EntityStartUtils
import brooklyn.util.internal.TimeExtras

/**
 * Test the operation of the {@link QpidBroker} class.
 *
 * TODO clarify test purpose
 */
public class QpidIntegrationTest {
    private static final Logger log = LoggerFactory.getLogger(QpidIntegrationTest.class)

    static { TimeExtras.init() }

    private Application app
    private Location testLocation
    private QpidBroker qpid

    static class TestApplication extends AbstractApplication {
        public TestApplication(Map properties=[:]) {
            super(properties)
        }
    }

    @BeforeMethod(groups = "Integration")
    public void setup() {
        app = new TestApplication();
        testLocation = new LocalhostMachineProvisioningLocation(name:'london', count:2)
    }

    @AfterMethod(groups = "Integration")
    public void shutdown() {
        if (qpid != null && qpid.getAttribute(Startable.SERVICE_UP)) {
	        EntityStartUtils.stopEntity(qpid)
        }
    }

    /**
     * Test that the broker starts up and sets SERVICE_UP correctly.
     */
    @Test(groups = "Integration")
    public void canStartupAndShutdown() {
        qpid = new QpidBroker(owner:app);
        qpid.start([ testLocation ])
        executeUntilSucceedsWithFinallyBlock ([:], {
            assertTrue qpid.getAttribute(JavaApp.SERVICE_UP)
        }, {
            qpid.stop()
        })
        assertFalse qpid.getAttribute(JavaApp.SERVICE_UP)
    }

    /**
     * Test that setting the 'queue' property causes a named queue to be created.
     */
    @Test(groups = "Integration")
    public void testCreatingQueues() {
        String queueName = "testQueue"
        int number = 20
        String content = "01234567890123456789012345678901"

        // Start broker with a configured queue
        qpid = new QpidBroker(owner:app, queue:queueName);
        qpid.start([ testLocation ])
        executeUntilSucceeds([:], {
            assertTrue qpid.getAttribute(JavaApp.SERVICE_UP)
        })

        try {
            // Check queue created
            assertFalse qpid.queueNames.isEmpty()
            assertEquals qpid.queueNames.size(), 1
            assertTrue qpid.queueNames.contains(queueName)
            assertEquals qpid.ownedChildren.size(), 1
            assertFalse qpid.queues.isEmpty()
            assertEquals qpid.queues.size(), 1

            // Get the named queue entity
            QpidQueue queue = qpid.queues[queueName]
            assertNotNull queue

            // Connect to broker using JMS and send messages
            Connection connection = getQpidConnection(qpid)
            clearQueue(connection, queue.bindingUrl)
            sendMessages(connection, number, queue.bindingUrl, content)
            Thread.sleep 1000

            // Check messages arrived
            assertEquals queue.getAttribute(QpidQueue.MESSAGE_COUNT), number
            assertEquals queue.getAttribute(QpidQueue.QUEUE_DEPTH), number * content.length()

            // Clear the messages
            assertEquals clearQueue(connection, queue.bindingUrl), number
            Thread.sleep 1000

            // Check messages cleared
            assertEquals queue.getAttribute(QpidQueue.MESSAGE_COUNT), 0
            assertEquals queue.getAttribute(QpidQueue.QUEUE_DEPTH), 0
	        connection.close()

            // Close the JMS connection
        } finally {
            // Stop broker
	        qpid.stop()
        }
    }

    private Connection getQpidConnection(QpidBroker qpid) {
        int port = qpid.getAttribute(Attributes.AMQP_PORT)
        AMQConnectionFactory factory = new AMQConnectionFactory("amqp://admin:admin@brooklyn/localhost?brokerlist='tcp://localhost:${port}'")
        Connection connection = factory.createConnection();
        connection.start();
        return connection
    }

    private void sendMessages(Connection connection, int count, String queueName, String content="") {
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE)
        Queue destination = session.createQueue(queueName)
        MessageProducer messageProducer = session.createProducer(destination)

        count.times {
            TextMessage message = session.createTextMessage(content)
            messageProducer.send(message);
        }

        session.close()
    }

    private int clearQueue(Connection connection, String queueName) {
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE)
        Queue destination = session.createQueue(queueName)
        MessageConsumer messageConsumer = session.createConsumer(destination)

        int received = 0
        while (messageConsumer.receive(500) != null) received++

        session.close()
        
        received
    }
}