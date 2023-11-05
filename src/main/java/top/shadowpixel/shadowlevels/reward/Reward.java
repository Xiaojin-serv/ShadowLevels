package top.shadowpixel.shadowlevels.reward;

import lombok.Getter;
import lombok.var;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import top.shadowpixel.shadowcore.api.config.Configuration;
import top.shadowpixel.shadowcore.api.function.components.ExecutableEvent;
import top.shadowpixel.shadowcore.util.text.ColorUtils;
import top.shadowpixel.shadowlevels.object.enums.RewardStatus;
import top.shadowpixel.shadowlevels.util.ItemUtils;
import top.shadowpixel.shadowlevels.util.LocaleUtils;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

public class Reward {
    private final EnumMap<RewardStatus, ExecutableEvent> events = new EnumMap<>(RewardStatus.class);
    private final EnumMap<RewardStatus, ItemStack> items = new EnumMap<>(RewardStatus.class);

    @Getter
    private final List<String> permissions = new ArrayList<>();
    @Getter
    private final List<String> rewards = new ArrayList<>();
    @Getter
    private final String name;

    @Getter
    private final int level;
    @Getter
    private final int page;
    @Getter
    private final int slot;
    private final RewardList parent;

    public Reward(@NotNull RewardList parent, @NotNull Configuration section) {
        //Init
        this.parent = parent;
        this.name = section.getName();

        this.level = section.getInt("Level", 0);
        this.page = section.getInt("Page", 1);
        this.slot = section.getInt("Slot", 1);

        this.permissions.addAll(section.getStringList("Permissions"));
        this.rewards.addAll(ColorUtils.colorize(section.getStringList("Rewards")));

        //Load items
        loadItems(section);
    }

    public void loadItems(Configuration section) {
        for (RewardStatus value : RewardStatus.values()) {
            loadItem(value, section);

            if (value == RewardStatus.UNLOCKED) {
                events.put(RewardStatus.UNLOCKED, ExecutableEvent.of(section.getStringList("Events")));
            }
        }
    }

    @SuppressWarnings ("unused")
    @NotNull
    public List<String> getRewardLore() {
        return rewards;
    }

    @NotNull
    public ItemStack getNonPermissionsItem() {
        return getItem(RewardStatus.NO_PERMISSIONS);
    }

    @NotNull
    public ItemStack getLockedItem() {
        return getItem(RewardStatus.LOCKED);
    }

    @NotNull
    public ItemStack getUnlockedItem() {
        return getItem(RewardStatus.UNLOCKED);
    }

    @NotNull
    public ItemStack getReceivedItem() {
        return getItem(RewardStatus.RECEIVED);
    }

    @NotNull
    public ItemStack getItem(RewardStatus status) {
        return items.get(status);
    }

    public ExecutableEvent getUnlockedEvent() {
        return events.get(RewardStatus.UNLOCKED);
    }

    public ExecutableEvent getLockedEvent(Player player) {
        return getEvent(player, RewardStatus.LOCKED);
    }

    public ExecutableEvent getNoPermEvent(Player player) {
        return getEvent(player, RewardStatus.NO_PERMISSIONS);
    }

    public ExecutableEvent getReceivedEvent(Player player) {
        return getEvent(player, RewardStatus.RECEIVED);
    }

    public ExecutableEvent getEvent(Player player, RewardStatus status) {
        return events.getOrDefault(status, RewardManager.defaultEvents.get(LocaleUtils.getLocale(player).getName()).get(status));
    }

    private void loadItem(RewardStatus status, Configuration section) {
        Configuration customs = section.getConfigurationSection("Custom-Items"), item;

        var name = status.getName();
        if (customs != null && (item = customs.getConfigurationSection(name)) != null) {
            items.put(status, ItemUtils.builder().fromConfigSection(item).build());
            events.put(status, ExecutableEvent.of(item.getStringList("Events")));
        } else if ((item = parent.getConfiguration().getConfigurationSection("Custom-Items." + status.getName())) != null) {
            items.put(status, ItemUtils.builder().fromConfigSection(item).build());
            events.put(status, ExecutableEvent.of(item.getStringList("Events")));
        } else {
            items.put(status, RewardManager.defaultItems.get(status.getName()));
        }
    }
}
