package top.shadowpixel.shadowlevels.object.listeners;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import lombok.var;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.jetbrains.annotations.NotNull;
import top.shadowpixel.shadowlevels.ShadowLevels;
import top.shadowpixel.shadowlevels.bungee.BungeeManager;
import top.shadowpixel.shadowlevels.level.LevelManager;

import java.util.UUID;

public class BungeeListener implements PluginMessageListener {

    private ShadowLevels plugin;

    public BungeeListener() {
        this.plugin = ShadowLevels.getInstance();
    }

    @Override
    public void onPluginMessageReceived(@NotNull String channel, @NotNull Player sb, byte[] message) {
        if (!channel.equalsIgnoreCase(BungeeManager.CHANNEL)) {
            return;
        }

        var input = ByteStreams.newDataInput(message);
        var action = input.readUTF().toLowerCase();
        var node = input.readUTF();
        var levelSystem = input.readUTF();
        var uuid = UUID.fromString(input.readUTF());

        switch (node.toLowerCase()) {
            case "levels":
                handleLevels(action, levelSystem, uuid, input);
                break;
            case "exps":
                handleExps(action, levelSystem, uuid, input);
                break;
            case "multiple":
                handleMultiple(levelSystem, uuid, input);
                break;
            case "reset":
                handleReset(levelSystem, uuid);
                break;
        }
    }

    @SuppressWarnings("DataFlowIssue")
    private void handleReset(String level, UUID uuid) {
        if (level.equals("*")) {
            LevelManager.getInstance().resetAll(Bukkit.getPlayer(uuid));
            return;
        }

        var data = plugin.getDataManager().getPlayerData(uuid).getLevelData(level);
        assert data != null;
        data.reset();
    }

    private void handleLevels(String action, String levelSystem, UUID uuid, ByteArrayDataInput input) {
        var data = plugin.getDataManager().getPlayerData(uuid).getLevelData(levelSystem);
        assert data != null;

        var amount = input.readInt();
        switch (action.toLowerCase()) {
            case "add":
                data.addLevels(amount);
                break;
            case "set":
                data.setLevels(amount);
                break;
            case "remove":
                data.removeLevels(amount);
                break;
        }
    }

    private void handleExps(String action, String levelSystem, UUID uuid, ByteArrayDataInput input) {
        var data = plugin.getDataManager().getPlayerData(uuid).getLevelData(levelSystem);
        assert data != null;

        var amount = input.readInt();
        switch (action.toLowerCase()) {
            case "add":
                data.addExps(amount);
                break;
            case "set":
                data.setExps(amount);
                break;
            case "remove":
                data.removeExps(amount);
                break;
        }
    }

    private void handleMultiple(String levelSystem, UUID uuid, ByteArrayDataInput input) {
        var data = plugin.getDataManager().getPlayerData(uuid).getLevelData(levelSystem);
        assert data != null;
        data.setMultiple(input.readFloat());
    }
}
