package top.shadowpixel.shadowlevels.level;

import lombok.Getter;
import lombok.ToString;
import lombok.var;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.shadowpixel.shadowcore.api.function.components.ExecutableEvent;
import top.shadowpixel.shadowcore.api.util.Optional;
import top.shadowpixel.shadowcore.util.text.ColorUtils;
import top.shadowpixel.shadowcore.util.text.StringUtils;
import top.shadowpixel.shadowlevels.ShadowLevels;
import top.shadowpixel.shadowlevels.data.DataManager;
import top.shadowpixel.shadowlevels.util.LocaleUtils;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Class for a level system.
 */
@SerializableAs("ShadowLevels-Level")
@ToString
public class Level implements ConfigurationSerializable {

    private final HashMap<String, Map<String, ExecutableEvent>> events = new HashMap<>();

    @Getter
    private final String  name;
    private Integer maxLevels, requiredExps;
    private HashMap<Integer, Integer>      expsToLevel;
    private LinkedHashMap<Integer, String> colors;

    public Level(String name) {
        this.name = name;
        this.maxLevels = 100;
        this.requiredExps = 5000;
        this.expsToLevel = new HashMap<>();
        this.expsToLevel.put(1, 100);
        this.colors = new LinkedHashMap<>();
        this.colors.put(0, "&7");
        this.colors.put(10, "&f");

        loadEvents();
    }

    @SuppressWarnings("unchecked")
    public Level(Map<String, Object> map) {
        this.name = map.getOrDefault("Name", "Unnamed").toString();

        if (map.containsKey("Max-levels")) {
            this.maxLevels = (int) map.get("Max-levels");
        }

        if (map.containsKey("Colors")) {
            this.colors = (LinkedHashMap<Integer, String>) map.get("Colors");
        }

        if (map.containsKey("Default-exps-to-level-up")) {
            this.requiredExps = (int) map.get("Default-exps-to-level-up");
        }

        if (map.containsKey("Custom-exps-to-level-up")) {
            this.expsToLevel = (HashMap<Integer, Integer>) map.get("Custom-exps-to-level-up");
        }

        loadEvents();
    }

    /**
     * Load events.
     */
    public void loadEvents() {
        var plugin = ShadowLevels.getInstance();
        plugin.getLocaleManager().getLocales().forEach((name, locale) -> {
            var events = locale.getConfig("Events");
            if (events == null) {
                return;
            }

            var section = events.getConfigurationSection("Events.Levels." + this.name);
            if (section == null) {
                return;
            }

            var keys = section.getKeys();
            var loadedEvents = new HashMap<String, ExecutableEvent>(keys.size());

            if (section.isConfigurationSection("Level-Up-Events")) {
                var levelUpSection = section.getConfigurationSection("Level-Up-Events");
                assert levelUpSection != null;

                levelUpSection.getKeys().forEach(key -> {
                    var event = ExecutableEvent.of(levelUpSection.getStringList(key));
                    event.replacePermanently("%prefix%", ShadowLevels.getPrefix());
                    if (StringUtils.isInteger(key)) {
                        event.replaceAllPermanently("%level%|%levels%", key);
                    }

                    loadedEvents.put("LEVEL_UP_" + key.toUpperCase(), event);
                });

                keys.remove("Level-Up-Events");
            }

            keys.remove("Player-Added-Levels");
            keys.forEach(key -> {
                var event = Objects.equals(key, "Player-Added-Levels") ?
                            ExecutableEvent.of(section.getStringList("Level-Up-Events")) :
                            ExecutableEvent.of(section.getStringList(key));
                event.replacePermanently("%prefix%", ShadowLevels.getPrefix());
                loadedEvents.put(key, event);
            });

            this.events.put(name, loadedEvents);
        });
    }

    /**
     * @return The max levels of this level
     * @deprecated Some players may be using permissions to limit their max levels.
     * Use {@link #getDefaultMaxLevels()} instead.
     */
    @Deprecated
    public int getMaxLevels() {
        return this.maxLevels;
    }

