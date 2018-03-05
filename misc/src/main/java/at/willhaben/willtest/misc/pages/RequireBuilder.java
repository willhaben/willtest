package at.willhaben.willtest.misc.pages;

import at.willhaben.willtest.misc.utils.ConditionType;

public class RequireBuilder {

    private final PageObject pageObject;
    private final RequireType requireType;

    RequireBuilder(PageObject pageObject, RequireType requireType) {
        this.pageObject = pageObject;
        this.requireType = requireType;
    }

    @SuppressWarnings("unchecked")
    public void clickable(long timeout) {
        pageObject.getWait(timeout).until(requireType.buildCondition(ConditionType.CLICKABLE));
    }

    @SuppressWarnings("unchecked")
    public void visible(long timeout) {
        pageObject.getWait(timeout).until(requireType.buildCondition(ConditionType.VISIBLE));
    }

    public void clickable() {
        clickable(PageObject.DEFAULT_WAIT_TIMEOUT);
    }

    public void visible() {
        visible(PageObject.DEFAULT_WAIT_TIMEOUT);
    }
}
