package at.willhaben.misc.test;

public class StaticResourceHtmlUtil {

    public static String getAbsolutePath(String fileName) {
        return StaticResourceHtmlUtil.class.getClassLoader().getResource(fileName + ".html").getFile();
    }
}
