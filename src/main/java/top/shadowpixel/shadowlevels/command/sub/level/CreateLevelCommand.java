package top.shadowpixel.shadowlevels.command.sub.level;

import org.jetbrains.annotations.NotNull;
import top.shadowpixel.shadowcore.api.command_v2.CommandContext;
import top.shadowpixel.shadowcore.api.command_v2.SubCommand;
import top.shadowpixel.shadowcore.object.enums.SenderType;
import top.shadowpixel.shadowlevels.ShadowLevels;
import top.shadowpixel.shadowlevels.level.LevelManager;
import top.shadowpixel.shadowlevels.util.LocaleUtils;

public class CreateLevelCommand extends SubCommand {
    public CreateLevelCommand() {
        super("CreateLevel", "CreateLevelSystem", "cls");
        setSenderType(SenderType.CONSOLE);
    }

    @Override
    public boolean execute(@NotNull CommandContext ctx) {
        if (ShadowLevels.getInstance().getLevel(ctx.arguments()[1].getValue()) != null) {
            ctx.sender().sendMessage(LocaleUtils.getMessage(ctx.sender(), "Messages.Levels.Existed"));
            return true;
        }

        LevelManager.getInstance().create(ctx.arguments()[1].getValue());
        return true;
    }
}
