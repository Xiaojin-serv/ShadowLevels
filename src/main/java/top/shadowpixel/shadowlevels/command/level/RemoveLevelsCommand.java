package top.shadowpixel.shadowlevels.command.level;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import top.shadowpixel.shadowcore.util.collection.ListUtils;
import top.shadowpixel.shadowlevels.command.LevelCommand;
import top.shadowpixel.shadowlevels.data.DataHandler;
import top.shadowpixel.shadowlevels.level.Level;
import top.shadowpixel.shadowlevels.object.enums.ModificationType;

public class RemoveLevelsCommand extends LevelCommand {

    public RemoveLevelsCommand() {
        super(ListUtils.immutableList("RemoveLevel", "RemoveLevels"),
                ListUtils.immutableList("ShadowLevels.Commands.RemoveLevels"));
        path = "Remove-Levels";
    }

    @Override
    public void run(@NotNull Player player, @NotNull Level level, int value) {
        level.getLevelData(player).removeLevels(value);
    }

    @Override
    public void executeOffline(CommandSender sender, String label, String[] arguments) {
        DataHandler.modifyLevels(sender, arguments[0], arguments[1], ModificationType.REMOVE, Integer.parseInt(arguments[2]));
    }
}
