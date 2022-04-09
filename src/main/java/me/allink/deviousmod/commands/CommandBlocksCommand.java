package me.allink.deviousmod.commands;

import me.allink.deviousmod.client.DeviousModClient;
import me.allink.deviousmod.command.ChatCommandBase;
import me.allink.deviousmod.exception.NotEnoughArgumentsException;
import me.allink.deviousmod.util.ChatUtil;
import me.allink.deviousmod.util.Utilities;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.List;

public class CommandBlocksCommand extends ChatCommandBase {
    Utilities utilities;
    ChatUtil chatUtil;

    public CommandBlocksCommand(int minimumArguments, String command, String usage, String description, List<String> aliases) {
        super(minimumArguments, command, usage, description, aliases);
        utilities = Utilities.getInstance();
        chatUtil = utilities.getChatUtil();
    }


    @Override
    public void execute(String[] args) throws NotEnoughArgumentsException {
        super.execute(args);
        List<String> locations = new ArrayList<>();

        for (BlockPos blockPos : DeviousModClient.commandBlocks.keySet()) {
            locations.add(String.format("x: %d, y: %d, z: %d", blockPos.getX(), blockPos.getY(), blockPos.getZ()));
        }

        chatUtil.sendMessage(String.format("[%s]", String.join(", ", locations)));
    }
}
