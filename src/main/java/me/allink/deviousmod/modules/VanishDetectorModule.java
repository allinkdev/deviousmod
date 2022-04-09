package me.allink.deviousmod.modules;

import java.util.UUID;
import me.allink.deviousmod.client.DeviousModClient;
import me.allink.deviousmod.managers.ModuleManager;
import me.allink.deviousmod.module.ModuleBase;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.DisconnectedScreen;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Formatting;

public class VanishDetectorModule extends ModuleBase {
    public VanishDetectorModule(String name, String description, String category,
        ModuleManager manager) {
        super(name, description, category, manager);
    }

    @Override
    public void onEnabled() {
        MinecraftClient client = MinecraftClient.getInstance();
        ClientPlayNetworkHandler networkHandler = client.getNetworkHandler();
        if (!client.isInSingleplayer() && client.world != null) {
            client.world.disconnect();
            client.disconnect();
            client.setScreen(new DisconnectedScreen(null, new LiteralText("Disconnected"), new LiteralText("Rejoin to enable VanishDetector.").formatted(Formatting.GREEN)));
        }
    }


    @Override
    public void onDisabled() {
        for (UUID uuid : DeviousModClient.hiddenPlayers.keySet()) {
            DeviousModClient.inGamePlayers.remove(uuid);
        }
    }
}
