package me.allink.deviousmod.modules;

import me.allink.deviousmod.manager.Managers;
import me.allink.deviousmod.managers.DiscordRPCManager;
import me.allink.deviousmod.managers.ModuleManager;
import me.allink.deviousmod.module.ModuleBase;
import net.minecraft.client.MinecraftClient;

public class DiscordRPCModule extends ModuleBase {
    public DiscordRPCModule(String name, String description, String category,
        ModuleManager manager) {
        super(name, description, category, manager);
    }

    @Override
    public void onEnabled() {
        MinecraftClient client = MinecraftClient.getInstance();
        DiscordRPCManager rpcManager = Managers.getDiscordRPCManager();
        if (rpcManager != null) {
            rpcManager.onEnable();
        }
    }

    @Override
    public void onDisabled() {
        DiscordRPCManager rpcManager = Managers.getDiscordRPCManager();
        rpcManager.onDisable();
    }
}
