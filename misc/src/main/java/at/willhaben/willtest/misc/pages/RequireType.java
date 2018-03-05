package at.willhaben.willtest.misc.pages;

import at.willhaben.willtest.misc.utils.ConditionType;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import static java.util.Arrays.asList;

public final class RequireType {

    private List<WebElement> webElementList;
    private List<By> byList;

    public RequireType(WebElement... elements) {
        this.webElementList = asList(elements);
    }

    public RequireType(By... bys) {
        this.byList = asList(bys);
    }

    public ExpectedCondition buildCondition(ConditionType condition) {
        switch (condition) {
            case CLICKABLE:
                return buildClickableCondition();
            case VISIBLE:
                return buildVisibleCondition();
            default:
                throw new IllegalArgumentException("Illegal waiting condition [" + condition.toString() + "].");
        }
    }

    private ExpectedCondition buildClickableCondition() {
        Stream<ExpectedCondition<WebElement>> conditionStream;
        if(Objects.nonNull(webElementList)) {
            conditionStream = webElementList.stream()
                    .map(ExpectedConditions::elementToBeClickable);
        } else {
            conditionStream = byList.stream()
                    .map(ExpectedConditions::elementToBeClickable);
        }
        return ExpectedConditions.and(conditionStream.toArray(ExpectedCondition[]::new));
    }

    private ExpectedCondition buildVisibleCondition() {
        if(Objects.nonNull(webElementList)) {
            return ExpectedConditions.visibilityOfAllElements(webElementList);
        } else {
            return ExpectedConditions.and(byList.stream()
                    .map(ExpectedConditions::visibilityOfAllElementsLocatedBy)
                    .toArray(ExpectedCondition[]::new));
        }
    }
}
