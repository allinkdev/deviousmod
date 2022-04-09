package me.allink.deviousmod.mixin.network;

import me.allink.deviousmod.util.ChatUtil;
import me.allink.deviousmod.util.Utilities;
import net.minecraft.nbt.NbtTagSizeTracker;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(PacketByteBuf.class)
public class PacketByteBufMixin {
    @ModifyArg(method = "readNbt*", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/PacketByteBuf;readNbt(Lnet/minecraft/nbt/NbtTagSizeTracker;)Lnet/minecraft/nbt/NbtCompound;"))
    private NbtTagSizeTracker increaseLimit(NbtTagSizeTracker in) {
        Utilities utilities = Utilities.getInstance();
        ChatUtil chatUtil = utilities.getChatUtil();

        chatUtil.sendMessage(new LiteralText("Avoided invalid packet disconnect. (dbg info: increaselimit)").formatted(Formatting.GOLD));
        return NbtTagSizeTracker.EMPTY;
    }
}
