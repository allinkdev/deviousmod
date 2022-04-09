package me.allink.deviousmod.commands;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.List;
import me.allink.deviousmod.command.ChatCommandBase;
import me.allink.deviousmod.exception.NotEnoughArgumentsException;
import me.allink.deviousmod.managers.ModuleManager;
import me.allink.deviousmod.util.ChatUtil;
import me.allink.deviousmod.util.EncryptionUtil;
import me.allink.deviousmod.util.ItemUtil;
import me.allink.deviousmod.util.Utilities;
import net.minecraft.client.MinecraftClient;
import net.minecraft.nbt.StringNbtReader;

public class EncryptArmorStandCommand extends ChatCommandBase {
    Utilities utilities;
    ItemUtil itemUtil;
    ChatUtil chatUtil;

    public EncryptArmorStandCommand(int minimumArguments, String command, String usage, String description, List<String> aliases) {
        super(minimumArguments, command, usage, description, aliases);
        this.utilities = Utilities.getInstance();
        this.itemUtil = utilities.getItemUtil();
        this.chatUtil = utilities.getChatUtil();
    }


    @Override
    public void execute(String[] args) throws NotEnoughArgumentsException {
        super.execute(args);
        MinecraftClient client = MinecraftClient.getInstance();
        chatUtil.sendMessage("Generating encrypted armour stand...");
        String toEncrypt = String.format("From %s: %s", client.player.getName().getString(), String.join(" ", args));
        ModuleManager.getModule("NoRender").toggled = false;
        String nbt = String.format("{EntityTag:{CustomName:'{\"text\":\"%s\"}',CustomNameVisible:1b}}", EncryptionUtil.encrypt(toEncrypt).replaceAll("&", "§"));
        try {
            itemUtil.giveItem("armor_stand", (byte) 1, StringNbtReader.parse(nbt));
        } catch (CommandSyntaxException e) {
            e.printStackTrace();
        }
    }
}
