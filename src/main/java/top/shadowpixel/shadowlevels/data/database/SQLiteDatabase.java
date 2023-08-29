package top.shadowpixel.shadowlevels.data.database;

import lombok.Cleanup;
import lombok.var;
import top.shadowpixel.shadowcore.api.database.sqlite.SimpleSQLiteDatabase;
import top.shadowpixel.shadowlevels.ShadowLevels;
import top.shadowpixel.shadowlevels.util.Logger;

import java.io.File;
import java.sql.SQLException;

public class SQLiteDatabase extends SimpleSQLiteDatabase {

    public SQLiteDatabase(File file, String table) {
        super(ShadowLevels.getInstance(), file, table);
    }

    @Override
    public void doAfterConnecting() {
        var table = ((ShadowLevels) plugin).getConfiguration().getString("Data.SQLite.Table");

        try {
            @Cleanup var stmt = getConnection().createStatement();
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS " + table + "  (UUID VARCHAR(50) PRIMARY KEY , Data TEXT)");
        } catch (SQLException e) {
            Logger.error("An error occurred while creating table", e);
        }

        ((ShadowLevels) plugin).getDataManager().loadOnline();
    }
}
