package me.allink.deviousmod.mixin.hud;

import java.util.List;
import java.util.Objects;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.hud.ChatHudLine;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChatScreen.class)
public class ChatScreenMixin {
    @Shadow protected TextFieldWidget chatField;

    @Inject(method = "init()V", at = @At("TAIL"))
    public void init(CallbackInfo ci) {
        this.chatField.setMaxLength(Integer.MAX_VALUE);
    }

    /*@Inject(method = "mouseClicked(DDI)Z", at = @At("HEAD"))
    public void mouseClicked(double mouseX, double mouseY, int button,
        CallbackInfoReturnable<Boolean> cir) {
        System.out.println("hi");
        MinecraftClient client = MinecraftClient.getInstance();
        ChatHud chatHud = client.inGameHud.getChatHud();
        ChatHudAccessor chatHudAccessor = ((ChatHudAccessor) chatHud);
        List<ChatHudLine<OrderedText>> visibleMessages = chatHudAccessor.getVisibleMessages();
        double chatScale = chatHudAccessor.getChatScale();
        double d = mouseX - 2.0D;
        double e = (double)client.getWindow().getScaledHeight() - mouseY - 40.0D;
        d = MathHelper.floor(d / chatScale);
        e = MathHelper.floor(e / (chatScale * (client.options.chatLineSpacing + 1.0D)));
        if (!(d < 0.0D) && !(e < 0.0D)) {
            int i = Math.min(chatHud.getHeight() / 9, visibleMessages.size());
            if (d <= (double) MathHelper.floor(
                (double) chatHud.getWidth() / chatHudAccessor.getChatScale())) {
                Objects.requireNonNull(client.textRenderer);
                if (e < (double) (9 * i + i)) {
                    Objects.requireNonNull(client.textRenderer);
                    int j = (int) (e / 9.0D + (double) chatHudAccessor.getScrolledLines());
                    if (j >= 0 && j < visibleMessages.size()) {
                        ChatHudLine<OrderedText> chatHudLine = visibleMessages.get(j);
                        client.keyboard.setClipboard(((Text) chatHudLine.getText()).getString());
                    }
                }
            }
        }
    }*/
}
