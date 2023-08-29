package top.shadowpixel.shadowlevels.api.events;

import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import top.shadowpixel.shadowlevels.level.Level;

@Getter
public class PlayerLevelUpEvent extends AbstractPlayerEvent {

    private static final HandlerList handlerList = new HandlerList();
    private final        Level       levelSystem;

    public PlayerLevelUpEvent(@NotNull Player who, Level level) {
        super(who);
        this.levelSystem = level;
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
