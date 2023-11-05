package top.shadowpixel.shadowlevels.command.sub.level;

import lombok.var;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import top.shadowpixel.shadowcore.api.command_v2.CommandContext;
import top.shadowpixel.shadowcore.api.command_v2.component.CommandArgument;
import top.shadowpixel.shadowlevels.command.sub.LevelCommand;
import top.shadowpixel.shadowlevels.data.DataHandler;
import top.shadowpixel.shadowlevels.level.Level;
import top.shadowpixel.shadowlevels.level.LevelData;
import top.shadowpixel.shadowlevels.object.enums.ModificationType;
import top.shadowpixel.shadowlevels.util.LocaleUtils;

public class SetLevelsCommand extends LevelCommand {
    public SetLevelsCommand() {
        super("SetLevels", "SetLevel");
        super.permissions.add("ShadowLevels.Commands.SetLevels");
    }

    @Override
    public boolean execute(@NotNull CommandContext ctx, @NotNull Player player, @NotNull LevelData levelData, @NotNull CommandArgument value) {
        var amount = value.getInt();
        levelData.setLevels(amount);
        if (ctx.arguments().length >= 5 &&  !ctx.arguments()[4].getValue().equalsIgnoreCase("true")) {
            return true;
        }

        LocaleUtils.sendCmdMessage(ctx.sender(), "Success.Set-Levels",
                "%player%", player.getName(),
                "%amount%", String.valueOf(amount),
                "%level-system%", levelData.getLevel().getName());
        return true;
    }

    @Override
    public boolean executeOffline(@NotNull CommandContext ctx, @NotNull String player, @NotNull Level level, @NotNull CommandArgument value) {
        var arguments = ctx.arguments();
        DataHandler.modifyLevels(ctx.sender(), arguments[1].getValue(), arguments[2].getValue(), ModificationType.SET, value.getInt());
        return true;
    }
}