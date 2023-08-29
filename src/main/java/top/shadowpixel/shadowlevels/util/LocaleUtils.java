package top.shadowpixel.shadowlevels.util;

import org.bukkit.command.CommandSender;
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

    public static Locale getLocale(CommandSender sender) {
        return LangSwitcherHook.getLocale(sender);
    }

    public static Configuration getMessage(CommandSender sender) {
        return getLocale(sender).getConfig("Messages");
    }

    public static String getMessage(String path, String... replacements) {
        return ReplaceUtils.coloredReplace(getInstance().getDefaultMessage().getString(path), replacements);
    }

    public static String getMessage(CommandSender sender, String path, String... replacements) {
        return ReplaceUtils.coloredReplace(getMessage(sender).getString(path, "Incorrect path: " + path).replaceAll("\\{prefix}|%prefix%", ShadowLevels.getPrefix()), sender, replacements);
    }

    public static String getCmdMessage(CommandSender sender, String path, String... replacements) {
        return getMessage(sender, "Messages.Command-Messages." + path, replacements);
    }
}
