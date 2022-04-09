package me.allink.deviousmod.mixin.constants;

import net.minecraft.SharedConstants;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SharedConstants.class)
public class SharedConstantsMixin {
    @Inject(method = "stripInvalidChars(Ljava/lang/String;)Ljava/lang/String;", at = @At("RETURN"), cancellable = true)
    private static void stripInvalidChars(String s, CallbackInfoReturnable<String> cir) {
        cir.setReturnValue(s);
    }
}
