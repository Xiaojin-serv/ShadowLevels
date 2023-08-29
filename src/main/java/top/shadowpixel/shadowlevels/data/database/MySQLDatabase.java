package top.shadowpixel.shadowlevels.data.database;

import lombok.Cleanup;
import lombok.var;
import top.shadowpixel.shadowcore.api.database.mysql.SimpleMySQLDatabase;
import top.shadowpixel.shadowlevels.ShadowLevels;
import top.shadowpixel.shadowlevels.util.Logger;

import java.sql.SQLException;

public class MySQLDatabase extends SimpleMySQLDatabase {

    public MySQLDatabase(String host, String port, String database, String user, String password) {
        super(ShadowLevels.getInstance(), host, port, database, user, password);
    }

    @Override
    public void doAfterConnecting() {
        var table = ((ShadowLevels) plugin).getConfiguration().getString("Data.MySQL.Table");

        try {
            @Cleanup var stmt = getConnection().createStatement();
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS " + table + "  (UUID VARCHAR(50) PRIMARY KEY , Data TEXT)");
        } catch (SQLException e) {
            Logger.error("An error occurred while creating table", e);
        }

        ((ShadowLevels) plugin).getDataManager().loadOnline();
    }
}
