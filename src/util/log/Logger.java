package util.log;

public final class Logger {

    private static boolean active = false;

    private Logger() {}

    public static void setActive(boolean active) {
        Logger.active = active;
    }

    public static void log(String s) {
        if (!active)
            return;
        System.out.println(s);
    }

    public static void logf(String s, Object... args) {
        if (!active)
            return;
        log(String.format(s, args));
    }
}
