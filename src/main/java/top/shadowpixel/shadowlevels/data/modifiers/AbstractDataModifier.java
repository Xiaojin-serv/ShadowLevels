package top.shadowpixel.shadowlevels.data.modifiers;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.shadowpixel.shadowcore.object.enums.StorageMethod;
import top.shadowpixel.shadowcore.object.interfaces.DataModifier;
import top.shadowpixel.shadowlevels.ShadowLevels;
import top.shadowpixel.shadowlevels.data.PlayerData;

import java.io.File;
import java.io.InputStream;
import java.io.Reader;
import java.util.UUID;

public abstract class AbstractDataModifier implements DataModifier<PlayerData> {

    protected final ShadowLevels plugin;

    public AbstractDataModifier() {
        this.plugin = ShadowLevels.getInstance();
    }

    public @Nullable PlayerData load(@NotNull UUID uuid, @NotNull File file) {
        throw new UnsupportedOperationException();
    }

    public @Nullable PlayerData load(@NotNull UUID uuid, Reader reader) {
        throw new UnsupportedOperationException();
    }

    public @Nullable PlayerData load(@NotNull UUID uuid) {
        throw new UnsupportedOperationException();
    }

    public @NotNull File getDataFile(@NotNull UUID uuid) {
        throw new UnsupportedOperationException();
    }

    public StorageMethod[] getStorageMethod() {
        throw new UnsupportedOperationException();
    }

    public boolean remove(@NotNull UUID uuid) {
        throw new UnsupportedOperationException();
    }

    public boolean create(@NotNull UUID uuid) {
        throw new UnsupportedOperationException();
    }

    public boolean create(@NotNull UUID uuid, @NotNull PlayerData data) {
        throw new UnsupportedOperationException();
    }

    public boolean save(@NotNull PlayerData data) {
        throw new UnsupportedOperationException();
    }

    public boolean exists(@NotNull UUID uuid) {
        throw new UnsupportedOperationException();
    }

    public @Nullable PlayerData load(@NotNull UUID uuid, InputStream is) {
        throw new UnsupportedOperationException();
    }
}
