package at.willhaben.willtest.config;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.internal.ElementScrollBehavior;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;

/**
 * If an element should be clicked, and it is not in the viewport (not visible), then selenium has to scroll
 * to make the element clickable. However, if you have the element then in the top of the viewport, then you might
 * get problems with an fixed top menu bar, which would prevent your element from being clicked. This configurator
 * lets you decide if you want to get scrolled elements at the top or bottom of the viewport.
 * <p>
 */
public class ElementScrollBehaviourConfigurator implements WebDriverConfigurationParticipant<WebDriver> {
    private final ElementScrollBehavior elementScrollBehavior;

    public ElementScrollBehaviourConfigurator(ElementScrollBehavior elementScrollBehavior) {
        this.elementScrollBehavior = elementScrollBehavior;
    }

    @Override
    public void addDesiredCapabilities(DesiredCapabilities desiredCapabilities) {
        desiredCapabilities.setCapability(CapabilityType.ELEMENT_SCROLL_BEHAVIOR, elementScrollBehavior);
    }
}
