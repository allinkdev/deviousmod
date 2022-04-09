package me.allink.deviousmod.mixin.text;

import me.allink.deviousmod.managers.ModuleManager;
import me.allink.deviousmod.module.ModuleBase;
import net.minecraft.text.Style;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Style.class)
public class StyleMixin {
    @Inject(at = @At("RETURN"), method = "getFont()Lnet/minecraft/util/Identifier;", cancellable = true)
    public void getFont(CallbackInfoReturnable<Identifier> cir) {
        Identifier comic = new Identifier("deviousmod", "comic");
        Identifier roboto = new Identifier("deviousmod", "roboto");
        Identifier inter = new Identifier("deviousmod", "inter");

        if(cir.getReturnValue() != null) {
            if(cir.getReturnValue().getNamespace().equals("deviousmod")) return; // I still like Roboto on my ModuleList
        }

        ModuleBase robotoEverywhereModule = ModuleManager.getModule("RobotoEverywhere");
        ModuleBase comicSansEverywhereModule = ModuleManager.getModule("ComicSansEverywhere");
        ModuleBase interEverywhereModule = ModuleManager.getModule("InterEverywhere");

        if (robotoEverywhereModule.isToggled()) {
            cir.setReturnValue(roboto);
        }

        if (comicSansEverywhereModule.isToggled()) {
            cir.setReturnValue(comic);
        }

        if (interEverywhereModule.isToggled()) {
            cir.setReturnValue(inter);
        }
    }
}
