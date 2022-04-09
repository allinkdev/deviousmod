package me.allink.deviousmod.modules;

import java.util.List;
import me.allink.deviousmod.managers.ModuleManager;
import me.allink.deviousmod.module.ModuleBase;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.OtherClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Box;

public class ClownFishAuraModule extends ModuleBase {
    public String name;
    public String description;

    public ClownFishAuraModule(String name, String description, String category,
        ModuleManager manager) {
        super(name, description, category, manager);
    }

    @Override
    public void tick(MinecraftClient client) {
        super.tick(client);
        if (client.world == null || client.player == null) return;
        if (!client.player.isHolding(Items.TROPICAL_FISH)) return;

        List<Entity> otherPlayers = client.world.getOtherEntities(client.player, new Box(client.player.getPos().subtract(6, 6, 6), client.player.getPos().add(6, 6, 6)), (entity -> entity instanceof OtherClientPlayerEntity));
        if (otherPlayers.size() > 0) {
            client.interactionManager.interactItem(client.player, client.world, Hand.MAIN_HAND);
        }
    }
}
