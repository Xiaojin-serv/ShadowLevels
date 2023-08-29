package top.shadowpixel.shadowlevels.data.modifiers;

import lombok.var;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.shadowpixel.shadowcore.api.config.Configuration;
import top.shadowpixel.shadowcore.api.config.ConfigurationProvider;
import top.shadowpixel.shadowcore.object.enums.StorageMethod;
import top.shadowpixel.shadowlevels.data.DataManager;
import top.shadowpixel.shadowlevels.data.PlayerData;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.util.Objects;
import java.util.UUID;

public class YamlDataModifier extends AbstractDataModifier {

    public static final StorageMethod[] STORAGE_METHODS = {StorageMethod.YAML};

    @Override
    public @Nullable PlayerData load(@NotNull UUID uuid, @NotNull File file) {
        Configuration config;
        try {
            config = Objects.requireNonNull(ConfigurationProvider.getProvider("Yaml")).load(file);
        } catch (IOException e) {
            return null;
        }

        var data = load(config);
        if (data == null) {
            return null;
        }

        data.setOwner(uuid);
        return data;
    }

    @Override
    public @Nullable PlayerData load(@NotNull UUID uuid, Reader reader) {
        Configuration config;
        try {
            config = Objects.requireNonNull(ConfigurationProvider.getProvider("Yaml")).load(reader);
        } catch (IOException e) {
            return null;
        }

        return Objects.requireNonNull(load(config));
    }

    @Override
    public @Nullable PlayerData load(@NotNull UUID uuid) {
        return load(uuid, getDataFile(uuid));
    }

    @Override
    public @NotNull File getDataFile(@NotNull UUID uuid) {
        return new File(DataManager.getInstance().getStorageFile(), uuid + ".yml");
    }

    @Override
    public StorageMethod[] getStorageMethod() {
        return STORAGE_METHODS;
    }

    @Override
    public boolean remove(@NotNull UUID uuid) {
        return getDataFile(uuid).delete();
    }

    @Override
    public boolean exists(@NotNull UUID uuid) {
        return getDataFile(uuid).exists();
    }

    @Override
    public boolean create(@NotNull UUID uuid) {
        return create(uuid, new PlayerData(uuid));
    }

    @Override
    public boolean create(@NotNull UUID uuid, @NotNull PlayerData data) {
        var config = new Configuration();
        config.set("PlayerData", data);

        try {
            Objects.requireNonNull(ConfigurationProvider.getProvider("Yaml")).save(config, getDataFile(uuid));
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    @Override
    public boolean save(@NotNull PlayerData data) {
        try {
            var config = new Configuration();
            config.set("PlayerData", data);
            Objects.requireNonNull(ConfigurationProvider.getProvider("Yaml")).save(config, getDataFile(data.getOwner()));
            return true;
        } catch (Throwable e) {
            return false;
        }
    }

    private @Nullable PlayerData load(Configuration c) {
        try {
            return (PlayerData) c.get("PlayerData");
        } catch (Throwable e) {
            return null;
        }
    }
}
