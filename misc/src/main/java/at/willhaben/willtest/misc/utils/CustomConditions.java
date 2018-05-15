package at.willhaben.willtest.misc.utils;

import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;

public final class CustomConditions {
    private CustomConditions() {
    }

    /**
     * {@link ExpectedConditions#stalenessOf(WebElement)} does not work well with annotation driven fields
     * (f.i. {@link org.openqa.selenium.support.FindBy}), since the proxy created for the annotated field
     * throws an {@link NoSuchElementException} instead of {@link StaleElementReferenceException}.
     * To have a utility method which works with both annotation driven and custom looked up {@link WebElement}
     * instances, I have written this method.
     *
     * @param element
     * @return a condition which delivers true if the element is stale or missing.
     */
    public static ExpectedCondition<Boolean> stalenessOrAbsenceOf(WebElement element) {
        return webDriver -> {
            try {
                element.getAttribute("justanythingtomakesomeactionwiththeelement");
                return false;
            } catch (StaleElementReferenceException | NoSuchElementException e) {
                return true;
            }
        };
    }

    public static ExpectedCondition<Boolean> selectHasOptionWithText(WebElement select, String optionText) {
        return webDriver -> new Select(select).getOptions().stream().map(WebElement::getText).anyMatch(optionText::equals);
    }
}
