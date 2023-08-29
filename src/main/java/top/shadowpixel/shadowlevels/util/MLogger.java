package top.shadowpixel.shadowlevels.util;

import org.jetbrains.annotations.NotNull;
import top.shadowpixel.shadowcore.util.text.ReplaceUtils;
import top.shadowpixel.shadowlevels.ShadowLevels;

public class MLogger {

    private static final ShadowLevels plugin = ShadowLevels.getInstance();

    private MLogger() {
        throw new UnsupportedOperationException();
    }

    public static void info(String path) {
        Logger.info(plugin.getDefaultMessage().getString(path));
    }

    public static void infoReplaced(String path, String... replacements) {
        Logger.info(ReplaceUtils.coloredReplace(get(path), replacements));
    }

    public static void warn(String path) {
        Logger.warn(plugin.getDefaultMessage().getString(path));
    }

    public static void warn(String path, Throwable throwable) {
        Logger.warn(get(path), throwable);
    }

    public static void error(String path) {
        Logger.error(plugin.getDefaultMessage().getString(path));
    }

    public static void error(String path, Throwable throwable) {
        Logger.error(get(path), throwable);
    }

    public static @NotNull String get(String path) {
        return plugin.getDefaultMessage().getString(path);
    }
}
