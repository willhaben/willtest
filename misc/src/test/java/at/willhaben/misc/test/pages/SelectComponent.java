package at.willhaben.misc.test.pages;

import at.willhaben.willtest.misc.pages.find.AbstractUiComponent;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ISelect;
import org.openqa.selenium.support.ui.Select;

import java.util.Arrays;
import java.util.List;

public class SelectComponent extends AbstractUiComponent<ISelect> {

    @Override
    public Class<ISelect> getType() {
        return ISelect.class;
    }

    @Override
    public List<Class> getFieldInterfacedTypes() {
        return Arrays.asList(ISelect.class);
    }

    @Override
    public Select generateElement(WebElement element) {
        return new Select(element);
    }


}
