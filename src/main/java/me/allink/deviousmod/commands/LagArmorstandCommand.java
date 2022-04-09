package me.allink.deviousmod.commands;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.List;
import me.allink.deviousmod.command.ChatCommandBase;
import me.allink.deviousmod.exception.NotEnoughArgumentsException;
import me.allink.deviousmod.managers.ModuleManager;
import me.allink.deviousmod.util.ChatUtil;
import me.allink.deviousmod.util.CommandBlockUtil;
import me.allink.deviousmod.util.ItemUtil;
import me.allink.deviousmod.util.Utilities;
import net.minecraft.nbt.StringNbtReader;

public class LagArmorstandCommand extends ChatCommandBase {
    Utilities utilities;
    CommandBlockUtil commandBlockUtil;
    ItemUtil itemUtil;
    ChatUtil chatUtil;

    public LagArmorstandCommand(int minimumArguments, String command, String usage, String description, List<String> aliases) {
        super(minimumArguments, command, usage, description, aliases);
        utilities = Utilities.getInstance();
        itemUtil = utilities.getItemUtil();
        chatUtil = utilities.getChatUtil();
    }


    @Override
    public void execute(String[] args) throws NotEnoughArgumentsException {
        super.execute(args);
        chatUtil.sendMessage("Since this command can cause A LOT of lag, NoRender has automatically been turned on. It is not recommended that you disable it while using this armor stand!");
        ModuleManager.getModule("NoRender").toggled = true;

        String nbt = "{display:{Name:'{\"text\":\"Devious Armor Stand\",\"color\":\"white\",\"bold\":\"false\",\"italic\":\"false\"}'}}";
        try {
            itemUtil.giveItem("armor_stand", (byte) 1, StringNbtReader.parse(nbt));
        } catch (CommandSyntaxException e) {
            e.printStackTrace();
        }
    }
}
