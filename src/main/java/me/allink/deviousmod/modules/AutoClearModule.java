package me.allink.deviousmod.modules;

import me.allink.deviousmod.managers.ModuleManager;
import me.allink.deviousmod.module.ModuleBase;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;

public class AutoClearModule extends ModuleBase {

    public AutoClearModule(String name, String description, String category,
        ModuleManager manager) {
        super(name, description, category, manager);
    }

    @Override
    public void tick(MinecraftClient client) {
        if (client.world != null) {
            if (client.player.isCreative()) {
                PlayerInventory playerInventory = client.player.getInventory();

                for (int i = 9; i <= 44; i++) {
                    client.interactionManager.clickCreativeStack(ItemStack.EMPTY, i);
                }
            }
        }
    }
}
