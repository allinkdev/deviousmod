package me.allink.deviousmod.modules;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import me.allink.deviousmod.client.DeviousModClient;
import me.allink.deviousmod.json.config.Config;
import me.allink.deviousmod.managers.ModuleManager;
import me.allink.deviousmod.module.ModuleBase;
import net.minecraft.client.MinecraftClient;

public class JoinCommandsModule extends ModuleBase {
    public JoinCommandsModule(String name, String description, String category,
        ModuleManager manager) {
        super(name, description, category, manager);
    }

    @Override
    public void onJoin(final MinecraftClient client) {
        Config config = DeviousModClient.CONFIG;
        List<String> chatCommands = config.chatCommands;
        Timer t = new Timer();

        int i = 0;
        for (String chatCommand : chatCommands) {
            t.schedule(new TimerTask() {
                @Override
                public void run() {
                    if (client.player == null) return;
                    client.player.sendChatMessage(chatCommand);
                }
            }, 200L * i);
            i++;
        }
    }
}
