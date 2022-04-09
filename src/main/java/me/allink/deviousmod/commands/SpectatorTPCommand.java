package me.allink.deviousmod.commands;

import me.allink.deviousmod.client.DeviousModClient;
import me.allink.deviousmod.command.ChatCommandBase;
import me.allink.deviousmod.exception.NotEnoughArgumentsException;
import me.allink.deviousmod.util.ChatUtil;
import me.allink.deviousmod.util.Utilities;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.packet.c2s.play.SpectatorTeleportC2SPacket;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Formatting;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

public class SpectatorTPCommand extends ChatCommandBase {
    Utilities utilities;
    ChatUtil chatUtil;
    MinecraftClient client;

    public SpectatorTPCommand(int minimumArguments, String command, String usage, String description, List<String> aliases) {
        super(minimumArguments, command, usage, description, aliases);
        utilities = Utilities.getInstance();
        chatUtil = utilities.getChatUtil();
        client = MinecraftClient.getInstance();
    }


    @Override
    public void execute(String[] args) throws NotEnoughArgumentsException {
        super.execute(args);
        String target = args[0];
        UUID uuid;

        try {
            uuid = UUID.fromString(target);
        } catch (Exception e) {
            try {
                uuid = DeviousModClient.uuidCache.get(target);
            } catch (Exception e1) {
                chatUtil.sendMessage(new LiteralText(String.format("Couldn't find target \"%s\".", target)).formatted(Formatting.RED));
                return;
            }
        }

        if (!client.player.isSpectator()) {
            client.player.sendChatMessage("/gamemode spectator");

            Timer t = new Timer();
            UUID finalUuid = uuid;
            t.schedule(new TimerTask() {
                @Override
                public void run() {
                    client.getNetworkHandler().sendPacket(new SpectatorTeleportC2SPacket(finalUuid));
                    chatUtil.sendMessage(String.format("Teleported to %s.", target));
                }
            }, 290L);
        } else {
            client.getNetworkHandler().sendPacket(new SpectatorTeleportC2SPacket(uuid));
            chatUtil.sendMessage(String.format("Teleported to %s.", target));
        }

    }
}
