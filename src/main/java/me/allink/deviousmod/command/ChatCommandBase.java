package me.allink.deviousmod.command;

import me.allink.deviousmod.exception.NotEnoughArgumentsException;
import me.allink.deviousmod.util.ChatUtil;
import me.allink.deviousmod.util.Utilities;

import java.util.List;

public class ChatCommandBase {
    public String command;
    public String description;
    public String usage;
    public List<String> aliases;
    int minimumArguments;
    Utilities utilities;
    ChatUtil chatUtil;

    public ChatCommandBase(int minimumArguments, String command, String usage, String description, List<String> aliases) {
        this.minimumArguments = minimumArguments;
        this.command = command;
        this.description = description;
        this.usage = usage;
        this.utilities = Utilities.getInstance();
        this.chatUtil = utilities.getChatUtil();
        this.aliases = aliases;
    }

    /**
     * Execute the chat command with arguments
     *
     * @param args The arguments of the chat command
     */
    public void execute(String[] args) throws NotEnoughArgumentsException {
        if (args.length < minimumArguments) {
            String errorMessage = String.format("%s", usage);
            throw new NotEnoughArgumentsException(errorMessage);
        }
    }
}
