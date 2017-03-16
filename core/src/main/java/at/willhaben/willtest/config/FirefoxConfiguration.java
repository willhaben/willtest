package at.willhaben.willtest.config;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxBinary;
import org.openqa.selenium.firefox.FirefoxProfile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Central class to configure anything on a firefox instance which is more than what a
 * {@link WebDriverConfigurationParticipant} enables to change. Builds the output of
 * {@link FirefoxConfigurationParticipant} instances together into a {@link FirefoxProfile}, and
 * takes care of calling {@link FirefoxBinaryProvider} to get a {@link FirefoxBinary}.
 * {@link FirefoxConfigurationParticipant} instances can also change the settings of the {@link FirefoxBinary}.
 */
public class FirefoxConfiguration<D extends WebDriver> {
    private final List<FirefoxConfigurationParticipant> firefoxConfigurationParticipantList = new ArrayList<>();
    private FirefoxBinaryProvider firefoxBinaryProvider = new DefaultFirefoxBinaryProvider();

    public FirefoxConfiguration() {
    }

    public void setFirefoxBinaryProvider(FirefoxBinaryProvider firefoxBinaryProvider) {
        this.firefoxBinaryProvider = firefoxBinaryProvider;
    }

    /**
     * Adds a {@link FirefoxConfigurationParticipant} to the list. This participant will be called before creating
     * the actual firefox browser, and it can influence the {@link FirefoxProfile}
     * @param firefoxConfigurationParticipant the participant to be added
     * @return this to enable method chaining
     */
    public FirefoxConfiguration<D> addFirefoxConfigurationParticipant(
            FirefoxConfigurationParticipant firefoxConfigurationParticipant) {
        Objects.requireNonNull(firefoxConfigurationParticipant);
        this.firefoxConfigurationParticipantList.add(firefoxConfigurationParticipant);
        return this;
    }

    protected List<FirefoxConfigurationParticipant> getFirefoxConfigurationParticipantList() {
        return Collections.unmodifiableList(firefoxConfigurationParticipantList);
    }

    /**
     * @return fully configured firefox binary
     */
    public FirefoxBinary getFirefoxBinary() {
        FirefoxBinary firefoxBinary = firefoxBinaryProvider.getFirefoxBinary();
        firefoxConfigurationParticipantList.forEach(participant -> participant.adjustFirefoxBinary(firefoxBinary));
        return firefoxBinary;
    }

    /**
     * @return fully configured firefox profile
     */
    public FirefoxProfile getFirefoxProfile() {
        FirefoxProfile firefoxProfile = new FirefoxProfile();
        firefoxConfigurationParticipantList.forEach(participant -> participant.adjustFirefoxProfile(firefoxProfile));
        return firefoxProfile;
    }
}
