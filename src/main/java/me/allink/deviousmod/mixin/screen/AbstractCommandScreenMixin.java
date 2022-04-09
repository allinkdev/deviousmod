package me.allink.deviousmod.mixin.screen;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.CommandSuggestor;
import net.minecraft.client.gui.screen.ingame.AbstractCommandBlockScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractCommandBlockScreen.class)
public class AbstractCommandScreenMixin {

    @Shadow
    private CommandSuggestor commandSuggestor;

    @Inject(method = "removed()V", at = @At("HEAD"), cancellable = true)
    public void removed(CallbackInfo ci) {
        MinecraftClient.getInstance().keyboard.setRepeatEvents(true);
        ci.cancel();
    }

    @Inject(method = "onCommandChanged(Ljava/lang/String;)V", at = @At("HEAD"), cancellable = true)
    public void onCommandChanged(String text, CallbackInfo ci) {
        if (this.commandSuggestor == null) {
            ci.cancel();
        }
    }
}
