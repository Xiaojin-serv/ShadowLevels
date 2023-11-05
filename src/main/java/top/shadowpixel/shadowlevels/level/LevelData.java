package top.shadowpixel.shadowlevels.level;

import lombok.Getter;
import lombok.ToString;
import lombok.var;
import org.bukkit.ChatColor;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.shadowpixel.shadowcore.api.function.EventExecutor;
import top.shadowpixel.shadowcore.util.text.ColorUtils;
import top.shadowpixel.shadowlevels.ShadowLevels;
import top.shadowpixel.shadowlevels.api.events.*;
import top.shadowpixel.shadowlevels.object.enums.ModificationType;
import top.shadowpixel.shadowlevels.reward.Reward;
import top.shadowpixel.shadowlevels.reward.RewardList;
import top.shadowpixel.shadowlevels.util.Utils;

import java.util.*;

import static top.shadowpixel.shadowcore.util.object.NumberUtils.*;

@SuppressWarnings({"unused", "LombokGetterMayBeUsed"})
@SerializableAs("ShadowLevels-LevelData")
@ToString
public class LevelData implements ConfigurationSerializable {

    @Getter
    protected int levels = 0;
    @Getter
    protected double exps = 0;
    @Getter
    protected float multiple = 1.0F;
    protected HashMap<String, List<String>> receivedRewards = new HashMap<>(0);
    private Player player;
    private Level level;

    public LevelData() {
    }

    public LevelData(@Nullable Player player, @NotNull Level level) {
        this(player, level, level.getDefaultLevel(), 0, 1.0F);
    }

    public LevelData(@Nullable Player player, @NotNull Level level, int levels, int exps, float multiple) {
        this(player, level, levels, exps, multiple, new HashMap<>());
    }

    public LevelData(Player player, Level level, int levels, int exps, float multiple, Map<String, List<String>> receivedRewards) {
        this.player = player;
        this.level = level;
        this.levels = levels;
        this.exps = exps;
        this.multiple = multiple;
        this.receivedRewards = new HashMap<>(receivedRewards);
    }

    @SuppressWarnings({"unchecked", "unused"})
    public LevelData(@NotNull Map<String, Object> map) {
        if (map.containsKey("levels")) {
            this.levels = asInt(map.get("levels"), 0);
        }

        if (map.containsKey("exps")) {
            this.exps = asDouble(map.get("exps"), 0);
        }

        if (map.containsKey("multiple")) {
            this.multiple = asFloat(map.get("multiple"), 0.0F);
        }

        if (map.containsKey("received-rewards")) {
            this.receivedRewards = new HashMap<>((Map<String, List<String>>) map.get("received-rewards"));
        }
    }

    /**
     * Set the player's level(s).
     *
     * @param amount Level(s) to set
     */
    public void setLevels(int amount) {
        if (amount < 0 || amount == this.levels)
            return;

        var oldValue = this.levels;
        Utils.callEvent(new PlayerLevelsModifyEvent(player, oldValue, amount, level, ModificationType.SET));
    }

    public void setLevelsSilently(int amount) {
        if (amount < 0 || amount == this.levels)
            return;

        this.levels = amount;
    }

    /**
     * Set the player's exp(s).
     *
     * @param amount Exp(s) to set
     */
    public void setExps(double amount) {
        if (amount < 0 || amount == this.exps)
            return;

        var oldValue = this.exps;
        Utils.callEvent(new PlayerExpsModifyEvent(player, oldValue, amount, level, ModificationType.SET));
    }

    public void setExpsSilently(double amount) {
        if (amount < 0 || amount == this.exps)
            return;

        this.exps = amount;
    }

    /**
     * @return Exps that player wants to upgrade
     */
    public double getRequiredExps() {
        return level.getRequiredExps(player);
    }

    /**
     * Set the player's multiples.
     */
    public void setMultiple(float amount) {
        if (amount < 0 || amount == this.multiple)
            return;

        var oldValue = this.multiple;
        Utils.callEvent(new PlayerMultipleModifyEvent(player, level, oldValue, amount));
    }

