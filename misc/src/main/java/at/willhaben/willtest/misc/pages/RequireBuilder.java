package at.willhaben.willtest.misc.pages;

import at.willhaben.willtest.misc.utils.ConditionType;
import at.willhaben.willtest.misc.utils.WhFluentWait;

public class RequireBuilder {

    private final PageObject pageObject;
    private final RequireType requireType;
    private String errorMessage;

    RequireBuilder(PageObject pageObject, RequireType requireType) {
        this.pageObject = pageObject;
        this.requireType = requireType;
    }

    public RequireBuilder withErrorMessage(String message) {
        this.errorMessage = message;
        return this;
    }

    public void clickable() {
        clickable(PageObject.DEFAULT_WAIT_TIMEOUT);
    }

    public void clickable(long timeout) {
        clickable(pageObject.getWait(timeout));
    }

    @SuppressWarnings("unchecked")
    public void clickable(WhFluentWait waiter) {
        waiter.until(errorMessage, requireType.buildCondition(ConditionType.CLICKABLE));
    }

    public void visible() {
        visible(PageObject.DEFAULT_WAIT_TIMEOUT);
    }

    public void visible(long timeout) {
        visible(pageObject.getWait(timeout));
    }

    @SuppressWarnings("unchecked")
    public void visible(WhFluentWait waiter) {
        waiter.until(errorMessage, requireType.buildCondition(ConditionType.VISIBLE));
    }
}
