package me.allink.deviousmod.commands;

import me.allink.deviousmod.command.ChatCommandBase;
import me.allink.deviousmod.util.Utilities;

import java.util.List;

public class TestCommand extends ChatCommandBase {

    public TestCommand(int minimumArguments, String command, String usage, String description, List<String> aliases) {
        super(minimumArguments, command, usage, description, aliases);
    }

    @Override
    public void execute(String[] args) {
        Utilities utilities = Utilities.getInstance();
        //BanUtil.banPlayer("matchbox_17");
    }
}
