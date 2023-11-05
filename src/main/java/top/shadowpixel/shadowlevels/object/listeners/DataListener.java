package top.shadowpixel.shadowlevels.object.listeners;

import lombok.var;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import top.shadowpixel.shadowcore.util.entity.PlayerUtils;
import top.shadowpixel.shadowcore.util.entity.SenderUtils;
import top.shadowpixel.shadowlevels.ShadowLevels;
import top.shadowpixel.shadowlevels.data.DataManager;
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
    public void onLogin(AsyncPlayerPreLoginEvent event) {
        var uuid = event.getUniqueId();
        var loaded = DataManager.getInstance()
                .load(uuid, true);

        if (!loaded) {
            var msg = LocaleUtils.getMessage("Messages.Data.Player.Failed-to-load");
            var adminMsg = LocaleUtils.getMessage("Messages.Data.Admin.Failed-to-load",
                    "%player%", uuid.toString());
            Logger.error(adminMsg);
            PlayerUtils.getOnlineOperators()
                    .forEach(s -> s.sendMessage(adminMsg));

            event.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_OTHER);
            event.setKickMessage(msg);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onJoin(PlayerJoinEvent event) {
        var player = event.getPlayer();
        var data = ShadowLevels.getPlayerData(player);
        for (var ld : data.getLevels().values()) {
            ld.setPlayer(player);
            ld.checkLevelUp();
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onQuit(PlayerQuitEvent event) {
        var player = event.getPlayer();
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            var unloaded = DataManager.getInstance().unload(player.getUniqueId());
            if (!unloaded) {
                Bukkit.getScheduler().runTask(plugin, () -> {
                    var msg = LocaleUtils.getMessage(player, "Messages.Data.Admin.Failed-to-unload",
                            "%player%", player.getName());
                    Logger.error(msg);
                    PlayerUtils.getOnlineOperators()
                            .forEach(p -> SenderUtils.sendMessage(p, msg));
                });
            }
        });

        RewardManager.getInstance().removeRewardMenu(player);
    }
}
