package top.shadowpixel.shadowlevels.data;

import lombok.Getter;
import lombok.ToString;
import lombok.var;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.shadowpixel.shadowcore.api.database.SQLDatabase;
import top.shadowpixel.shadowcore.api.uid.UUIDStorage;
import top.shadowpixel.shadowcore.api.util.Optional;
import top.shadowpixel.shadowcore.api.util.PlayerCache;
import top.shadowpixel.shadowcore.object.enums.StorageMethod;
import top.shadowpixel.shadowcore.object.interfaces.DataModifier;
import top.shadowpixel.shadowcore.object.interfaces.Manager;
import top.shadowpixel.shadowcore.util.collection.ArrayUtils;
import top.shadowpixel.shadowcore.util.collection.ListUtils;
import top.shadowpixel.shadowcore.util.entity.PlayerUtils;
import top.shadowpixel.shadowlevels.ShadowLevels;
import top.shadowpixel.shadowlevels.data.database.MySQLDatabase;
import top.shadowpixel.shadowlevels.data.database.SQLiteDatabase;
import top.shadowpixel.shadowlevels.data.modifiers.JsonDataModifier;
import top.shadowpixel.shadowlevels.data.modifiers.SQLDataModifier;
import top.shadowpixel.shadowlevels.data.modifiers.YamlDataModifier;
import top.shadowpixel.shadowlevels.level.LevelData;
import top.shadowpixel.shadowlevels.level.LevelManager;
import top.shadowpixel.shadowlevels.util.Logger;

import java.io.File;
import java.util.*;

@SuppressWarnings({"ResultOfMethodCallIgnored", "unused", "UnusedReturnValue"})
@ToString(of = {"loadedData", "storageMethod", "storageFile", "dataModifier", "db"})
public class DataManager implements Manager {

    private static final List<DataModifier<PlayerData>> dataModifiers = ListUtils.immutableList(
            new YamlDataModifier(),
            new JsonDataModifier(),
            new SQLDataModifier()
    );

    private final ShadowLevels plugin;
    private final HashMap<UUID, PlayerData> loadedData = new HashMap<>();

    protected DataModifier<PlayerData> dataModifier;
    @Getter
    private StorageMethod storageMethod;
    @Getter
    private File storageFile;
    private SQLDatabase db;

    public DataManager(ShadowLevels plugin) {
        this.plugin = plugin;
    }

    public static DataManager getInstance() {
        return ShadowLevels.getInstance().getDataManager();
    }

    public void initialize() {
        var config = plugin.getConfiguration();
        try {
            storageMethod = StorageMethod.valueOf(plugin.getConfiguration().getString("Data.Storage-Method", "YAML").toUpperCase());
            if (storageMethod == StorageMethod.PROPERTIES) {
                Logger.error("&cProperties storage is still unsupported!",
                        "&cIt will be completed in the future!");
                storageMethod = StorageMethod.YAML;
            }
        } catch (IllegalArgumentException e) {
            storageMethod = StorageMethod.YAML;
        }


        initDataModifier();
        if (storageMethod.isDatabase()) {
            initDb();
        } else {
            storageFile = new File(config.getString("Data.Non-DB-Storage-File", "")
                    .replace("{default}", plugin.getDataFolder().toString())
                    .replace("%default%", plugin.getDataFolder().toString())
            );
            storageFile.mkdirs();
            if (!storageFile.isDirectory()) {
                Logger.error("Invalid storage file!",
                        "Correct it in Config.yml, then type '/sl reload'");
                storageFile = new File(plugin.getDataFolder(), "PlayerData");
                return;
            }

            loadOnline();
        }

        DataHandler.initialize(plugin);
    }

    @Override
    public void unload() {
        this.loadedData.values().forEach(this::save);
        this.loadedData.clear();

        if (db != null) {
            db.close();
        }
    }

    /**
     * Create data if not exist.
     *
     * @param uuid Player uuid
     * @return Whether created or not
     */
    public boolean create(UUID uuid) {
        if (exists(uuid)) {
            return false;
        }

        return createWithoutCheck(uuid);
    }

    /**
     * Load a data.
     *
     * @param uuid Player uuid
     * @return Whether loaded or not
     */
    public boolean load(UUID uuid) {
        var data = dataModifier.load(uuid);
        if (data != null) {
            this.loadedData.put(uuid, completeData(data));
            return true;
        }

        return false;
    }

    /**
     * Load data. <p>
     * If create is true, the data will be created if it was absent.
     *
     * @param uuid   Player uuid
     * @param create Create if not exist
     * @return Whether loaded or not
     */
    public boolean load(UUID uuid, boolean create) {
        if (exists(uuid)) {
            return load(uuid);
        } else if (!create) {
            return false;
        }

        var result = createWithoutCheck(uuid);
        if (result) {
            this.loadedData.put(uuid, completeData(new PlayerData(uuid)));
        }

        return result;
    }

    public void loadOnline() {
        PlayerUtils.getOnlinePlayers().forEach(p -> load(p.getUniqueId(), true));
    }

    /**
     * Unload and save data.
     *
     * @param uuid Player uuid
     * @return Whether unloaded or not
     */
    public boolean unload(UUID uuid) {
        return unload(uuid, true);
    }

    /**
     * Unload data. <p>
     * If {@param save} is true, this data will be saved or dropped.
     *
     * @param uuid Player uuid
     * @param save Whether saving
     * @return Whether saved or not
     */
    public boolean unload(UUID uuid, boolean save) {
        if (save) {
            var data = getPlayerData(uuid);
            save(data);
        }

        return this.loadedData.remove(uuid) != null;
    }

