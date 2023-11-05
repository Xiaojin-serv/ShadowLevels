package top.shadowpixel.shadowlevels.data;

import lombok.var;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.shadowpixel.shadowcore.api.uid.UUIDStorage;
import top.shadowpixel.shadowcore.util.entity.SenderUtils;
import top.shadowpixel.shadowlevels.ShadowLevels;
import top.shadowpixel.shadowlevels.level.LevelData;
import top.shadowpixel.shadowlevels.object.enums.ModificationType;
import top.shadowpixel.shadowlevels.util.LocaleUtils;
import top.shadowpixel.shadowmessenger.ShadowMessenger;

import java.util.function.Consumer;

/**
 * Modify players' data who aren't in the server(offline or in other servers)
 */
public class DataHandler {

    private static ShadowLevels plugin;

    public static void initialize(@NotNull ShadowLevels pl) {
        plugin = pl;
    }

    public static void modifyLevels(@Nullable CommandSender sender, @NotNull String name, @NotNull String level, @NotNull ModificationType type, int amount) {
        if (!isBungeeMode()) {
            OfflineHandler.modifyLevelsOffline(sender, name, level, type, amount);
            return;
        }

        ShadowMessenger.query("ShadowLevels " + type.getName() + "Level " + name + " " + level + " " + amount,
                result -> {
                    if (result.getReturnValue().equalsIgnoreCase("ok")) {
                        if (sender != null) {
                            LocaleUtils.sendCmdMessage(sender, "Success." + type.getName() + "-Levels",
                                    "%level-system%", level,
                                    "%amount%", String.valueOf(amount),
                                    "%player%", name);
                            showMessage(sender, "through-bungee", "%name%", name);
                        }
                    } else {
                        OfflineHandler.modifyLevelsOffline(sender, name, level, type, amount);
                    }
                });
    }

    @SuppressWarnings("unused")
    public static void modifyExps(@NotNull String name, @NotNull String level, @NotNull ModificationType type, double amount) {
        modifyExps(null, name, level, type, amount);
    }

    public static void modifyExps(@Nullable CommandSender sender, @NotNull String name, @NotNull String level, @NotNull ModificationType type, double amount) {
        if (!isBungeeMode()) {
            OfflineHandler.modifyExpsOffline(sender, name, level, type, amount);
            return;
        }

        ShadowMessenger.query("ShadowLevels " + type.getName() + "Exps " + name + " " + level + " " + amount,
                result -> {
                    if (result.getReturnValue().equalsIgnoreCase("OK")) {
                        if (sender != null) {
                            LocaleUtils.sendCmdMessage(sender, "Success." + type.getName() + "-Exps",
                                    "%level-system%", level,
                                    "%amount%", String.valueOf(amount),
                                    "%player%", name);
                            showMessage(sender, "through-bungee");
                        }
                    } else {
                        OfflineHandler.modifyExpsOffline(sender, name, level, type, amount);
                    }
                });
    }

    @SuppressWarnings("unused")
    public static void modifyMultiple(@NotNull String name, @NotNull String level, float amount) {
        modifyMultiple(null, name, level, amount);
    }

    public static void modifyMultiple(@Nullable CommandSender sender, @NotNull String name, @NotNull String level, float amount) {
        if (!isBungeeMode()) {
            OfflineHandler.modifyMultipleOffline(sender, name, level, amount);
            return;
        }

        ShadowMessenger.query("ShadowLevels SetMultiple " + name + " " + level + " " + amount,
                result -> {
                    if (result.getReturnValue().equalsIgnoreCase("OK")) {
                        if (sender != null) {
                            LocaleUtils.sendCmdMessage(sender, "Success.Set-Multiple",
                                    "%level-system%", level,
                                    "%multiple%", String.valueOf(amount),
                                    "%player%", name);
                        }
                    } else {
                        OfflineHandler.modifyMultipleOffline(sender, name, level, amount);
                    }
                });

        showMessage(sender, "through-bungee");
    }

    public static void reset(@NotNull String name, @NotNull String level) {
        reset(null, name, level);
    }

    public static void reset(@Nullable CommandSender sender, @NotNull String name, @NotNull String level) {
        if (!isBungeeMode()) {
            OfflineHandler.resetOffline(sender, name, level);
            return;
        }

        ShadowMessenger.query("ShadowLevels Reset " + name + " " + level + " 0",
                result -> {
                    if (result.getReturnValue().equalsIgnoreCase("OK")) {
                        if (sender != null) {
                            SenderUtils.sendMessage(sender, LocaleUtils.getCmdMessage(sender, "Success.Reset",
                                    "%level-system%", level,
                                    "%player%", name));
                        }
                    } else {
                        OfflineHandler.resetOffline(sender, name, level);
                    }
                });

        showMessage(sender, "through-bungee");
    }

