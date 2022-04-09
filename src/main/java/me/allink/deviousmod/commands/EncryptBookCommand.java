package me.allink.deviousmod.commands;

import com.google.common.base.Splitter;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import joptsimple.internal.Strings;
import me.allink.deviousmod.command.ChatCommandBase;
import me.allink.deviousmod.exception.NotEnoughArgumentsException;
import me.allink.deviousmod.util.ChatUtil;
import me.allink.deviousmod.util.EncryptionUtil;
import me.allink.deviousmod.util.ItemUtil;
import me.allink.deviousmod.util.Utilities;
import net.minecraft.client.MinecraftClient;
import net.minecraft.nbt.StringNbtReader;

import java.util.ArrayList;
import java.util.List;

public class EncryptBookCommand extends ChatCommandBase {
    Utilities utilities;
    ChatUtil chatUtil;
    ItemUtil itemUtil;

    public EncryptBookCommand(int minimumArguments, String command, String usage, String description, List<String> aliases) {
        super(minimumArguments, command, usage, description, aliases);
        utilities = Utilities.getInstance();
        chatUtil = utilities.getChatUtil();
        itemUtil = utilities.getItemUtil();
    }


    @Override
    public void execute(String[] args) throws NotEnoughArgumentsException {
        super.execute(args);
        MinecraftClient client = MinecraftClient.getInstance();
        chatUtil.sendMessage("Generating encrypted book...");
        Iterable<String> toEncrypt = Splitter.fixedLength(300).split(String.join(" ", args));
        List<String> encryptedPages = new ArrayList<>();

        for (String s : toEncrypt) {
            encryptedPages.add(String.format("'{\"text\":\"%s\"}'", EncryptionUtil.encrypt(s)));
        }


        String nbt = String.format("{pages:[%s],author:\"%s\",title:\"%s\"}", Strings.join(encryptedPages, ",").replaceAll("&", "§"), client.player.getName().getString(), "Encrypted Message");
        System.out.println(nbt);
        try {
            itemUtil.giveItem("written_book", (byte) 1, StringNbtReader.parse(nbt));
        } catch (CommandSyntaxException e) {
            e.printStackTrace();
        }

    }
}