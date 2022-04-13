package me.allink.deviousmod.util;

import com.google.gson.Gson;
import me.allink.deviousmod.client.DeviousModClient;
import me.allink.deviousmod.types.ashcon.AshconPlayer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3i;

import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class BanUtil {
    private final CommandBlockUtil commandBlockUtil;

    public BanUtil(CommandBlockUtil commandBlockUtil) {
        this.commandBlockUtil = commandBlockUtil;
    }

    /**
     * Kick the requested player from the server (requries OP)
     *
     * @param identifier Who to kick
     */
    public void kickPlayer(String identifier) {
        MinecraftClient.getInstance().player.sendChatMessage("/kick " + identifier);
    }


    /**
     * Ban the requested player from the server (requries OP)
     *
     * @param identifier Who to ban
     */
    public void banPlayer(String identifier) {
        MinecraftClient.getInstance().player.sendChatMessage("/ban " + identifier);
    }
}
