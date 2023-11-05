package top.shadowpixel.shadowlevels.api;

import lombok.Getter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.shadowpixel.shadowlevels.ShadowLevels;
import top.shadowpixel.shadowlevels.data.DataManager;
import top.shadowpixel.shadowlevels.data.PlayerData;
import top.shadowpixel.shadowlevels.level.Level;
import top.shadowpixel.shadowlevels.level.LevelManager;
import top.shadowpixel.shadowlevels.reward.RewardList;
import top.shadowpixel.shadowlevels.reward.RewardManager;
import top.shadowpixel.shadowlevels.reward.RewardMenu;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@SuppressWarnings("unused")
public class ShadowLevelsAPI {

    @Getter
    private static ShadowLevels plugin;
    @Getter
    private static boolean      initialized = false;

    public static void initialize(ShadowLevels plugin) {
        if (initialized)
            return;

        initialized = true;
        ShadowLevelsAPI.plugin = plugin;
    }

    /**
     * @param name Name
     * @return The specific level sysytem
     */
    @Nullable
    public static Level getLevel(String name) {
        return getLevelManager().getLevelSystem(name);
    }

    /**
     * @return All loaded level systems
     */
    @NotNull
    public static Map<String, Level> getLoadedLevels() {
        return getLevelManager().getLoadedLevels();
    }

    /**
     * @param name Name
     * @return The specific reward list
     */
    @Nullable
    public static RewardList getRewardList(String name) {
        return getRewardManager().getRewardList(name);
    }

    @NotNull
    public static List<RewardList> getRewardList(Level level) {
        return getRewardManager().getRewardLists(level);
    }

    /**
     * @param player     Menu owner
     * @param rewardList Reward list
     * @return Player's reward menu of this reward list
     */
    @NotNull
    public static RewardMenu getRewardMenu(Player player, RewardList rewardList) {
        return getRewardManager().getRewardMenu(player, rewardList);
    }

    /**
     * @param player Menu owner
     * @param name   Reward list
     * @return Player's reward menu of the reward list with this name
     */
    @Nullable
    public static RewardMenu getRewardMenu(Player player, String name) {
        return getRewardManager().getRewardMenu(player, name);
    }

    @Contract("null -> null; !null -> !null")
    public static PlayerData getPlayerData(Player player) {
        return getDataManager().getPlayerData(player);
    }

    @Contract("null -> null")
    public static PlayerData getPlayerData(UUID uuid) {
        return getDataManager().getPlayerData(uuid);
    }

    /**
     * @return Level manager
     */
    @NotNull
    public static LevelManager getLevelManager() {
        return getPlugin().getLevelManager();
    }

    /**
     * @return Reward manager
     */
    @NotNull
    public static RewardManager getRewardManager() {
        return getPlugin().getRewardManager();
    }

    @NotNull
    public static DataManager getDataManager() {
        return getPlugin().getDataManager();
    }

}
