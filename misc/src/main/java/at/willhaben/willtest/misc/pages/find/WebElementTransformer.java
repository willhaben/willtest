package at.willhaben.willtest.misc.pages.find;

import org.openqa.selenium.WebElement;

public interface WebElementTransformer<T> {

    T generateElement(WebElement element);
}
