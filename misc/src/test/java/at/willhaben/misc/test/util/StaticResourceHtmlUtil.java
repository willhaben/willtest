package at.willhaben.misc.test.util;

public class StaticResourceHtmlUtil {

    public static String getAbsolutePath(String fileName) {
        return "file://" + StaticResourceHtmlUtil.class.getClassLoader().getResource(fileName + ".html").getFile();
    }
}
