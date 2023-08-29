package top.shadowpixel.shadowlevels.reward;

import lombok.var;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryEvent;
import top.shadowpixel.shadowcore.api.function.EventExecutor;
import top.shadowpixel.shadowcore.api.function.components.ExecutableEvent;
import top.shadowpixel.shadowcore.api.menu.components.MenuItem;
import top.shadowpixel.shadowcore.api.menu.components.MenuPage;
import top.shadowpixel.shadowcore.api.menu.impl.PlayerMenu;
import top.shadowpixel.shadowcore.util.entity.SenderUtils;
import top.shadowpixel.shadowcore.util.text.ReplaceUtils;
import top.shadowpixel.shadowlevels.ShadowLevels;
import top.shadowpixel.shadowlevels.api.events.PlayerClaimedRewardEvent;
import top.shadowpixel.shadowlevels.api.events.RewardMenuClosedEvent;
import top.shadowpixel.shadowlevels.api.events.RewardMenuOpenedEvent;
import top.shadowpixel.shadowlevels.data.DataManager;
import top.shadowpixel.shadowlevels.level.Level;
import top.shadowpixel.shadowlevels.util.ItemUtils;
import top.shadowpixel.shadowlevels.util.event.EventUtils;

public class RewardMenu extends PlayerMenu {

    private static final ShadowLevels plugin = ShadowLevels.getInstance();

    private final Player     player;
    private final RewardList rewardList;

    public RewardMenu(Player player, RewardList rewardList) {
        super(player, ReplaceUtils.coloredReplace(rewardList.getTitle(), player), rewardList.getName());
        this.player = player;
        this.rewardList = rewardList;

        var section = rewardList.getConfiguration();
        var size = section.getInt("Size", 54);

        //Add reward items
        for (Reward reward : rewardList.getRewards()) {
            if (!hasPage(reward.getPage())) {
                addPage(
                        reward.getPage(),
                        ReplaceUtils.coloredReplace(section.getString("Title"), player, "%page%", String.valueOf(reward.getPage())),
                        size
                );
            }

            getPage(reward.getPage()).setItem(reward.getSlot(), getRewardItem(rewardList.getLevel(), reward));
        }

        var slotSection = section.getConfigurationSection("Item-Slots");
        assert slotSection != null;

        //Add custom items
        var customItemsSection = section.getConfigurationSection("Custom-Items");
        assert customItemsSection != null;

        customItemsSection.getKeys().forEach(name -> {
            var itemSection = customItemsSection.getConfigurationSection(name);
            var item = MenuItem.of(ItemUtils.builder(player, itemSection).build());
            assert itemSection != null;
            var event = ExecutableEvent.of(itemSection.getStringList("Events"));
            item.addClickAction((a, b) -> EventExecutor.execute(plugin, player, event));
            slotSection.getIntegerList(name)
                    .forEach(slot -> getPages().keySet().forEach(page -> setItem(page, slot, item)));
        });

        for (int page : getPages().keySet()) {
            if (page != getPages().size()) {
                var item = getItem("Next-Page");
                item.addClickAction((menu, event) -> changePage(getCurrentPage() + 1));
                slotSection.getIntegerList("Next-Page").forEach(slot -> setItem(page, slot, item));
            }

            if (page > 1) {
                var item = getItem("Previous-Page");
                item.addClickAction((menu, event) -> changePage(getCurrentPage() - 1));
                slotSection.getIntegerList("Previous-Page").forEach(slot -> setItem(page, slot, item));
            }
        }

        for (MenuPage page : getPages().values()) {
            for (int i = 0; i < page.size(); i++) {
                page.getProperty().setUnclickable(i);
            }
        }
    }

    public static RewardMenu createMenu(Player player, RewardList rewardList) {
        var menu = new RewardMenu(player, rewardList);
        //noinspection DataFlowIssue
        plugin.getMenuHandler("Reward")
                .addMenu(player.getUniqueId().toString(), menu);
        return menu;
    }

