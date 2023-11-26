package top.shadowpixel.shadowlevels.command;

import lombok.var;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.shadowpixel.shadowcore.api.command_v2.CommandContext;
import top.shadowpixel.shadowcore.api.command_v2.SubCommand;
import top.shadowpixel.shadowcore.api.exception.command.ParameterizedCommandInterruptedException;
import top.shadowpixel.shadowlevels.ShadowLevels;
import top.shadowpixel.shadowlevels.command.sub.*;
import top.shadowpixel.shadowlevels.command.sub.exp.AddExpsCommand;
import top.shadowpixel.shadowlevels.command.sub.exp.AddRandomExpsCommand;
import top.shadowpixel.shadowlevels.command.sub.exp.RemoveExpsCommand;
import top.shadowpixel.shadowlevels.command.sub.exp.SetExpsCommand;
import top.shadowpixel.shadowlevels.command.sub.level.AddLevelsCommand;
import top.shadowpixel.shadowlevels.command.sub.level.CreateLevelCommand;
import top.shadowpixel.shadowlevels.command.sub.level.RemoveLevelsCommand;
import top.shadowpixel.shadowlevels.command.sub.level.SetLevelsCommand;
import top.shadowpixel.shadowlevels.command.sub.multiple.SetMultipleCommand;
import top.shadowpixel.shadowlevels.command.sub.reward.CreateRewardCommand;
import top.shadowpixel.shadowlevels.command.sub.reward.OpenRewardCommand;
import top.shadowpixel.shadowlevels.command.sub.reward.RewardCommand;
import top.shadowpixel.shadowlevels.util.LocaleUtils;

import java.util.Collection;
import java.util.stream.Collectors;

public class MainCommand extends SubCommand {
    public MainCommand() {
        super("ShadowLevels");
    }

    @Override
    public void initialize() {
        addSubCommand(
                new HelpCommand(),
                new AdminCommand(),
                new ReloadCommand(),
                new ResetCommand(),
                new StatsCommand(),
                new StatsOfflineCommand(),
                /* Exp */
                new AddExpsCommand(),
                new RemoveExpsCommand(),
                new SetExpsCommand(),
                /* Level */
                new CreateLevelCommand(),
                new AddLevelsCommand(),
                new AddRandomExpsCommand(),
                new RemoveLevelsCommand(),
                new SetLevelsCommand(),
                /* Reward */
                new RewardCommand(),
                new OpenRewardCommand(),
                new CreateRewardCommand(),
                /* Multiple */
                new SetMultipleCommand()
        );

        this.exceptionHandlers.clear();
        /* Default exception handlers */
        addExceptionHandler("unknown cmd", "command not found",
                ctx -> LocaleUtils.sendCmdMessage(ctx.sender(), "Errors.Unknown-Command",
                        "%cmd%", ctx.label()));
        addExceptionHandler("not player", "not player",
                ctx -> LocaleUtils.sendCmdMessage(ctx.sender(), "Errors.Only-For-Player"));
        addExceptionHandler("not console", "not console",
                ctx -> LocaleUtils.sendCmdMessage(ctx.sender(), "Errors.Only-For-Console"));
        addExceptionHandler("no permissions", "no permissions",
                ctx -> LocaleUtils.sendCmdMessage(ctx.sender(), "Errors.No-Permissions"));
        addExceptionHandler("no argument", t -> t instanceof IndexOutOfBoundsException,
                ctx -> {
                    var num = ctx.exception().getMessage();
                    if (num.toLowerCase().startsWith("index")) {
                        num = num.split(" ")[1];
                    }

                    ctx.sender().sendMessage(LocaleUtils.getCmdMessage(ctx.sender(),
                            "Errors.Params-Error",
                            "%pos%", String.valueOf(Integer.parseInt(num) + 1)));
                    return true;
                });

        addExceptionHandler("parameter error", t -> t instanceof ParameterizedCommandInterruptedException, ctx -> {
            var exc = (ParameterizedCommandInterruptedException) ctx.exception();
            var argument = exc.getArgument();
            switch (exc.getMessage()) {
                case "not int":
                    LocaleUtils.sendCmdMessage(ctx.sender(), "Errors.Not-An- Integer",
                            "%pos%", String.valueOf(argument.getIndex() + 1));
                    return true;
                case "not double":
                    LocaleUtils.sendCmdMessage(ctx.sender(), "Errors.Not-An-Number",
                            "%pos%", String.valueOf(argument.getIndex() + 1));
                    return true;
                case "player not found":
                    LocaleUtils.sendCmdMessage(ctx.sender(), "Errors.Player-Not-Found",
                            "%pos%", String.valueOf(argument.getIndex() + 1),
                            "%name%", argument.getValue(),
                            "%player%", argument.getValue());
                    return true;
                case "level not found":
                    LocaleUtils.sendCmdMessage(ctx.sender(), "Errors.Level-Not-Found",
                            "%pos%", String.valueOf(argument.getIndex() + 1),
                            "%level%", argument.getValue());
                    return true;
                case "reward not found":
                    LocaleUtils.sendCmdMessage(ctx.sender(), "Errors.Reward-Not-Found",
                            "%pos%", String.valueOf(argument.getIndex() + 1),
                            "%reward%", argument.getValue());
                    return true;
            }

            return false;
        });
    }

    @Override
    public boolean execute(@NotNull CommandContext ctx) {
        if (ctx.arguments().length > 0) {
            return false;
        }

        var sender = ctx.sender();
        LocaleUtils.sendMessage(sender, "Messages.Info",
                "%cmd%", ctx.label(),
                "%version%", ShadowLevels.getVersion());
        return true;
    }

    @Override
    public @Nullable Collection<String> complete(@NotNull CommandContext ctx) {
        if (ctx.arguments().length > 1) {
            return RUN_SUBCOMMAND;
        }

        return getSubCommands().stream()
                .filter(cmd -> cmd.isExcecutable(ctx.sender()))
                .map(SubCommand::getName)
                .collect(Collectors.toList());
    }
}