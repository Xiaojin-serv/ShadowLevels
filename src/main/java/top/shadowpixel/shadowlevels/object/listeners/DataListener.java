package top.shadowpixel.shadowlevels.object.listeners;

import lombok.var;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import top.shadowpixel.shadowcore.util.entity.PlayerUtils;
import top.shadowpixel.shadowcore.util.entity.SenderUtils;
import top.shadowpixel.shadowlevels.ShadowLevels;
import top.shadowpixel.shadowlevels.data.DataManager;
import top.shadowpixel.shadowlevels.level.LevelData;
import top.shadowpixel.shadowlevels.reward.RewardManager;
import top.shadowpixel.shadowlevels.util.LocaleUtils;
import top.shadowpixel.shadowlevels.util.Logger;

/**
 * Events about players' behaviors.
 */
public class DataListener implements Listener {

    private final ShadowLevels plugin;

    public DataListener(ShadowLevels plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onLogin(PlayerLoginEvent event) {
        var player = event.getPlayer();
        var loaded = DataManager.getInstance()
                .load(player.getUniqueId(), true);

        if (!loaded) {
            var msg = LocaleUtils.getMessage(player, "Messages.Data.Player.Failed-to-load");
            if (plugin.getConfiguration().getString("Data.Failed-to-Load-Action", "kick").equalsIgnoreCase("kick")) {
                event.setResult(PlayerLoginEvent.Result.KICK_OTHER);
                event.setKickMessage(msg);
            } else {
                SenderUtils.sendMessage(player, msg, "%player%", player.getName());
            }

            msg = LocaleUtils.getMessage(player, "Messages.Data.Admin.Failed-to-load",
                    "%player%", player.getName());
            Bukkit.getConsoleSender().sendMessage(msg);
            for (Player s : PlayerUtils.getOnlineOperators()) {
                s.sendMessage(msg);
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onJoin(PlayerJoinEvent event) {
        var player = event.getPlayer();
        var data = ShadowLevels.getPlayerData(player);
        for (LevelData ld : data.getLevels().values()) {
            ld.setPlayer(player);
            ld.checkLevelUp();
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onQuit(PlayerQuitEvent event) {
        var player = event.getPlayer();
        var unloaded = DataManager.getInstance().unload(player.getUniqueId());
        if (!unloaded) {
            var msg = LocaleUtils.getMessage(player, "Messages.Data.Admin.Failed-to-unload", "%player%", player.getName());
            PlayerUtils.getOnlineOperators()
                    .forEach(p -> SenderUtils.sendMessage(p, msg));
            Logger.error(LocaleUtils.getMessage(player, "Messages.Data.Admin.Failed-to-unload",
                    "%player%", player.getName(),
                    "%player%", player.getName()));
        }

        RewardManager.getInstance().removeRewardMenu(player);
    }
}
