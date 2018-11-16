package at.willhaben.willtest.util;

public class RemoteSelectionUtils {

    private static final String DEFAULT_REMOTE = "";
    private static final boolean REMOTE = true;

    public static Boolean getRemoteIsSet() {
        return Boolean.valueOf(Environment.getValue("remote", DEFAULT_REMOTE));
    }

    public static boolean isRemote() {
        return getRemoteIsSet().equals(REMOTE);
    }
}
