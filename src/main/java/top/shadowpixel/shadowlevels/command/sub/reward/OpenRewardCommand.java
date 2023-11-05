package top.shadowpixel.shadowlevels.command.sub.reward;

import lombok.var;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.shadowpixel.shadowcore.api.command_v2.CommandContext;
import top.shadowpixel.shadowcore.api.command_v2.SubCommand;
import top.shadowpixel.shadowlevels.reward.RewardManager;
import top.shadowpixel.shadowlevels.util.CommandUtils;
import top.shadowpixel.shadowlevels.util.LocaleUtils;

import java.util.Collection;

/* /sl OpenReward <player> <reward> */
public class OpenRewardCommand extends SubCommand {
    public OpenRewardCommand() {
        super("OpenReward");
        super.permissions.add("ShadowLevels.Commands.OpenReward");
    }

    @Override
    public boolean execute(@NotNull CommandContext ctx) {
        var arguments = ctx.arguments();
        var player = arguments[1].getOnlinePlayer();
        var rl = CommandUtils.getRewardList(arguments[2]);

        RewardManager.getInstance().getRewardMenu(player, rl).openMenu();
        if (arguments.length > 4 && !arguments[3].getValue().equalsIgnoreCase("true")) {
            return true;
        }

        LocaleUtils.sendCmdMessage(ctx.sender(), "Success.Open-Reward");
        return true;
    }

    @Override
    public @Nullable Collection<String> complete(@NotNull CommandContext ctx) {
        switch (ctx.arguments().length) {
            case 2:
                return ONLINE_PLAYERS_LIST;
            case 3:
                return RewardManager.getInstance().getLoadedRewards().keySet();
        }

        return EMPTY_LIST;
    }
}
