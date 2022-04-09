package me.allink.deviousmod.commands;

import joptsimple.internal.Strings;
import me.allink.deviousmod.client.DeviousModClient;
import me.allink.deviousmod.command.ChatCommandBase;
import me.allink.deviousmod.exception.NotEnoughArgumentsException;
import me.allink.deviousmod.util.ChatUtil;
import me.allink.deviousmod.util.Utilities;

import java.util.List;

public class UnignoreCommand extends ChatCommandBase {
    Utilities utilities;
    ChatUtil chatUtil;

    public UnignoreCommand(int minimumArguments, String command, String usage, String description, List<String> aliases) {
        super(minimumArguments, command, usage, description, aliases);
        utilities = Utilities.getInstance();
        chatUtil = utilities.getChatUtil();
    }


    @Override
    public void execute(String[] args) throws NotEnoughArgumentsException {
        super.execute(args);
        if (args == null) return;
        String message = Strings.join(args, " ").trim();
        DeviousModClient deviousMod = DeviousModClient.getInstance();
        DeviousModClient.ignore.remove(message);
        chatUtil.sendMessage(String.format("No longer ignoring messages that contain \"%s\".", message));
        deviousMod.ignoreListUpdate();
    }
}
