package at.willhaben.willtest.config;

import org.openqa.selenium.firefox.FirefoxProfile;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * Activates adblocker for the given firefox profile.
 *
 * Expects, that a adblock.xpi exists in classpath root as resource file. This can be achieved by the following
 * POM snippet for example:
 *
 * {@code
<plugin>
    <groupId>org.codehaus.gmaven</groupId>
    <artifactId>groovy-maven-plugin</artifactId>
    <executions>
        <execution>
            <id>download-adblocker</id>
            <phase>generate-test-resources</phase>
            <goals>
                <goal>execute</goal>
            </goals>
            <configuration>
            <source><![CDATA[
                AntBuilder ant = new AntBuilder();
                String fileName = "adblock.xpi";
                String adBlockerPath = project.build.testOutputDirectory + File.separator + fileName;
                File adBlockerFile = new File(adBlockerPath);
                if ( !adBlockerFile.exists() ) {
                    String adBlockerURL = "https://addons.mozilla.org/firefox/downloads/latest/adblock-plus/addon-1865-latest.xpi";
                    println("Downloading adblocker firefox plugin from " + adBlockerURL + "...");
                    ant.mkdir(dir:project.build.testOutputDirectory);
                    ant.get(
                        src:adBlockerURL,
                        dest:adBlockerPath,
                        skipexisting:false );
                }
            ]]></source>
            </configuration>
        </execution>
    </executions>
</plugin>}
 *
 * Created by liptak on 2016.11.21..
 */
public enum AdBlockerConfigurator implements FirefoxConfigurationParticipant {
    INSTANCE;

    private static final String ADBLOCK_XPI_RESOURCE_PATH = "/adblock.xpi";

    public static <T extends WebDriverProvider> T usingAdBlocker( T webDriverProvider ) {
        webDriverProvider.addFirefoxConfigurationParticipant(INSTANCE);
        return webDriverProvider;
    }

    @Override
    public void adjustFirefoxProfile(FirefoxProfile firefoxProfile) {
        try {
            URL adBlockerXPIResourceURL = this.getClass().getResource(ADBLOCK_XPI_RESOURCE_PATH);
            if ( adBlockerXPIResourceURL != null && new File(adBlockerXPIResourceURL.toURI()).exists() ) {
                firefoxProfile.addExtension(new File(adBlockerXPIResourceURL.getFile()));
            }
            else {
                throw new IllegalStateException("Expected a file called '" + ADBLOCK_XPI_RESOURCE_PATH +
                        "' in classpath root! First try a 'mvn clean install' on your maven project. " +
                        "If it does not help, your maven module is not correctly configured. " +
                        "For configuration details please read the JavaDOC of " + this.getClass().getName() + "!");
            }
        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException( "Cannot install adblocker extension!", e );
        }
    }
}
