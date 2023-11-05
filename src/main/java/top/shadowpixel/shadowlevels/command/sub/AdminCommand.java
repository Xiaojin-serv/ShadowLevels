package top.shadowpixel.shadowlevels.command.sub;

public class AdminCommand extends HelpCommand {
    public AdminCommand() {
        super("Admin");
        permissions.add("ShadowLevels.Commands.Admin");
        name = "Admin";
    }
}
