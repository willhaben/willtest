package at.willhaben.willtest.junit5;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface BrowserUtil {
    Class<? extends BrowserUtilExtension>[] value() default {};
}