    public void setMultipleSilently(float amount) {
        if (amount < 0 || amount == this.multiple)
            return;

        this.multiple = amount;
    }

    /**
     * @return Exps the player has ever received
     */
    public double getTotalExps() {
        var total = exps;
        int i = 0;
        while ( i < levels ) {
            i++;
            total += this.level.getRequiredExps(i);
        }

        return total;
    }

    public int getMaxLevels() {
        return this.level.getMaxLevels(player);
    }

    /**
     * @return The rewards the player has received of this reward list
     */
    public List<String> getReceivedRewards(@Nullable RewardList rewardList) {
        if (rewardList == null) {
            return Collections.emptyList();
        }

        return getReceivedRewards(rewardList.getName());
    }

    public List<String> getReceivedRewards(String name) {
        return this.receivedRewards.get(name);
    }

    /**
     * @return The rewards the player has received
     */
    public HashMap<String, ? extends List<String>> getReceivedRewards() {
        return this.receivedRewards;
    }

    /**
     * @return If player reached max levels
     */
    public boolean isMax() {
        return this.levels >= getMaxLevels();
    }

    /**
     * @return Percentage
     */
    public float getPercentage() {
        var percentage = (float) exps / (float) getRequiredExps() * 100F;
        return Float.isNaN(percentage) ? 100.0F : Math.abs(Math.min(percentage, 100.0F));
    }

    /**
     * @return Progress bar
     */
    public String getProgressBar() {
        return getProgressBar(ShadowLevels.getInstance().getConfiguration().getInt("Progress-Bar.Default-Length"));
    }

    /**
     * @param length Length of progress bar
     * @return Progress bar
     */
    public String getProgressBar(int length) {
        if (length <= 0) {
            return "";
        }

        //Init variables
        var config = ShadowLevels.getInstance().getConfiguration().getConfigurationSection("Progress-Bar");
        assert config != null;
        var filled = ColorUtils.colorize(config.getString("Filled", "&b■"));
        var blank = ColorUtils.colorize(config.getString("Blank", "&7■"));

        //Build progress bar string
        var progress = new StringBuilder();
        var filledLength = (int) (getPercentage() / 100F * length);
        for (int i = 0; i < length; i++) {
            if (i < filledLength) {
                if (ChatColor.getLastColors(filled).equals(ChatColor.getLastColors(progress.toString()))) {
                    progress.append(ChatColor.stripColor(filled));
                    continue;
                }

                progress.append(filled);
            } else {
                if (ChatColor.getLastColors(blank).equals(ChatColor.getLastColors(progress.toString()))) {
                    progress.append(ChatColor.stripColor(blank));
                    continue;
                }

                progress.append(blank);
            }
        }

        return progress.toString();
    }

    /**
     * Add the player's level(s).
     *
     * @param amount Level(s) to add
     */
    public void addLevels(int amount) {
        if (amount < 1)
            return;

        var oldValue = this.levels;
        Utils.callEvent(new PlayerLevelsModifyEvent(player, oldValue, amount, level, ModificationType.ADD));
    }

    public void addLevelsSilently(int amount) {
        if (amount < 1)
            return;

        this.levels += amount;
    }

    /**
     * Add the player's exp(s).
     *
     * @param amount Exp(s) to add
     */
    public void addExps(double amount) {
        if (amount < 1)
            return;

        var oldValue = this.exps;
        Utils.callEvent(new PlayerExpsModifyEvent(player, oldValue, amount, level, ModificationType.ADD));
    }

    public void addExpsSilently(double amount) {
        if (amount < 1)
            return;

        this.exps += amount;
    }

    /**
     * Add player's received reward.
     *
     * @param name Name
     */
    public void addReceivedReward(RewardList rewardList, String name) {
        if (!receivedRewards.containsKey(rewardList.getName())) {
            receivedRewards.put(rewardList.getName(), new ArrayList<>());
        }

        this.receivedRewards.get(rewardList.getName()).add(name);
    }

