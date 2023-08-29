package top.shadowpixel.shadowlevels;

import lombok.Getter;
import lombok.var;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.shadowpixel.shadowcore.api.config.Configuration;
import top.shadowpixel.shadowcore.api.locale.Locale;
import top.shadowpixel.shadowcore.api.plugin.AbstractPlugin;
import top.shadowpixel.shadowcore.api.util.time.MSTimer;
import top.shadowpixel.shadowcore.object.interfaces.Manager;
import top.shadowpixel.shadowcore.util.plugin.DescriptionChecker;
import top.shadowpixel.shadowcore.util.plugin.PluginUtils;
import top.shadowpixel.shadowcore.util.text.ColorUtils;
import top.shadowpixel.shadowlevels.api.ShadowLevelsAPI;
import top.shadowpixel.shadowlevels.bungee.BungeeManager;
import top.shadowpixel.shadowlevels.command.*;
import top.shadowpixel.shadowlevels.command.exps.AddExpsCommand;
import top.shadowpixel.shadowlevels.command.exps.AddRandomExpsCommand;
import top.shadowpixel.shadowlevels.command.exps.RemoveExpsCommand;
import top.shadowpixel.shadowlevels.command.exps.SetExpsCommand;
import top.shadowpixel.shadowlevels.command.level.AddLevelsCommand;
import top.shadowpixel.shadowlevels.command.level.CreateLevelsCommand;
import top.shadowpixel.shadowlevels.command.level.RemoveLevelsCommand;
import top.shadowpixel.shadowlevels.command.level.SetLevelsCommand;
import top.shadowpixel.shadowlevels.command.multiple.SetMultipleCommand;
import top.shadowpixel.shadowlevels.command.reward.CreateRewardCommand;
import top.shadowpixel.shadowlevels.command.reward.OpenRewardCommand;
import top.shadowpixel.shadowlevels.command.reward.RewardCommand;
import top.shadowpixel.shadowlevels.config.ConfigManager;
import top.shadowpixel.shadowlevels.data.DataManager;
import top.shadowpixel.shadowlevels.data.PlayerData;
import top.shadowpixel.shadowlevels.level.Level;
import top.shadowpixel.shadowlevels.level.LevelData;
import top.shadowpixel.shadowlevels.level.LevelListener;
import top.shadowpixel.shadowlevels.level.LevelManager;
import top.shadowpixel.shadowlevels.locale.LocaleManager;
import top.shadowpixel.shadowlevels.object.Metrics;
import top.shadowpixel.shadowlevels.object.hooks.PlaceholderHook;
import top.shadowpixel.shadowlevels.object.listeners.DataListener;
import top.shadowpixel.shadowlevels.reward.RewardList;
import top.shadowpixel.shadowlevels.reward.RewardManager;
import top.shadowpixel.shadowlevels.util.MLogger;

import java.io.File;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@SuppressWarnings("LombokGetterMayBeUsed")
public final class ShadowLevels extends AbstractPlugin {

    public static final Manager.Property
            CONFIG_MANAGER = Manager.Property.of(ConfigManager.class, "configManager"),
            LOCALE_MANAGER = Manager.Property.of(LocaleManager.class, "localeManager"),
            LEVEL_MANAGER  = Manager.Property.of(LevelManager.class, "levelManager"),
            DATA_MANAGER   = Manager.Property.of(DataManager.class, "dataManager"),
            REWARD_MANAGER = Manager.Property.of(RewardManager.class, "rewardManager"),
            BUNGEE_MANAGER = Manager.Property.of(BungeeManager.class, "bungeeManager");

    private static ShadowLevels  instance;
    private static File          CONFIG_FILE;
    @Getter
    private        ConfigManager configManager;
    @Getter
    private        LocaleManager localeManager;
    @Getter
    private        LevelManager  levelManager;
    @Getter
    private        DataManager   dataManager;
    @Getter
    private        RewardManager rewardManager;
    @Getter
    private        BungeeManager bungeeManager;
    private        boolean       isEnabled = false;

    @NotNull
    public static PlayerData getPlayerData(@NotNull Player player) {
        return getPlayerData(player.getUniqueId());
    }

    @NotNull
    public static PlayerData getPlayerData(@NotNull UUID uuid) {
        return getInstance().dataManager.getPlayerData(uuid);
    }

    public static String getPrefix() {
        return ColorUtils.colorize(getInstance().getConfiguration().getString("Prefix"));
    }

    public static String getVersion() {
        return getInstance().getDescription().getVersion();
    }

    public static File getConfigFile() {
        if (CONFIG_FILE == null) {
            CONFIG_FILE = new File(getInstance().getDataFolder(), "Config.yml");
        }

        return CONFIG_FILE;
    }

    public static ShadowLevels getInstance() {
        return instance;
    }

