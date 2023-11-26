package top.shadowpixel.shadowlevels.util;

import lombok.var;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import top.shadowpixel.shadowcore.api.command_v2.component.CommandArgument;
import top.shadowpixel.shadowcore.api.exception.command.ParameterizedCommandInterruptedException;
import top.shadowpixel.shadowlevels.data.ModificationStatus;

@SuppressWarnings ("unused")
public class Utils {
    public Utils() {
        throw new UnsupportedOperationException();
    }

    public static void callEvent(Event event) {
        Bukkit.getPluginManager().callEvent(event);
    }

    public static double calcValue(double base, @NotNull CommandArgument value) {
        double amount;
        if (value.getValue().endsWith("%")) {
            var percentage = value.getAsPercentage();
            amount = base * percentage;
        } else {
            amount = value.getDouble();
        }

        return amount;
    }

    public static boolean isValidNumber(@NotNull String num) {
        if (num.endsWith("%")) {
            num = num.substring(0, num.length() - 1);
        }

        try {
            Double.parseDouble(num);
            return true;
        } catch (Throwable t) {
            return false;
        }
    }

    public static boolean isValidNumber(@NotNull CommandArgument argument) {
        return isValidNumber(argument.getValue());
    }

    public static void showMessage(@NotNull CommandSender sender, @NotNull ModificationStatus status, @NotNull CommandArgument argument, @NotNull Runnable success) {
        switch (status) {
            case SUCCESS:
                success.run();
                break;
            case PROXY_MODE:
                LocaleUtils.sendMessage(sender, "Messages.Data.Through-Bungee");
                break;
            case PLAYER_NOT_FOUND:
                throw new ParameterizedCommandInterruptedException(argument, "player not found");
        }
    }
}
