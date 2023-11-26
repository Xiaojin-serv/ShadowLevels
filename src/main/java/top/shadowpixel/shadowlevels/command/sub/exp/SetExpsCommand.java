package top.shadowpixel.shadowlevels.command.sub.exp;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import top.shadowpixel.shadowcore.api.command_v2.CommandContext;
import top.shadowpixel.shadowcore.api.command_v2.component.CommandArgument;
import top.shadowpixel.shadowlevels.command.sub.LevelCommand;
import top.shadowpixel.shadowlevels.data.ModificationStatus;
import top.shadowpixel.shadowlevels.data.DataHandler;
import top.shadowpixel.shadowlevels.level.Level;
import top.shadowpixel.shadowlevels.level.LevelData;
import top.shadowpixel.shadowlevels.object.enums.ModificationType;
import top.shadowpixel.shadowlevels.util.LocaleUtils;

import static top.shadowpixel.shadowlevels.util.Utils.calcValue;

public class SetExpsCommand extends LevelCommand {
    public SetExpsCommand() {
        super("SetExps", "SetExp");
        super.permissions.add("ShadowLevels.Commands.SetExps");
    }

    @Override
    public boolean execute(@NotNull CommandContext ctx, @NotNull Player player, @NotNull LevelData levelData, @NotNull CommandArgument value) {
        double amount = calcValue(levelData.getExps(), value);
        levelData.setExps(amount);

        if (ctx.arguments().length >= 5 &&  !ctx.arguments()[4].getValue().equalsIgnoreCase("true")) {
            return true;
        }

        LocaleUtils.sendCmdMessage(ctx.sender(), "Success.Set-Exps",
                "%player%", player.getName(),
                "%amount%", String.valueOf(amount),
                "%level-system%", levelData.getLevel().getName());
        return true;
    }

    @Override
    public void sendSucceedMessage(@NotNull CommandContext ctx, @NotNull String player, @NotNull String level, @NotNull String amount) {
        LocaleUtils.sendCmdMessage(ctx.sender(), "Success.Set-Exps",
                "%player%", player,
                "%amount%", amount,
                "%level-system%", level);
    }

    @Override
    public ModificationStatus executeOffline(@NotNull CommandContext ctx, @NotNull CommandArgument player, @NotNull Level level, @NotNull CommandArgument value) {
        return DataHandler.modifyExps(player.getValue(), level.getName(), ModificationType.SET, value.getValue(), ctx);
    }
}