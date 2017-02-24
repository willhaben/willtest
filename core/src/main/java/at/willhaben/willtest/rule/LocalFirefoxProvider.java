package at.willhaben.willtest.rule;

import at.willhaben.willtest.config.DefaultFirefoxBinaryProvider;
import at.willhaben.willtest.config.FirefoxBinaryProvider;
import org.openqa.selenium.firefox.FirefoxBinary;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.remote.DesiredCapabilities;

public class LocalFirefoxProvider extends AbstractFirefoxProvider<LocalFirefoxProvider,FirefoxDriver> {
    private final FirefoxBinaryProvider firefoxBinaryProvider = new DefaultFirefoxBinaryProvider();

    @Override
    protected FirefoxDriver constructWebDriver(DesiredCapabilities desiredCapabilities) {
        FirefoxBinary firefoxBinary = firefoxBinaryProvider.getFirefoxBinary();
        getFirefoxConfigurationParticipantList().forEach(participant-> participant.adjustFirefoxBinary(firefoxBinary));
        return new FirefoxDriver(firefoxBinary, createFirefoxProfile());
    }

    @Override
    public LocalFirefoxProvider getThis() {
        return this;
    }
}
