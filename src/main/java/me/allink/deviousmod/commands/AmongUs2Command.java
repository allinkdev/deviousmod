package me.allink.deviousmod.commands;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import me.allink.deviousmod.client.DeviousModClient;
import me.allink.deviousmod.command.ChatCommandBase;
import me.allink.deviousmod.exception.NotEnoughArgumentsException;
import me.allink.deviousmod.util.ChatUtil;
import me.allink.deviousmod.util.ItemUtil;
import me.allink.deviousmod.util.Utilities;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Formatting;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;

public class AmongUs2Command extends ChatCommandBase {
    Utilities utilities;
    ItemUtil itemUtil;
    ChatUtil chatUtil;

    public AmongUs2Command(int minimumArguments, String command, String usage, String description, List<String> aliases) {
        super(minimumArguments, command, usage, description, aliases);
        this.utilities = Utilities.getInstance();
        this.itemUtil = utilities.getItemUtil();
        this.chatUtil = utilities.getChatUtil();
    }

    @Override
    public void execute(String[] args) throws NotEnoughArgumentsException {
        super.execute(args);
        try {
            itemUtil.giveItem("axolotl_spawn_egg", (byte) 1, String.format("{EntityTag:{CustomName:'{\"extra\":\"%s\"}',id:armor_stand,Invisible:1b,NoGravity:1b}}", "XAMONGUSX".repeat(1024 * 7)), 0);
            chatUtil.sendMessage("You are now the VERY sussy wussy impostor from Among Us.");
        } catch (CommandSyntaxException e) {
            e.printStackTrace();
        }
    }
}
