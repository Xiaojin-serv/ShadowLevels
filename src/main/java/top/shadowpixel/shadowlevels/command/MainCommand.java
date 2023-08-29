package top.shadowpixel.shadowlevels.command;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import top.shadowpixel.shadowcore.api.command.PluginCommand;
import top.shadowpixel.shadowcore.util.entity.SenderUtils;
import top.shadowpixel.shadowlevels.ShadowLevels;
import top.shadowpixel.shadowlevels.util.LocaleUtils;

public class MainCommand extends PluginCommand {

    public MainCommand() {
        super("ShadowLevels");
    }

    @Override
    public void execute(@NotNull CommandSender sender, @NotNull String label, String[] arguments) {
        SenderUtils.sendMessage(sender, LocaleUtils.getMessage(sender, "Messages.Info"),
                "%cmd%", label,
                "%version%", ShadowLevels.getVersion());
    }
}
