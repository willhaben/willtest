package at.willhaben.willtest.misc.pages.find;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

public abstract class AbstractUiComponent<T> implements WebElementTransformer<T> {

    public abstract Class<T> getType();

    public boolean isFieldCompatible(Field field) {
        return getType().isAssignableFrom(field.getType()) || isCompatibleList(field);
    }

    public boolean isCompatibleList(Field field) {
        if(!List.class.isAssignableFrom(field.getType())) {
            return false;
        }

        Type genericType = field.getGenericType();
        if (!(genericType instanceof ParameterizedType)) {
            return false;
        }

        Type listType = ((ParameterizedType) genericType).getActualTypeArguments()[0];

        if (!getType().equals(listType)) {
            return false;
        }
        return true;
    }

    public abstract List<Class> getFieldInterfacedTypes();
}
