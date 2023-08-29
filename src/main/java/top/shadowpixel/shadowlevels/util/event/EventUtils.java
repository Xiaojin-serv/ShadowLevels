package top.shadowpixel.shadowlevels.util.event;

import org.bukkit.Bukkit;
import org.bukkit.event.Event;

public class EventUtils {

    private EventUtils() {
        throw new UnsupportedOperationException();
    }

    public static void call(Event event) {
        Bukkit.getPluginManager().callEvent(event);
    }
}
