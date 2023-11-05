package top.shadowpixel.shadowlevels.api.events;

import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import top.shadowpixel.shadowlevels.level.Level;

@Getter
public class AfterPlayerDataResetEvent extends AbstractPlayerEvent {
    private static final HandlerList handlerList = new HandlerList();

    @Getter
    private final Level levelSystem;

    public AfterPlayerDataResetEvent(@NotNull Player who, @NotNull Level levelSystem) {
        super(who);
        this.levelSystem = levelSystem;
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }
}
