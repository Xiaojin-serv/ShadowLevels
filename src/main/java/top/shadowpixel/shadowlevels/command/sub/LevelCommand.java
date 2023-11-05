package top.shadowpixel.shadowlevels.command.sub;

import lombok.var;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.shadowpixel.shadowcore.api.command_v2.CommandContext;
import top.shadowpixel.shadowcore.api.command_v2.SubCommand;
import top.shadowpixel.shadowcore.api.command_v2.component.CommandArgument;
import top.shadowpixel.shadowcore.util.collection.ListUtils;
import top.shadowpixel.shadowlevels.level.Level;
import top.shadowpixel.shadowlevels.level.LevelData;
import top.shadowpixel.shadowlevels.level.LevelManager;
import top.shadowpixel.shadowlevels.util.CommandUtils;

import java.util.Collection;

public abstract class LevelCommand extends SubCommand {
    public LevelCommand(String name) {
        super(name);
    }

    public LevelCommand(@NotNull String name, @Nullable String... aliases) {
        super(name, aliases);
    }

    @Override
    public final boolean execute(@NotNull CommandContext ctx) {
        var argument = ctx.arguments();
        var level = getLevel(argument[2]);
        var value = argument[3];
        var player = argument[1].getPlayer();
        if (player == null) {
            return executeOffline(ctx, argument[1].getValue(), level, value);
        }

        return execute(ctx, player, level.getLevelData(player), value);
    }

    @Override
    public @Nullable Collection<String> complete(@NotNull CommandContext ctx) {
        switch (ctx.arguments().length) {
            case 2:
                return ONLINE_PLAYERS_LIST;
            case 3:
                return LevelManager.getInstance().getLoadedLevels().keySet();
            case 5:
                return ListUtils.asList("true", "false");
        }

        return null;
    }

    public abstract boolean execute(@NotNull CommandContext ctx, @NotNull Player player, @NotNull LevelData levelData, @NotNull CommandArgument value);

    public abstract boolean executeOffline(@NotNull CommandContext ctx, @NotNull String player, @NotNull Level level, @NotNull CommandArgument value);

    @NotNull
    public Level getLevel(@NotNull CommandArgument argument) {
        return CommandUtils.getLevel(argument);
    }
}
