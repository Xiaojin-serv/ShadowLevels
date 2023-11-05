package top.shadowpixel.shadowlevels.data;

import lombok.ToString;
import lombok.var;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.shadowpixel.shadowcore.util.collection.MapUtils;
import top.shadowpixel.shadowlevels.level.LevelData;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@SerializableAs("ShadowLevels-PlayerData")
@ToString
public class PlayerData implements ConfigurationSerializable {
    private UUID owner;
    private Map<String, LevelData> levelData = new HashMap<>();

    public PlayerData(UUID owner) {
        this.owner = owner;
    }

    @SuppressWarnings({"unchecked", "unused"})
    public PlayerData(Map<String, Object> map) {
        if (map.containsKey("Levels")) {
            this.levelData = (Map<String, LevelData>) map.get("Levels");
        }
    }

    @NotNull
    public UUID getOwner() {
        return owner;
    }

    public void setOwner(UUID owner) {
        if (this.owner == null) {
            this.owner = owner;
        }
    }

    @Nullable
    public LevelData getLevelData(String name) {
        return MapUtils.smartMatch(name, levelData);
    }

    public Map<String, LevelData> getLevels() {
        return levelData;
    }

    @NotNull
    @Override
    public Map<String, Object> serialize() {
        var dataMap = new HashMap<String, Object>();
        dataMap.put("Levels", this.levelData);
        return dataMap;
    }
}
