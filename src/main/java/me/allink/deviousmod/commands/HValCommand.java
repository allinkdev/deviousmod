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

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.List;

public class HValCommand extends ChatCommandBase {
    Utilities utilities;
    ChatUtil chatUtil;

    public HValCommand(int minimumArguments, String command, String usage, String description, List<String> aliases) {
        super(minimumArguments, command, usage, description, aliases);
        this.utilities = Utilities.getInstance();
        this.chatUtil = utilities.getChatUtil();
    }

    @Override
    public void execute(String[] args) throws NotEnoughArgumentsException {
        super.execute(args);
        MinecraftClient client = MinecraftClient.getInstance();
        try {
            String message = String.format("#%s", Strings.join(args, " "));
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            String time = "" + (System.currentTimeMillis() / 10000);
            String key = DeviousModClient.CONFIG.hbotKey;
            String raw = message.replaceAll("&[0-9a-fklmnor]", "") + ";" + client.player.getUuidAsString() + ";" + time + ";" + key;
            byte[] hash = md.digest(raw.getBytes(StandardCharsets.UTF_8));
            BigInteger big_int = new BigInteger(1, Arrays.copyOfRange(hash, 0, 4));
            String strHash = big_int.toString(Character.MAX_RADIX);
            client.getNetworkHandler().sendPacket(new ChatMessageC2SPacket(String.format("%s %s", message, strHash)));
        } catch (Exception e) {
            chatUtil.sendMessage(new LiteralText("Oops! Did you forget to set your HBot key? Check the logs for more details...").formatted(Formatting.RED));
            e.printStackTrace();
        }
    }
}
