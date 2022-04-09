package me.allink.deviousmod.commands;

import me.allink.deviousmod.command.ChatCommandBase;
import me.allink.deviousmod.util.CommandBlockUtil;
import me.allink.deviousmod.util.EncryptionUtil;
import me.allink.deviousmod.util.Utilities;
import net.minecraft.client.MinecraftClient;

import java.util.List;

public class SteganographyCommand extends ChatCommandBase {


    public SteganographyCommand(int minimumArguments, String command, String usage, String description, List<String> aliases) {
        super(minimumArguments, command, usage, description, aliases);
    }

    public static void sendEncryptedMessage(String input) {
        String toSay = EncryptionUtil.encrypt(input);

        MinecraftClient client = MinecraftClient.getInstance();
        if (toSay.length() >= 256) {
            Utilities utilities = Utilities.getInstance();
            CommandBlockUtil commandBlockUtil = utilities.getCommandBlockUtil();
            commandBlockUtil.placeRepeating(String.format("/sudo %s c:%s", client.player.getUuidAsString(), toSay));
        } else {
            client.player.sendChatMessage(toSay);
        }
        System.out.println(toSay);

    }

    @Override
    public void execute(String[] args) {
        sendEncryptedMessage(String.join(" ", args));
    }
}
