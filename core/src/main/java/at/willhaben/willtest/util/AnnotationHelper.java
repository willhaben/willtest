package at.willhaben.willtest.util;

public class AnnotationHelper {

    @SuppressWarnings("unchecked")
    public static <T> T getFirstSuperClassAnnotation(Class testClass, Class<T> type) {
        if (testClass.isAssignableFrom(Object.class)) {
            return null;
        }
        T browserUtil = (T) testClass.getAnnotation(type);
        if (browserUtil != null) {
            return browserUtil;
        } else {
            return getFirstSuperClassAnnotation(testClass.getSuperclass(), type);
        }
    }
}
