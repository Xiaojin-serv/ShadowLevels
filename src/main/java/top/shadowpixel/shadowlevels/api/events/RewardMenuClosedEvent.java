package top.shadowpixel.shadowlevels.api.events;

import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import top.shadowpixel.shadowlevels.level.Level;
import top.shadowpixel.shadowlevels.reward.RewardMenu;

@Getter
public class RewardMenuClosedEvent extends Event {
    private static final HandlerList handlerList = new HandlerList();

    private final Player     player;
    private final Level      level;
    private final RewardMenu rewardMenu;

    public RewardMenuClosedEvent(Player player, Level level, RewardMenu rewardMenu) {
        this.player = player;
        this.level = level;
        this.rewardMenu = rewardMenu;
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }
}
