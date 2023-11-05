package top.shadowpixel.shadowlevels.command.sub.reward;

import lombok.var;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.shadowpixel.shadowcore.api.command_v2.CommandContext;
import top.shadowpixel.shadowcore.api.command_v2.SubCommand;
import top.shadowpixel.shadowcore.object.enums.SenderType;
import top.shadowpixel.shadowlevels.level.LevelManager;
import top.shadowpixel.shadowlevels.reward.RewardManager;
import top.shadowpixel.shadowlevels.util.CommandUtils;

import java.util.Collection;

/* /sl CreateReward <level> <reward> */
public class CreateRewardCommand extends SubCommand {
    public CreateRewardCommand() {
        super("CreateReward");
        identifiers.add("cr");
        setSenderType(SenderType.CONSOLE);
    }

    @Override
    public boolean execute(@NotNull CommandContext ctx) {
        var arguments = ctx.arguments();
        var level = CommandUtils.getLevel(arguments[1]);
        var name = arguments[2].getValue();
        RewardManager.getInstance().create(name, level);
        return true;
    }

    @Override
    public @Nullable Collection<String> complete(@NotNull CommandContext ctx) {
        if (ctx.arguments().length == 2) {
            return LevelManager.getInstance().getLoadedLevels().keySet();
        }

        return EMPTY_LIST;
    }
}
