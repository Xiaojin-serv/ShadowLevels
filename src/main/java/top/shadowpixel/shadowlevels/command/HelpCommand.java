package top.shadowpixel.shadowlevels.command;

import lombok.var;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.shadowpixel.shadowcore.api.command.subcommand.SubCommand;
import top.shadowpixel.shadowcore.util.entity.SenderUtils;
import top.shadowpixel.shadowcore.util.object.ObjectUtils;
import top.shadowpixel.shadowcore.util.text.ReplaceUtils;
import top.shadowpixel.shadowlevels.util.LocaleUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class HelpCommand extends SubCommand {

    protected String name = "Help";

    public HelpCommand() {
        super("Help");
    }

    public HelpCommand(String alias, String permissions) {
        super(alias, permissions);
    }

    @Override
    public void execute(@NotNull CommandSender sender, @NotNull String label, String[] arguments) {
        var page = ObjectUtils.getOrElse(arguments, 0, "1");
        var pages = getPages(sender);

        if (!pages.contains(page)) {
            page = "1";
        }

        SenderUtils.sendMessage(sender, ReplaceUtils.replace(getHelps(sender, page), "%cmd%", label,
                "%page%", page));
    }

    @Override
    public @Nullable List<String> complete(@NotNull CommandSender sender, @NotNull String label, String[] arguments) {
        return new ArrayList<>(getPages(sender));
    }

    @SuppressWarnings("DataFlowIssue")
    private Set<String> getPages(CommandSender sender) {
        return LocaleUtils.getMessage(sender).getConfigurationSection("Messages.Command-Messages.Success." + name).getKeys();
    }

    private List<String> getHelps(CommandSender sender, String page) {
        return LocaleUtils.getMessage(sender).getStringList("Messages.Command-Messages.Success." + name + "." + page);
    }
}