    @Override
    public void enable() {
        var timer = new MSTimer();
        instance = this;

        //Config manager initialization
        PluginUtils.initManager(this, CONFIG_MANAGER);
        logger.addReplacement("{prefix}", getPrefix());
        logger.addReplacement("%prefix%", getPrefix());

        //Locale manager initialization
        var localeFile = new File(getConfiguration().getString("Locale.File", "%default%/Locale")
                .replace("{default}", getDataFolder().toString())
                .replace("%default%", getDataFolder().toString()));
        PluginUtils.initManager(this, LOCALE_MANAGER, this, localeFile);
        if (getDefaultLocale() == LocaleManager.getInternal()) {
            this.logger.warn("Internal locale is in use!");
        }

        //Send welcome tips
        var lang = getDefaultMessage();
        logger.info(
                "",
                "",
                "&7&lShadow&b&lLevels &7>> &a" + lang.getString("Messages.Welcome") + "!",
                "",
                "&f" + lang.getString("Messages.Version") + ": &av" + getVersion(),
                "&f" + lang.getString("Messages.Author") + ": &aXiaoJin_awa_",
                "",
                ""
        );

        //Serializations registration
        registerSerializations(LevelData.class, PlayerData.class, Level.class);

        //Check plugin.yml
        if (!new DescriptionChecker(
                this,
                "ShadowLevels",
                "XiaoJin_awa_",
                "1.4.0").check()) {
            MLogger.error("Messages.OnEnable.Error-Plugin_yml");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        //Menu handlers registration
        registerMenuHandler("Reward");

        //Commands initialization
        try {
            MLogger.info("Messages.OnEnable.Register-Command");
            initCommands();
            MLogger.info("Messages.OnEnable.Succeed");
        } catch (Exception e) {
            MLogger.error("Messages.OnEnable.Failed", e);
        }

        //Listeners registration
        try {
            MLogger.info("Messages.OnEnable.Register-Listener");
            registerListener("Level", new LevelListener(this));
            registerListener("Data", new DataListener(this));
            MLogger.info("Messages.OnEnable.Succeed");
        } catch (Exception e) {
            MLogger.error("Messages.OnEnable.Failed", e);
        }

        //Level manager initialization
        PluginUtils.initManager(this, LEVEL_MANAGER);

        //Reward manager initialization
        PluginUtils.initManager(this, REWARD_MANAGER);

        //Data manager initialization
        PluginUtils.initManager(this, DATA_MANAGER);

        //Bungee manager initialize
        PluginUtils.initManager(this, BUNGEE_MANAGER);

        //PAPI Extension registration
        if (getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            new PlaceholderHook(this).register();
        }

        //Final step
        MLogger.infoReplaced("Messages.OnEnable.Enabled",
                "%time%", String.valueOf(timer.getTimePassed()));
        ShadowLevelsAPI.initialize(this);
        new Metrics(this, 16842);
        isEnabled = true;
    }

    @Override
    public void disable() {
        if (!isEnabled) return;

        PluginUtils.unloadManager(
                dataManager,
                levelManager,
                localeManager,
                configManager
        );
        MLogger.infoReplaced("Messages.OnDisable.Disabled");
    }

    @NotNull
    @Override
    @Deprecated
    public FileConfiguration getConfig() {
        return super.getConfig();
    }

    @Nullable
    @Deprecated
    public Configuration getConfig(String name) {
        return getConfiguration(name);
    }

    @Nullable
    public Configuration getConfiguration(String name) {
        return this.configManager.getConfiguration(name);
    }

    @NotNull
    public Configuration getConfiguration() {
        return Objects.requireNonNull(getConfiguration("Config"), "Config is null");
    }

    public Locale getDefaultLocale() {
        return this.localeManager == null || this.localeManager.getDefaultLocale() == null ?
               LocaleManager.getInternal() : this.localeManager.getDefaultLocale();
    }

    public Configuration getDefaultMessage() {
        return getDefaultLocale().getConfig("Messages");
    }

    public Level getLevel(String name) {
        return this.levelManager.getLevelSystem(name);
    }

    public void initCommands() {
        //Register command
        registerCommandHandler();
        var command = this.commandHandler.registerCommand("ShadowLevels", new MainCommand());

        //Add subcommands
        assert command != null;
        command.addCommand(
                new AdminCommand(),
                new AddLevelsCommand(),
                new AddExpsCommand(),
                new AddRandomExpsCommand(),
                new CreateLevelsCommand(),
                new CreateRewardCommand(),
                new HelpCommand(),
                new ReloadCommand(),
                new RemoveLevelsCommand(),
                new RemoveExpsCommand(),
                new ResetCommand(),
                new StatsCommand(),
                new SetLevelsCommand(),
                new SetExpsCommand(),
                new SetMultipleCommand(),
                new OpenRewardCommand(),
                new RewardCommand(),
                new StatsOfflineCommand()
        );
    }

    public @Nullable RewardList getRewardList(@NotNull String name) {
        return getRewardManager().getRewardList(name);
    }

    @NotNull
    public List<RewardList> getRewardLists(Level level) {
        return getRewardManager().getRewardLists(level);
    }
}
