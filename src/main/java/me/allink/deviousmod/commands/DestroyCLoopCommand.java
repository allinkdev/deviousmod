package me.allink.deviousmod.commands;

import me.allink.deviousmod.command.ChatCommandBase;
import me.allink.deviousmod.exception.NotEnoughArgumentsException;
import me.allink.deviousmod.util.ChatUtil;
import me.allink.deviousmod.util.CommandBlockUtil;
import me.allink.deviousmod.util.Utilities;

import java.util.List;

public class DestroyCLoopCommand extends ChatCommandBase {
    Utilities utilities;
    CommandBlockUtil commandBlockUtil;
    ChatUtil chatUtil;

    public DestroyCLoopCommand(int minimumArguments, String command, String usage, String description, List<String> aliases) {
        super(minimumArguments, command, usage, description, aliases);
        this.utilities = Utilities.getInstance();
        this.commandBlockUtil = utilities.getCommandBlockUtil();
        this.chatUtil = utilities.getChatUtil();
    }


    @Override
    public void execute(String[] args) throws NotEnoughArgumentsException {
        super.execute(args);
        if (args == null) return;
        commandBlockUtil.destroyLooping();
        chatUtil.sendMessage("Destroyed command block loop!");
    }
}
