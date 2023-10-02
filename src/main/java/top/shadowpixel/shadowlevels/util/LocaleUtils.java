package top.shadowpixel.shadowlevels.util;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import top.shadowpixel.shadowcore.api.config.Configuration;
import top.shadowpixel.shadowcore.api.locale.Locale;
import top.shadowpixel.shadowcore.util.text.ReplaceUtils;
import top.shadowpixel.shadowlevels.ShadowLevels;
import top.shadowpixel.shadowlevels.object.hooks.LangSwitcherHook;

import static top.shadowpixel.shadowlevels.ShadowLevels.getInstance;

public class LocaleUtils {

    private LocaleUtils() {
        throw new UnsupportedOperationException();
    }

    public static Locale getLocale(@NotNull CommandSender sender) {
        return LangSwitcherHook.getLocale(sender);
    }

    public static Configuration getMessage(@NotNull CommandSender sender) {
        return getLocale(sender).getConfig("Messages");
    }

    public static String getMessage(@NotNull String path, String... replacements) {
        return ReplaceUtils.coloredReplace(getInstance().getDefaultMessage().getString(path), replacements);
    }

    public static String getMessage(@NotNull CommandSender sender, @NotNull String path, String... replacements) {
        return ReplaceUtils.coloredReplace(getMessage(sender).getString(path, "Incorrect path: " + path).replaceAll("\\{prefix}|%prefix%", ShadowLevels.getPrefix()), sender, replacements);
    }

    public static String getCmdMessage(@NotNull CommandSender sender, @NotNull String path, String... replacements) {
        return getMessage(sender, "Messages.Command-Messages." + path, replacements);
    }

    public static void sendCmdMessage(@NotNull CommandSender sender, @NotNull String path, String... replacements) {
        sender.sendMessage(getCmdMessage(sender, path, replacements));
    }
}
