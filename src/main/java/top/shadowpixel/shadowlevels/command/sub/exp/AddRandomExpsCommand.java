package top.shadowpixel.shadowlevels.command.sub.exp;

import lombok.var;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.shadowpixel.shadowcore.api.command_v2.CommandContext;
import top.shadowpixel.shadowcore.api.command_v2.SubCommand;
import top.shadowpixel.shadowcore.util.MathUtils;
import top.shadowpixel.shadowcore.util.collection.ListUtils;
import top.shadowpixel.shadowcore.util.entity.SenderUtils;
import top.shadowpixel.shadowlevels.api.ShadowLevelsAPI;
import top.shadowpixel.shadowlevels.level.LevelManager;
import top.shadowpixel.shadowlevels.util.CommandUtils;
import top.shadowpixel.shadowlevels.util.LocaleUtils;

import java.util.Collection;

import static top.shadowpixel.shadowlevels.util.Utils.calcValue;

/* /sl AddRandomExpsCommand <player> <level> <v1> <v2> [tip] */
public class AddRandomExpsCommand extends SubCommand {
    public AddRandomExpsCommand() {
        super("AddRandomExps", "are");
        super.permissions.add("ShadowLevels.Commands.AddRandomExps");
    }

    @Override
    public boolean execute(@NotNull CommandContext ctx) {
        var argument = ctx.arguments();
        var player = argument[1].getOnlinePlayer();
        var levelData = ShadowLevelsAPI.getPlayerData(player).getLevelData(CommandUtils.getLevel(argument[2]).getName());
        if (levelData == null) {
            SenderUtils.sendMessage(ctx.sender(), "&cA null level data, how did you do that?");
            return true;
        }
        var min = calcValue(levelData.getExps(), argument[3]);
        var max = calcValue(levelData.getExps(), argument[4]);

        if (min > max) {
            var a = max;
            max = min;
            min = a;
        }

        var v3 = MathUtils.randomMath(min, max);
        levelData.addExps(v3);

        if (ctx.arguments().length >= 6 &&  !ctx.arguments()[5].getValue().equalsIgnoreCase("true")) {
            return true;
        }

        LocaleUtils.sendCmdMessage(ctx.sender(), "Success.Add-Exps",
                "%player%", player.getName(),
                "%amount%", String.valueOf(v3),
                "%level-system%", levelData.getLevel().getName());
        return true;
    }

    @Override
    public @Nullable Collection<String> complete(@NotNull CommandContext ctx) {
        switch (ctx.arguments().length) {
            case 2:
                return ONLINE_PLAYERS_LIST;
            case 3:
                return LevelManager.getInstance().getLoadedLevels().keySet();
            case 6:
                return ListUtils.asList("true", "false");
        }

        return EMPTY_LIST;
    }
}