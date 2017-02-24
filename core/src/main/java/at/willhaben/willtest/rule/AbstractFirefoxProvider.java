package at.willhaben.willtest.rule;

import at.willhaben.willtest.config.FirefoxConfigurationParticipant;
import org.junit.runner.Description;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public abstract class AbstractFirefoxProvider<P extends AbstractFirefoxProvider<P,D>, D extends WebDriver>
        extends AbstractSeleniumProvider<P,D>
        implements FirefoxProvider<P,D>{

    private final List<FirefoxConfigurationParticipant> firefoxConfigurationParticipantList = new ArrayList<>();

    @Override
    public P addFirefoxConfigurationParticipant(FirefoxConfigurationParticipant firefoxConfigurationParticipant) {
        Objects.requireNonNull(firefoxConfigurationParticipant);
        this.firefoxConfigurationParticipantList.add(firefoxConfigurationParticipant);
        return getThis();
    }

    @Override
    protected DesiredCapabilities createDesiredCapabilities(Description description) {
        DesiredCapabilities desiredCapabilities = super.createDesiredCapabilities(description);
        desiredCapabilities.setCapability("applicationCacheEnabled", false);
        desiredCapabilities.setJavascriptEnabled(true);
        desiredCapabilities.setBrowserName("firefox");
        FirefoxProfile firefoxProfile = createFirefoxProfile();
        firefoxConfigurationParticipantList.forEach(participant->participant.adjustFirefoxProfile(firefoxProfile));
        desiredCapabilities.setCapability(FirefoxDriver.PROFILE, firefoxProfile);
        return desiredCapabilities;
    }

    protected List<FirefoxConfigurationParticipant> getFirefoxConfigurationParticipantList() {
        return Collections.unmodifiableList(firefoxConfigurationParticipantList);
    }

    protected FirefoxProfile createFirefoxProfile() {
        return new FirefoxProfile();
    }
}
