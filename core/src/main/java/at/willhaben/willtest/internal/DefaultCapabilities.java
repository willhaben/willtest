package at.willhaben.willtest.internal;

import org.openqa.selenium.remote.DesiredCapabilities;

public interface DefaultCapabilities {

    <T extends DesiredCapabilities> T addCapabilities(T capabilities);
}
