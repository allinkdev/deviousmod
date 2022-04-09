package me.allink.deviousmod.mixin.hud;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import me.allink.deviousmod.client.DeviousModClient;
import me.allink.deviousmod.managers.ModuleManager;
import me.allink.deviousmod.modules.AntiSpamModule;
import me.allink.deviousmod.modules.NoRenderModule;
import me.allink.deviousmod.modules.TimestampModule;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.hud.ChatHudLine;
import net.minecraft.text.LiteralText;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChatHud.class)
public abstract class ChatHudMixin {

    @Shadow
    @Final
    private List<ChatHudLine<Text>> messages;

    @Shadow
    @Final
    private List<ChatHudLine<OrderedText>> visibleMessages;

    @Shadow
    private int scrolledLines;

    @Shadow
    private boolean hasUnreadNewMessages;
    @Shadow
    @Final
    private MinecraftClient client;
    @Shadow
    @Final
    private List<String> messageHistory;

    @Shadow
    protected abstract void removeMessage(int messageId);

    @Shadow
    public abstract int getWidth();

    @Shadow
    public abstract double getChatScale();

    @Shadow
    protected abstract boolean isChatFocused();

    @Inject(method = "addMessage(Lnet/minecraft/text/Text;IIZ)V", at = @At("HEAD"), cancellable = true)
    public void addMessage(Text message, int messageId, int timestamp, boolean refresh, CallbackInfo ci) {
        TimestampModule timestampModule = (TimestampModule) ModuleManager.getModule("ChatTimestamps");
        //SenderUUIDModule senderUUIDModule = (SenderUUIDModule) ModuleManager.getModule("SenderUUID");
        AntiSpamModule antiSpamModule = (AntiSpamModule) ModuleManager.getModule("AntiSpam");
        NoRenderModule noRenderModule = (NoRenderModule) ModuleManager.getModule("NoRender");
        Text originalMessage = message;

        String content = message.getString();

        for (String ignore : DeviousModClient.ignore) {
            if (content.toLowerCase(Locale.ROOT).contains(ignore.toLowerCase(Locale.ROOT))) {
                ci.cancel();
            }
        }

        int matches = 0;

        for (Text previousChatMessage : DeviousModClient.rawChatMessages) {
            String previousMessageContent = previousChatMessage.getString();
            if (previousMessageContent.toLowerCase(Locale.ROOT).equals(content.toLowerCase(Locale.ROOT)) && !content.contains("left the game") && !content.contains("joined the game")) {
                matches++;
            }
        }

        UUID sender = DeviousModClient.messageList.get(message);
        DeviousModClient.rawChatMessages.add(originalMessage);

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();

        Text timestampText = new LiteralText("[" + dtf.format(now) + "] ").formatted(Formatting.GRAY);
        String originalUsername = DeviousModClient.usernameCache.get(sender);
        /*ClickEvent originalUsernameClickEvent = new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, (originalUsername == null) ? "" : originalUsername);
        Text originalUsernameText = new LiteralText("");*/

        /*ClickEvent clickEvent = null;
        if (sender != null) {
            if (!sender.toString().equalsIgnoreCase("00000000-0000-0000-0000-000000000000") && senderUUIDModule.toggled) {
                clickEvent = new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, sender.toString());
                if (originalUsername != null) {
                    originalUsernameText = (sender.toString().equalsIgnoreCase("00000000-0000-0000-0000-000000000000")) ? Text.of("") : new LiteralText(String.format("(%s) ", (originalUsername == null) ? "UUID" : originalUsername)).formatted(Formatting.GRAY);
                } else {
                    originalUsernameText = Text.of("");
                }
            }
        }*/

        /*if (timestampModule.toggled) {
            message = new LiteralText("");
            message = message.copy().append(timestampText);

            if (messageId != 0) {
                this.removeMessage(messageId);
            }

            int i = MathHelper.floor((double) this.getWidth() / this.getChatScale());
            List<OrderedText> list = ChatMessages.breakRenderedChatMessageLines(message, i, this.client.textRenderer);
            boolean bl = this.isChatFocused();

            OrderedText orderedText;
            for (Iterator var8 = list.iterator(); var8.hasNext(); this.visibleMessages.add(0, new ChatHudLine(timestamp, orderedText, messageId))) {
                orderedText = (OrderedText) var8.next();
                if (bl && this.scrolledLines > 0) {
                    this.hasUnreadNewMessages = true;
                    this.scroll(1.0D);
                }
            }

            if (!refresh) {
                this.messages.add(0, new ChatHudLine(timestamp, message, messageId));
            }
        }*/

    }
}
