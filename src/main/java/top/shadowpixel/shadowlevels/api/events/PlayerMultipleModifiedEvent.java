package top.shadowpixel.shadowlevels.api.events;

import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import top.shadowpixel.shadowlevels.level.Level;
import top.shadowpixel.shadowlevels.object.enums.ModificationType;

@Getter
public class PlayerMultipleModifiedEvent extends AbstractPlayerEvent {

    private static final HandlerList handlerList = new HandlerList();
    private final        Level       levelSystem;
    @Getter
    private final        Float       oldMultiple, value;
    @Getter
    private final ModificationType modificationType = ModificationType.SET;

    public PlayerMultipleModifiedEvent(@NotNull Player who, Level levelSystem, Float oldMultiple, Float value) {
        super(who);
        this.levelSystem = levelSystem;
        this.oldMultiple = oldMultiple;
        this.value = value;
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