    /**
     * Add player's received rewards.
     *
     * @param names Iterable of levels' name
     */
    public void addReceivedRewards(RewardList rewardList, Iterable<String> names) {
        names.forEach(name -> this.addReceivedReward(rewardList, name));
    }

    /**
     * Remove the player's level(s)
     *
     * @param amount Level(s) to remove
     */
    public void removeLevels(int amount) {
        if (amount < 0)
            return;

        amount = Math.min(levels, amount);

        var oldValue = this.levels;
        Utils.callEvent(new PlayerLevelsModifyEvent(player, oldValue, amount, level, ModificationType.REMOVE));
    }

    public void removeLevelsSilently(int amount) {
        if (amount < 0)
            return;

        amount = Math.min(levels, amount);
        this.levels -= amount;
    }

    /**
     * Remove the player's exp(s)
     *
     * @param amount Exp(s) to remove
     */
    public void removeExps(double amount) {
        if (amount < 0)
            return;

        var oldValue = this.exps;
        Utils.callEvent(new PlayerExpsModifyEvent(player, oldValue, amount, level, ModificationType.REMOVE));
    }

    public void removeExpsSilently(double amount) {
        if (amount < 0)
            return;

        this.exps -= amount;
    }

    /**
     * Reset data.
     */
    public void reset() {
        Utils.callEvent(new PlayerDataResetEvent(player, level));
    }

    public void resetSilently() {
        this.levels = 0;
        this.exps = 0;
        this.multiple = 1;
        this.receivedRewards.clear();
    }

    /**
     * @return Color of current level
     */
    public String getColor() {
        return level.getColor(this.levels);
    }

    /**
     * @return Owner of this data
     */
    @NotNull
    public Player getPlayer() {
        return player;
    }

    /**
     * Set owner player of this data.
     *
     * @param player Owner of this data
     */
    public void setPlayer(@NotNull Player player) {
        if (this.player == null) {
            this.player = player;
        }
    }

    /**
     * Get the initial level of this data.
     *
     * @return Level
     */
    @NotNull
    public Level getLevel() {
        return level;
    }

    /**
     * Set the initial level of this data.
     *
     * @param level Initial level
     */
    public void setLevel(Level level) {
        if (this.level == null) {
            this.level = level;
        }
    }

    /**
     * Check level up.
     */
    public void checkLevelUp() {
        if (isMax()) {
            return;
        }

        if (this.exps >= this.getRequiredExps()) {
            while ( this.exps >= this.getRequiredExps() && this.levels < getMaxLevels() ) {
                this.exps -= this.getRequiredExps();
                this.levels++;
                EventExecutor.execute(ShadowLevels.getInstance(), player, level.getLevelUpEvent(player, this.levels));
            }

            Utils.callEvent(new PlayerLevelUpEvent(player, this.level));
        }
    }

    /**
     * Check whether the player has received this reward.
     */
    public boolean hasReceived(RewardList rewardList, Reward reward) {
        return hasReceived(rewardList, reward.getName());
    }

    /**
     * Check whether the player has received this reward.
     */
    public boolean hasReceived(RewardList rewardList, String name) {
        if (!this.receivedRewards.containsKey(rewardList.getName())) {
            return false;
        }

        return this.receivedRewards.get(rewardList.getName()).contains(name);
    }

    /**
     * Creates a Map representation of this class.
     * <p>
     * This class must provide a method to restore this class, as defined in
     * the {@link ConfigurationSerializable} interface javadocs.
     *
     * @return Map containing the current state of this class
     */
    @NotNull
    @Override
    public Map<String, Object> serialize() {
        var map = new HashMap<String, Object>();
        map.put("levels", levels);
        map.put("exps", exps);
        map.put("multiple", multiple);
        map.put("received-rewards", receivedRewards);
        return map;
    }
}
