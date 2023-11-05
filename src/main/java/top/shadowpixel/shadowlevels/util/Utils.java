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
}
