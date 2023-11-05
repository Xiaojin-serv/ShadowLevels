package top.shadowpixel.shadowlevels.command.sub.reward;

import lombok.var;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.shadowpixel.shadowcore.api.command_v2.CommandContext;
import top.shadowpixel.shadowcore.api.command_v2.SubCommand;
import top.shadowpixel.shadowcore.object.enums.SenderType;
import top.shadowpixel.shadowlevels.reward.RewardManager;
import top.shadowpixel.shadowlevels.util.CommandUtils;

import java.util.Collection;

public class RewardCommand extends SubCommand {
    public RewardCommand() {
        super("Reward");
        setSenderType(SenderType.PLAYER);
    }

    @Override
    public boolean execute(@NotNull CommandContext ctx) {
        var rl = CommandUtils.getRewardList(ctx.arguments()[1]);
        var player = (Player) ctx.sender();
        RewardManager.getInstance().getRewardMenu(player, rl).openMenu(player);
        return true;
    }

    @Override
    public @Nullable Collection<String> complete(@NotNull CommandContext ctx) {
        if (ctx.arguments().length == 2) {
            return RewardManager.getInstance().getLoadedRewards().keySet();
        }

        return EMPTY_LIST;
    }
}