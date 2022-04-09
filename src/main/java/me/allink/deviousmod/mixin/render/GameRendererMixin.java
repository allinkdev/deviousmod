package me.allink.deviousmod.mixin.render;

import me.allink.deviousmod.managers.ModuleManager;
import me.allink.deviousmod.modules.HandsR4N00bsModule;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {

	@Inject(method = "renderHand(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/Camera;F)V", at = @At("HEAD"), cancellable = true)
	public void renderArm(MatrixStack matrices, Camera camera, float tickDelta,
		CallbackInfo ci) {
		HandsR4N00bsModule handsR4N00bsModule = (HandsR4N00bsModule) ModuleManager.getModule("HandsR4N00bs");
		if(handsR4N00bsModule.isToggled()) {
			ci.cancel();
		}
	}
}
