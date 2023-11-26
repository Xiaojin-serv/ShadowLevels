package top.shadowpixel.shadowlevels.data;

import lombok.var;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import top.shadowpixel.shadowcore.api.command_v2.CommandContext;
import top.shadowpixel.shadowcore.api.command_v2.component.CommandArgument;
import top.shadowpixel.shadowcore.api.uid.UUIDStorage;
import top.shadowpixel.shadowlevels.ShadowLevels;
import top.shadowpixel.shadowlevels.level.LevelData;
import top.shadowpixel.shadowlevels.object.enums.ModificationType;
import top.shadowpixel.shadowlevels.util.LocaleUtils;
import top.shadowpixel.shadowlevels.util.Utils;
import top.shadowpixel.shadowmessenger.ShadowMessenger;
import top.shadowpixel.shadowmessenger.common.TaskResult;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

@SuppressWarnings ({"unused", "BooleanMethodIsAlwaysInverted"})
public class DataHandler {
    private static final ShadowLevels plugin = ShadowLevels.getInstance();

    @NotNull
    public static ModificationStatus modifyLevels(@NotNull String playerName, @NotNull String levelName, @NotNull ModificationType type, @NotNull String amount, @NotNull CommandContext... ctx) {
        if (!isBungeeMode()) {
            return OfflineHandler.modifyLevelsOffline(playerName, levelName, type, amount);
        }

        ShadowMessenger.query("ShadowLevels " + type.getName() + "Level " + playerName + " " + levelName + " " + amount,
                result -> onResult(result, playerName, sender -> LocaleUtils.sendCmdMessage(sender, "Success." + type.getName() + "-Levels",
                        "%level-system%", levelName,
                        "%amount%", amount,
                        "%player%", playerName), () -> OfflineHandler.modifyLevelsOffline(playerName, levelName, type, amount), ctx));

        return ModificationStatus.PROXY_MODE;
    }

    @NotNull
    public static ModificationStatus modifyExps(@NotNull String playerName, @NotNull String levelName, @NotNull ModificationType type, @NotNull String amount, @NotNull CommandContext... ctx) {
        if (!isBungeeMode()) {
            return OfflineHandler.modifyExpsOffline(playerName, levelName, type, amount);
        }

        ShadowMessenger.query("ShadowLevels " + type.getName() + "Exps " + playerName + " " + levelName + " " + amount,
                result -> onResult(result, playerName, sender -> LocaleUtils.sendCmdMessage(sender, "Success." + type.getName() + "-Exps",
                        "%level-system%", levelName,
                        "%amount%", amount,
                        "%player%", playerName), () -> OfflineHandler.modifyExpsOffline(playerName, levelName, type, amount), ctx));
        return ModificationStatus.PROXY_MODE;
    }

    @NotNull
    public static ModificationStatus modifyMultiple(@NotNull String playerName, @NotNull String levelName, float amount, @NotNull CommandContext... ctx) {
        if (!isBungeeMode()) {
            return OfflineHandler.modifyMultipleOffline(playerName, levelName, amount);
        }

        ShadowMessenger.query("ShadowLevels SetMultiple " + playerName + " " + levelName + " " + amount,
                result -> onResult(result, playerName, sender -> LocaleUtils.sendCmdMessage(sender, "Success.Set-Multiple",
                            "%level-system%", levelName,
                            "%amount%", String.valueOf(amount),
                            "%multiple%", String.valueOf(amount),
                            "%player%", playerName), () -> OfflineHandler.modifyMultipleOffline(playerName, levelName, amount), ctx));
        return ModificationStatus.PROXY_MODE;
    }


    @NotNull
    public static ModificationStatus reset(@NotNull String playerName, @NotNull String levelName, @NotNull CommandContext... ctx) {
        if (levelName.equals("*")) return resetAll(playerName, ctx);

        if (!isBungeeMode()) {
            return OfflineHandler.resetOffline(playerName, levelName);
        }

        ShadowMessenger.query("ShadowLevels Reset " + playerName + " " + levelName + " 0",
                result -> onResult(result, playerName, sender -> LocaleUtils.sendCmdMessage(sender, "Success.Reset",
                            "%level-system%", levelName,
                            "%player%", playerName), () -> OfflineHandler.resetOffline(playerName, levelName), ctx));
        return ModificationStatus.PROXY_MODE;
    }

    @NotNull
    public static ModificationStatus resetAll(@NotNull String playerName, @NotNull CommandContext... ctx) {
        if (!isBungeeMode()) {
            return OfflineHandler.resetAllOffline(playerName);
        }

        ShadowMessenger.query("ShadowLevels Reset " + playerName + " * 0",
                result -> onResult(result, playerName, sender -> LocaleUtils.sendCmdMessage(sender, "Success.Reset",
                            "%level-system%", "*",
                            "%player%", playerName), () -> OfflineHandler.resetAllOffline(playerName), ctx));
        return ModificationStatus.PROXY_MODE;
    }

