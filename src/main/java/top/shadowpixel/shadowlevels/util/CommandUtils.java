package top.shadowpixel.shadowlevels.util;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;
import top.shadowpixel.shadowcore.util.entity.SenderUtils;
import top.shadowpixel.shadowcore.util.text.StringUtils;
import top.shadowpixel.shadowlevels.ShadowLevels;
import top.shadowpixel.shadowlevels.level.Level;

public class CommandUtils {

    public static final ShadowLevels plugin = ShadowLevels.getInstance();

    private CommandUtils() {
        throw new UnsupportedOperationException();
    }

    public static boolean checkArgument(CommandSender sender, String[] arguments, int len) {
        if (arguments.length < len) {
            SenderUtils.sendMessage(sender, LocaleUtils.getCmdMessage(sender, "Errors.Params-Error"));
            return false;
        }

        return true;
    }

    @Nullable
    public static Player findPlayer(CommandSender sender, String name) {
        Player player;
        if ((player = Bukkit.getPlayer(name)) == null) {
            sendCommandMessage(sender, "Errors.Player-Not-Found", "%name%", name);
        }

        return player;
    }

    @Nullable
    public static Level findLevel(CommandSender sender, String name) {
        Level level;
        if ((level = plugin.getLevel(name)) == null) {
            sendCommandMessage(sender, "Errors.Level-Not-Found");
        }

        return level;
    }

    public static @Nullable Integer findInt(CommandSender sender, String integer, int index) {
        if (StringUtils.isInteger(integer)) {
            return Integer.valueOf(integer);
        }

        sendCommandMessage(sender, "Errors.Not-An-Integer", "%pos%", String.valueOf(index));
        return null;
    }

    public static @Nullable Double findDouble(CommandSender sender, String d, int index) {
        try {
            return Double.parseDouble(d);
        } catch (Throwable throwable) {
            sendCommandMessage(sender, "Errors.Not-An-Number", "%pos%", String.valueOf(index));
            return null;
        }
    }

    public static void sendCommandMessage(CommandSender sender, String path, String... replacements) {
        LocaleUtils.sendCmdMessage(sender, path, replacements);
    }
}
