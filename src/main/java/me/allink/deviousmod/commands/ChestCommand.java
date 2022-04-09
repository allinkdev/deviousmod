package me.allink.deviousmod.commands;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import joptsimple.internal.Strings;
import me.allink.deviousmod.command.ChatCommandBase;
import me.allink.deviousmod.util.ChatUtil;
import me.allink.deviousmod.util.ItemUtil;
import me.allink.deviousmod.util.Utilities;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Formatting;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ChestCommand extends ChatCommandBase {
    Utilities utilities;
    ChatUtil chatUtil;
    ItemUtil itemUtil;
    String[] rainbow = {"red_stained_glass_pane", "orange_stained_glass_pane", "yellow_stained_glass_pane", "lime_stained_glass_pane", "cyan_stained_glass_pane", "blue_stained_glass_pane", "purple_stained_glass_pane"};

    public ChestCommand(int minimumArguments, String command, String usage, String description, List<String> aliases) {
        super(minimumArguments, command, usage, description, aliases);
        utilities = Utilities.getInstance();
        chatUtil = utilities.getChatUtil();
        itemUtil = utilities.getItemUtil();
    }


    @Override
    public void execute(String[] args) {
        final String originalItem = args[0];
        final String item = (args[0].startsWith("minecraft:")) ? args[0] : "minecraft:" + args[0];
        String nbt = null;
        if (args.length > 1) {
            ArrayList<String> nbtArgs = new ArrayList<>(Arrays.asList(args));
            nbtArgs.remove(originalItem);
            nbt = Strings.join(nbtArgs, " ");
        }

        chatUtil.sendMessage(String.format("Giving you a \"%s\" with %s NBT in a chest", item, (nbt == null) ? "no" : nbt.trim() + " as the"));

        List<String> chestItems = new ArrayList<>();

        int x = 0;
        for (int i = 0; i < 27; i++) {
            if (i == 13) {
                if (nbt != null) {
                    chestItems.add(String.format("{Count:1b, Slot:%s, id:\"%s\", tag:%s}", i, item, nbt));
                } else {
                    chestItems.add(String.format("{Count:1b, Slot:%s, id:\"%s\"}", i, item));
                }
            } else {
                chestItems.add(String.format("{Count:1b, Slot:%s, id:\"minecraft:%s\", tag:{display:{Name:'{\"text\":\"Devious Mod\",\"color\":\"yellow\",\"italic\":\"false\"}'}}}", i, rainbow[x]));
            }
            x++;
            if (x >= rainbow.length) {
                x = 0;
            }
        }

        String chestNbt = String.format("{BlockEntityTag:{Items:[%s]},display:{Name:'{\"text\":\"Devious Chest\",\"color\":\"yellow\",\"italic\":\"false\"}'}}", Strings.join(chestItems, ","));

        try {
            itemUtil.giveItem("minecraft:chest", (byte) 1, chestNbt);
        } catch (CommandSyntaxException e) {
            chatUtil.sendMessage(new LiteralText(e.getMessage()).formatted(Formatting.RED));
        }
    }
}
