package top.shadowpixel.shadowlevels.command.exps;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import top.shadowpixel.shadowcore.util.collection.ListUtils;
import top.shadowpixel.shadowlevels.command.LevelCommand;
import top.shadowpixel.shadowlevels.data.DataHandler;
import top.shadowpixel.shadowlevels.level.Level;
import top.shadowpixel.shadowlevels.object.enums.ModificationType;

public class SetExpsCommand extends LevelCommand {

    public SetExpsCommand() {
        super(ListUtils.immutableList("SetExp", "SetExps"),
                "ShadowLevels.Commands.SetExps");
        path = "Set-Exps";
    }

    @Override
    public void run(@NotNull Player player, @NotNull Level level, int value) {
        level.getLevelData(player).setExps(value);
    }

    @Override
    public void executeOffline(CommandSender sender, String label, String[] arguments) {
        DataHandler.modifyExps(sender, arguments[0], arguments[1], ModificationType.SET, Integer.parseInt(arguments[2]));
    }
}
