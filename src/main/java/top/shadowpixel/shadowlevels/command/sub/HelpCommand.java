package top.shadowpixel.shadowlevels.command.sub;

import lombok.var;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.shadowpixel.shadowcore.api.command_v2.CommandContext;
import top.shadowpixel.shadowcore.api.command_v2.SubCommand;
import top.shadowpixel.shadowcore.util.entity.SenderUtils;
import top.shadowpixel.shadowcore.util.text.ReplaceUtils;
import top.shadowpixel.shadowlevels.util.LocaleUtils;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public class HelpCommand extends SubCommand {
    protected String name = "Help";

    public HelpCommand() {
        super("Help");
    }

    public HelpCommand(String name) {
        super(name);
    }

    @Override
    public boolean execute(@NotNull CommandContext ctx) {
        var sender = ctx.sender();
        var label = ctx.label();
        var pages = getPages(sender);
        var page = "1";
        if (ctx.arguments().length > 1) {
            page = ctx.arguments()[1].getValue();
        }

        if (!pages.contains(page)) {
            page = "1";
        }

        SenderUtils.sendMessage(sender, ReplaceUtils.replace(getHelps(sender, page), "%cmd%", label,
                "%page%", page));
        return true;
    }

    @Override
    public @Nullable Collection<String> complete(@NotNull CommandContext ctx) {
        return getPages(ctx.sender());
    }

    @SuppressWarnings("DataFlowIssue")
    private Set<String> getPages(CommandSender sender) {
        return LocaleUtils.getMessage(sender).getConfigurationSection("Messages.Command-Messages.Success." + name).getKeys();
    }

    private List<String> getHelps(CommandSender sender, String page) {
        return LocaleUtils.getMessage(sender).getStringList("Messages.Command-Messages.Success." + name + "." + page);
    }
}
