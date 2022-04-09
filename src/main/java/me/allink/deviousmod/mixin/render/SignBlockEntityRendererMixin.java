package me.allink.deviousmod.mixin.render;

import me.allink.deviousmod.managers.ModuleManager;
import net.minecraft.client.render.block.entity.SignBlockEntityRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SignBlockEntityRenderer.class)
public class SignBlockEntityRendererMixin {

	@Inject(method = "render*", at = @At("HEAD"), cancellable = true)
	public void render(CallbackInfo ci) {
		if (ModuleManager.isEnabled("NoRender")) {
			ci.cancel();
		}
	}
}
