package me.allink.deviousmod.modules;

import me.allink.deviousmod.managers.ModuleManager;
import me.allink.deviousmod.module.ModuleBase;
import me.allink.deviousmod.util.ItemUtil;
import me.allink.deviousmod.util.Utilities;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.MathHelper;

public class RainbowArmourModule extends ModuleBase {
    //String enchantmentNbt;

    public RainbowArmourModule(String name, String description, String category,
        ModuleManager manager) {
        super(name, description, category, manager);
    }

    @Override
    public void tick(MinecraftClient client) {
        if (client.world == null) return;
        try {

            Utilities utilities = Utilities.getInstance();
            ItemUtil itemUtil = utilities.getItemUtil();
            float x = System.currentTimeMillis() % 2000 / 1000F;
            float red = 0.5F + 0.5F * MathHelper.sin(x * (float) Math.PI);
            float green =
                    0.5F + 0.5F * MathHelper.sin((x + 4F / 3F) * (float) Math.PI);
            float blue =
                    0.5F + 0.5F * MathHelper.sin((x + 8F / 3F) * (float) Math.PI);
            int textColor = 0x04 << 16 | (int) (red * 256) << 16
                    | (int) (green * 256) << 8 | (int) (blue * 256);
            String format = String.format("{display:{color:%s}}", textColor);
            //System.out.println(format);
            itemUtil.giveItem("leather_helmet", (byte) 1, format, -31);
            itemUtil.giveItem("leather_chestplate", (byte) 1, format, -30);
            itemUtil.giveItem("leather_leggings", (byte) 1, format, -29);
            itemUtil.giveItem("leather_boots", (byte) 1, format, -28);
        } catch (Exception e) {

        }
    }
}
