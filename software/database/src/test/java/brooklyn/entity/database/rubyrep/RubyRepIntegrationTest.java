package brooklyn.entity.database.rubyrep;

import static org.testng.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import brooklyn.entity.basic.ApplicationBuilder;
import brooklyn.entity.basic.Entities;
import brooklyn.entity.database.DatabaseNode;
import brooklyn.entity.database.VogellaExampleAccess;
import brooklyn.entity.database.mysql.MySqlIntegrationTest;
import brooklyn.entity.database.mysql.MySqlNode;
import brooklyn.entity.database.postgresql.PostgreSqlIntegrationTest;
import brooklyn.entity.database.postgresql.PostgreSqlNode;
import brooklyn.entity.proxying.EntitySpecs;
import brooklyn.location.Location;
import brooklyn.location.basic.LocalhostMachineProvisioningLocation;
import brooklyn.test.entity.TestApplication;

public class RubyRepIntegrationTest {

    public static final Logger log = LoggerFactory.getLogger(RubyRepIntegrationTest.class);
    protected TestApplication tapp;

    @BeforeMethod(alwaysRun = true)
    public void setUp() {
        tapp = ApplicationBuilder.newManagedApp(TestApplication.class);
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown() {
        if (tapp != null) Entities.destroy(tapp);
        tapp = null;
    }

    @Test(groups = "Integration")
    public void test_localhost_mysql() throws Exception {
        MySqlNode db1 = tapp.createAndManageChild(EntitySpecs.spec(MySqlNode.class)
                .configure("creationScriptContents", MySqlIntegrationTest.CREATION_SCRIPT)
                .configure("port", "9111"));

        MySqlNode db2 = tapp.createAndManageChild(EntitySpecs.spec(MySqlNode.class)
                .configure("creationScriptContents", MySqlIntegrationTest.CREATION_SCRIPT)
                .configure("port", "9112"));


        startInLocation(tapp, db1, db2, new LocalhostMachineProvisioningLocation());
        testReplication(db1, db2);
    }

    /**
     * Altered to use a single postgresql server to avoid issues with shared memory limits
     */
    @Test(groups = {"Integration"})
    public void test_localhost_postgres() throws Exception {
        String createTwoDbsScript = PostgreSqlIntegrationTest.CREATION_SCRIPT +
                PostgreSqlIntegrationTest.CREATION_SCRIPT.replaceAll("CREATE USER.*", "").replaceAll(" feedback", " feedback1");
        
        PostgreSqlNode db1 = tapp.createAndManageChild(EntitySpecs.spec(PostgreSqlNode.class)
                .configure("creationScriptContents", createTwoDbsScript)
                .configure("port", "9113"));

        startInLocation(tapp, db1, "feedback", db1, "feedback1", new LocalhostMachineProvisioningLocation());
        testReplication(db1, "feedback", db1, "feedback1");
    }

    @Test(enabled = false, groups = "Integration") // TODO this doesn't appear to be supported by RubyRep
    public void test_localhost_postgres_mysql() throws Exception {
        PostgreSqlNode db1 = tapp.createAndManageChild(EntitySpecs.spec(PostgreSqlNode.class)
                .configure("creationScriptContents", PostgreSqlIntegrationTest.CREATION_SCRIPT)
                .configure("port", "9115"));

        MySqlNode db2 = tapp.createAndManageChild(EntitySpecs.spec(MySqlNode.class)
                .configure("creationScriptContents", MySqlIntegrationTest.CREATION_SCRIPT)
                .configure("port", "9116"));


        startInLocation(tapp, db1, db2, new LocalhostMachineProvisioningLocation());
        testReplication(db1, db2);
    }

    public static void startInLocation(TestApplication tapp, DatabaseNode db1, DatabaseNode db2, Location... locations) throws Exception {
        startInLocation(tapp, db1, "feedback", db2, "feedback", locations);
    }

    /**
     * Configures rubyrep to connect to the two databases and starts the app
     */
    public static void startInLocation(TestApplication tapp, DatabaseNode db1, String dbName1, DatabaseNode db2, String dbName2, Location... locations) throws Exception {
        tapp.createAndManageChild(EntitySpecs.spec(RubyRepNode.class)
                .configure("leftDatabase", db1)
                .configure("rightDatabase", db2)
                .configure("leftUsername", "sqluser")
                .configure("rightUsername", "sqluser")
                .configure("rightPassword", "sqluserpw")
                .configure("leftPassword", "sqluserpw")
                .configure("leftDatabaseName", dbName1)
                .configure("rightDatabaseName", dbName2)
                .configure("replicationInterval", 1)
        );

        tapp.start(Arrays.asList(locations));
    }
    

    public static void testReplication(DatabaseNode db1, DatabaseNode db2) throws Exception {
        testReplication(db1, "feedback", db2, "feedback");
    }

    /**
     * Tests replication between the two databases by altering the first and checking the change is applied to the second
     */
    public static void testReplication(DatabaseNode db1, String dbName1, DatabaseNode db2, String dbName2) throws Exception {
        String db1Url = db1.getAttribute(DatabaseNode.DB_URL);
        String db2Url = db2.getAttribute(DatabaseNode.DB_URL);

        log.info("Testing replication between " + db1Url + " and " + db2Url);

        VogellaExampleAccess vea1 = new VogellaExampleAccess(db1 instanceof MySqlNode ? "com.mysql.jdbc.Driver" : "org.postgresql.Driver",
                db1Url, dbName1);
        VogellaExampleAccess vea2 = new VogellaExampleAccess(db2 instanceof MySqlNode ? "com.mysql.jdbc.Driver" : "org.postgresql.Driver",
                db2Url, dbName2);

        try {
            vea1.connect();
            List<List<String>> rs = vea1.readDataBase();
            assertEquals(rs.size(), 1);

            vea2.connect();
            rs = vea2.readDataBase();
            assertEquals(rs.size(), 1);

            log.info("Modifying left database");
            vea1.modifyDataBase();

            log.info("Reading left database");
            rs = vea1.readDataBase();
            assertEquals(rs.size(), 2);

            log.info("Reading right database");
            rs = vea2.readDataBase();

            for (int i = 0; i < 60 && rs.size() != 2; i++) {
                log.info("Sleeping for a second");
                Thread.sleep(1000);
                rs = vea2.readDataBase();
            }

            assertEquals(rs.size(), 2);
        } finally {
            vea1.close();
            vea2.close();
        }
    }
}