    /**
     * @param player Player
     * @return Current max level
     */
    public int getMaxLevels(Player player) {
        var max = -1;
        for (var perm : player.getEffectivePermissions()) {
            var node = "ShadowLevels.Levels." + this.name + ".MaxLevels.";
            if (perm.getPermission().toLowerCase().startsWith(node.toLowerCase())) {
                try {
                    max = Math.max(max, Integer.parseInt(perm.getPermission().substring(node.length())));
                } catch (NumberFormatException ignored) {
                }
            }
        }

        return max > -1 ? max : getDefaultMaxLevels();
    }

    /**
     * @return Default max levels
     */
    public int getDefaultMaxLevels() {
        return this.maxLevels;
    }

    /**
     * @param nextLevel The level that required
     * @return The exps that're required to level up, of level >= max level, 0 will be returned
     */
    public int getRequiredExps(int nextLevel) {
        if (nextLevel > maxLevels) {
            return 0;
        }

        if (expsToLevel.containsKey(nextLevel))
            return expsToLevel.get(nextLevel);

        int exps = getDefaultRequiredExps();
        for (var entry : expsToLevel.entrySet()) {
            var l1 = entry.getKey();
            if (l1 >= nextLevel) {
                exps = entry.getValue();
                break;
            }
        }

        return exps;
    }

    /**
     * @param player Player
     * @return Exps that player has to upgrade
     */
    public int getRequiredExps(Player player) {
        if (isMax(player)) {
            return getLevelData(player).getExps();
        }

        return getRequiredExps(getLevelData(player).levels + 1);
    }

    /**
     * @return The default exps to level up
     */
    public int getDefaultRequiredExps() {
        return requiredExps;
    }

    /**
     * @param player Player
     * @return If player reached max levels
     */
    public boolean isMax(Player player) {
        return getLevelData(player).isMax();
    }

    /**
     * @param level Current level
     * @return Color of current level
     */
    public String getColor(Integer level) {
        if (colors.containsKey(level)) {
            return colors.get(level);
        }

        String color = null;
        for (var entry : colors.entrySet()) {
            var level1 = entry.getKey();
            var color1 = entry.getValue();
            if (level1 > level) {
                break;
            } else {
                color = color1;
            }
        }

        return ColorUtils.colorize(color == null ? "&f" : color);
    }

    /**
     * Get the events that will be played when player level up.
     *
     * @return Events that will be played when player level up
     */
    public ExecutableEvent getLevelUpEvent(Player player, int nextLevel) {
        var events = this.events.get(LocaleUtils.getLocale(player).getName());
        if (events == null) {
            return ExecutableEvent.emptyEvent();
        }

        return events.getOrDefault("Level-Up-Events",
                events.getOrDefault("LEVEL_UP_" + nextLevel,
                        events.getOrDefault("LEVEL_UP_DEFAULT", ExecutableEvent.emptyEvent())));
    }

    /**
     * Get an event with specific key.
     *
     * @param locale Locale
     * @param key    Event key
     * @return Event
     */
    public ExecutableEvent getEvent(String locale, String key) {
        return Optional.of(this.events).next(e -> e.get(locale)).ret(e -> e.get(key),
                ExecutableEvent.emptyEvent());
    }

    /**
     * Get an event with specific key.
     *
     * @param player Player
     * @param key    Key
     * @return Event
     */
    public ExecutableEvent getEvent(Player player, String key) {
        return getEvent(LocaleUtils.getLocale(player).getName(), key);
    }

    /**
     * @param player Player
     * @return Player's data of this level system
     */
    public @NotNull LevelData getLevelData(@NotNull Player player) {
        return Objects.requireNonNull(ShadowLevels.getPlayerData(player).getLevelData(this.name), "LevelData is null");
    }

    public @Nullable LevelData getOfflineData(String playerName) {
        return Optional.of(DataManager.getInstance().getOfflineData(playerName))
                .ret(s -> s.getLevelData(this.name));
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
        map.put("Name", name);
        map.put("Max-levels", maxLevels);
        map.put("Colors", colors);
        map.put("Default-exps-to-level-up", requiredExps);
        map.put("Custom-exps-to-level-up", expsToLevel);
        return map;
    }
}
