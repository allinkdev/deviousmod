package me.allink.deviousmod.mixin.client;

import com.github.hhhzzzsss.util.MathUtils;
import java.security.SecureRandom;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import me.allink.deviousmod.managers.ModuleManager;
import me.allink.deviousmod.util.CommandBlockUtil;
import me.allink.deviousmod.util.Utilities;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtDouble;
import net.minecraft.nbt.NbtFloat;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.network.packet.c2s.play.BookUpdateC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.RandomStringUtils;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin {
    @Shadow
    @Nullable
    public ClientPlayerEntity player;
    @Shadow
    private int itemUseCooldown;

    @Shadow public abstract void updateWindowTitle();

    @Inject(method = "doItemUse", at = @At("HEAD"), cancellable = true)
    public void doItemUse(CallbackInfo ci) {
        //thanks hhhzzzsss for heavily inspiring this
        this.player.swingHand(Hand.MAIN_HAND);

        if (ModuleManager.getModule("FastUse").isToggled()) {
            itemUseCooldown = 0;
        }
        
        MinecraftClient client = ((MinecraftClient) (Object) this); // yes this is really needed https://fabricmc.net/wiki/tutorial:mixin_examples
        ItemStack heldItem = client.player.getStackInHand(Hand.MAIN_HAND);
        if (heldItem != null) {
            if (heldItem.getName().asString().equals("Devious Armor Stand")) {
                Random random = new Random();
                String[] colors = {"dark_blue", "dark_green", "dark_aqua", "dark_red", "dark_purple", "gold", "gray", "dark_gray", "blue", "green", "aqua", "red"};
                int length = 3192;
                int min = 0x4E00;
                int max = 0x9FFF;
                //NbtCompound nbt = new NbtCompound();
                NbtCompound entityTag = new NbtCompound();

                StringBuilder builder = new StringBuilder();
                for (int i = 0; i < length; i++) {
                    char character = (char) Math.floor(Math.random() * (max - min) + min);
                    if (character != '"' && character != '\\') {
                        builder.append(character);
                    }
                }
                entityTag.putString("CustomName", String.format("{\"text\":\"%s\",\"bold\":\"true\",\"italic\":\"true\",\"underlined\":\"true\",\"strikethrough\":\"true\",\"obfuscated\":\"true\",\"color\":\"%s\"}", builder, colors[random.nextInt(colors.length)]));
                NbtList posTag = new NbtList();
                int range = 15;
                NbtDouble x = NbtDouble.of(client.player.getX() + (Math.floor(Math.random() * (range + range) - range)));
                NbtDouble y = NbtDouble.of(client.player.getY() + (Math.floor(Math.random() * (range + range) - range)));
                NbtDouble z = NbtDouble.of(client.player.getZ() + (Math.floor(Math.random() * (range + range) - range)));
                posTag.add(x);
                posTag.add(y);
                posTag.add(z);
                entityTag.put("Pos", posTag);
                entityTag.putByte("ShowArms", (byte) 1);
                NbtCompound poseTag = new NbtCompound();
                String[] names = {"Body", "Head", "LeftLeg", "RightLeg", "LeftArm", "RightArm"};
                for (String name : names) {
                    NbtList rotList = new NbtList();
                    NbtFloat a = NbtFloat.of((float) random.nextInt(360));
                    NbtFloat b = NbtFloat.of((float) random.nextInt(360));
                    NbtFloat c = NbtFloat.of((float) random.nextInt(360));
                    rotList.add(a);
                    rotList.add(b);
                    rotList.add(c);
                    poseTag.put(name, rotList);
                }
                entityTag.put("Pose", poseTag);
                entityTag.putByte("CustomNameVisible", (byte) 1);
                entityTag.putBoolean("NoGravity", true);
                heldItem.getNbt().put("EntityTag", entityTag);
                client.interactionManager.clickCreativeStack(heldItem, 36 + client.player.getInventory().selectedSlot);
            } else if (heldItem.getName().asString().equals("Devious Sign")) {
                Random random = new Random();

                int x = random.nextInt(16777215);
                int y = 255;
                int z = random.nextInt(16777215);

                NbtCompound blockEntityTag = new NbtCompound();
                blockEntityTag.putInt("x", x);
                blockEntityTag.putInt("y", y);
                blockEntityTag.putInt("z", z);

                blockEntityTag.putString("Text1", "{\"text\":\"g6_ was false\"}");
                blockEntityTag.putString("Text2", "{\"text\":\"banned with no\"}");
                blockEntityTag.putString("Text3", "{\"text\":\"proof #freeg6_\"}");
                heldItem.getNbt().put("BlockEntityTag", blockEntityTag);
                client.interactionManager.clickCreativeStack(heldItem, 36 + client.player.getInventory().selectedSlot);
            } else if (heldItem.getItem() == Items.WRITABLE_BOOK) {
                if (ModuleManager.getModule("BookFill").isToggled()) {
                    ci.cancel();
                    new Thread(() -> {
                        SecureRandom secureRandom = new SecureRandom();
                        List<String> pages = Lists.newArrayList();
                        String[] source = "DEVIOUSMOD".split("");
                        StringBuilder title = new StringBuilder();
                        int index = secureRandom.nextInt(source.length);
                        for (int i = 0; i < source.length; i++) {
                            if (i == index) {
                                title.append(RandomStringUtils.randomAlphanumeric(1).toUpperCase(Locale.ROOT));
                            } else {
                                title.append(source[i]);
                            }
                        }

                        for (int i = 0; i < 100; i++) {
                            /*StringBuilder string = new StringBuilder();
                            for (int x = 0; x < 798; x++) {
                                string.append((char) secureRandom.nextInt(0xFFFF, 0x10FFFF));
                            }*/
                            pages.add(RandomStringUtils.randomAlphanumeric(798));
                        }

                        NbtList nbtList = new NbtList();
                        Objects.requireNonNull(nbtList);
                        for (String page : pages) {
                            nbtList.add(NbtString.of(page));
                        }
                        heldItem.setSubNbt("author", NbtString.of(client.player.getGameProfile().getName()));
                        heldItem.setSubNbt("title", NbtString.of(title.toString()));

                        int i = client.player.getActiveHand() == Hand.MAIN_HAND ? client.player.getInventory().selectedSlot : 40;
                        client.getNetworkHandler().sendPacket(new BookUpdateC2SPacket(i, pages, Optional.of(title.toString().trim())));
                    }).start();
                }
            } else if (ModuleManager.getModule("BookFill").isToggled() && heldItem.getItem() == Items.WRITTEN_BOOK) {
                if (heldItem.getNbt() != null) {
                    if (Objects.equals(heldItem.getNbt().getString("author"), client.player.getGameProfile().getName())) {
                        ci.cancel();
                    }
                }
            } else if (heldItem.getItem() == Items.BAT_SPAWN_EGG && heldItem.getName().asString().equals("Devious Fireball")) {
                NbtCompound nbtCompound = new NbtCompound();
                nbtCompound.putString("id", "minecraft:fireball");
                nbtCompound.putString("CustomName", "{\"text\":\"DeviousMod Fireball\",\"color\":\"dark_red\",\"bold\":\"true\"}");
                nbtCompound.putFloat("ExplosionPower", 100);
                nbtCompound.putByte("CustomNameVisible", (byte) 1);

                //nbtCompound.put("direction", direction);
                NbtCompound witherNbt = new NbtCompound();
                ///summon fireball ~ ~ ~ {NoGravity:1b,Passengers:[{id:wither,Invul:100,CustomName:'{"text":"DeviousMod","color":"aqua","bold":"true"}',CustomNameVisible:1b}],ExplosionPower:30}
                witherNbt.putString("CustomName", "{\"text\":\"DeviousMod\",\"color\":\"aqua\",\"bold\":\"true\"}");
                witherNbt.putByte("CustomNameVisible", (byte) 1);
                witherNbt.putInt("Invul", 100);
                witherNbt.putString("id", "wither");

                NbtList passengers = new NbtList();
                passengers.add(witherNbt);

                nbtCompound.put("Passengers", passengers);
                System.out.println(nbtCompound);
                heldItem.getNbt().put("EntityTag", nbtCompound);
                client.interactionManager.clickCreativeStack(heldItem, 36 + client.player.getInventory().selectedSlot);
                Timer t = new Timer();

                t.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        client.interactionManager.interactBlock(client.player, client.world, Hand.MAIN_HAND, new BlockHitResult(client.player.getPos(), Direction.DOWN, client.player.getBlockPos(), false));
                    }
                }, 200L);
            } else if (heldItem.getItem() == Items.LIGHTNING_ROD && heldItem.getName().asString().equals("Devious Cannon")) {
                /*NbtCompound rootNbt = new NbtCompound();
                rootNbt.putString("id", "minecraft:axolotl_spawn_egg");
                rootNbt.putByte("Count", (byte) 1);
                NbtCompound tagNbt = new NbtCompound();
                NbtCompound entityTag = new NbtCompound();
                entityTag.putString("id", "minecraft:creeper");
                entityTag.putByte("ExplosionRadius", (byte) 10);
                entityTag.putShort("Fuse", (short) 0);
                entityTag.putByte("ignited", (byte) 1);
                entityTag.putByte("powered", (byte) 1);
                tagNbt.put("EntityTag", entityTag);
                rootNbt.put("tag", tagNbt);
                client.interactionManager.clickCreativeStack(ItemStack.fromNbt(rootNbt), 37);
                client.interactionManager.clickCreativeStack(Items.BARRIER.getDefaultStack(), 38);
                client.player.getInventory().selectedSlot = 2;*/
                final Utilities utilities = Utilities.getInstance();
                final CommandBlockUtil commandBlockUtil = utilities.getCommandBlockUtil();

                Timer t = new Timer();
                for (int i = 0; i < 100; i++) {
                    t.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            Vec3d target = MathUtils.offsetLookingAngle(
                                    client.player.getPos(),
                                    client.player.getRotationClient(),
                                    1, 1, 25
                            );
                            commandBlockUtil.placeRepeating(String.format("/summon creeper %s %s %s {ExplosionRadius:15, Fuse:0, ignited:1, powered:1}", target.getX(), target.getY(), target.getZ()), client.player.getBlockPos(), true);
                        }
                    }, 100 * i);
                }
            }
        }
    }
}
