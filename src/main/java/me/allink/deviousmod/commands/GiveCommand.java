package me.allink.deviousmod.commands;

import joptsimple.internal.Strings;
import me.allink.deviousmod.command.ChatCommandBase;
import me.allink.deviousmod.exception.NotEnoughArgumentsException;
import me.allink.deviousmod.util.ChatUtil;
import me.allink.deviousmod.util.ItemUtil;
import me.allink.deviousmod.util.Utilities;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Formatting;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GiveCommand extends ChatCommandBase {
    MinecraftClient client;
    Utilities utilities;
    ItemUtil itemUtil;
    ChatUtil chatUtil;

    public GiveCommand(int minimumArguments, String command, String usage, String description, List<String> aliases) {
        super(minimumArguments, command, usage, description, aliases);
        this.client = MinecraftClient.getInstance();
        this.utilities = Utilities.getInstance();
        this.itemUtil = utilities.getItemUtil();
        this.chatUtil = utilities.getChatUtil();
    }


    public void execute(String[] args) throws NotEnoughArgumentsException {
        super.execute(args);
        if (args == null) return;
        final String originalItem = args[0];
        final String item = (args[0].startsWith("minecraft:")) ? args[0] : "minecraft:" + args[0];
        String nbt = null;
        if (args.length > 1) {
            ArrayList<String> nbtArgs = new ArrayList<>(Arrays.asList(args));
            nbtArgs.remove(originalItem);
            nbt = Strings.join(nbtArgs, " ");
        }

        chatUtil.sendMessage(String.format("Giving you a \"%s\" with %s NBT", item, (nbt == null) ? "no" : nbt.trim() + " as the"));

        if (nbt != null) {
            try {
                itemUtil.giveItem(item, (byte) 1, nbt);
            } catch (Exception e) {
                chatUtil.sendMessage(new LiteralText(e.getMessage()).formatted(Formatting.RED));
            }
        } else {
            itemUtil.giveItem(item, (byte) 1);
        }
    }
}
