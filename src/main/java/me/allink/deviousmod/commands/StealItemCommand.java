package me.allink.deviousmod.commands;

import me.allink.deviousmod.client.DeviousModClient;
import me.allink.deviousmod.command.ChatCommandBase;
import me.allink.deviousmod.exception.NotEnoughArgumentsException;
import me.allink.deviousmod.util.ChatUtil;
import me.allink.deviousmod.util.ItemUtil;
import me.allink.deviousmod.util.Utilities;
import net.minecraft.item.ItemStack;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Formatting;

import java.util.List;
import java.util.UUID;

public class StealItemCommand extends ChatCommandBase {
    Utilities utilities;
    ItemUtil itemUtil;
    ChatUtil chatUtil;

    public StealItemCommand(int minimumArguments, String command, String usage, String description, List<String> aliases) {
        super(minimumArguments, command, usage, description, aliases);
        utilities = Utilities.getInstance();
        itemUtil = utilities.getItemUtil();
        chatUtil = utilities.getChatUtil();
    }


    @Override
    public void execute(String[] args) throws NotEnoughArgumentsException {
        super.execute(args);
        String target = args[0];

        if (!args[0].contains("-")) {
            UUID id = DeviousModClient.uuidCache.get(target);
            if (id != null) {
                target = id.toString();
            }
        }

        try {
            UUID id = UUID.fromString(target);
            ItemStack itemStack = DeviousModClient.heldItemMap.get(id);

            if (itemStack == null) {
                chatUtil.sendMessage(new LiteralText("Could not find what they are holding.").formatted(Formatting.RED));
                return;
            }

            itemUtil.giveItem(itemStack.getItem().toString(), (byte) 1, itemStack.getNbt());
        } catch (Exception e) {
            chatUtil.sendMessage(new LiteralText("Please enter a valid username or UUID.").formatted(Formatting.RED));
            e.printStackTrace();
        }


    }
}
