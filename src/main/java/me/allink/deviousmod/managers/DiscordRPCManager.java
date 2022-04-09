package me.allink.deviousmod.managers;

import me.allink.deviousmod.client.DeviousModClient;
import me.allink.deviousmod.manager.ManagerBase;
import me.allink.deviousmod.modules.DiscordRPCModule;
import net.arikia.dev.drpc.DiscordEventHandlers;
import net.arikia.dev.drpc.DiscordRPC;
import net.arikia.dev.drpc.DiscordRichPresence;
import net.minecraft.client.MinecraftClient;

public class DiscordRPCManager extends ManagerBase {
    final DiscordRPCModule discordRPCModule = (DiscordRPCModule) ModuleManager.getModule("DiscordRPC");
    public boolean enabled;

    public DiscordRPCManager(boolean enabled) {
        this.enabled = enabled;
    }

    public void onMainMenu() {
        if (!discordRPCModule.isToggled()) return;
        DiscordRichPresence presence = new DiscordRichPresence.Builder(DeviousModClient.CONFIG.mainMenuText).setDetails(String.format("Using DeviousMod %s", DeviousModClient.version)).setSmallImage("astolfo", DeviousModClient.CONFIG.iconText).setBigImage("logo", DeviousModClient.CONFIG.bigIconText).build();
        DiscordRPC.discordUpdatePresence(presence);
    }

    public void onPlayGame() {
        if (!discordRPCModule.isToggled()) return;
        String state;
        String icon;

        MinecraftClient client = MinecraftClient.getInstance();
        if (client.isInSingleplayer()) {
            state = "Playing Singleplayer";
            icon = "unknown";
        } else {
            if (DeviousModClient.CONFIG.showIP) {
                String address = client.getCurrentServerEntry().address;
                state = String.format(DeviousModClient.CONFIG.inGameText, address);
                if (address.contains("kaboom")) {
                    icon = "kaboom";
                } else if (address.contains("veast")) {
                    icon = "avast";
                } else if (address.contains("hypixel")) {
                    icon = "hypixel";
                } else if (address.contains("cubecraft")) {
                    icon = "cubecraft";
                } else if (address.contains("minehut")) {
                    icon = "minehut";
                } else if (address.contains("mineplex")) {
                    icon = "mineplex";
                } else if (address.contains("totalfreedom")) {
                    icon = "totalfreedom";
                } else {
                    icon = "unknown";
                }
            } else {
                state = String.format(DeviousModClient.CONFIG.inGameText, "Multiplayer");
                icon = "unknown";
            }
        }

        DiscordRichPresence presence = new DiscordRichPresence.Builder(state).setDetails(String.format("Using DeviousMod %s", DeviousModClient.version)).setSmallImage(icon, DeviousModClient.CONFIG.iconText).setBigImage("logo", DeviousModClient.CONFIG.bigIconText).build();
        DiscordRPC.discordUpdatePresence(presence);
    }

    public void onDisable() {
        System.out.println("Discord RPC disable");
        DiscordRPC.discordShutdown();
    }

    public void onEnable() {
        System.out.println("Discord RPC enable");
        DiscordEventHandlers handlers = new DiscordEventHandlers.Builder().build();
        DiscordRPC.discordInitialize("939402184850817074", handlers, true);
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.world != null) {
            onPlayGame();
        } else {
            onMainMenu();
        }
    }

    @Override
    public void init() {
        if (discordRPCModule.isToggled()) {
            onEnable();
        }
    }
}
