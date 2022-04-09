package me.allink.deviousmod.commands;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import joptsimple.internal.Strings;
import me.allink.deviousmod.command.ChatCommandBase;
import me.allink.deviousmod.exception.NotEnoughArgumentsException;
import me.allink.deviousmod.util.ChatUtil;
import me.allink.deviousmod.util.ItemUtil;
import me.allink.deviousmod.util.Utilities;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Formatting;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SpawnerCommand extends ChatCommandBase {
    Utilities utilities;
    ItemUtil itemUtil;
    ChatUtil chatUtil;

    public SpawnerCommand(int minimumArguments, String command, String usage, String description, List<String> aliases) {
        super(minimumArguments, command, usage, description, aliases);
        this.utilities = Utilities.getInstance();
        this.itemUtil = utilities.getItemUtil();
        this.chatUtil = utilities.getChatUtil();
    }


    @Override
    public void execute(String[] args) throws NotEnoughArgumentsException {
        super.execute(args);
        String entity = args[0];
        String nbt = null;
        if (args.length > 1) {
            ArrayList<String> nbtArgs = new ArrayList<>(Arrays.asList(args));
            nbtArgs.remove(entity);
            nbt = Strings.join(nbtArgs, " ");
        }

        chatUtil.sendMessage(String.format("Giving you a \"%s\" spawner with %s NBT", entity, (nbt == null) ? "no extra" : nbt.trim() + " as the extra"));

        if (nbt != null) {
            try {
                itemUtil.giveItem("spawner", (byte) 1, String.format("{BlockEntityTag:{Delay:-1, SpawnData:{id:\"%s\",%s}, SpawnPotentials:[{Weight: 1000, Entity:{id:\"%s\"}, %s]}}}", entity, nbt.replaceFirst("\\{", "").replaceFirst("}", ""), entity, nbt));
            } catch (Exception e) {
                chatUtil.sendMessage(new LiteralText("Failed to parse NBT. Did you mistype something within the NBT?").formatted(Formatting.RED));
                try {
                    itemUtil.giveItem("spawner", (byte) 1, String.format("{BlockEntityTag:{Delay: -1, SpawnData:{id:\"%s\"}, SpawnPotentials:[{Weight: 1000, Entity:{id:\"%s\"}}]}}", entity, entity));
                } catch (CommandSyntaxException ex) {
                    ex.printStackTrace();
                }
            }
        } else {
            try {
                itemUtil.giveItem("spawner", (byte) 1, String.format("{BlockEntityTag:{Delay: -1, SpawnData:{id:\"%s\"}, SpawnPotentials:[{Weight: 1000, Entity:{id:\"%s\"}}]}}", entity, entity));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
