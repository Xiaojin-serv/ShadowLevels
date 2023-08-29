package top.shadowpixel.shadowlevels.api.events;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

abstract class AbstractPlayerEvent extends PlayerEvent {

    public AbstractPlayerEvent(@NotNull Player who) {
        super(who);
    }
}
