package me.allink.deviousmod.commands;

import me.allink.deviousmod.client.DeviousModClient;
import me.allink.deviousmod.command.ChatCommandBase;
import me.allink.deviousmod.exception.NotEnoughArgumentsException;
import me.allink.deviousmod.util.ChatUtil;
import me.allink.deviousmod.util.Utilities;
import net.minecraft.text.LiteralText;

import java.util.List;

public class SetPrefixCommand extends ChatCommandBase {
    Utilities utilities;
    ChatUtil chatUtil;

    public SetPrefixCommand(int minimumArguments, String command, String usage, String description, List<String> aliases) {
        super(minimumArguments, command, usage, description, aliases);
        utilities = Utilities.getInstance();
        chatUtil = utilities.getChatUtil();
    }


    @Override
    public void execute(String[] args) throws NotEnoughArgumentsException {
        super.execute(args);
        String newPrefix = args[0];
        DeviousModClient.prefix = newPrefix;
        DeviousModClient.getInstance().updatePrefix();
        chatUtil.sendMessage(new LiteralText(String.format("Set prefix to %s", newPrefix)));
    }
}
