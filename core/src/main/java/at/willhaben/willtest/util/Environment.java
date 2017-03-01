package at.willhaben.willtest.util;

/**
 * Used to get a system property or environment variable.
 */
public final class Environment {
    private Environment() {
    }

    /**
     * Checks if the specified key is available in the system properties {@link System#getProperty(String)},
     * environment variables {@link System#getenv(String)} or takes the default value. In this sequence.
     *
     * @param key          Key or name of the property
     * @param defaultValue Value taken if nothing is available
     * @return The value of the system property, variable or the default value
     */
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