    public static boolean isBungeeMode() {
        return plugin.getConfiguration().getBoolean("Data.Bungee-Mode") && plugin.getServer().getPluginManager().isPluginEnabled("ShadowMessenger");
    }

    public static void sendMessage(@NotNull Consumer<CommandSender> cons, @NotNull CommandContext... ctx) {
        if (ctx != null && ctx.length > 0) {
            cons.accept(ctx[0].sender());
        }
    }

    public static void onResult(@NotNull TaskResult result, @NotNull String playerName, @NotNull Consumer<CommandSender> success, Supplier<ModificationStatus> supplier, @NotNull CommandContext... ctx) {
        if (result.getReturnValue().equalsIgnoreCase("ok")) {
            sendMessage(success, ctx);
        } else {
            var status = supplier.get();
            if (status.equals(ModificationStatus.PLAYER_NOT_FOUND)) {
                sendMessage(sender -> LocaleUtils.sendCmdMessage(sender, "Errors.Player-Not-Found",
                        "%player%", playerName,
                        "%name%", playerName), ctx);
            } else {
                sendMessage(success.andThen(sender -> LocaleUtils.sendMessage(sender, "Messages.Data.Offline")), ctx);
            }
        }
    }

    public static class OfflineHandler {

        @NotNull
        public static ModificationStatus resetOffline(@NotNull String playerName, @NotNull String levelName) {
            return levelName.equals("*") ? resetAllOffline(playerName) : handleLevelOffline(playerName, levelName, ld -> {
                ld.resetSilently();
                return ModificationStatus.SUCCESS;
            });
        }

        @NotNull
        public static ModificationStatus resetAllOffline(@NotNull String playerName) {
            return handleDataOffline(playerName, data -> {
                data.getLevels().clear();
                return ModificationStatus.SUCCESS;
            });
        }

        @NotNull
        public static ModificationStatus modifyLevelsOffline(@NotNull String playerName, @NotNull String levelName, @NotNull ModificationType type, @NotNull String amount) {
            return handleLevelOffline(playerName, levelName, data -> {
                var modAmount = Utils.calcValue(data.getLevels(), new CommandArgument(0, amount));
                switch (type) {
                    case ADD:
                        data.addLevelsSilently((int) modAmount);
                        break;
                    case REMOVE:
                        data.removeLevelsSilently((int) modAmount);
                        break;
                    case SET:
                        data.setLevelsSilently((int) modAmount);
                        break;
                }

                return ModificationStatus.SUCCESS;
            });
        }

        @NotNull
        public static ModificationStatus modifyExpsOffline(@NotNull String playerName, @NotNull String levelName, @NotNull ModificationType type, @NotNull String amount) {
            return handleLevelOffline(playerName, levelName, data -> {
                var modAmount = Utils.calcValue(data.getExps(), new CommandArgument(0, amount));
                switch (type) {
                    case ADD:
                        data.addExpsSilently((int) modAmount);
                        break;
                    case REMOVE:
                        data.removeExpsSilently((int) modAmount);
                        break;
                    case SET:
                        data.setExpsSilently((int) modAmount);
                        break;
                }

                return ModificationStatus.SUCCESS;
            });
        }

        @NotNull
        public static ModificationStatus modifyMultipleOffline(@NotNull String playerName, @NotNull String levelName, float amount) {
            return handleLevelOffline(playerName, levelName, levelData -> {
                levelData.setMultipleSilently(amount);
                return ModificationStatus.SUCCESS;
            });
        }

        @NotNull
        public static ModificationStatus handleLevelOffline(@NotNull String playerName, @NotNull String level, @NotNull Function<LevelData, ModificationStatus> function) {
            return handleDataOffline(playerName, data -> {
                var ld = data.getLevelData(level);
                if (ld == null) {
                    return ModificationStatus.ILLEGAL_ARGUMENT;
                }

                return function.apply(ld);
            });
        }

        @NotNull
        public static ModificationStatus handleDataOffline(@NotNull String playerName, @NotNull Function<PlayerData, ModificationStatus> consumer) {
            var uuid = UUIDStorage.getUniqueID(playerName);
            if (uuid == null) {
                return ModificationStatus.PLAYER_NOT_FOUND;
            }

            var dataManager = DataManager.getInstance();
            var data = dataManager.dataModifier.load(uuid);
            if (data == null) {
                return ModificationStatus.PLAYER_NOT_FOUND;
            }

            var status = ModificationStatus.SUCCESS;
            data = dataManager.completeData(data);
            status = consumer.apply(data);
            dataManager.dataModifier.save(data);
            return status;
        }
    }
}