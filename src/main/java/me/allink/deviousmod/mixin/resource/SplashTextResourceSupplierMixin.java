package me.allink.deviousmod.mixin.resource;

import net.minecraft.client.resource.SplashTextResourceSupplier;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(SplashTextResourceSupplier.class)
public class SplashTextResourceSupplierMixin {
    @Mutable
    @Shadow
    @Final
    private static Identifier RESOURCE_ID;

    @Inject(method = "prepare(Lnet/minecraft/resource/ResourceManager;Lnet/minecraft/util/profiler/Profiler;)Ljava/util/List;", at = @At("HEAD"))
    public void prepare(ResourceManager resourceManager, Profiler profiler, CallbackInfoReturnable<List<String>> cir) {
        RESOURCE_ID = new Identifier("deviousmod", "splashes.txt");
    }
}
