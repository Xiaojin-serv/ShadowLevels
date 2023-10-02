package top.shadowpixel.shadowlevels.config;

import top.shadowpixel.shadowcore.api.config.AbstractConfigManager;
import top.shadowpixel.shadowcore.util.collection.ListUtils;
import top.shadowpixel.shadowlevels.ShadowLevels;

import java.util.Collection;
import java.util.List;

public class ConfigManager extends AbstractConfigManager<ShadowLevels> {

    private final List<ConfigurationInfo> INFOS = ListUtils.immutableList(
            ConfigurationInfo.of(plugin, "Config", "Config.yml"),
            ConfigurationInfo.of(plugin, "Items", "Items.yml")
    );

    public ConfigManager(ShadowLevels plugin) {
        super(plugin);
    }

    public static ConfigManager getInstance() {
        return ShadowLevels.getInstance().getConfigManager();
    }

    @Override
    public Collection<ConfigurationInfo> getConfigs() {
        return INFOS;
    }
}
