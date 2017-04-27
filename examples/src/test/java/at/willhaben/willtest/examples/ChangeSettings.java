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

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assume.assumeThat;

public class ChangeSettings {
    private static final String GEO_LOCATION_IN_THE_MIDDLE_OF_SANKT_POELTEN =
            "data:application/json,{\"location\":{\"lat\":48.201023,\"lng\":15.620250},\"accuracy\":10}";

    @BeforeClass
    public static void beforeClass() {
        Utils.assumeHavingFirefoxConfigured();
    }

    @Rule
    public SeleniumRule seleniumRule = new SeleniumRule()
            //with withDefaultFirefoxSettings settings is getting your location is granted.
            //See at.willhaben.willtest.config.DefaultFirefoxConfigurationParticipant
            .withDefaultFirefoxSettings()
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
        assertThat(webDriver.findElement(By.cssSelector("td#latitude")).getText(),is("48.20102"));
        assertThat(webDriver.findElement(By.cssSelector("td#longitude")).getText(),is("15.62025"));
    }
}