    /**
     * @param level  Level system
     * @param reward Reward
     * @param type   Order number (1 -> Unlocked, 2 -> Locked, 3 -> Received, 4 -> No Permissions)
     * @return Item
     */
    @SuppressWarnings("SuspiciousListRemoveInLoop")
    public MenuItem getRewardItem(Level level, Reward reward, int type) {
        MenuItem item = null;
        var data = level.getLevelData(player);
        switch (type) {
            case 1:
                item = MenuItem.of(reward.getUnlockedItem());
                item.addClickAction((menu, event) -> {
                    data.addReceivedReward(rewardList, reward.getName());
                    setItem(event.getSlot(), getRewardItem(level, reward, 3));
                    EventExecutor.execute(ShadowLevels.getInstance(), player, reward.getUnlockedEvent());
                    EventUtils.call(new PlayerClaimedRewardEvent(player, level, reward));
                });
                break;
            case 2:
                item = MenuItem.of(reward.getLockedItem());
                item.addClickAction((menu, event) ->
                        EventExecutor.execute(ShadowLevels.getInstance(), player, reward.getLockedEvent(player)));
                break;
            case 3:
                item = MenuItem.of(reward.getReceivedItem());
                item.addClickAction((menu, event) ->
                        EventExecutor.execute(ShadowLevels.getInstance(), player, reward.getReceivedEvent(player)));
                break;
            case 4:
                item = MenuItem.of(reward.getNoPermissionsItem());
                item.addClickAction((menu, event) ->
                        EventExecutor.execute(ShadowLevels.getInstance(), player, reward.getNoPermEvent(player)));
                break;
        }

        assert item != null;

        var meta = item.getItemMeta();
        assert meta != null;

        var lore = meta.getLore();
        assert lore != null;

        for (int i = 0; i < lore.size(); i++) {
            var line = lore.get(i);
            if (line.contains("%reward%") || line.contains("%rewards%")) {
                lore.remove(i);
                lore.addAll(i, reward.getRewards());
            }
        }

        meta.setLore(ReplaceUtils.coloredReplace(lore, player));
        meta.setDisplayName(meta.getDisplayName().replace("%level%", String.valueOf(reward.getLevel())));
        item.setItemMeta(meta);
        return item.clone();
    }

    private MenuItem getRewardItem(Level level, Reward reward) {
        MenuItem item;
        var data = level.getLevelData(player);
        if (data.hasReceived(rewardList, reward)) {
            item = getRewardItem(level, reward, 3);
        } else if (!SenderUtils.hasPermissions(player, reward.getPermissions())) {
            item = getRewardItem(level, reward, 4);
        } else if (data.getLevels() < reward.getLevel()) {
            item = getRewardItem(level, reward, 2);
        } else {
            item = getRewardItem(level, reward, 1);
        }

        return item;
    }

    public void refreshItems() {
        var data = rewardList.getLevel().getLevelData(player);
        for (Reward reward : rewardList.getRewards()) {
            if (data.hasReceived(rewardList, reward)) {
                continue;
            }

            getPage(reward.getPage()).setItem(reward.getSlot(), getRewardItem(rewardList.getLevel(), reward));
        }
    }

    @Override
    public void openMenu() {
        super.openMenu();
        this.checkPerms();
        EventUtils.call(new RewardMenuOpenedEvent(player, this.rewardList.getLevel(), this));
    }

    @Override
    public void handleEvent(InventoryEvent event) {
        super.handleEvent(event);

        //Save data
        if (event instanceof InventoryCloseEvent) {
            DataManager.getInstance().save((Player) ((InventoryCloseEvent) event).getPlayer());
            EventUtils.call(new RewardMenuClosedEvent(player, this.rewardList.getLevel(), this));
        }
    }

    private void checkPerms() {

    }

    private MenuItem getItem(String path) {
        return MenuItem.of(ItemUtils.builder(player,
                rewardList.getConfiguration().contains("Custom-Items." + path) ?
                rewardList.getConfiguration().getConfigurationSection("Custom-Items." + path) :
                plugin.getConfiguration("Items").getConfigurationSection("Items." + path)).build());
    }
}
