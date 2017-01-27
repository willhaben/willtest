package at.willhaben.willtest.config;

import com.google.common.base.Function;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriver.Timeouts;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * Created by weisgrmi on 01.09.2016.
 */
public class TimeoutsConfigurationParticipant implements WebDriverConfigurationParticipant {
    private Long implicitWait;
    private TimeUnit implicitWaitUnit;
    private Long scriptTimeout;
    private TimeUnit scriptTimeoutUnit;
    private Long pageLoadTimeout;
    private TimeUnit pageLoadTimeoutUnit;

    private static final long CONDITION_POLL_INTERVAL = 200;

    @Override
    public void postConstruct(WebDriver webDriver) {
        Optional.ofNullable(implicitWait)
                .ifPresent(wait -> webDriver.manage().timeouts().implicitlyWait(wait, implicitWaitUnit));
        Optional.ofNullable(scriptTimeout)
                .ifPresent(wait -> webDriver.manage().timeouts().setScriptTimeout(wait, scriptTimeoutUnit));
        Optional.ofNullable(pageLoadTimeout)
                .ifPresent(wait -> webDriver.manage().timeouts().implicitlyWait(wait, pageLoadTimeoutUnit));
    }

    public Optional<Long> getImplicitWaitInMilliSeconds() {
        return Optional.ofNullable(implicitWait)
                .map(wait -> TimeUnit.MILLISECONDS.convert(wait, implicitWaitUnit));
    }

    public Optional<Long> getScriptTimeoutInMilliSeconds() {
        return Optional.ofNullable(scriptTimeout)
                .map(wait -> TimeUnit.MILLISECONDS.convert(wait, scriptTimeoutUnit));
    }

    public Optional<Long> getPageLoadTimeoutInMilliSeconds() {
        return Optional.ofNullable(pageLoadTimeout)
                .map(wait -> TimeUnit.MILLISECONDS.convert(wait, pageLoadTimeoutUnit));
    }

    public Wait<WebDriver> waitOverridingImplicitWait(WebDriver webDriver, int seconds) {
        return new Wait<WebDriver>() {
            @Override
            public <T> T until(Function<? super WebDriver, T> function) {
                Timeouts timeouts = webDriver.manage().timeouts();
                try {
                    timeouts.implicitlyWait(0, TimeUnit.SECONDS);
                    return new WebDriverWait(webDriver, seconds, CONDITION_POLL_INTERVAL).until(function);
                } finally {
                    timeouts.implicitlyWait(implicitWait, implicitWaitUnit);
                }
            }
        };
    }

    public <T extends WebDriverProvider> T addTo(T webDriverProvider) {
        webDriverProvider.addWebDriverConfigurationParticipant(this);
        return webDriverProvider;
    }

    public TimeoutsConfigurationParticipant withImplicitWait(Long timeout, TimeUnit timeUnit) {
        Objects.requireNonNull(timeout);
        Objects.requireNonNull(timeUnit);
        this.implicitWait = timeout;
        this.implicitWaitUnit = timeUnit;
        return this;
    }

    public TimeoutsConfigurationParticipant withScriptTimeout(Long timeout, TimeUnit timeUnit) {
        Objects.requireNonNull(timeout);
        Objects.requireNonNull(timeUnit);
        this.scriptTimeout = timeout;
        this.scriptTimeoutUnit = timeUnit;
        return this;
    }

    public TimeoutsConfigurationParticipant withPageLoadTimeout(Long timeout, TimeUnit timeUnit) {
        Objects.requireNonNull(timeout);
        Objects.requireNonNull(timeUnit);
        this.pageLoadTimeout = timeout;
        this.pageLoadTimeoutUnit = timeUnit;
        return this;
    }

    public TimeoutsConfigurationParticipant withoutImplicitWait() {
        this.implicitWait = null;
        return this;
    }

    public TimeoutsConfigurationParticipant withoutPageLoadTimeout() {
        this.pageLoadTimeout = null;
        return this;
    }

    public TimeoutsConfigurationParticipant withoutScriptTimeout() {
        this.scriptTimeout = null;
        return this;
    }
}