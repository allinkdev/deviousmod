package me.allink.deviousmod.commands;

import joptsimple.internal.Strings;
import me.allink.deviousmod.client.DeviousModClient;
import me.allink.deviousmod.command.ChatCommandBase;
import me.allink.deviousmod.exception.NotEnoughArgumentsException;
import me.allink.deviousmod.util.ChatUtil;
import me.allink.deviousmod.util.Utilities;

import java.util.List;

public class IgnoreCommand extends ChatCommandBase {
    Utilities utilities;
    ChatUtil chatUtil;

    public IgnoreCommand(int minimumArguments, String command, String usage, String description, List<String> aliases) {
        super(minimumArguments, command, usage, description, aliases);
        this.utilities = Utilities.getInstance();
        this.chatUtil = utilities.getChatUtil();
    }


    @Override
    public void execute(String[] args) throws NotEnoughArgumentsException {
        super.execute(args);
        if (args == null) return;
        String message = Strings.join(args, " ").trim();
        chatUtil.sendMessage(String.format("Now ignoring messages that contain \"%s\".", message));
        DeviousModClient deviousMod = DeviousModClient.getInstance();
        DeviousModClient.ignore.add(message);
        deviousMod.ignoreListUpdate();
    }
}
