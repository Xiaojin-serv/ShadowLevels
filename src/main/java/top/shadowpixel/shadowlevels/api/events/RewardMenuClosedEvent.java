package top.shadowpixel.shadowlevels.api.events;

import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import top.shadowpixel.shadowlevels.level.Level;
import top.shadowpixel.shadowlevels.reward.RewardMenu;

@Getter
public class RewardMenuClosedEvent extends AbstractPlayerEvent {
    private static final HandlerList handlerList = new HandlerList();
    private final Level level;
    private final RewardMenu rewardMenu;

    public RewardMenuClosedEvent(@NotNull Player player, @NotNull Level level, @NotNull RewardMenu rewardMenu) {
        super(player);
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
