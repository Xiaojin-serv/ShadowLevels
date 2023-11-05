package top.shadowpixel.shadowlevels.level;

import lombok.Getter;
import lombok.ToString;
import lombok.var;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.shadowpixel.shadowcore.api.config.ConfigurationProvider;
import top.shadowpixel.shadowcore.object.interfaces.Manager;
import top.shadowpixel.shadowcore.util.ConfigurationUtils;
import top.shadowpixel.shadowcore.util.collection.MapUtils;
import top.shadowpixel.shadowcore.util.io.FileUtils;
import top.shadowpixel.shadowlevels.ShadowLevels;
import top.shadowpixel.shadowlevels.data.DataManager;
import top.shadowpixel.shadowlevels.object.tasks.ExperienceUpdatingTask;
import top.shadowpixel.shadowlevels.util.Logger;
import top.shadowpixel.shadowlevels.util.MLogger;

import java.io.File;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Objects;

@ToString
public class LevelManager implements Manager {

    private final ShadowLevels plugin;
    @Getter
    private final HashMap<String, Level> loadedLevels = new HashMap<>();
    @Getter
    private File file;
    private BukkitTask experienceBarTask;

    public LevelManager(ShadowLevels plugin) {
        this.plugin = plugin;
    }

    public static LevelManager getInstance() {
        return ShadowLevels.getInstance().getLevelManager();
    }

    @Override
    public void initialize() {
        this.file = new File(plugin.getConfiguration().getString("Levels.File", "%default%/Levels")
                .replace("%default%", String.valueOf(plugin.getDataFolder())));
        //noinspection ResultOfMethodCallIgnored
        this.file.mkdirs();

        this.loadLevels();

        if (!ShadowLevels.getInstance().getConfiguration().getString("Experience-Bar-Level-System", "off")
                .equalsIgnoreCase("off")) {
            this.experienceBarTask = Bukkit.getScheduler().runTaskTimer(plugin, new ExperienceUpdatingTask(plugin), 200, 200);
        }
    }

    @Override
    public void unload() {
        this.loadedLevels.clear();
        if (experienceBarTask != null) {
            this.experienceBarTask.cancel();
            this.experienceBarTask = null;
        }
    }

    /**
     * @param name Name of level system
     * @return Level system
     */
    @Nullable
    public Level getLevelSystem(String name) {
        return MapUtils.smartMatch(name, this.loadedLevels);
    }

    /**
     * @param name Name of level system
     * @return Load level system
     */
    public boolean loadLevel(String name) {
        return loadLevel(name, new File(this.file, name + ".yml"));
    }

    @Deprecated
    public boolean loadLevel(File file) {
        return loadLevel(file.getName(), file);
    }

    public boolean loadLevel(String name, File file) {
        if (!file.exists() || !plugin.getConfiguration().getStringList("Levels.Enabled").contains(name)) {
            return false;
        }

        try {
            //noinspection DataFlowIssue
            var config = ConfigurationProvider.getProvider("Yaml").load(file);
            var level = (Level) config.get("Level-System");
            this.loadedLevels.put(name, level);
            MLogger.infoReplaced("Messages.Levels.Loaded", "%name%", name);
        } catch (Exception e) {
            Logger.error("An error occurred while loading a level", e);
            return false;
        }

        return true;
    }

    /**
     * Load existed level systems
     */
    public void loadLevels() {
        var list = this.file.list();
        var count = 0;
        if (list != null) {
            for (String s : list) {
                if (!s.contains(".")) {
                    continue;
                }

                if (this.loadLevel(s.substring(0, s.indexOf(".")))) {
                    count++;
                }
            }
        }

        if (count == 0) {
            MLogger.info("Messages.Levels.No-Loads");
        } else {
            MLogger.infoReplaced("Messages.Levels.Total-Loads", "%count%", String.valueOf(count));
        }
    }

    /**
     * Create a level system if not existed.
     *
     * @param name The name;
     */
    @Deprecated
    public void createLevelSystem(String name) {
        create(name);
    }

    /**
     * Create a level system if not existed.
     *
     * @param name The name;
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void create(String name) {
        var file = new File(this.file, name + ".yml");
        if (file.exists()) {
            return;
        }

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                //Read line
                var lines = FileUtils.readAllLines(Objects.requireNonNull(plugin.getResource("Levels/Default.yml")),
                        "$name$", name);

                //Write to local
                file.createNewFile();
                Files.write(file.toPath(), lines);

                //Add to config
                ConfigurationUtils.add(this.plugin.getConfiguration(), "Levels.Enabled", name);
                //noinspection DataFlowIssue
                ConfigurationProvider.getProvider("Yaml")
                        .save(plugin.getConfiguration("Config"), ShadowLevels.getConfigFile());

                //Load
                var newLevel = new Level(name);
                this.loadedLevels.put(name, newLevel);

                //Add empty level data to online players
                DataManager.getInstance().getLoadedData().values().forEach(v ->
                        v.getLevels().put(name, new LevelData(Bukkit.getPlayer(v.getOwner()), newLevel)));

                MLogger.info("Messages.Levels.Created-Successfully");
            } catch (Exception e) {
                MLogger.error("Messages.Levels.Failed-to-Create", e);
            }
        });
    }

    public void updateExperienceBar(Player player) {
        var config = plugin.getConfiguration();
        if (config.getString("Experience-Bar-Level-System", "off").equalsIgnoreCase("off")) {
            return;
        }

        var defaultLevel = getLevelSystem(config.getString("Experience-Bar-Level-System"));
        if (defaultLevel == null) {
            return;
        }

        LevelData data = defaultLevel.getLevelData(player);
        player.setLevel(data.getLevels());
        player.setExp(Math.min(data.getPercentage() / 100F, 0.99F));
    }

    public void resetAll(@NotNull Player player) {
        DataManager.getInstance().getPlayerData(player).getLevels().values().forEach(LevelData::reset);
    }
}
