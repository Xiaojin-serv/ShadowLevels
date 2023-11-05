package top.shadowpixel.shadowlevels.command.sub;

import lombok.var;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.shadowpixel.shadowcore.api.command_v2.CommandContext;
import top.shadowpixel.shadowcore.api.command_v2.SubCommand;
import top.shadowpixel.shadowcore.util.entity.SenderUtils;
import top.shadowpixel.shadowlevels.ShadowLevels;
import top.shadowpixel.shadowlevels.level.LevelData;
import top.shadowpixel.shadowlevels.util.CommandUtils;
import top.shadowpixel.shadowlevels.util.LocaleUtils;

import java.util.Collection;

import static top.shadowpixel.shadowlevels.util.LocaleUtils.sendCmdMessage;

public class StatsCommand extends SubCommand {
    public StatsCommand() {
        super("Stats");
    }

    @Override
    public boolean execute(@NotNull CommandContext ctx) {
        var sender = ctx.sender();
        var arguments = ctx.arguments();
        var level = CommandUtils.getLevel(arguments[1]);

        LevelData data = null;
        if (arguments.length > 2 && sender.hasPermission("ShadowLevels.Commands.Stats.Others")) {
            var player = arguments[2].getOnlinePlayer();
            data = level.getLevelData(player);
        } else if (sender instanceof Player) {
            data = level.getLevelData((Player) sender);
        }

        if (data == null) {
            sendCmdMessage(sender, "Errors.Only-For-Player");
            return true;
        }

        SenderUtils.sendMessage(sender,
                LocaleUtils.getMessage(sender).getStringList("Messages.Command-Messages.Success.Stats-Message." + level.getName()),
                "{levels}", String.valueOf(data.getLevels()),
                "{exps}", String.valueOf(data.getExps()),
                "{next-levels}", String.valueOf(data.getLevels() + 1),
                "{required-exps}", String.valueOf(data.getRequiredExps()),
                "{percentage}", String.valueOf(data.getPercentage()),
                "{progressbar}", data.getProgressBar());

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
