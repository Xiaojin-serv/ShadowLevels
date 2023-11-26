package top.shadowpixel.shadowlevels.level;

import lombok.var;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import top.shadowpixel.shadowcore.api.function.EventExecutor;
import top.shadowpixel.shadowcore.api.function.components.ExecutableEvent;
import top.shadowpixel.shadowlevels.ShadowLevels;
import top.shadowpixel.shadowlevels.api.events.*;
import top.shadowpixel.shadowlevels.data.DataManager;
import top.shadowpixel.shadowlevels.reward.RewardManager;
import top.shadowpixel.shadowlevels.util.Utils;

/**
 * Events about modification of level(s), exp(s) and multiple(s).
 */
public class LevelListener implements Listener {

    private final ShadowLevels plugin;

    public LevelListener(ShadowLevels plugin) {
        this.plugin = plugin;
    }

    /**
     * Invoked when level(s) modified
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onLevelsModification(PlayerLevelsModifyEvent event) {
        if (event.isCancelled()) {
            return;
        }

        //Variables initialization
        var player = event.getPlayer();
        var value = event.getValue();
        var level = event.getLevelSystem();
        var data = level.getLevelData(player);
        var type = event.getModificationType();
        var eevent = ExecutableEvent.emptyEvent();

        //Data operation
        switch (type) {
            case ADD:
                data.levels += value;
                eevent = level.getLevelUpEvent(player, event.getOldLevels() + value);
                break;
            case SET:
                data.levels = value;
                eevent = level.getEvent(player, "Player-Set-Levels");
                break;
            case REMOVE:
                data.levels -= value;
                eevent = level.getEvent(player, "Player-Removed-Levels");
                break;
        }

        //Replacements
        eevent.replace("%levels%", () -> String.valueOf(value));
        eevent.replace("%prefix%", ShadowLevels::getPrefix);
        eevent.replace("%level-system%", level::getName);

        //Event execution
        EventExecutor.execute(plugin, player, eevent);
        Utils.callEvent(new PlayerLevelsModifiedEvent(player, event.getOldLevels(), value, level, type));
    }

    /**
     * Invoked when exp(s) modified.
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onExpsModification(PlayerExpsModifyEvent event) {
        if (event.isCancelled()) {
            return;
        }

        //Variables initialization
        var player = event.getPlayer();
        var value = event.getValue();
        var level = event.getLevelSystem();
        var data = level.getLevelData(player);
        var type = event.getModificationType();
        var eevent = ExecutableEvent.emptyEvent();

        //Data operation
        switch (type) {
            case ADD:
                data.exps += value * data.multiple;
                eevent = level.getEvent(player, "Player-Received-Exps");
                eevent.replaceAll("%exp_mul%|%exps_mul%", String.valueOf((int) (value * data.multiple)));
                break;
            case SET:
                data.exps = value;
                eevent = level.getEvent(player, "Player-Set-Exps");
                break;
            case REMOVE:
                data.exps -= value;
                eevent = level.getEvent(player, "Player-Removed-Exps");
                break;
        }

        //Replacements
        eevent.replace("%prefix%", ShadowLevels::getPrefix);
        eevent.replace("%level-system%", level::getName);
        eevent.replaceAll("%exp%|%exps%", () -> String.valueOf(value));

        //Event executation
        EventExecutor.execute(plugin, player, eevent);
        Utils.callEvent(new PlayerExpsModifiedEvent(player, event.getOldExps(), value, level, type));
    }

    /**
     * Invoked when multiple modified.
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onMPLModification(PlayerMultipleModifyEvent event) {
        if (event.isCancelled()) {
            return;
        }

        //Variables initialization
        var player = event.getPlayer();
        var value = event.getValue();
        var level = event.getLevelSystem();
        var eevent = level.getEvent(player, "Player-Set-Multiple");

        //Data operation
        level.getLevelData(player).multiple = value;

        //Replacements
        eevent.replace("%multiple%", () -> String.valueOf(value));
        eevent.replace("%prefix%", ShadowLevels::getPrefix);
        eevent.replace("%level-system%", level::getName);

        //Event executation
        EventExecutor.execute(plugin, player, eevent);
        Utils.callEvent(new PlayerMultipleModifiedEvent(player, level, event.getOldValue(), value));

        //Save data
        DataManager.getInstance().save(player);
    }

    /**
     * Invoked when reseting a player's data
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onReset(PlayerDataResetEvent event) {
        if (event.isCancelled()) {
            return;
        }

        //Variables initialization
        var player = event.getPlayer();
        var level = event.getLevelSystem();
        if (level == null) {
            return;
        }

        var data = level.getLevelData(player);
        var eevent = level.getEvent(player, "Reset");

        //Data operation
        data.exps = 0;
        data.levels = 0;
        data.multiple = 1;
        data.receivedRewards.clear();

        //Replacements
        eevent.replace("%prefix%", ShadowLevels::getPrefix);
        eevent.replace("%level-system%", level::getName);

        //Event executation
        EventExecutor.execute(plugin, player, eevent);
        Utils.callEvent(new AfterPlayerDataResetEvent(player, level));

        //Update exp bar
        LevelManager.getInstance().updateExperienceBar(player);

        //Refresh reward
        refreshRewards(player, level);

        //Save data
        DataManager.getInstance().save(player);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onLevelModified(PlayerLevelsModifiedEvent event) {
        var player = event.getPlayer();
        var level = event.getLevelSystem();

        //Check level up
        level.getLevelData(player).checkLevelUp();

        //Update exp bar
        LevelManager.getInstance().updateExperienceBar(player);

        //Refresh reward
        refreshRewards(player, level);

        //Save data
        DataManager.getInstance().save(player);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onExpsModified(PlayerExpsModifiedEvent event) {
        var player = event.getPlayer();
        var level = event.getLevelSystem();

        //Check level up
        level.getLevelData(player).checkLevelUp();

        //Update exp bar
        LevelManager.getInstance().updateExperienceBar(player);

        //Refresh reward
        refreshRewards(player, level);

        //Save data
        DataManager.getInstance().save(player);
    }

    private void refreshRewards(Player player, Level level) {
        plugin.getRewardLists(level).forEach(reward -> RewardManager.getInstance().getRewardMenu(player, reward).refreshItems());
    }
}
