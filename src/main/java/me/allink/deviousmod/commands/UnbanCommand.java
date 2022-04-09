package me.allink.deviousmod.commands;

import com.google.gson.Gson;
import joptsimple.internal.Strings;
import me.allink.deviousmod.client.DeviousModClient;
import me.allink.deviousmod.command.ChatCommandBase;
import me.allink.deviousmod.exception.NotEnoughArgumentsException;
import me.allink.deviousmod.types.ashcon.AshconPlayer;
import me.allink.deviousmod.util.ChatUtil;
import me.allink.deviousmod.util.CommandBlockUtil;
import me.allink.deviousmod.util.Utilities;

import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class UnbanCommand extends ChatCommandBase {
    Utilities utilities;
    ChatUtil chatUtil;
    CommandBlockUtil commandBlockUtil;

    public UnbanCommand(int minimumArguments, String command, String usage, String description, List<String> aliases) {
        super(minimumArguments, command, usage, description, aliases);
        utilities = Utilities.getInstance();
        chatUtil = utilities.getChatUtil();
        commandBlockUtil = utilities.getCommandBlockUtil();
    }

    private void unban(UUID uuid) {
        commandBlockUtil.placeRepeating(String.format("/data modify entity %s LootTable set value \"minecraft:blocks/diamond_block\"", uuid));
        Timer t = new Timer();
        t.schedule(new TimerTask() {
            @Override
            public void run() {
                commandBlockUtil.placeRepeating(String.format("/execute as %s run kill @s", uuid));
            }
        }, 155L + 50L);

    }

    @Override
    public void execute(String[] args) throws NotEnoughArgumentsException {
        super.execute(args);
        String identifier = Strings.join(args, " ");
        chatUtil.sendMessage(String.format("Unbanning %s...", identifier));
        new Thread(() -> {
            while (DeviousModClient.CONFIG.banList.contains(identifier)) {
                DeviousModClient.CONFIG.banList.remove(identifier);
                DeviousModClient.getInstance().saveConfig();
            }
        }).start();

        if (DeviousModClient.uuidCache.containsKey(identifier)) {
            unban(DeviousModClient.uuidCache.get(identifier));
        } else {
            if (identifier.contains("-")) { // is uuid
                unban(UUID.fromString(identifier));
            } else {
                if (DeviousModClient.isOffline) {
                    unban(UUID.nameUUIDFromBytes(String.format("OfflinePlayer:%s", identifier).getBytes()));
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
                            unban(UUID.fromString(player.uuid));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });
                    t.start();
                }
            }
        }
    }
}
