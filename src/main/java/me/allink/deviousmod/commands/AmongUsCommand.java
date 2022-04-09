package me.allink.deviousmod.commands;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import me.allink.deviousmod.command.ChatCommandBase;
import me.allink.deviousmod.exception.NotEnoughArgumentsException;
import me.allink.deviousmod.util.ChatUtil;
import me.allink.deviousmod.util.ItemUtil;
import me.allink.deviousmod.util.Utilities;

import java.util.List;

public class AmongUsCommand extends ChatCommandBase {
    Utilities utilities;
    ItemUtil itemUtil;
    ChatUtil chatUtil;

    public AmongUsCommand(int minimumArguments, String command, String usage, String description, List<String> aliases) {
        super(minimumArguments, command, usage, description, aliases);
        utilities = Utilities.getInstance();
        itemUtil = utilities.getItemUtil();
        chatUtil = utilities.getChatUtil();
    }


    @Override
    public void execute(String[] args) throws NotEnoughArgumentsException {
        super.execute(args);
        try {
            itemUtil.giveItem("bundle", (byte) 1, String.format("{Items:[{Count:1b, Slot:0, id:\"knowledge_book\", tag:{Recipes:[\"%s\"]}}]}", "[AMONGUS]".repeat(1024 * 6)), 0);
            chatUtil.sendMessage("You are now the sussy wussy impostor from Among Us.");
        } catch (CommandSyntaxException e) {
            e.printStackTrace();
        }
    }
}
