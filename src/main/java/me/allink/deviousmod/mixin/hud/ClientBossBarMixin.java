package me.allink.deviousmod.mixin.hud;

import me.allink.deviousmod.managers.ModuleManager;
import me.allink.deviousmod.modules.NoRenderModule;
import net.minecraft.client.gui.hud.ClientBossBar;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(ClientBossBar.class)
public class ClientBossBarMixin {
    @ModifyVariable(method = "<init>(Ljava/util/UUID;Lnet/minecraft/text/Text;FLnet/minecraft/entity/boss/BossBar$Color;Lnet/minecraft/entity/boss/BossBar$Style;ZZZ)V", at = @At("HEAD"), index = 2)
    private static Text init(Text text) {
        NoRenderModule noRenderModule = (NoRenderModule) ModuleManager.getModule("NoRender");
        if (!noRenderModule.toggled) return text;

        if (text.getString().length() > 16) {
            return new LiteralText(text.getString().substring(0, 16));
        }

        return new LiteralText(text.getString());
    }
}
