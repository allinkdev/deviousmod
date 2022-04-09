package me.allink.deviousmod.commands;

import me.allink.deviousmod.command.ChatCommandBase;
import me.allink.deviousmod.exception.NotEnoughArgumentsException;
import me.allink.deviousmod.util.ChatUtil;
import me.allink.deviousmod.util.Utilities;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;

import java.util.List;

//From hhhzzzsss' HClientv1
public class RandomTPCommand extends ChatCommandBase {
    Utilities utilities;
    ChatUtil chatUtil;

    public RandomTPCommand(int minimumArguments, String command, String usage, String description, List<String> aliases) {
        super(minimumArguments, command, usage, description, aliases);
        utilities = Utilities.getInstance();
        chatUtil = utilities.getChatUtil();
    }


    private static void setPosition(double x, double y, double z, MinecraftClient mc) {
        mc.player.setPos(x, y, z);
        mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.Full(mc.player.getX(), mc.player.getBoundingBox().minY, mc.player.getZ(), mc.player.getYaw(), mc.player.getPitch(), false));
    }


    @Override
    public void execute(String[] args) throws NotEnoughArgumentsException {
        super.execute(args);
        chatUtil.sendMessage("Teleporting to a random location...");
        MinecraftClient client = MinecraftClient.getInstance();
        double x = client.player.getX();
        double y = client.player.getY();
        double z = client.player.getZ();
        double x_change = (Math.random() - 0.5) * 2000.0;
        double z_change = (Math.random() - 0.5) * 2000.0;
        double y_change = 256 - y;
        while (y_change > 50.0) {
            y += 50.0;
            y_change -= 50.0;
            setPosition(x, y, z, client);
        }
        if (y_change > 0.0) {
            y += y_change;
            setPosition(x, y, z, client);
        }
        x_change /= 15.0;
        z_change /= 15.0;
        for (int i = 0; i < 15; i++) {
            x += x_change;
            z += z_change;
            setPosition(x, y, z, client);
        }
    }
}
