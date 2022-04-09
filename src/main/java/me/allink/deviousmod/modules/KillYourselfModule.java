package me.allink.deviousmod.modules;

import java.util.Timer;
import java.util.TimerTask;
import me.allink.deviousmod.managers.ModuleManager;
import me.allink.deviousmod.module.ModuleBase;
import net.minecraft.client.MinecraftClient;

public class KillYourselfModule extends ModuleBase {
    Timer piss;

    public KillYourselfModule(String name, String description, String category,
        ModuleManager manager) {
        super(name, description, category, manager);
    }

    @Override
    public void onEnabled() {
        super.onEnabled();
        MinecraftClient client = MinecraftClient.getInstance();
        this.piss = new Timer();
        piss.schedule(new TimerTask() {
            @Override
            public void run() {
                if (client.world == null) {
                    ModuleManager.getModule("KillYourself").setToggled(false);
                    piss.cancel();
                    return;
                }

                client.player.sendChatMessage("/suicide");
            }
        }, 0L, 1000L);
    }

    @Override
    public void onLeave(MinecraftClient client) {
        super.onLeave(client);
        piss.cancel();
        ModuleManager.getModule("KillYourself").setToggled(false);
    }

    @Override
    public void onDisabled() {
        super.onDisabled();
        piss.cancel();
    }
}
