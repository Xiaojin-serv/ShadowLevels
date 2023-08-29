package top.shadowpixel.shadowlevels.command;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.shadowpixel.shadowcore.api.command.subcommand.SubCommand;
import top.shadowpixel.shadowcore.util.collection.ListUtils;
import top.shadowpixel.shadowcore.util.plugin.PluginUtils;
import top.shadowpixel.shadowlevels.bungee.BungeeManager;
import top.shadowpixel.shadowlevels.config.ConfigManager;
import top.shadowpixel.shadowlevels.data.DataManager;
import top.shadowpixel.shadowlevels.level.LevelManager;
import top.shadowpixel.shadowlevels.locale.LocaleManager;
import top.shadowpixel.shadowlevels.reward.RewardManager;

import java.util.Collections;
import java.util.List;

import static top.shadowpixel.shadowlevels.util.CommandUtils.plugin;
import static top.shadowpixel.shadowlevels.util.CommandUtils.sendCommandMessage;

public class ReloadCommand extends SubCommand {

    public ReloadCommand() {
        super(ListUtils.immutableList("Reload", "rl"),
                "ShadowLevels.Commands.Reload");
    }

    @Override
    public void execute(@NotNull CommandSender sender, @NotNull String label, String[] arguments) {
        if (arguments.length > 0) {
            switch (arguments[0].toLowerCase()) {
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

        sendCommandMessage(sender, "Success.Reload");
    }

    @Override
    public @Nullable List<String> complete(@NotNull CommandSender sender, @NotNull String label, String[] arguments) {
        if (arguments.length == 1) {
            return ListUtils.asList("Config", "Locale", "Levels", "Rewards");
        }

        return Collections.emptyList();
    }
}
