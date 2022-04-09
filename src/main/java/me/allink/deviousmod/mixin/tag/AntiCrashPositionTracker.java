package me.allink.deviousmod.mixin.tag;

import me.allink.deviousmod.managers.ModuleManager;
import me.allink.deviousmod.modules.NoNBTFuckeryModule;
import me.allink.deviousmod.util.ChatUtil;
import me.allink.deviousmod.util.Utilities;
import net.minecraft.nbt.NbtTagSizeTracker;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(NbtTagSizeTracker.class)
public class AntiCrashPositionTracker {
    @Shadow
    private final long maxBytes;
    Utilities utilities;
    ChatUtil chatUtil;
    @Shadow
    private long allocatedBytes;

    public AntiCrashPositionTracker(long max) {
        this.maxBytes = max;
        this.utilities = new Utilities();
        this.chatUtil = utilities.getChatUtil();
    }

    /**
     * @author hhhzzzsss
     * @reason le trolle
     */
    @Overwrite
    public void add(long bits) {
        NoNBTFuckeryModule noNBTFuckeryModule = (NoNBTFuckeryModule) ModuleManager.getModule("NoNBTFuckery");

        if(noNBTFuckeryModule.isToggled()) return;

        this.allocatedBytes += bits / 8L;
        if (this.allocatedBytes > this.maxBytes) {
            /*MinecraftClient.getInstance().player.sendMessage(new LiteralText(
                "Crash prevented: Tried to read NBT tag that was too big; tried to allocate: " + this.allocatedBytes + "bytes where max allowed: " + this.maxBytes + "b"), false);
            */return;
        }
    }
}
