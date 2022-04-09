package me.allink.deviousmod.util;

import me.allink.deviousmod.screen.CommandBarScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.network.MessageType;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;

import java.util.UUID;

public class ChatUtil {
    MinecraftClient client;

    public ChatUtil(MinecraftClient client) {
        System.out.println("ChatUtil initialized");
        this.client = client;
    }

    /**
     * Send a chat message to the client
     *
     * @param message The message
     */
    public void sendMessage(String message) {
        sendMessage(new LiteralText(message));
    }

    /**
     * Send a chat message to the client
     *
     * @param message The message
     */
    public void sendMessage(Text message) {
        if (!(client.currentScreen instanceof CommandBarScreen)) {
            InGameHud inGameHud = client.inGameHud;
            inGameHud.addChatMessage(MessageType.SYSTEM, new LiteralText("§7[§b§lDeviousMod§7] §r").append(message), UUID.fromString("00000000-0000-0000-0000-000000000000"));
        } else {
            CommandBarScreen.getInstance().addMessage(new LiteralText("§7[§b§lDeviousMod§7] §r").append(message));
        }
    }

}
