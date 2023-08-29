package top.shadowpixel.shadowlevels.object.hooks;

import dev.lone.itemsadder.api.CustomStack;
import lombok.var;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

public class ItemsAdderHook {

    @Nullable
    public static CustomStack getStack(String namespace) {
        if (!Bukkit.getPluginManager().isPluginEnabled("ItemsAdder")) {
            return null;
        }

        return CustomStack.getInstance(namespace);
    }

    public static @Nullable ItemStack getItemStack(String namespace) {
        var stack = getStack(namespace);
        if (stack != null) {
            return stack.getItemStack();
        }

        return null;
    }
}
