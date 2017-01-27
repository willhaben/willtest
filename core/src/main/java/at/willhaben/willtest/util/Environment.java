package at.willhaben.willtest.util;

/**
 * Created by weisgrmi on 02.09.2016.
 */
public final class Environment {
    private Environment() {
        throw new UnsupportedOperationException();
    }

    public static String getValue(String key, String defaultValue) {
        String result = System.getProperty(key);

        if (result == null) {
            result = System.getenv(key);
        }

        if (result == null) {
            result = defaultValue;
        }

        return result;
    }
}