    private static void showMessage(@Nullable CommandSender sender, @NotNull String type, @Nullable String... replacement) {
        if (sender != null) {
            switch (type.toLowerCase()) {
                case "through-bungee":
                    SenderUtils.sendMessage(sender, LocaleUtils.getMessage(sender, "Messages.Data.Through-Bungee"));
                    break;
                case "player-not-found":
                    SenderUtils.sendMessage(sender, LocaleUtils.getCmdMessage(sender, "Errors." + "Player-Not-Found"), replacement);
                    break;
                case "offline":
                    SenderUtils.sendMessage(sender, LocaleUtils.getMessage(sender, "Messages.Data.Offline"));
                    break;
            }
        }
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public static boolean isBungeeMode() {
        return plugin.getConfiguration().getBoolean("Data.Bungee-Mode") && plugin.getServer().getPluginManager().isPluginEnabled("ShadowMessenger");
    }

    private static class OfflineHandler {

        private static void resetOffline(@Nullable CommandSender sender, String name, String level) {
            var modified = level.equals("*") ? resetAllOffline(sender, name) : handleLevelOffline(sender, name, level, LevelData::resetSilently);
            if (sender != null && modified) {
                LocaleUtils.sendCmdMessage(sender, "Success.Reset",
                        "%level-system%", level,
                        "%player%", name);
                showMessage(sender, "offline");
            }
        }

        private static boolean resetAllOffline(@Nullable CommandSender sender, String name) {
            return handleDataOffline(sender, name, data -> data.getLevels().values().forEach(LevelData::resetSilently));
        }

        private static void modifyLevelsOffline(@Nullable CommandSender sender, String name, String level, ModificationType type, int amount) {
            var modified = false;
            switch (type) {
                case ADD:
                    modified = handleLevelOffline(sender, name, level, data -> data.addLevelsSilently(amount));
                    break;
                case REMOVE:
                    modified = handleLevelOffline(sender, name, level, data -> data.removeLevelsSilently(amount));
                    break;
                case SET:
                    modified = handleLevelOffline(sender, name, level, data -> data.setLevelsSilently(amount));
                    break;
            }

            if (!modified) return;
            if (sender != null) {
                LocaleUtils.sendCmdMessage(sender, "Success." + type.getName() + "-Levels",
                        "%level-system%", level,
                        "%amount%", String.valueOf(amount),
                        "%player%", name);
                showMessage(sender, "offline");
            }
        }

        private static void modifyExpsOffline(@Nullable CommandSender sender, String name, String level, ModificationType type, double amount) {
            var modified = false;
            switch (type) {
                case ADD:
                    modified = handleLevelOffline(sender, name, level, data -> data.addExpsSilently(amount));
                    break;
                case REMOVE:
                    modified = handleLevelOffline(sender, name, level, data -> data.removeExpsSilently(amount));
                    break;
                case SET:
                    modified = handleLevelOffline(sender, name, level, data -> data.setExpsSilently(amount));
                    break;
            }

            if (!modified) return;
            if (sender != null) {
                LocaleUtils.sendCmdMessage(sender, "Success." + type.getName() + "-Exps",
                        "%level-system%", level,
                        "%amount%", String.valueOf(amount),
                        "%player%", name);
                showMessage(sender, "offline");
            }
        }

        private static void modifyMultipleOffline(@Nullable CommandSender sender, String name, String level, float amount) {
            var modified = handleLevelOffline(sender, name, level, data -> data.setMultipleSilently(amount));
            if (sender != null && modified) {
                LocaleUtils.sendCmdMessage(sender, "Success.Set-Multiple",
                        "%level-system%", level,
                        "%multiple%", String.valueOf(amount),
                        "%player%", name);
                showMessage(sender, "offline");
            }
        }

        private static boolean handleLevelOffline(@Nullable CommandSender sender, String name, String level, Consumer<LevelData> consumer) {
            return handleDataOffline(sender, name, data -> consumer.accept(data.getLevelData(level)));
        }

        private static boolean handleDataOffline(@Nullable CommandSender sender, String name, @NotNull Consumer<PlayerData> consumer) {
            var uuid = UUIDStorage.getUniqueID(name);
            if (uuid == null) {
                showMessage(sender, "player-not-found", "%name%", name);
                return false;
            }

            var dataManager = plugin.getDataManager();
            var data = dataManager.dataModifier.load(uuid);
            if (data == null) {
                showMessage(sender, "player-not-found", "%name%", name);
                return false;
            }

            data = dataManager.completeData(data);
            consumer.accept(data);
            dataManager.dataModifier.save(data);
            return true;
        }
    }
}
