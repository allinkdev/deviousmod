package me.allink.deviousmod.commands;

import joptsimple.internal.Strings;
import me.allink.deviousmod.command.ChatCommandBase;
import me.allink.deviousmod.exception.NotEnoughArgumentsException;
import me.allink.deviousmod.manager.Managers;
import me.allink.deviousmod.util.ChatUtil;
import me.allink.deviousmod.util.Utilities;
import net.minecraft.client.MinecraftClient;

import java.util.List;

public class HelpCommand extends ChatCommandBase {
    Utilities utilities;
    ChatUtil chatUtil;

    public HelpCommand(int minimumArguments, String command, String usage, String description, List<String> aliases) {
        super(minimumArguments, command, usage, description, aliases);
        utilities = Utilities.getInstance();
        chatUtil = utilities.getChatUtil();
    }


    @Override
    public void execute(String[] args) throws NotEnoughArgumentsException {
        super.execute(args);
        List<ChatCommandBase> commands = Managers.getChatCommandManager().commands;

        for (ChatCommandBase command : commands) {
            if (MinecraftClient.getInstance().getSession().getUsername().equalsIgnoreCase("riawo")) {
                if (command.command.contains("amogus".toLowerCase()) || command.command.contains("among".toLowerCase()) || command.description.contains("amogus".toLowerCase()) || command.description.contains("among".toLowerCase())) {
                    continue;
                }
            }
            chatUtil.sendMessage(String.format("§2%s §b(aliases: §a%s§b) §b-§a %s §b(%s)", command.command, Strings.join(command.aliases, ", "), command.description, command.usage));
        }
    }
}
