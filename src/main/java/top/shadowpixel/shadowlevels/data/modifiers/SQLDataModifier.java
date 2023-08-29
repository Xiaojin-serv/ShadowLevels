package top.shadowpixel.shadowlevels.data.modifiers;

import lombok.Cleanup;
import lombok.var;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.shadowpixel.shadowcore.api.config.Serializations;
import top.shadowpixel.shadowcore.api.database.SQLDatabase;
import top.shadowpixel.shadowcore.object.enums.StorageMethod;
import top.shadowpixel.shadowcore.util.sql.SQLUtils;
import top.shadowpixel.shadowlevels.data.DataManager;
import top.shadowpixel.shadowlevels.data.PlayerData;
import top.shadowpixel.shadowlevels.util.Logger;

import java.util.UUID;

public class SQLDataModifier extends AbstractDataModifier {

    private static final StorageMethod[] STORAGE_METHODS = {StorageMethod.MYSQL, StorageMethod.SQLITE};

    @Override
    public @Nullable PlayerData load(@NotNull UUID uuid) {
        try {
            //Load from SQL
            @Cleanup var pstmt = SQLUtils.prepareStmt(getDb().getConnection(),
                    "SELECT * FROM " + getTable() + " WHERE UUID=?",
                    uuid.toString());
            @Cleanup var resultSet = pstmt.executeQuery();
            if (!resultSet.next()) {
                return null;
            }

            //Set UUID
            PlayerData data = Serializations.deserializeJson(resultSet.getString("Data"), PlayerData.class);
            data.setOwner(uuid);
            return data;
        } catch (Throwable e) {
            Logger.error("An error occurred while loading data for " + uuid);
            return null;
        }
    }

    @Override
    public StorageMethod[] getStorageMethod() {
        return STORAGE_METHODS;
    }

    @Override
    public boolean remove(@NotNull UUID uuid) {
        try {
            @Cleanup var ps = SQLUtils.prepareStmt(getDb().getConnection(),
                    "DELETE FROM " + getTable() + " WHERE UUID=?",
                    uuid.toString());
            return ps.executeUpdate() > 0;
        } catch (Throwable e) {
            Logger.error("An error occurred while deleting data for " + uuid, e);
            return false;
        }
    }

    @Override
    public boolean create(@NotNull UUID uuid) {
        return create(uuid, new PlayerData(uuid));
    }

    @Override
    public boolean create(@NotNull UUID uuid, @NotNull PlayerData data) {
        try {
            return SQLUtils.executeUpdate(getDb().getConnection(),
                    "INSERT INTO " + getTable() + " VALUES (?, ?)",
                    uuid.toString(), "") > 0;
        } catch (Throwable e) {
            Logger.error("An error occurred while creating data.", e);
            return false;
        }
    }

    @Override
    public boolean save(@NotNull PlayerData data) {
        try {
            SQLUtils.executeUpdate(getDb().getConnection(), "UPDATE " + getTable() + " SET Data=? WHERE UUID=?",
                    Serializations.serializeJson(data),
                    data.getOwner().toString());
            return true;
        } catch (Throwable e) {
            Logger.error("An error occurred while saving data for " + data.getOwner(), e);
            return false;
        }
    }

    @Override
    public boolean exists(@NotNull UUID uuid) {
        try {
            @Cleanup var pstmt = SQLUtils.prepareStmt(getDb().getConnection(),
                    "SELECT * FROM " + getTable() + " WHERE UUID=?",
                    uuid.toString());
            @Cleanup var set = pstmt.executeQuery();
            return set.next();
        } catch (Throwable e) {
            Logger.error("An error occurred while querying from SQL", e);
            return false;
        }
    }

    @Nullable
    private String getTable() {
        switch (DataManager.getInstance().getStorageMethod()) {
            case MYSQL:
                return plugin.getConfiguration().getString("Data.MySQL.Table");
            case SQLITE:
                return plugin.getConfiguration().getString("Data.SQLite.Table");
        }

        return null;
    }

    private SQLDatabase getDb() {
        return DataManager.getInstance().getDatabase();
    }
}
