package at.willhaben.willtest.util;

import at.willhaben.willtest.junit5.BrowserUtil;
import at.willhaben.willtest.junit5.BrowserUtilExtension;
import at.willhaben.willtest.junit5.OptionModifier;
import org.junit.jupiter.api.extension.ExtensionContext;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class AnnotationHelper {

    @SuppressWarnings("unchecked")
    public static <T> T getFirstSuperClassAnnotation(Class testClass, Class<T> type) {
        testClass = testClass.getSuperclass();
        if (testClass.isAssignableFrom(Object.class)) {
            return null;
        }
        T browserUtil = (T) testClass.getAnnotation(type);
        if (browserUtil != null) {
            return browserUtil;
        } else {
            return getFirstSuperClassAnnotation(testClass, type);
        }
    }

    public static <T extends BrowserUtilExtension> List<T> getBrowserUtilList(BrowserUtil browserUtilAnnotation, Class<T> type) {
        return getBrowserUtilList(browserUtilAnnotation, type, true);
    }

    @SuppressWarnings("unchecked")
    public static <T extends BrowserUtilExtension> List<T> getBrowserUtilList(BrowserUtil browserUtilAnnotation, Class<T> type, boolean allowMultipleExtension) {
        if (browserUtilAnnotation != null) {
            List<T> extensions = Arrays.stream(browserUtilAnnotation.value())
                    .filter(type::isAssignableFrom)
                    .map(extension -> {
                        try {
                            return (T) extension.getConstructor().newInstance();
                        } catch (Exception e) {
                            throw new RuntimeException("Can't instantiate " + extension.getName() + ".", e);
                        }
                    })
                    .collect(Collectors.toList());
            if (!allowMultipleExtension) {
                if (extensions.size() > 1) {
                    //TODO:Change that we can have two OptionModifier classes
                    throw new IllegalStateException("Only one extension of type " + OptionModifier.class.getName() + ".");
                }
            }
            return extensions;
        } else {
            return Collections.emptyList();
        }
    }

    public static <T extends Annotation> List<T> getAnnotationOrdered(ExtensionContext context, Class<T> type) {
        Class<?> testClass = context.getRequiredTestClass();
        T superClassAnnotation = getFirstSuperClassAnnotation(testClass, type);
        T testClassAnnotation = testClass.getAnnotation(type);
        T methodAnnotation = context.getTestMethod()
                .map(method -> method.getAnnotation(type))
                .orElse(null);
        List<T> annotationList = new ArrayList<>();
        if (superClassAnnotation != null) {
            annotationList.add(superClassAnnotation);
        }
        if (testClassAnnotation != null) {
            annotationList.add(testClassAnnotation);
        }
        if (methodAnnotation != null) {
            annotationList.add(methodAnnotation);
        }
        return annotationList;
    }

    public static <T extends BrowserUtilExtension> List<T> getBrowserUtilExtensionList(ExtensionContext context, Class<T> utilType, boolean allowMultipleExtension) {
        return getAnnotationOrdered(context, BrowserUtil.class).stream()
                .flatMap(anno -> getBrowserUtilList(anno, utilType, allowMultipleExtension).stream())
                .collect(Collectors.toList());
    }
}
