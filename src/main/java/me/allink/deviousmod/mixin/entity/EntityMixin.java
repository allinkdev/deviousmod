package me.allink.deviousmod.mixin.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public class EntityMixin {
    @Inject(method = "readNbt(Lnet/minecraft/nbt/NbtCompound;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;populateCrashReport(Lnet/minecraft/util/crash/CrashReportSection;)V"), cancellable = true)
    public void readNbt(NbtCompound nbt, CallbackInfo ci) {
        Entity entity = ((Entity) (Object) this);
        entity.remove(Entity.RemovalReason.UNLOADED_WITH_PLAYER);
        ci.cancel();
    }

    @Inject(at = @At("RETURN"), method = "isInvisibleTo(Lnet/minecraft/entity/player/PlayerEntity;)Z", cancellable = true)
    public void isInvisible(PlayerEntity player, CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(false);
    }
}
