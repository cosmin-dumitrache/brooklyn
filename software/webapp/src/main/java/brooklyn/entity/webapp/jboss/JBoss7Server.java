package brooklyn.entity.webapp.jboss;

import brooklyn.catalog.Catalog;
import brooklyn.entity.basic.SoftwareProcess;
import brooklyn.entity.proxying.ImplementedBy;
import brooklyn.entity.trait.HasShortName;
import brooklyn.entity.webapp.JavaWebAppService;
import brooklyn.entity.webapp.JavaWebAppSoftwareProcess;
import brooklyn.event.basic.BasicAttributeSensor;
import brooklyn.event.basic.BasicAttributeSensorAndConfigKey;
import brooklyn.event.basic.BasicConfigKey;
import brooklyn.event.basic.PortAttributeSensorAndConfigKey;
import brooklyn.util.flags.SetFromFlag;

@Catalog(name="JBoss Application Server 7", description="AS7: an open source Java application server from JBoss", iconUrl="classpath:///jboss-logo.png")
@ImplementedBy(JBoss7ServerImpl.class)
public interface JBoss7Server extends JavaWebAppSoftwareProcess, JavaWebAppService, HasShortName {

    @SetFromFlag("version")
    BasicConfigKey<String> SUGGESTED_VERSION =
            new BasicConfigKey<String>(SoftwareProcess.SUGGESTED_VERSION, "7.1.1.Final");
    // note: 7.1.2.Final fixes many bugs but is not available for download,
    // see https://community.jboss.org/thread/197780
    // 7.2.0.Final should be out during Q3 2012

    @SetFromFlag("downloadUrl")
    BasicAttributeSensorAndConfigKey<String> DOWNLOAD_URL = new BasicAttributeSensorAndConfigKey<String>(
            SoftwareProcess.DOWNLOAD_URL, "http://download.jboss.org/jbossas/7.1/jboss-as-${version}/jboss-as-${version}.tar.gz");

    @SetFromFlag("bindAddress")
    BasicAttributeSensorAndConfigKey<String> BIND_ADDRESS =
            new BasicAttributeSensorAndConfigKey<String>(String.class, "jboss.bind.address", 
                "Address of interface JBoss should listen on, defaulting 0.0.0.0 (but could set e.g. to attributeWhenReady(HOSTNAME)", 
                "0.0.0.0");

    @SetFromFlag("managementHttpPort")
    PortAttributeSensorAndConfigKey MANAGEMENT_HTTP_PORT =
            new PortAttributeSensorAndConfigKey("webapp.jboss.managementHttpPort", "Management port", "9990+");

    /** @deprecated since 0.5.0; use MANAGEMENT_HTTP_PORT instead */
    @Deprecated @SetFromFlag("managementPort")
    PortAttributeSensorAndConfigKey MANAGEMENT_PORT = MANAGEMENT_HTTP_PORT;

    @SetFromFlag("managementHttpsPort")
    PortAttributeSensorAndConfigKey MANAGEMENT_HTTPS_PORT =
            new PortAttributeSensorAndConfigKey("webapp.jboss.managementHttpsPort", "Management port", "9443+");

    @SetFromFlag("managementNativePort")
    PortAttributeSensorAndConfigKey MANAGEMENT_NATIVE_PORT =
            new PortAttributeSensorAndConfigKey("webapp.jboss.managementNativePort", "Management native port", "10999+");

    @SetFromFlag("portIncrement")
    // FIXME this is confusing, can we get rid of it?  (why is it added to the MANAGEMENT_HTTP_PORT? just set that port correctly!)
    BasicAttributeSensorAndConfigKey<Integer> PORT_INCREMENT =
            new BasicAttributeSensorAndConfigKey<Integer>(Integer.class, "webapp.jboss.portIncrement", "Port increment, for all ports in config file", 0);

    @SetFromFlag("deploymentTimeout")
    BasicAttributeSensorAndConfigKey<Integer> DEPLOYMENT_TIMEOUT =
            new BasicAttributeSensorAndConfigKey<Integer>(Integer.class, "webapp.jboss.deploymentTimeout", "Deployment timeout, in seconds", 600);
    
    BasicAttributeSensorAndConfigKey<String> TEMPLATE_CONFIGURATION_URL = new BasicAttributeSensorAndConfigKey<String>(
            String.class, "webapp.jboss.templateConfigurationUrl", "Template file (in freemarker format) for the standalone.xml file", 
            "classpath://brooklyn/entity/webapp/jboss/jboss7-standalone.xml");

    BasicAttributeSensor<Integer> MANAGEMENT_STATUS =
            new BasicAttributeSensor<Integer>(Integer.class, "webapp.jboss.managementStatus", "HTTP response code for the management server");
}
