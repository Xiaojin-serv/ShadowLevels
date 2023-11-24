package top.shadowpixel.shadowlevels.data;

import java.util.function.Consumer;
import java.util.function.Function;

import org.jetbrains.annotations.NotNull;

import lombok.var;
import top.shadowpixel.shadowlevels.level.LevelData;
import top.shadowpixel.shadowlevels.object.enums.ModificationType;
import top.shadowpixel.shadowlevels.util.Utils;

public class NewDataHandler {
    @NotNull
    public static ModificationStatus modifyLevels(@NotNull String playerName, @NotNull String levelName, @NotNull ModificationType type, @NotNull String amount) {
        if (!isBungeeMode()) {
            return OfflineHandler.modifyLevelsOffline(playerName, levelName, type, amount);
        }
    }

    @NotNull
    public static ModificationStatus modifyExps(@NotNull String playerName, @NotNull String levelName, @NotNull ModificationType type, int amount) {
        if (!isBungeeMode()) {
            return OfflineHandler.modifyExpsOffline(playerName, levelName, type, amount);
        }
    }

    public static boolean isBungeeMode() {
        return plugin.getConfiguration().getBoolean("Data.Bungee-Mode") && plugin.getServer().getPluginManager().isPluginEnabled("ShadowMessenger");
    }

    public static class OfflineHandler {
        @NotNull
        public static ModificationStatus resetOffline(@NotNull String playerName, @NotNull String levelName) {
            return levelName.equals("*") ? resetAllOffline(playerName) : handleLevelOffline(playerName, levelName, LevelData::resetSilently);
        }

        @NotNull
        public static ModificationStatus resetAllOffline(@NotNull String playerName) {
            var mod = handleDataOffline(playerName, data -> {
                data.getLevels().clear();
                return ModificationStatus.SUCCESS;
            });

            return mod;
        }

        @NotNull
        public static ModificationStatus modifyLevelsOffline(@NotNull String playerName, @NotNull String levelName, @NotNull ModificationType type, @NotNull String amount) {
            var modified = ModificationStatus.SUCCESS;
            handleLevelOffline(playerName, levelName, data -> {
                var modAmount = Utils.calcValue(data.getLevels(), amount);
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

            return modified;
        }

        @NotNull
        public static ModificationStatus modifyExpsOffline(@NotNull String playerName, @NotNull String levelName, @NotNull ModificationType type, @NotNull String amount) {
            var modified = ModificationStatus.SUCCESS;
            handleLevelOffline(playerName, levelName, data -> {
                var modAmount = Utils.calcValue(data.getLevels(), amount);
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

            return modified;
        }

        @NotNull
        public static ModificationStatus modifyMultipleOffline(@NotNull String playerName, @NotNull String levelName, float amount) {
            var mod = handleLevelOffline(playerName, levelName, levelData -> {
                levelData.setMultipleSilently(amount);
                return ModificationStatus.SUCCESS;
            })

            return mod;
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
            consumer.apply(data);
            status = dataManager.dataModifier.save(data);
            return status;
        }
    }
}