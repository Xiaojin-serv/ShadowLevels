package top.shadowpixel.shadowlevels.api.events;

import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import top.shadowpixel.shadowlevels.level.Level;
import top.shadowpixel.shadowlevels.object.enums.ModificationType;

@Getter
public class PlayerLevelsModifiedEvent extends AbstractPlayerEvent {
    private static final HandlerList handlerList = new HandlerList();

    private final Integer oldLevels, value;
    @Getter
    private final Level levelSystem;
    @Getter
    private final ModificationType modificationType;

    public PlayerLevelsModifiedEvent(@NotNull Player player,
                                     @NotNull Integer oldLevels,
                                     @NotNull Integer value,
                                     @NotNull Level levelSystem,
                                     @NotNull ModificationType modificationType) {
        super(player);
        this.oldLevels = oldLevels;
        this.value = value;
        this.levelSystem = levelSystem;
        this.modificationType = modificationType;
    }

    @NotNull
    public static HandlerList getHandlerList() {
        return handlerList;
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }
}
