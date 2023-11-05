package top.shadowpixel.shadowlevels.command.sub.multiple;

import lombok.var;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import top.shadowpixel.shadowcore.api.command_v2.CommandContext;
import top.shadowpixel.shadowcore.api.command_v2.component.CommandArgument;
import top.shadowpixel.shadowlevels.command.sub.LevelCommand;
import top.shadowpixel.shadowlevels.data.DataHandler;
import top.shadowpixel.shadowlevels.level.Level;
import top.shadowpixel.shadowlevels.level.LevelData;
import top.shadowpixel.shadowlevels.util.LocaleUtils;

/* /sl SetMultiple <player> <level> <value> [tip] */
public class SetMultipleCommand extends LevelCommand {
    public SetMultipleCommand() {
        super("SetMultiple");
        super.permissions.add("ShadowLevels.Commands.SetMultiple");
    }

    @Override
    public boolean execute(@NotNull CommandContext ctx, @NotNull Player player, @NotNull LevelData levelData, @NotNull CommandArgument value) {
        var amount = value.getDouble();
        levelData.setMultiple((float) amount);
        if (ctx.arguments().length >= 5 &&  !ctx.arguments()[4].getValue().equalsIgnoreCase("true")) {
            return true;
        }

        LocaleUtils.sendCmdMessage(ctx.sender(), "Success.Set-Multiple",
                "%player%", player.getName(),
                "%amount%", String.valueOf(amount),
                "%level-system%", levelData.getLevel().getName(),
                "%multiple%", String.valueOf(amount));
        return true;
    }

    @Override
    public boolean executeOffline(@NotNull CommandContext ctx, @NotNull String player, @NotNull Level level, @NotNull CommandArgument value) {
        var arguments = ctx.arguments();
        DataHandler.modifyMultiple(ctx.sender(), arguments[1].getValue(), arguments[2].getValue(), (float) arguments[3].getDouble());
        return true;
    }
}
