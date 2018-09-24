package at.willhaben.willtest.misc.pages.find;

import org.openqa.selenium.support.pagefactory.DefaultFieldDecorator;
import org.openqa.selenium.support.pagefactory.ElementLocatorFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class CustomFieldDecorator extends DefaultFieldDecorator {

    private List<AbstractUiComponent> customFactories = new ArrayList<>();

    public CustomFieldDecorator(ElementLocatorFactory factory) {
        super(factory);

    }

    public CustomFieldDecorator addCustomUiComponent(AbstractUiComponent customFactory) {
        this.customFactories.add(customFactory);
        return this;
    }

    @Override
    public Object decorate(ClassLoader loader, Field field) {
        Object element = super.decorate(loader, field);
        if(Objects.isNull(element)) {

            Optional<AbstractUiComponent> suitableFactory = getSuitableFactory(field);

            if(suitableFactory.isPresent()) {
                AbstractUiComponent customFactory = suitableFactory.get();
                if(customFactory.isCompatibleList(field)) {
                    WhListInvocationHandler handler = new WhListInvocationHandler(factory.createLocator(field), customFactory);
                    return Proxy.newProxyInstance(loader, new Class[]{List.class}, handler);
                }
                WhInvocationHandler handler = new WhInvocationHandler(factory.createLocator(field), customFactory);
                return Proxy.newProxyInstance(loader, (Class<?>[]) customFactory.getFieldInterfacedTypes().toArray(), handler);
            }
        }
        return element;
    }

    private Optional<AbstractUiComponent> getSuitableFactory(Field field) {
        return customFactories.stream()
                .filter(factory -> factory.isFieldCompatible(field))
                .findFirst();
    }
}
