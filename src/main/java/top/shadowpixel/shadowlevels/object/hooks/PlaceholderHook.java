package top.shadowpixel.shadowlevels.object.hooks;

import lombok.val;
import lombok.var;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.shadowpixel.shadowcore.api.util.Optional;
import top.shadowpixel.shadowlevels.ShadowLevels;
import top.shadowpixel.shadowlevels.api.ShadowLevelsAPI;
import top.shadowpixel.shadowlevels.reward.RewardManager;

import java.util.Collection;

public class PlaceholderHook extends PlaceholderExpansion {

    private final ShadowLevels plugin;

    public PlaceholderHook(ShadowLevels plugin) {
        this.plugin = plugin;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "ShadowLevels";
    }

    @Override
    public @NotNull String getAuthor() {
        return "XiaoJin_awa_";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0";
    }

    @Override
    public @Nullable String onPlaceholderRequest(Player player, @NotNull String params) {
        val arguments = params.split("_");
        if (player == null || arguments.length < 2) {
            return null;
        }

        val data = Optional.of(ShadowLevels.getPlayerData(player))
                .next(t -> t.getLevelData(arguments[0]))
                .getOrElse(null);
        var level = ShadowLevelsAPI.getLevel(arguments[0]);
        if (data == null || data.getLevel() == null || level == null) {
            return null;
        }

        switch (arguments[1].toLowerCase()) {
            case "exp":
            case "exps":
                return String.valueOf(data.getExps());
            case "total-exp":
            case "total-exps":
                return String.valueOf(data.getTotalExps());
            case "level":
            case "levels":
                return String.valueOf(data.getLevels());
            case "max-level":
            case "max-levels":
                return String.valueOf(data.getLevel().getMaxLevels(player));
            case "next-level":
            case "next-levels":
                return String.valueOf(data.getLevels() + (data.isMax() ? 0 : 1));
            case "required-exp":
            case "required-exps":
                return String.valueOf(data.getRequiredExps());
            case "ismax":
                return String.valueOf(data.isMax());
            case "percentage":
                var per = String.valueOf(data.getPercentage());
                return per.substring(0, per.contains(".") ? per.indexOf(".") + 2 : per.length());
            case "multiple":
            case "multiples":
                return String.valueOf(data.getMultiple());
            case "color":
                return data.getColor();
            case "reward":
            case "rewards":
                if (arguments.length == 2) {
                    var rewards = RewardManager.getInstance().getRewardLists(level);
                    return String.valueOf(rewards.stream().mapToInt(s -> s.getRewards().size()).sum());
                }

                var reward = RewardManager.getInstance().getRewardList(arguments[2]);
                if (reward == null) {
                    return "0";
                }

                return String.valueOf(reward.getRewards().size());
            case "received":
            case "received-reward":
            case "received-rewards":
            case "received-reward-count":
            case "received-rewards-count":
                if (arguments.length == 2) {
                    return String.valueOf(data.getReceivedRewards().values().stream().mapToLong(Collection::size).sum());
                }

                var rewards = data.getReceivedRewards(plugin.getRewardList(arguments[2]));
                if (arguments.length < 3 || plugin.getRewardList(arguments[2]) == null || rewards == null) {
                    return 0 + "";
                }

                return String.valueOf(rewards.size());
            case "progress":
            case "progressbar":
                if (arguments.length > 2) {
                    try {
                        return data.getProgressBar(Integer.parseInt(arguments[2]));
                    } catch (Exception ignore) {
                    }
                }

                return data.getProgressBar();
        }

        return null;
    }
}
