package top.shadowpixel.shadowlevels.reward;

import lombok.Getter;
import lombok.ToString;
import lombok.var;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.shadowpixel.shadowcore.api.config.ConfigurationProvider;
import top.shadowpixel.shadowcore.api.function.components.ExecutableEvent;
import top.shadowpixel.shadowcore.api.util.Optional;
import top.shadowpixel.shadowcore.object.interfaces.Manager;
import top.shadowpixel.shadowcore.util.ConfigurationUtils;
import top.shadowpixel.shadowcore.util.collection.MapUtils;
import top.shadowpixel.shadowcore.util.io.FileUtils;
import top.shadowpixel.shadowlevels.ShadowLevels;
import top.shadowpixel.shadowlevels.level.Level;
import top.shadowpixel.shadowlevels.object.enums.RewardStatus;
import top.shadowpixel.shadowlevels.util.ItemUtils;
import top.shadowpixel.shadowlevels.util.Logger;
import top.shadowpixel.shadowlevels.util.MLogger;

import java.io.File;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
@ToString
public class RewardManager implements Manager {

    public static Map<String, ItemStack>                          defaultItems;
    public static Map<String, Map<RewardStatus, ExecutableEvent>> defaultEvents;

    private final ShadowLevels                                 plugin;
    private final HashMap<String, RewardList>                  loadedRewards = new HashMap<>();
    private final HashMap<Player, HashMap<String, RewardMenu>> rewardMenus   = new HashMap<>();

    @Getter
    private File file;

    public RewardManager(ShadowLevels plugin) {
        this.plugin = plugin;
    }

    public static RewardManager getInstance() {
        return ShadowLevels.getInstance().getRewardManager();
    }

    @Override
    public void initialize() {
        this.file = new File(plugin.getConfiguration().getString("Rewards.File", "%default%/Rewards")
                .replace("%default%", String.valueOf(plugin.getDataFolder())));
        //noinspection ResultOfMethodCallIgnored
        this.file.mkdirs();

        //Load items and events
        loadDefaultEvents();
        loadDefaultItems();

        //Load Rewards
        loadRewards();
    }

    @Override
    public void unload() {
        loadedRewards.clear();
        rewardMenus.clear();
    }

    @Nullable
    public RewardList getRewardList(@NotNull String name) {
        return MapUtils.smartMatch(name, loadedRewards);
    }

    @Nullable
    public RewardMenu getRewardMenu(@NotNull Player player, @NotNull RewardList rewardList) {
        if (!rewardMenus.containsKey(player)) {
            this.rewardMenus.put(player, new HashMap<>());
        }

        if (!this.rewardMenus.get(player).containsKey(rewardList.getName())) {
            this.rewardMenus.get(player).put(rewardList.getName(), RewardMenu.createMenu(player, rewardList));
        }

        return rewardMenus.get(player).get(rewardList.getName());
    }

    @Nullable
    public RewardMenu getRewardMenu(@NotNull Player player, @NotNull String rewardList) {
        RewardList list;
        if ((list = getRewardList(rewardList)) == null) {
            return null;
        }

        return getRewardMenu(player, list);
    }

    public void removeRewardMenu(@NotNull Player player, @NotNull String name) {
        if (!rewardMenus.containsKey(player)) {
            return;
        }

        rewardMenus.get(player).remove(name);
    }

    public void removeRewardMenu(@NotNull Player player, @NotNull RewardList list) {
        if (!rewardMenus.containsKey(player)) {
            return;
        }

        rewardMenus.get(player).remove(list.getName());
    }

    public void removeRewardMenu(@NotNull Player player) {
        rewardMenus.remove(player);
    }

    public ArrayList<RewardList> getRewardLists(@NotNull Level level) {
        return (ArrayList<RewardList>) loadedRewards.values().stream()
                .filter(r -> r.getLevel().equals(level))
                .collect(Collectors.toList());
    }

    @Deprecated
    public File getRewardFile() {
        return file;
    }

    public Map<String, RewardList> getLoadedRewards() {
        return Collections.unmodifiableMap(this.loadedRewards);
    }

