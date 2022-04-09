package me.allink.deviousmod.mixin.entity;

import net.minecraft.client.render.entity.PufferfishEntityRenderer;
import net.minecraft.entity.passive.PufferfishEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(PufferfishEntityRenderer.class)
public class PufferFishEntityRendererMixin {
    @Redirect(method = "render(Lnet/minecraft/entity/passive/PufferfishEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/passive/PufferfishEntity;getPuffState()I"))
    public int getPuffState(PufferfishEntity instance) {
        int originalPuffState = instance.getPuffState();
        if (originalPuffState < 0) {
            return 0;
        } else return Math.min(originalPuffState, 2);
    }
}
