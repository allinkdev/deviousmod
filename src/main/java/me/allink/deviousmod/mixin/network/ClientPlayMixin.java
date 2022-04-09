package me.allink.deviousmod.mixin.network;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.suggestion.Suggestion;
import com.mojang.datafixers.util.Pair;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import me.allink.deviousmod.client.DeviousModClient;
import me.allink.deviousmod.manager.Managers;
import me.allink.deviousmod.managers.DiscordRPCManager;
import me.allink.deviousmod.managers.ModuleManager;
import me.allink.deviousmod.module.ModuleBase;
import me.allink.deviousmod.modules.ChatFilterModule;
import me.allink.deviousmod.modules.DeathPosModule;
import me.allink.deviousmod.modules.ItemLoggerModule;
import me.allink.deviousmod.modules.NoNBTFuckeryModule;
import me.allink.deviousmod.modules.NoRenderModule;
import me.allink.deviousmod.modules.VanishDetectorModule;
import me.allink.deviousmod.util.ChatUtil;
import me.allink.deviousmod.util.EncryptionUtil;
import me.allink.deviousmod.util.Utilities;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientCommandSource;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.MessageType;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.c2s.play.RequestCommandCompletionsC2SPacket;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.CommandSuggestionsS2CPacket;
import net.minecraft.network.packet.s2c.play.DeathMessageS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityEquipmentUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.GameJoinS2CPacket;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import net.minecraft.network.packet.s2c.play.ParticleS2CPacket;
import net.minecraft.network.packet.s2c.play.PlaySoundS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerRespawnS2CPacket;
import net.minecraft.network.packet.s2c.play.WorldTimeUpdateS2CPacket;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameMode;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public abstract class ClientPlayMixin {
    Vec3d diedAt;

    List<ParticleType<?>> ignoredParticles = List.of(ParticleTypes.ELDER_GUARDIAN.getType(), ParticleTypes.EXPLOSION.getType(), ParticleTypes.EXPLOSION_EMITTER.getType());
    List<SoundEvent> ignoredSoundEvents = List.of(SoundEvents.ENTITY_ENDERMAN_SCREAM, SoundEvents.ENTITY_ENDER_DRAGON_DEATH, SoundEvents.BLOCK_ANVIL_DESTROY, SoundEvents.ENTITY_ELDER_GUARDIAN_CURSE);

    @Shadow
    @Final
    private MinecraftClient client;
    @Shadow
    private ClientWorld world;

    @Shadow
    public abstract ClientCommandSource getCommandSource();

    @Inject(method = "onWorldTimeUpdate(Lnet/minecraft/network/packet/s2c/play/WorldTimeUpdateS2CPacket;)V", at = @At("TAIL"))
    public void onWorldTimeUpdate(WorldTimeUpdateS2CPacket packet, CallbackInfo ci) {
        DeviousModClient.timeSinceLastTick = Instant.now();
    }

    @Inject(method = "onPlayerList(Lnet/minecraft/network/packet/s2c/play/PlayerListS2CPacket;)V", at = @At("TAIL"))
    public void onPlayerList(PlayerListS2CPacket packet, CallbackInfo ci) {
        VanishDetectorModule vanishDetectorModule = (VanishDetectorModule) ModuleManager.getModule("VanishDetector");
        Utilities utilities = Utilities.getInstance();
        ChatUtil chatUtil = utilities.getChatUtil();
        MinecraftClient client = MinecraftClient.getInstance();

        if (packet.getAction() == PlayerListS2CPacket.Action.ADD_PLAYER) {
            for (PlayerListS2CPacket.Entry entry : packet.getEntries()) {
                if (entry.getProfile().getId().equals(client.player.getUuid())) continue;
                String displayName = DeviousModClient.usernameCache.get(entry.getProfile().getId());

                if (vanishDetectorModule.isToggled()) {
                    if (DeviousModClient.hiddenPlayers.containsKey(entry.getProfile().getId())) {
                        chatUtil.sendMessage(new LiteralText(displayName).formatted(Formatting.RED).append(new LiteralText(" exited vanish or changed their skin.").formatted(Formatting.GOLD)));
                    }
                }
                DeviousModClient.tabPlayers.add(entry.getProfile());
                if (!DeviousModClient.hiddenPlayers.containsKey(entry.getProfile().getId())) {
                    DeviousModClient.inGamePlayers.put(entry.getProfile().getId(), new PlayerListEntry(entry));
                }
                GameProfile profile = entry.getProfile();
                if (!DeviousModClient.uuidCache.containsKey(profile.getName())) {
                    DeviousModClient.uuidCache.put(profile.getName(), profile.getId());
                }

                if (!DeviousModClient.usernameCache.containsKey(profile.getId())) {
                    UUID id = profile.getId();
                    /*DeviousModClient.usernameCache.put(id, "Resolving...");
                    Thread t = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            HttpRequest request = HttpRequest.newBuilder(URI.create("https://api.ashcon.app/mojang/v2/user/" + profile.getId().toString()))
                                    .GET()
                                    .build();

                            CompletableFuture<HttpResponse<String>> responseFuture = DeviousModClient.CLIENT.sendAsync(request, HttpResponse.BodyHandlers.ofString());
                            HttpResponse<String> response;
                            try {
                                response = responseFuture.get();
                            } catch (Exception e) {
                                e.printStackTrace();
                                DeviousModClient.usernameCache.put(profile.getId(), "Error");
                                return;
                            }

                            AshconPlayer player = new Gson().fromJson(response.body(), AshconPlayer.class);
                            DeviousModClient.usernameCache.put(id, player.username);
                        }


                    });
                    t.start();*/
                    DeviousModClient.usernameCache.put(id, profile.getName());
                }
            }
        } else if (packet.getAction() == PlayerListS2CPacket.Action.REMOVE_PLAYER) {
            if (vanishDetectorModule.isToggled()) {
                ClientPlayNetworkHandler thiz = ((ClientPlayNetworkHandler) (Object) this);

                Timer t = new Timer();

                t.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        new Thread(() -> {
                            Iterator<PlayerListS2CPacket.Entry> iterator = packet.getEntries().iterator();
                            while (iterator.hasNext()) {
                                PlayerListS2CPacket.Entry entry = iterator.next();
                                try {
                                    DeviousModClient.inGamePlayers.remove(entry.getProfile().getId());
                                    String displayName = DeviousModClient.usernameCache.get(entry.getProfile().getId());
                                    List<Suggestion> suggestions = DeviousModClient.getInstance().getSuggestions("/scoreboard players add " + displayName.substring(0, displayName.length() - 1)).get().getList();
                                    System.out.println(suggestions.size());
                                    if (suggestions.size() == 0) {
                                        DeviousModClient.hiddenPlayers.remove(entry.getProfile().getId());
                                        DeviousModClient.inGamePlayers.remove(entry.getProfile().getId());
                                    } else {
                                        for (GameProfile tabPlayer : DeviousModClient.tabPlayers) {
                                            if (tabPlayer.getId().equals(entry.getProfile().getId())) {
                                                DeviousModClient.hiddenPlayers.put(entry.getProfile().getId(), new PlayerListEntry(new PlayerListS2CPacket.Entry(tabPlayer, 0, GameMode.CREATIVE, new LiteralText(tabPlayer.getName()))));
                                            }
                                        }
                                        chatUtil.sendMessage(new LiteralText(displayName).formatted(Formatting.RED).append(new LiteralText(" entered vanish or changed their skin.").formatted(Formatting.GOLD)));
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }).start();
                    }
                }, 1000L);
            }
        }
    }

    @Inject(method = "onGameMessage(Lnet/minecraft/network/packet/s2c/play/GameMessageS2CPacket;)V", at = @At("HEAD"), cancellable = true)
    public void onGameMessage(GameMessageS2CPacket packet, CallbackInfo ci) {
        ChatFilterModule chatFilterModule = (ChatFilterModule) ModuleManager.getModule("ChatFilter");

        for (String ignore : DeviousModClient.ignore) {
            if (packet.getMessage().getString().toLowerCase(Locale.ROOT).contains(ignore.toLowerCase(Locale.ROOT))) {
                ci.cancel();
            }
        }

        if (chatFilterModule.isToggled()) {
            List<String> arguments = List.of(packet.getMessage().getString().split(" "));
            List<String> newArguments = new ArrayList<>();
            boolean replace = false;
            for (String argument : arguments) {
                boolean replaceMe = false;
                String replaceWith = "";
                for (String swear : ChatFilterModule.words) {
                    if (argument.toLowerCase(Locale.ROOT).contains(swear.toLowerCase(Locale.ROOT))) {
                        replace = true;
                        replaceMe = true;
                        replaceWith = argument.replaceAll(swear, "*".repeat(swear.length()));
                    }
                }
                if (replaceMe) {
                    newArguments.add(replaceWith);
                } else {
                    newArguments.add(argument);
                }
            }

            if (replace) {
                ci.cancel();
                this.client.inGameHud.addChatMessage(packet.getType(), new LiteralText(String.join(" ", newArguments)), packet.getSender());
            }
        }

        if (packet.getSender().toString().equalsIgnoreCase("00000000-0000-0000-0000-000000000000")) {

        } else {
            if (packet.getMessage().getString().contains(DeviousModClient.encryptionChar)) {
                String decryptedMessage = EncryptionUtil.decryptMessage(packet.getMessage());
                if (!decryptedMessage.equals("Unable to decrypt") && !decryptedMessage.isBlank()) {
                    ci.cancel();
                    this.client.inGameHud.addChatMessage(packet.getType(), new LiteralText(String.format("<%s> %s", DeviousModClient.usernameCache.get(packet.getSender()), decryptedMessage)), packet.getSender());
                }
            }
        }
    }

    @Inject(method = "onParticle(Lnet/minecraft/network/packet/s2c/play/ParticleS2CPacket;)V", at = @At("HEAD"), cancellable = true)
    public void onParticle(ParticleS2CPacket packet, CallbackInfo ci) {
        NoRenderModule noRenderModule = (NoRenderModule) ModuleManager.getModule("NoRender");

        if (noRenderModule.toggled) {
            if (packet.getCount() > 20) {
                ci.cancel();
            }
            ParticleEffect packetParticleEffect = packet.getParameters();
            if (ignoredParticles.contains(packetParticleEffect.getType())) {
                ci.cancel();
            }
        }
    }

    @Inject(method = "onPlaySound(Lnet/minecraft/network/packet/s2c/play/PlaySoundS2CPacket;)V", at = @At("HEAD"), cancellable = true)
    public void onPlaySound(PlaySoundS2CPacket packet, CallbackInfo ci) {
        NoRenderModule noRenderModule = (NoRenderModule) ModuleManager.getModule("NoRender");

        if (noRenderModule.toggled) {
            SoundEvent soundEvent = packet.getSound();

            if (ignoredSoundEvents.contains(soundEvent)) {
                ci.cancel();
            }
        }

    }

    /*@Inject(method = "getPlayerList()Ljava/util/Collection;", at = @At("HEAD"), cancellable = true)
    public void getPlayerList(CallbackInfoReturnable<Collection<PlayerListEntry>> cir) {
        //Collection<PlayerListEntry> list = cir.getReturnValue();
    }*/

    /*@Inject(method = "onUpdateSelectedSlot(Lnet/minecraft/network/packet/s2c/play/UpdateSelectedSlotS2CPacket;)V", at = @At("HEAD"), cancellable = true)
    public void onUpdateSelectedSlot(UpdateSelectedSlotS2CPacket packet, CallbackInfo ci) {
        //ci.cancel();
        MinecraftClient client = MinecraftClient.getInstance();
        int previousSlot = client.player.getInventory().selectedSlot;
        //client.getNetworkHandler().sendPacket(new UpdateSelectedSlotC2SPacket(previousSlot));
    }*/

    @Inject(method = "sendPacket(Lnet/minecraft/network/Packet;)V", at = @At("HEAD"), cancellable = true)
    public void sendPacket(Packet<?> packet, CallbackInfo ci) {
        if (packet instanceof RequestCommandCompletionsC2SPacket) {
            RequestCommandCompletionsC2SPacket packet1 = (RequestCommandCompletionsC2SPacket) packet;
            if (packet1.getPartialCommand().length() > 2000) {
                ci.cancel();
            }
        }
    }

    @Inject(method = "onDeathMessage(Lnet/minecraft/network/packet/s2c/play/DeathMessageS2CPacket;)V", at = @At("HEAD"))
    public void onDeathMessage(DeathMessageS2CPacket packet, CallbackInfo ci) {
        Entity entity = world.getEntityById(packet.getEntityId());
        if (entity == client.player) {
            diedAt = client.player.getPos();
        }
    }

    @Inject(method = "onPlayerRespawn(Lnet/minecraft/network/packet/s2c/play/PlayerRespawnS2CPacket;)V", at = @At("HEAD"))
    public void onPlayerRespawn(PlayerRespawnS2CPacket packet, CallbackInfo ci) {
        DeathPosModule deathPosModule = (DeathPosModule) ModuleManager.getModule("DeathPos");
        if (diedAt != null && deathPosModule.isToggled()) {
            client.player.sendChatMessage(String.format("/tp %s %s %s", Math.round(diedAt.x), Math.round(diedAt.y), Math.round(diedAt.z)));
        }
    }

    @Inject(method = "onBlockEntityUpdate(Lnet/minecraft/network/packet/s2c/play/BlockEntityUpdateS2CPacket;)V", at = @At("TAIL"))
    public void onBlockEntityUpdate(BlockEntityUpdateS2CPacket packet, CallbackInfo ci) {
        ModuleBase commandBlockScannerModule = ModuleManager.getModule("CommandBlockScanner");
        if (commandBlockScannerModule.isToggled()) {
            if (packet.getBlockEntityType() == BlockEntityType.COMMAND_BLOCK) {
                assert client.world != null;
                DeviousModClient.discoverCommandBlock(packet.getPos(), client.world.getBlockEntity(packet.getPos()));
            }
        }
    }

    @Inject(method = "onEntityEquipmentUpdate(Lnet/minecraft/network/packet/s2c/play/EntityEquipmentUpdateS2CPacket;)V", at = @At("TAIL"))
    public void onEntityEquipmentUpdate(EntityEquipmentUpdateS2CPacket packet, CallbackInfo ci) {
        NoNBTFuckeryModule noNBTFuckeryModule = (NoNBTFuckeryModule) ModuleManager.getModule("NoNBTFuckery");

        if(noNBTFuckeryModule.isToggled()) return;
        List<Item> boots = List.of(Items.DIAMOND_BOOTS, Items.IRON_BOOTS, Items.CHAINMAIL_BOOTS, Items.GOLDEN_BOOTS, Items.LEATHER_BOOTS, Items.NETHERITE_BOOTS);
        Entity entity = client.world.getEntityById(packet.getId());
        ItemLoggerModule itemLoggerModule = (ItemLoggerModule) ModuleManager.getModule("ItemLogger");

        if (entity != null) {
            for (Pair<EquipmentSlot, ItemStack> equipmentSlotItemStackPair : packet.getEquipmentList()) {
                ItemStack item = equipmentSlotItemStackPair.getSecond();
                UUID id = entity.getUuid();
                if (equipmentSlotItemStackPair.getFirst().getEntitySlotId() >= 0 && !(equipmentSlotItemStackPair.getFirst().getEntitySlotId() > 1) && !(boots.contains(equipmentSlotItemStackPair.getSecond().getItem()))) {
                    DeviousModClient.heldItemMap.put(id, item);
                }
                if (item.hasNbt()) {
                    String playerName = DeviousModClient.usernameCache.get(id);
                    if (playerName != null) {
                        if (itemLoggerModule.isToggled()) {
                            String nbt = item.getNbt().toString();
                            String target = String.format("%s's %s: %s%n", playerName, item.getItem().toString(), nbt);
                            File folder = new File(String.valueOf(Paths.get(DeviousModClient.workingDirectory, "nbt")));
                            if (!folder.exists()) {
                                folder.mkdir();
                            }
                            File file = new File(String.valueOf(Paths.get(folder.getPath(), id + ".txt")));
                            try {
                                if (file.exists()) {
                                    Files.writeString(file.toPath(), target, StandardOpenOption.APPEND);
                                } else {
                                    Files.writeString(file.toPath(), target);
                                }
                            } catch (IOException ignored) {
                            }

                            System.out.println(target);
                        }
                        if (item.hasCustomName()) {
                            Text customName = Text.Serializer.fromJson(item.getSubNbt("display").getString("Name"));
                            System.out.println(customName.getString());
                            if (customName.getString().contains(DeviousModClient.encryptionChar)) {
                                String decryptedMessage = EncryptionUtil.decryptMessage(customName);
                                if (!decryptedMessage.equals("Unable to decrypt") && !decryptedMessage.isBlank()) {
                                    this.client.inGameHud.addChatMessage(MessageType.CHAT, new LiteralText(String.format("<%s> %s", DeviousModClient.usernameCache.get(this.client.world.getEntityById(packet.getId()).getUuid()), decryptedMessage)), this.client.world.getEntityById(packet.getId()).getUuid());
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Inject(method = "onGameJoin(Lnet/minecraft/network/packet/s2c/play/GameJoinS2CPacket;)V", at = @At("TAIL"))
    public void onGameJoin(GameJoinS2CPacket packet, CallbackInfo ci) {
        diedAt = null;
        DeviousModClient.usernameCache.remove(this.client.player.getUuid());
        DeviousModClient.usernameCache.put(this.client.player.getUuid(), this.client.player.getName().getString());
        DiscordRPCManager rpcManager = Managers.getDiscordRPCManager();
        rpcManager.onPlayGame();
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.getSession().getClientId().isEmpty()) {
            DeviousModClient.isOffline = true;
        } else {
            DeviousModClient.isOffline = !client.player.getUuidAsString().equals(client.getSession().getUuid());
        }
        Managers.getModuleManager().onJoin(client);
    }

    @Inject(method = "onCommandSuggestions(Lnet/minecraft/network/packet/s2c/play/CommandSuggestionsS2CPacket;)V", at = @At("TAIL"))
    public void onCommandSuggestions(CommandSuggestionsS2CPacket packet, CallbackInfo ci) {
        DeviousModClient.onCommandSuggestions(packet.getCompletionId(), packet.getSuggestions());
    }
}