    /**
     * Remove a data permanently.
     *
     * @param uuid Player uuid
     * @return Whether removed or not
     */
    public boolean remove(UUID uuid) {
        return remove(uuid, true);
    }

    /**
     * Remove a data permanently.
     *
     * @param uuid Player data
     * @param save Whether saving
     * @return Whether removed or not
     */
    public boolean remove(UUID uuid, boolean save) {
        if (isLoaded(uuid)) {
            unload(uuid, save);
        }

        return this.dataModifier.remove(uuid);
    }

    /**
     * Return whether specific data with this uuid is loaded.
     *
     * @param uuid Player uuid
     * @return Whether specific data with this uuid is loaded
     */
    public boolean isLoaded(UUID uuid) {
        return this.loadedData.containsKey(uuid);
    }

    /**
     * Return whether specific data with this uuid exists
     *
     * @param uuid Player uuid
     * @return Whether specific data with this uuid exists
     */
    public boolean exists(UUID uuid) {
        return this.dataModifier.exists(uuid);
    }

    /**
     * Save data without unloading.
     *
     * @param player Player
     * @return Whether saved successfully or not.
     */
    public boolean save(@NotNull Player player) {
        return save(player.getUniqueId());
    }

    /**
     * Save data without unloading.
     *
     * @param uuid Player uuid
     * @return Whether saved successfully or not.
     */
    public boolean save(UUID uuid) {
        var data = getPlayerData(uuid);
        return save(data);

    }

    /**
     * Save a data without unloading.
     *
     * @param data Data to save
     * @return Whether saved successfully or not
     */
    public boolean save(PlayerData data) {
        return this.dataModifier.save(data);
    }

    /**
     * Save all data
     */
    public void saveAll() {
        PlayerUtils.getOnlinePlayers().forEach(this::save);
    }

    /**
     * Get data with specific uuid.
     *
     * @param uuid Player uuid
     * @return Player data
     */
    @NotNull
    public PlayerData getPlayerData(@NotNull UUID uuid) {
        return this.loadedData.get(uuid);
    }

    /**
     * Get this player's data.
     *
     * @param player Player
     * @return Player data
     */
    @NotNull
    public PlayerData getPlayerData(@NotNull Player player) {
        return Objects.requireNonNull(getPlayerData(player.getUniqueId()), "null player data");
    }

    @Nullable
    public PlayerData getOfflineData(@NotNull String playerName) {
        //Fetch UUID
        return Optional.of(UUIDStorage.getUniqueID(playerName))
                //Load data
                .next(s -> getDataModifier().load(s))
                //Complete and return
                .ret(this::completeData);
    }

    /**
     * Get loaded data.
     *
     * @return Loaded data
     */
    @NotNull
    public Map<UUID, PlayerData> getLoadedData() {
        return Collections.unmodifiableMap(loadedData);
    }

    /**
     * Get the database in use. <p>
     * If the database is not used, null is returned.
     *
     * @return Database is use
     */
    @Nullable
    public SQLDatabase getDatabase() {
        return db;
    }

    /**
     * @return DataModifier
     */
    @NotNull
    public DataModifier<PlayerData> getDataModifier() {
        return dataModifier;
    }

    /**
     * Initialize DataModifier (For internal use only)
     */
    private void initDataModifier() {
        if (storageMethod == null)
            return;

        for (var dm : dataModifiers) {
            if (ArrayUtils.contains(dm.getStorageMethod(), storageMethod)) {
                this.dataModifier = dm;
            }
        }
    }

    /**
     * Initialize database (For internal use only)
     */
    private void initDb() {
        if (this.db != null) {
            this.db.close();
        }

        switch (this.storageMethod) {
            case MYSQL:
                var section = plugin.getConfiguration().getConfigurationSection("Data.MySQL");
                assert section != null;
                this.db = new MySQLDatabase(
                        section.getString("Host"),
                        section.getString("Port"),
                        section.getString("DataBase"),
                        section.getString("Username"),
                        section.getString("Password")
                );
                break;
            case SQLITE:
                var sqlite = plugin.getConfiguration().getConfigurationSection("Data.SQLite");
                assert sqlite != null;
                var file = new File(Objects.requireNonNull(sqlite.getString("File"))
                        .replace("{default}", plugin.getDataFolder().toString())
                        .replace("%default%", plugin.getDataFolder().toString()));
                this.db = new SQLiteDatabase(file,
                        sqlite.getString("Table"));
                break;
        }
    }

    /**
     * Complete player's data. </p>
     * Add absent LevelData and set owner.
     *
     * @param data PlayerData
     * @return Completed data
     */
    public PlayerData completeData(PlayerData data) {
        var levels = data.getLevels();
        //Put absent LevelData
        LevelManager.getInstance().getLoadedLevels().values().forEach(level -> {
            var name = level.getName();
            if (!levels.containsKey(name) || levels.get(name) == null) {
                levels.put(name, new LevelData(null, level));
            }
        });

        //Set LevelData's owner
        levels.forEach((name, d) -> {
            var level = plugin.getLevel(name);
            if (level != null) {
                d.setLevel(level);
                d.setPlayer(PlayerCache.getPlayer(data.getOwner()));
            }
        });

        return data;
    }

    private boolean createWithoutCheck(UUID uuid) {
        return this.dataModifier.create(uuid);
    }
}
