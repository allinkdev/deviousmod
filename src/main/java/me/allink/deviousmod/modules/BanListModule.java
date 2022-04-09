package me.allink.deviousmod.modules;

import java.util.Timer;
import java.util.TimerTask;
import me.allink.deviousmod.client.DeviousModClient;
import me.allink.deviousmod.managers.ModuleManager;
import me.allink.deviousmod.module.ModuleBase;
import me.allink.deviousmod.util.BanUtil;
import me.allink.deviousmod.util.Utilities;
import net.minecraft.client.MinecraftClient;

public class BanListModule extends ModuleBase {

    public BanListModule(String name, String description, String category,
        ModuleManager manager) {
        super(name, description, category, manager);
    }

    @Override
    public void onJoin(MinecraftClient client) {
        Timer t = new Timer();
        t.schedule(new TimerTask() {
            @Override
            public void run() {
                Utilities utilities = Utilities.getInstance();
                BanUtil banUtil = utilities.getBanUtil();

                for (String s : DeviousModClient.CONFIG.banList) {
                    banUtil.banPlayer(s);
                }
            }
        }, 3000L);
    }
}
