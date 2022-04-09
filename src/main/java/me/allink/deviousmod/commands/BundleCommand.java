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

public class BundleCommand extends ChatCommandBase {
    Utilities utilities;
    ChatUtil chatUtil;
    ItemUtil itemUtil;

    public BundleCommand(int minimumArguments, String command, String usage, String description, List<String> aliases) {
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

        chatUtil.sendMessage(String.format("Giving you a \"%s\" with %s NBT in a bundle", item, (nbt == null) ? "no" : nbt.trim() + " as the"));

        List<String> bundleItems = new ArrayList<>();

        if (nbt != null) {
            bundleItems.add(String.format("{Count:1b, Slot:0, id:\"%s\", tag:%s}", item, nbt));
        } else {
            bundleItems.add(String.format("{Count:1b, Slot:0, id:\"%s\"}", item));
        }

        String bundleNbt = String.format("{Items:[%s],display:{Name:'{\"text\":\"Devious Bundle\",\"color\":\"yellow\",\"italic\":\"false\"}'}}", Strings.join(bundleItems, ","));

        try {
            itemUtil.giveItem("minecraft:bundle", (byte) 1, bundleNbt);
        } catch (CommandSyntaxException e) {
            chatUtil.sendMessage(new LiteralText(e.getMessage()).formatted(Formatting.RED));
        }
    }
}