    @SuppressWarnings("DataFlowIssue")
    public void loadDefaultEvents() {
        var events = new HashMap<String, Map<RewardStatus, ExecutableEvent>>();
        plugin.getLocaleManager().getLocales().forEach((name, locale) -> {
            var map = new EnumMap<RewardStatus, ExecutableEvent>(RewardStatus.class);
            map.put(RewardStatus.LOCKED,
                    ExecutableEvent.of(locale.getConfig("Events").getStringList("Events.Reward-Locked")));
            map.put(RewardStatus.RECEIVED,
                    ExecutableEvent.of(locale.getConfig("Events").getStringList("Events.Reward-Received")));
            map.put(RewardStatus.NO_PERMISSIONS,
                    ExecutableEvent.of(locale.getConfig("Events").getStringList("Events.Reward-NoPermissions")));

            events.put(name, Collections.unmodifiableMap(map));
            map.values().forEach(e -> e.replacePermanently("%prefix%", ShadowLevels.getPrefix()));
        });
        defaultEvents = Collections.unmodifiableMap(events);
    }

    public void loadDefaultItems() {
        var map = new HashMap<String, ItemStack>();
        var items = Optional.of(plugin.getConfiguration("Items"))
                .ret(s -> s.getConfigurationSection("Items"));

        //Load items
        assert items != null;
        items.getKeys().forEach(item ->
                map.put(item, ItemUtils.builder().fromConfigSection(items.getConfigurationSection(item)).build()));

        defaultItems = Collections.unmodifiableMap(map);
    }

    public boolean loadReward(@NotNull String name) {
        return loadReward(name, new File(this.file, name + ".yml"));
    }

    @Deprecated
    public boolean loadReward(@NotNull File file) {
        return loadReward(file.getName(), file);
    }

    @SuppressWarnings("DataFlowIssue")
    public boolean loadReward(@NotNull String name, @NotNull File file) {
        if (!file.exists() || !plugin.getConfiguration().getStringList("Rewards.Enabled").contains(name)) {
            return false;
        }

        try {
            var config = ConfigurationProvider.getProvider("Yaml").load(file);
            if (plugin.getLevel(config.getString("Rewards-List.Level-System")) == null) {
                return false;
            }

            this.loadedRewards.put(name,
                    new RewardList(config.getConfigurationSection("Rewards-List"), name));
            MLogger.infoReplaced("Messages.Rewards.Loaded", "%name%", name);
        } catch (Exception e) {
            Logger.error("An error occurred while loading a reward", e);
            return false;
        }

        return true;
    }

    public void loadRewards() {
        var list = file.list();
        var count = 0;
        if (list != null) {
            for (String s : list) {
                if (!s.contains(".")) {
                    return;
                }

                if (this.loadReward(s.substring(0, s.indexOf(".")))) {
                    count++;
                }
            }
        }

        if (count == 0) {
            MLogger.info("Messages.Rewards.No-Loads");
        } else {
            MLogger.infoReplaced("Messages.Rewards.Total-Loads", "%count%", String.valueOf(count));
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void create(@NotNull String name, @NotNull Level level) {
        var file = new File(this.file, name + ".yml");
        if (file.exists()) {
            return;
        }

        try {
            //Read lines
            var lines = FileUtils.readAllLines(Objects.requireNonNull(plugin.getResource("Rewards/Default.yml")),
                    "$name$", level.getName());

            //Write to local
            file.getParentFile().mkdirs();
            file.createNewFile();
            Files.write(file.toPath(), lines);

            //Add to config
            ConfigurationUtils.add(plugin.getConfiguration(), "Rewards.Enabled", name);
            //noinspection DataFlowIssue
            ConfigurationProvider.getProvider("Yaml").save(plugin.getConfiguration("Config"), ShadowLevels.getConfigFile());

            //noinspection DataFlowIssue
            var reward = new RewardList(ConfigurationProvider.getProvider("Yaml").load(file)
                    .getConfigurationSection("Rewards-List"));
            reward.setName(name);
            this.loadedRewards.put(name, reward);
            MLogger.info("Messages.Rewards.Created-Successfully");
        } catch (Exception exc) {
            MLogger.error("Messages.Rewards.Failed-to-Create", exc);
        }
    }

    @Deprecated
    public void createRewardList(String name, Level level) {
        create(name, level);
    }
}
