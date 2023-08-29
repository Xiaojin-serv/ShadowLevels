package top.shadowpixel.shadowlevels.bungee;

import lombok.var;
import org.bukkit.Bukkit;
import top.shadowpixel.shadowcore.object.interfaces.Manager;
import top.shadowpixel.shadowlevels.ShadowLevels;
import top.shadowpixel.shadowlevels.object.listeners.BungeeListener;

public class BungeeManager implements Manager {
    //Channel name
    public static final String CHANNEL = "ShadowLevels:Sync".toLowerCase();

    private final ShadowLevels plugin;

    public BungeeManager(ShadowLevels plugin) {
        this.plugin = plugin;
    }

    public static BungeeManager getInstance() {
        return ShadowLevels.getInstance().getBungeeManager();
    }

    @Override
    public void initialize() {
        if (!isBungeeMode()) {
            return;
        }

        if (!plugin.getServer().getPluginManager().isPluginEnabled("ShadowMessenger")) {
            plugin.logger.warn("BungeeMode is enabled, but plugin ShadowMessenger is missing!",
                    "Please follow the tutorial and install it");
            return;
        }

        var messenger = Bukkit.getMessenger();
        messenger.registerIncomingPluginChannel(this.plugin, CHANNEL, new BungeeListener());
        messenger.registerOutgoingPluginChannel(this.plugin, CHANNEL);

        plugin.logger.info("&aBungee initialized!");
    }

    @Override
    public void unload() {
        if (!isBungeeMode()) {
            return;
        }

        var messenger = Bukkit.getMessenger();
        messenger.unregisterIncomingPluginChannel(this.plugin, CHANNEL);
        messenger.unregisterOutgoingPluginChannel(this.plugin, CHANNEL);
    }

    public boolean isBungeeMode() {
        return this.plugin.getConfiguration().getBoolean("Data.Bungee-Mode");
    }
}
