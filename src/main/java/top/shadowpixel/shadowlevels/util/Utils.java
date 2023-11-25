package top.shadowpixel.shadowlevels.util;

import lombok.var;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import top.shadowpixel.shadowcore.api.command_v2.component.CommandArgument;

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

        return StringUtils.isNumber(num);
    }

    public static boolean isValidNumber(@NotNull CommandArgument argument) {
        return isValidNumber(argument.getValue());
    }

    public static void showMessage(@NotNull CommandSender sender, @NotNull ModificationStatus status, @NotNull CommandArgument argument, @NotNull Consumer<CommandSender> success) {
        switch (status) {
            case ModificationStatus.SUCCESS:
                success.accept(sender);
                break;
            case ModificationStatus.PROXY_MODE:
                SenderUtils.sendMessage(sender, LocaleUtils.getMessage(sender, "Messages.Data.Through-Bungee"));
                break;
            case ModificationStatus.PLAYER_NOT_FOUND:
                throw new ParameterizedCommandInterruptedException(argument, "player not found");
                break;
        }
    }
}
