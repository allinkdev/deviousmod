package me.allink.deviousmod.mixin.entity;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import me.allink.deviousmod.client.DeviousModClient;
import me.allink.deviousmod.exception.NotEnoughArgumentsException;
import me.allink.deviousmod.manager.Managers;
import me.allink.deviousmod.managers.ChatCommandManager;
import me.allink.deviousmod.managers.ModuleManager;
import me.allink.deviousmod.modules.SuffixPrefixModule;
import me.allink.deviousmod.modules.VanishHiderModule;
import me.allink.deviousmod.util.ChatUtil;
import me.allink.deviousmod.util.CommandBlockUtil;
import me.allink.deviousmod.util.EncryptionUtil;
import me.allink.deviousmod.util.ItemUtil;
import me.allink.deviousmod.util.Utilities;
import net.minecraft.SharedConstants;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.network.MessageType;
import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientPlayerEntity.class)
public abstract class ClientPlayerEntityMixin {
    @Shadow
    @Final
    public ClientPlayNetworkHandler networkHandler;
    MinecraftClient client = MinecraftClient.getInstance();
    Utilities utilities = Utilities.getInstance();
    ChatUtil chatUtil = utilities.getChatUtil();
    CommandBlockUtil commandBlockUtil = utilities.getCommandBlockUtil();
    @Shadow
    private Hand activeHand;

    @Shadow
    public abstract void sendChatMessage(String message);

    @Inject(method = "sendChatMessage(Ljava/lang/String;)V", at = @At("HEAD"), cancellable = true)
    public void sendChatMessage(String message, CallbackInfo ci) {
        SecureRandom secureRandom = new SecureRandom();
        Map<String, String> placeholders = Map.ofEntries(
                Map.entry("{USERNAME}", client.getSession().getUsername()),
                Map.entry("{UUID}", client.player.getUuidAsString()),
                Map.entry("{RANDOM}", String.valueOf(secureRandom.nextInt(0, 100)))
        );

        ChatCommandManager chatCommandManager = Managers.getChatCommandManager();

        if (message.startsWith(DeviousModClient.prefix)) {
            ci.cancel();
            ArrayList<String> args = new ArrayList<>(Arrays.asList(message.split(" ")));
            String command = args.get(0).replace(DeviousModClient.prefix, "");
            args.remove(0);
            try {
                chatCommandManager.executeIfExists(command, args.toArray(new String[0]));
            } catch (NotEnoughArgumentsException ignored) {
            }
            return;
        } else if (message.startsWith(DeviousModClient.autoEncryptPrefix)) {
            ci.cancel();
            String encryptedMessage = EncryptionUtil.encrypt(message.replace(DeviousModClient.autoEncryptPrefix, ""));
            Utilities utilities = Utilities.getInstance();
            ItemUtil itemUtil = utilities.getItemUtil();
            try {
                String itemName = "minecraft:red_concrete";
                System.out.println(itemName);
                itemUtil.giveItem(itemName, (byte) 1, String.format("{display:{Name:'{\"text\":\"%s\"}'}}", encryptedMessage.replaceAll("&", "§")), 9);
                Timer t = new Timer();
                t.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        itemUtil.giveItem("air", (byte) 0, 9);
                    }
                }, 100L);
                this.client.inGameHud.addChatMessage(MessageType.CHAT, new LiteralText(String.format("<%s> %s", this.client.player.getGameProfile().getName(), message.replace(DeviousModClient.autoEncryptPrefix, ""))), this.client.player.getUuid());
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("An error occurred while giving you the encrypted item.");
            }
            return;
        } else if (!message.equals(SharedConstants.stripInvalidChars(message))) {
            ci.cancel();
            this.networkHandler.sendPacket(new ChatMessageC2SPacket(SharedConstants.stripInvalidChars(message)));
        } else if (message.startsWith("/vanish") || message.startsWith("/vanish enable") || message.equals("/v") ||
                message.equals("/v on") || message.equals("/v enable")) {
            VanishHiderModule vanishHiderModule = (VanishHiderModule) ModuleManager.getModule("VanishHider");
            if (vanishHiderModule.isToggled()) {
                commandBlockUtil.placeRepeating(String.format("/tellraw @a {\"color\":\"yellow\",\"translate\":\"multiplayer.player.left\",\"with\":[{\"extra\":[{\"color\":\"red\",\"text\":\"%s\"}],\"text\":\"\"}]}", client.player.getDisplayName().asString()));
            }
        }
        String placeholderReplacedMessage = message;

        for (String placeholder : placeholders.keySet()) {
            if (message.contains(placeholder)) {
                placeholderReplacedMessage = placeholderReplacedMessage.replaceAll(placeholder.replaceAll("\\{", "\\\\{"), placeholders.get(placeholder));
            }
        }

        if (!placeholderReplacedMessage.equals(message)) {
            ci.cancel();
            this.networkHandler.sendPacket(new ChatMessageC2SPacket(placeholderReplacedMessage));
        }

        SuffixPrefixModule suffixPrefixModule = (SuffixPrefixModule) ModuleManager.getModule("SuffixPrefix");
        if (suffixPrefixModule.isToggled() && !message.startsWith("/")) {
            ci.cancel();
            String newMessage = DeviousModClient.CONFIG.chatPrefix + " " + message + " " + DeviousModClient.CONFIG.chatSuffix;
            this.networkHandler.sendPacket(new ChatMessageC2SPacket(newMessage));
        }
    }

    @Inject(method = "showsDeathScreen()Z", at = @At("RETURN"), cancellable = true)
    public void showsDeathScreen(CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(false);
    }
}
