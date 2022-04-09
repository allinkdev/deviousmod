package me.allink.deviousmod.mixin.render;

import java.util.UUID;
import me.allink.deviousmod.client.DeviousModClient;
import me.allink.deviousmod.managers.ModuleManager;
import me.allink.deviousmod.modules.LoginNameModule;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(PlayerEntityRenderer.class)
public class PlayerEntityRendererMixin {
    @ModifyVariable(method = "renderLabelIfPresent(Lnet/minecraft/client/network/AbstractClientPlayerEntity;Lnet/minecraft/text/Text;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V", at = @At("HEAD"), index = 2)
    public Text inject(Text text) {
        LoginNameModule loginNameModule = (LoginNameModule) ModuleManager.getModule("LoginName");

        UUID cachedUUID = DeviousModClient.uuidCache.get(text.asString());
        Text cachedText = DeviousModClient.cachedReplacement.get(text);
        if (!loginNameModule.toggled) {
            return text;
        }

        if (cachedText != null) {
            return cachedText;
        }

        String firstUsername = (DeviousModClient.usernameCache.get(cachedUUID) != null) ? DeviousModClient.usernameCache.get(cachedUUID) : "";

        Text newText = Text.of(text.asString().replaceAll("§", "&") + ((loginNameModule.toggled ||
                !firstUsername.equals("")) ? " (" + firstUsername + ")" : text));
        DeviousModClient.cachedReplacement.put(text, newText);
        return newText;
    }

}
