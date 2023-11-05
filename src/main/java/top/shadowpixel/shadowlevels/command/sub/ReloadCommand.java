package top.shadowpixel.shadowlevels.command.sub;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.shadowpixel.shadowcore.api.command_v2.CommandContext;
import top.shadowpixel.shadowcore.api.command_v2.SubCommand;
import top.shadowpixel.shadowcore.util.collection.ListUtils;
import top.shadowpixel.shadowcore.util.plugin.PluginUtils;
import top.shadowpixel.shadowlevels.bungee.BungeeManager;
import top.shadowpixel.shadowlevels.config.ConfigManager;
import top.shadowpixel.shadowlevels.data.DataManager;
import top.shadowpixel.shadowlevels.level.LevelManager;
import top.shadowpixel.shadowlevels.locale.LocaleManager;
import top.shadowpixel.shadowlevels.reward.RewardManager;
import top.shadowpixel.shadowlevels.util.LocaleUtils;

import java.util.Collection;

import static top.shadowpixel.shadowlevels.util.CommandUtils.plugin;

public class ReloadCommand extends SubCommand {
    public ReloadCommand() {
        super("Reload", "rl");
        permissions.add("ShadowLevels.Commands.Reload");
    }

    @Override
    public boolean execute(@NotNull CommandContext ctx) {
        if (ctx.arguments().length > 1) {
            switch (ctx.arguments()[1].getValue().toLowerCase()) {
                case "config":
                    ConfigManager.getInstance().reload();
                    break;
                case "locale":
                    LocaleManager.getInstance().reload();
                    break;
                case "level":
                case "levels":
                    LevelManager.getInstance().reload();
                    break;
                case "reward":
                case "rewards":
                    RewardManager.getInstance().reload();
                    break;
                case "bungee":
                    BungeeManager.getInstance().reload();
                    break;
                case "data":
                case "datum":
                    DataManager.getInstance().reload();
                    break;
            }
        } else {
            if (plugin.getConfiguration().getBoolean("Data.Save-When-Reloading")) {
                DataManager.getInstance().saveAll();
            }

            PluginUtils.reloadManager(ConfigManager.getInstance(),
                    LocaleManager.getInstance(),
                    LevelManager.getInstance(),
                    RewardManager.getInstance(),
                    BungeeManager.getInstance(),
                    DataManager.getInstance());
        }

        LocaleUtils.sendCmdMessage(ctx.sender(), "Success.Reload");
        return true;
    }

    @Override
    public @Nullable Collection<String> complete(@NotNull CommandContext ctx) {
        if (ctx.arguments().length == 2) {
            return ListUtils.asList("Config", "Locale", "Levels", "Rewards");
        }

        return null;
    }
}
