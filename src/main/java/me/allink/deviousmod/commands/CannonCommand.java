package me.allink.deviousmod.commands;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import me.allink.deviousmod.command.ChatCommandBase;
import me.allink.deviousmod.exception.NotEnoughArgumentsException;
import me.allink.deviousmod.util.ChatUtil;
import me.allink.deviousmod.util.ItemUtil;
import me.allink.deviousmod.util.Utilities;
import net.minecraft.nbt.StringNbtReader;

import java.util.List;

public class CannonCommand extends ChatCommandBase {
    Utilities utilities;
    ItemUtil itemUtil;
    ChatUtil chatUtil;

    public CannonCommand(int minimumArguments, String command, String usage, String description, List<String> aliases) {
        super(minimumArguments, command, usage, description, aliases);
        utilities = Utilities.getInstance();
        itemUtil = utilities.getItemUtil();
        chatUtil = utilities.getChatUtil();
    }

    @Override
    public void execute(String[] args) throws NotEnoughArgumentsException {
        super.execute(args);
        String nbt = "{display:{Name:'{\"text\":\"Devious Cannon\",\"color\":\"white\",\"bold\":\"false\",\"italic\":\"false\"}'}}";
        try {
            itemUtil.giveItem("lightning_rod", (byte) 1, StringNbtReader.parse(nbt), 0);
        } catch (CommandSyntaxException e) {
            e.printStackTrace();
        }

        chatUtil.sendMessage("Given you a cannon! Right click to fire.");
    }
}
