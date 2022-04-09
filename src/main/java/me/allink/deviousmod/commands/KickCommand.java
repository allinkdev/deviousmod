package me.allink.deviousmod.commands;

import joptsimple.internal.Strings;
import me.allink.deviousmod.command.ChatCommandBase;
import me.allink.deviousmod.exception.NotEnoughArgumentsException;
import me.allink.deviousmod.util.BanUtil;
import me.allink.deviousmod.util.ChatUtil;
import me.allink.deviousmod.util.Utilities;

import java.util.List;

public class KickCommand extends ChatCommandBase {
    Utilities utilities;
    ChatUtil chatUtil;
    BanUtil banUtil;

    public KickCommand(int minimumArguments, String command, String usage, String description, List<String> aliases) {
        super(minimumArguments, command, usage, description, aliases);
        utilities = Utilities.getInstance();
        chatUtil = utilities.getChatUtil();
        banUtil = utilities.getBanUtil();
    }

    @Override
    public void execute(String[] args) throws NotEnoughArgumentsException {
        super.execute(args);
        String username = Strings.join(args, " ");
        chatUtil.sendMessage(String.format("Kicking %s...", username));
        banUtil.kickPlayer(username);
    }
}
