package me.allink.deviousmod.commands;

import me.allink.deviousmod.command.ChatCommandBase;
import me.allink.deviousmod.exception.NotEnoughArgumentsException;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.BlockPos;

import java.util.List;

public class QueryBlockCommand extends ChatCommandBase {
    public QueryBlockCommand(int minimumArguments, String command, String usage, String description, List<String> aliases) {
        super(minimumArguments, command, usage, description, aliases);
    }


    @Override
    public void execute(String[] args) throws NotEnoughArgumentsException {
        super.execute(args);
        double x = Double.parseDouble(args[0]);
        double y = Double.parseDouble(args[1]);
        double z = Double.parseDouble(args[2]);
        MinecraftClient client = MinecraftClient.getInstance();
        client.getNetworkHandler().getDataQueryHandler().queryBlockNbt(new BlockPos(x, y, z), (nbtCompound) -> {
            System.out.println(nbtCompound.toString());
        });
    }
}
