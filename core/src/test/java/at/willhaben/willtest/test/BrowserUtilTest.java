package at.willhaben.willtest.test;

import at.willhaben.willtest.junit5.BrowserUtil;
import at.willhaben.willtest.util.AnnotationHelper;
import org.hamcrest.Matchers;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;

public class BrowserUtilTest {

    @Test
    public void testBrowserUtilOnSuperClass() {
        BrowserUtil browserUtil = AnnotationHelper.getFirstSuperClassAnnotation(Testing.class, BrowserUtil.class);
        assertThat(browserUtil, Matchers.notNullValue());
    }

    @Test
    public void testNoBrowserUtil() {
        BrowserUtil browserUtil = AnnotationHelper.getFirstSuperClassAnnotation(NoAnnotation.class, BrowserUtil.class);
        assertThat(browserUtil, Matchers.nullValue());
        browserUtil = AnnotationHelper.getFirstSuperClassAnnotation(NoAnnotationSuperClass.class, BrowserUtil.class);
        assertThat(browserUtil, Matchers.nullValue());
    }

    private class Testing extends ValidSuperClass {}

    @BrowserUtil()
    private class ValidSuperClass {}

    private class NoAnnotation {}

    private class NoAnnotationSuperClass extends NoAnnotation {}
}
