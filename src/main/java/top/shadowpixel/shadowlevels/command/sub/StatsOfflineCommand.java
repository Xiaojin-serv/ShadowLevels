package top.shadowpixel.shadowlevels.command.sub;

import lombok.var;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.shadowpixel.shadowcore.api.command_v2.CommandContext;
import top.shadowpixel.shadowcore.api.command_v2.SubCommand;
import top.shadowpixel.shadowcore.util.collection.ListUtils;
import top.shadowpixel.shadowcore.util.entity.PlayerUtils;
import top.shadowpixel.shadowcore.util.entity.SenderUtils;
import top.shadowpixel.shadowlevels.ShadowLevels;
import top.shadowpixel.shadowlevels.api.ShadowLevelsAPI;
import top.shadowpixel.shadowlevels.level.LevelData;
import top.shadowpixel.shadowlevels.util.CommandUtils;
import top.shadowpixel.shadowlevels.util.LocaleUtils;

import java.util.Collection;

public class StatsOfflineCommand extends SubCommand {
    public StatsOfflineCommand() {
        super("StatsOffline", EMPTY_LIST, ListUtils.asList("ShadowLevels.Commands.StatsOffline"));
    }

    @Override
    public boolean execute(@NotNull CommandContext ctx) {
        var sender = ctx.sender();
        var arguments = ctx.arguments();
        var level = CommandUtils.getLevel(arguments[1]);

        if (PlayerUtils.isOnline(arguments[2].getValue())) {
            SenderUtils.sendMessage(sender, "&cThis player is online!");
            return true;
        }

        LevelData data = level.getOfflineData(arguments[2].getValue());
        if (data == null) {
            interruptCommand(arguments[1], "player is null");
            return true;
        }

        SenderUtils.sendMessage(sender,
                LocaleUtils.getMessage(sender).getStringList("Messages.Command-Messages.Success.Stats-Message." + level.getName()),
                "{levels}", String.valueOf(data.getLevels()),
                "{exps}", String.valueOf(data.getExps()),
                "{next-levels}", String.valueOf(data.getLevels() + 1),
                "{required-exps}", "&cFAILED",
                "{percentage}", "&cFAILED",
                "{progressbar}", "&cFAILED");

        SenderUtils.sendMessage(sender, "&aRewards the player has received:");
        for (var entry : data.getReceivedRewards().entrySet()) {
            var name = entry.getKey();
            var reward = entry.getValue();
            var list = ShadowLevelsAPI.getRewardList(name);
            if (list == null) {
                continue;
            }

            SenderUtils.sendMessage(sender, (name + "&7[&f" + reward.size() + "&7/&f" + list.getRewards().size() + "&7]&f " + String.join(", ", reward)));
        }

        return true;
    }

    @Override
    public @Nullable Collection<String> complete(@NotNull CommandContext ctx) {
        if (ctx.arguments().length == 2) {
            return ShadowLevels.getInstance().getLevelManager().getLoadedLevels().keySet();
        }

        return EMPTY_LIST;
    }
}
