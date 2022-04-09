package me.allink.deviousmod.mixin.render;

import me.allink.deviousmod.managers.ModuleManager;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(EntityRenderer.class)
public class EntityRendererMixin {

    @ModifyVariable(method = "renderLabelIfPresent(Lnet/minecraft/entity/Entity;Lnet/minecraft/text/Text;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V", at = @At("HEAD"), index = 2)
    public Text injected(Text text) {
        if (ModuleManager.isEnabled("NoRender")) {
            String string = text.getString();
            if (string.length() > 64) {
                string = string.substring(0, 64);
            }

            return new LiteralText(string.replaceAll("§", "&"));
        }

        return text;

        /*
        Text newText = text;
        NoRenderModule noRenderModule = (NoRenderModule) ModuleManager.getModule("NoRender");

        if (text.getString().contains(DeviousModClient.encryptionChar)) {
            if (DeviousModClient.alreadyDecrypted.containsKey(text.getString())) {
                return new LiteralText(DeviousModClient.alreadyDecrypted.get(text.getString()));
            }
            String result = EncryptionUtil.decryptMessage(text);
            //System.out.println(result);
            if (!result.equals("Unable to decrypt")) {
                return new LiteralText(result);
            }
        }

        if (noRenderModule.isToggled()) {
            newText = Text.of(text.asString().replaceAll("§", "&"));

            if (newText.asString().length() > 64) {
                return new LiteralText(newText.asString().substring(0, 64));
            }
        } else {
            return text;
        }

        return newText;
        */
    }
}
