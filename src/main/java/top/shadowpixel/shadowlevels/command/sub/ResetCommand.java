package top.shadowpixel.shadowlevels.command.sub;

import lombok.var;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.shadowpixel.shadowcore.api.command_v2.CommandContext;
import top.shadowpixel.shadowcore.api.command_v2.SubCommand;
import top.shadowpixel.shadowlevels.data.DataHandler;
import top.shadowpixel.shadowlevels.level.LevelManager;
import top.shadowpixel.shadowlevels.util.CommandUtils;
import top.shadowpixel.shadowlevels.util.LocaleUtils;

import java.util.ArrayList;
import java.util.Collection;

public class ResetCommand extends SubCommand {
    public ResetCommand() {
        super("Reset");
        permissions.add("ShadowLevels.Commands.Reset");
    }

    @Override
    public boolean execute(@NotNull CommandContext ctx) {
        var arguments = ctx.arguments();
        var sender = ctx.sender();
        var player = arguments[1].getPlayer();
        if (arguments[2].getValue().equals("*")) {
            if (player == null) {
                DataHandler.reset(sender, arguments[1].getValue(), "*");
                return true;
            }

            LevelManager.getInstance().resetAll(player);
            if (arguments.length >= 4 && !arguments[3].getValue().equalsIgnoreCase("true")) {
                return true;
            }

            LocaleUtils.sendCmdMessage(sender, "Success.Reset",
                    "%player%", arguments[1].getValue(),
                    "%level-system%", arguments[2].getValue());

            return true;
        }

        if (player == null) {
            DataHandler.reset(sender, arguments[1].getValue(), arguments[2].getValue());
            return true;
        }

        var level = CommandUtils.getLevel(arguments[2]);
        level.getLevelData(player).reset();

        if (arguments.length >= 4 && !arguments[3].getValue().equalsIgnoreCase("true")) {
            return true;
        }

        LocaleUtils.sendCmdMessage(sender, "Success.Reset",
                "%player%", arguments[1].getValue(),
                "%level-system%", arguments[2].getValue());

        return true;
    }

    @Override
    public @Nullable Collection<String> complete(@NotNull CommandContext ctx) {
        switch (ctx.arguments().length) {
            case 2:
                return ONLINE_PLAYERS_LIST;
            case 3:
                var list = new ArrayList<String>();
                list.add("*");
                list.addAll(LevelManager.getInstance().getLoadedLevels().keySet());
                return list;
        }

        return EMPTY_LIST;
    }
}
