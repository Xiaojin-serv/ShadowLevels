package top.shadowpixel.shadowlevels.object.hooks;

import lombok.var;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import top.shadowpixel.langswitcher.LangSwitcher;
import top.shadowpixel.shadowcore.api.locale.Locale;
import top.shadowpixel.shadowlevels.locale.LocaleManager;

public class LangSwitcherHook {

    private LangSwitcherHook() {
        throw new UnsupportedOperationException();
    }

    public static Locale getLocale(@NotNull CommandSender sender) {
        var lang = LocaleManager.getInstance().getDefaultLocale();
        if (sender instanceof Player && Bukkit.getPluginManager().isPluginEnabled("LangSwitcher")) {
            var loc = LocaleManager.getInstance().getLocale(LangSwitcher.getLang((Player) sender));
            if (loc != null) {
                lang = loc;
            }
        }

        return lang;
    }
}
