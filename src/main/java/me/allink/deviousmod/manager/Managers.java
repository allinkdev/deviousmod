package me.allink.deviousmod.manager;

import me.allink.deviousmod.client.DeviousModClient;
import me.allink.deviousmod.json.config.Config;
import me.allink.deviousmod.managers.ChatCommandManager;
import me.allink.deviousmod.managers.DiscordRPCManager;
import me.allink.deviousmod.managers.ModuleManager;

public class Managers {
    static ChatCommandManager chatCommandManager;
    static ModuleManager moduleManager;
    static DiscordRPCManager discordRPCManager;

    DeviousModClient deviousMod;
    Config config;

    public Managers(Config config, DeviousModClient deviousMod) {
        this.config = config;
        this.deviousMod = deviousMod;
    }

    public static ModuleManager getModuleManager() {
        return moduleManager;
    }

    public static ChatCommandManager getChatCommandManager() {
        return chatCommandManager;
    }

    public static DiscordRPCManager getDiscordRPCManager() {
        return discordRPCManager;
    }

    public void init() {
        chatCommandManager = new ChatCommandManager();
        chatCommandManager.init();
        moduleManager = new ModuleManager(config, deviousMod);
        moduleManager.init();
        discordRPCManager = new DiscordRPCManager(ModuleManager.getModule("DiscordRPC").isToggled());
        discordRPCManager.init();
    }
}
