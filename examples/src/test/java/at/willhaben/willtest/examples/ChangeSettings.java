package at.willhaben.willtest.examples;

import at.willhaben.willtest.config.FirefoxConfigurationParticipant;
import at.willhaben.willtest.config.WebDriverConfigurationParticipant;
import at.willhaben.willtest.misc.rule.SeleniumRule;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxProfile;

import java.time.Duration;
import java.time.temporal.TemporalUnit;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class ChangeSettings {
    private static final String GEO_LOCATION_IN_THE_MIDDLE_OF_SANKT_POELTEN =
            "data:application/json,{\"location\":{\"lat\":48.201023,\"lng\":15.620250},\"accuracy\":10}";

    @BeforeClass
    public static void beforeClass() {
        Utils.assumeHavingFirefoxConfigured();
    }

    @Rule
    public final SeleniumRule seleniumRule = new SeleniumRule()
            //with withDefaultFirefoxSettings settings is getting your location is granted.
            //See at.willhaben.willtest.config.DefaultFirefoxConfigurationParticipant
            .withDefaultFirefoxSettings()
            .withImplicitWait(Duration.ofSeconds(30))
            .withFirefoxConfigurationParticipant(
                new FirefoxConfigurationParticipant() {
                    @Override
                    public void adjustFirefoxProfile(FirefoxProfile firefoxProfile) {
                        firefoxProfile.setPreference("geo.wifi.uri",GEO_LOCATION_IN_THE_MIDDLE_OF_SANKT_POELTEN);
                    }
                })
            .addWebDriverConfigurationParticipant(new WebDriverConfigurationParticipant<WebDriver>() {
                @Override
                public void postConstruct(WebDriver webDriver) {
                    webDriver.manage().window().setSize(new Dimension(800, 600));
                }
            });

    @Test
    public void testMyLocation() {
        WebDriver webDriver = seleniumRule.getWebDriver();
        webDriver.get("https://mycurrentlocation.net/");
        By latitudeLocator = By.cssSelector("td#latitude");
        seleniumRule
                .getDefaultWebDriverWait()
                .until(wd -> !wd.findElement(latitudeLocator).getText().isEmpty());
        assertThat(webDriver.findElement(latitudeLocator).getText(),is("48.20102"));
        assertThat(webDriver.findElement(By.cssSelector("td#longitude")).getText(),is("15.62025"));
    }
}
