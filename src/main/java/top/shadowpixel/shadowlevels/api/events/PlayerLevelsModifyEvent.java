package top.shadowpixel.shadowlevels.api.events;

import lombok.Getter;
import lombok.ToString;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import top.shadowpixel.shadowlevels.level.Level;
import top.shadowpixel.shadowlevels.object.enums.ModificationType;

@Getter
@ToString
public class PlayerLevelsModifyEvent extends AbstractPlayerEvent implements Cancellable {
    private static final HandlerList handlerList = new HandlerList();

    private final Integer oldLevels, value;
    @Getter
    private final Level levelSystem;
    @Getter
    private final ModificationType modificationType;
    private Boolean cancel = false;

    public PlayerLevelsModifyEvent(@NotNull Player who, @NotNull Integer old, @NotNull Integer value, @NotNull Level level, @NotNull ModificationType modificationType) {
        super(who);
        this.oldLevels = old;
        this.value = value;
        this.levelSystem = level;
        this.modificationType = modificationType;
    }

    @NotNull
    public static HandlerList getHandlerList() {
        return handlerList;
    }

    /**
     * Gets the cancellation state of this event. A cancelled event will not
     * be executed in the server, but will still pass to other plugins
     *
     * @return true if this event is cancelled
     */
    @Override
    public boolean isCancelled() {
        return cancel;
    }

    /**
     * Sets the cancellation state of this event. A cancelled event will not
     * be executed in the server, but will still pass to other plugins.
     *
     * @param cancel true if you wish to cancel this event
     */
    @Override
    public void setCancelled(boolean cancel) {
        this.cancel = cancel;
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }
}
