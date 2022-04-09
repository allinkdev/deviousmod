package me.allink.deviousmod.mixin;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EnchantmentHelper.class)
public class EnchantmentHelperMixin {
	@Inject(method = "onTargetDamaged(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/entity/Entity;)V", at = @At("HEAD"), cancellable = true)
	private static void onTargetDamaged(LivingEntity user, Entity target, CallbackInfo ci) {
		ci.cancel();
	}

	@Inject(method = "onUserDamaged(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/entity/Entity;)V", at = @At("HEAD"), cancellable = true)
	private static void onUserDamaged(LivingEntity user, Entity attacker, CallbackInfo ci) {
		ci.cancel();
	}
}
