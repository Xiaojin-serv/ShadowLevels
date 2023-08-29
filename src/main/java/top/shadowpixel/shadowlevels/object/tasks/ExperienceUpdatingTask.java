package top.shadowpixel.shadowlevels.object.tasks;

import top.shadowpixel.shadowcore.util.plugin.PluginUtils;
import top.shadowpixel.shadowlevels.ShadowLevels;
import top.shadowpixel.shadowlevels.level.LevelManager;

public class ExperienceUpdatingTask implements Runnable {

    private final ShadowLevels plugin;

    public ExperienceUpdatingTask(ShadowLevels plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        PluginUtils.getOnlinePlayers().forEach(player -> LevelManager.getInstance().updateExperienceBar(player));
    }
}
