package me.allink.deviousmod.commands;

import me.allink.deviousmod.client.DeviousModClient;
import me.allink.deviousmod.command.ChatCommandBase;
import me.allink.deviousmod.exception.NotEnoughArgumentsException;
import me.allink.deviousmod.util.ChatUtil;
import me.allink.deviousmod.util.Utilities;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Formatting;

import java.util.List;
import java.util.UUID;

public class SpectateCommand extends ChatCommandBase {
    Utilities utilities;
    ChatUtil chatUtil;

    public SpectateCommand(int minimumArguments, String command, String usage, String description, List<String> aliases) {
        super(minimumArguments, command, usage, description, aliases);
        utilities = Utilities.getInstance();
        chatUtil = utilities.getChatUtil();
    }


    @Override
    public void execute(String[] args) throws NotEnoughArgumentsException {
        super.execute(args);

        MinecraftClient client = MinecraftClient.getInstance();
        String target = args[0];
        UUID uuid = UUID.randomUUID();

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
        }

        chatUtil.sendMessage(String.format("Now spectating %s", target));
        DeviousModClient.spectating = uuid;
    }
}
