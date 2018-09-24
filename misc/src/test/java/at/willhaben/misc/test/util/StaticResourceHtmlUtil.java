package at.willhaben.misc.test.util;

public class StaticResourceHtmlUtil {

    public static String resourceHtmlFilePath(String fileName) {
        return "file://" + StaticResourceHtmlUtil.class.getClassLoader().getResource(fileName + ".html").getFile();
    }
}
