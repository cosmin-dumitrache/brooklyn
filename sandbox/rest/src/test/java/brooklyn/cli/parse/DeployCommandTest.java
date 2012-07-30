package brooklyn.cli.parse;

import brooklyn.cli.commands.DeployCommand;
import org.iq80.cli.ParseException;
import org.testng.annotations.Test;

import static org.testng.Assert.assertTrue;

public class DeployCommandTest extends ParseTest {

    @Test
    public void testDeployCommandTypicalArgs() throws Exception {
        String[] args = {"deploy","--format","class", "brooklyn.cli.system.ExampleApp"};

        Object command = brooklynClient.getParser().parse(args);

        assertTrue(command instanceof DeployCommand);
        DeployCommand deployCommand = (DeployCommand) command;

        assertTrue(deployCommand.app.equals("brooklyn.cli.system.ExampleApp"));
        assertTrue(deployCommand.format.equals(DeployCommand.CLASS_FORMAT));
        assertTrue(deployCommand.classpath == null);
        assertTrue(deployCommand.config == null);
        assertTrue(deployCommand.locations == null);
        assertTrue(deployCommand.noStart == false);
    }

    @Test
    public void testDeployCommandAllArgs() throws Exception {
        String[] args = {"deploy",
                "--format","groovy",
                "--classpath","/path/to/my/app/target/classes",
                "--config","k1=v1,k2=v2,k3=v3",
                "--locations","aws-ec2:eu-west-1,aws-ec2:us-east-1",
                "--no-start",
                "MyTestApp.groovy"};

        Object command = brooklynClient.getParser().parse(args);

        assertTrue(command instanceof DeployCommand);
        DeployCommand deployCommand = (DeployCommand) command;

        assertTrue(deployCommand.app.equals("MyTestApp.groovy"));
        assertTrue(deployCommand.format.equals(DeployCommand.GROOVY_FORMAT));
        assertTrue(deployCommand.classpath.equals("/path/to/my/app/target/classes"));
        assertTrue(deployCommand.config.equals("k1=v1,k2=v2,k3=v3"));
        assertTrue(deployCommand.locations.equals("aws-ec2:eu-west-1,aws-ec2:us-east-1"));
        assertTrue(deployCommand.noStart == true);
    }

    @Test(expectedExceptions = {ParseException.class})
    public void testDeployCommandNoAppArg() throws Exception {
        String[] args = {"deploy"}; // no APP argument given, expecting exception

        Object command = brooklynClient.getParser().parse(args);
    }

}
