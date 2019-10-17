package at.willhaben.willtest.util;

public class RemoteSelectionUtils {

    private static final String DEFAULT_REMOTE = "";
    private static final boolean REMOTE = true;
    public static final String IS_REMOTE = "remote";
    public static final String REMOTE_PLATFORM = "remote.platform";

    public static Boolean getRemoteIsSet() {
        return Boolean.valueOf(Environment.getValue(IS_REMOTE, DEFAULT_REMOTE));
    }

    public static boolean isRemote() {
        return getRemoteIsSet().equals(REMOTE);
    }

    public static RemotePlatform getRemotePlatform() {
        return RemotePlatform.valueOf(Environment.getValue(REMOTE_PLATFORM, RemotePlatform.GRID.toString()));
    }

    public enum RemotePlatform {
        GRID,
        BROWSERSTACK
    }
}
