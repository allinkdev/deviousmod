package me.allink.deviousmod.commands;

import joptsimple.internal.Strings;
import me.allink.deviousmod.client.DeviousModClient;
import me.allink.deviousmod.command.ChatCommandBase;
import me.allink.deviousmod.exception.NotEnoughArgumentsException;
import me.allink.deviousmod.util.ChatUtil;
import me.allink.deviousmod.util.Utilities;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Formatting;
import org.apache.commons.codec.binary.Base64;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class KValCommand extends ChatCommandBase {
    Utilities utilities;
    ChatUtil chatUtil;

    public KValCommand(int minimumArguments, String command, String usage, String description, List<String> aliases) {
        super(minimumArguments, command, usage, description, aliases);
        this.utilities = Utilities.getInstance();
        this.chatUtil = utilities.getChatUtil();
    }

    @Override
    public void execute(String[] args) throws NotEnoughArgumentsException {
        super.execute(args);
        MinecraftClient client = MinecraftClient.getInstance();
        try {
            String message = String.format(">%s", Strings.join(args, " "));
            byte[] key = DeviousModClient.kcpToken.getBytes(StandardCharsets.UTF_8);
            byte[] macMessage = (String.format("%s:%s:%s", client.player.getUuidAsString(), String.valueOf(System.currentTimeMillis()).substring(0, 9), args[0].trim())).getBytes(StandardCharsets.UTF_8);
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(key, "HmacSHA256"));
            byte[] bytes = mac.doFinal(macMessage);
            String hash = new String(Base64.encodeBase64URLSafe(bytes));
            String newMessage = String.format("%s %s", message, hash.substring(0, 8));
            client.getNetworkHandler().sendPacket(new ChatMessageC2SPacket(newMessage));
        } catch (Exception e) {
            chatUtil.sendMessage(new LiteralText("Oops! Did you forget to set your KCP key? Check the logs for more details...").formatted(Formatting.RED));
            e.printStackTrace();
        }
    }
}
