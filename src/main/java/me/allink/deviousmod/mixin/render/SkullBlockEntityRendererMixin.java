package me.allink.deviousmod.mixin.render;

import me.allink.deviousmod.managers.ModuleManager;
import net.minecraft.block.AbstractSkullBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.SkullBlock.Type;
import net.minecraft.block.entity.SkullBlockEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.SkullBlockEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SkullBlockEntityRenderer.class)
public class SkullBlockEntityRendererMixin {

	@Inject(method = "render(Lnet/minecraft/block/entity/SkullBlockEntity;FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;II)V", at = @At("HEAD"), cancellable = true)
	public void render(SkullBlockEntity skullBlockEntity, float f, MatrixStack matrixStack,
		VertexConsumerProvider vertexConsumerProvider, int i, int j, CallbackInfo ci) {
		BlockState blockState = skullBlockEntity.getCachedState();
		if (((AbstractSkullBlock) blockState.getBlock()).getSkullType() == Type.PLAYER) {
			if (ModuleManager.isEnabled("NoRender")) {
				ci.cancel();
			}
		}
	}
}
