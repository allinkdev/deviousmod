package me.allink.deviousmod.commands;

import joptsimple.internal.Strings;
import me.allink.deviousmod.command.ChatCommandBase;
import me.allink.deviousmod.exception.NotEnoughArgumentsException;
import me.allink.deviousmod.util.ChatUtil;
import me.allink.deviousmod.util.CommandBlockUtil;
import me.allink.deviousmod.util.Utilities;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;

import java.util.List;

public class CLoopCommand extends ChatCommandBase {
    Utilities utilities;
    CommandBlockUtil commandBlockUtil;
    ChatUtil chatUtil;
    MinecraftClient client;

    public CLoopCommand(int minimumArguments, String command, String usage, String description, List<String> aliases) {
        super(minimumArguments, command, usage, description, aliases);
        this.client = MinecraftClient.getInstance();
        this.utilities = Utilities.getInstance();
        this.commandBlockUtil = utilities.getCommandBlockUtil();
        this.chatUtil = utilities.getChatUtil();
    }


    @Override
    public void execute(String[] args) throws NotEnoughArgumentsException {
        super.execute(args);
        if (args == null) return;
        PlayerEntity player = client.player;
        BlockPos location = player.getBlockPos();
        String providedCommand = Strings.join(args, " ");
        commandBlockUtil.placeLooping(providedCommand.trim());
        chatUtil.sendMessage(String.format("Placing command block loop with command \"%s\" at %d %d %d", providedCommand, location.getX(), location.getY(), location.getZ()));
    }
}
