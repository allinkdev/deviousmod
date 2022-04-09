package me.allink.deviousmod.commands;

import java.util.List;
import java.util.UUID;
import me.allink.deviousmod.client.DeviousModClient;
import me.allink.deviousmod.command.ChatCommandBase;
import me.allink.deviousmod.exception.NotEnoughArgumentsException;
import me.allink.deviousmod.managers.ModuleManager;
import me.allink.deviousmod.modules.VanishDetectorModule;
import me.allink.deviousmod.util.ChatUtil;
import me.allink.deviousmod.util.Utilities;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.LiteralText;

public class PlayersCommand extends ChatCommandBase {
    Utilities utilities;
    ChatUtil chatUtil;

    public PlayersCommand(int minimumArguments, String command, String usage, String description, List<String> aliases) {
        super(minimumArguments, command, usage, description, aliases);
        utilities = Utilities.getInstance();
        chatUtil = utilities.getChatUtil();
    }


    @Override
    public void execute(String[] args) throws NotEnoughArgumentsException {
        super.execute(args);
        MinecraftClient client = MinecraftClient.getInstance();
        VanishDetectorModule vanishDetectorModule = (VanishDetectorModule) ModuleManager.getModule("VanishDetector");
        if (vanishDetectorModule.isToggled()) {
            for (UUID uuid : DeviousModClient.inGamePlayers.keySet()) {
                if (!uuid.equals(client.player.getUuid())) {
                    chatUtil.sendMessage(String.format("%s (%s, vanished: %b)", DeviousModClient.inGamePlayers.get(uuid).getProfile().getName().replaceAll("§", "&"), uuid, false));
                }
            }

            for (UUID uuid : DeviousModClient.hiddenPlayers.keySet()) {
                if (!uuid.equals(client.player.getUuid())) {
                    chatUtil.sendMessage(String.format("%s (%s, vanished: %b)", DeviousModClient.hiddenPlayers.get(uuid).getProfile().getName().replaceAll("§", "&"), uuid, true));
                }
            }
        } else {
            chatUtil.sendMessage(new LiteralText("VanishDetector must be enabled for this command to work."));
        }
    }
}
