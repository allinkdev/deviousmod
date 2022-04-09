package me.allink.deviousmod.commands;

import com.google.common.base.Splitter;
import joptsimple.internal.Strings;
import me.allink.deviousmod.client.DeviousModClient;
import me.allink.deviousmod.command.ChatCommandBase;
import me.allink.deviousmod.exception.NotEnoughArgumentsException;
import me.allink.deviousmod.util.BlockUtil;
import me.allink.deviousmod.util.ChatUtil;
import me.allink.deviousmod.util.ItemUtil;
import me.allink.deviousmod.util.Utilities;
import net.minecraft.client.MinecraftClient;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.Vec3d;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class AutoSignCommand extends ChatCommandBase {
    Utilities utilities;
    ItemUtil itemUtil;
    BlockUtil blockUtil;
    ChatUtil chatUtil;

    public AutoSignCommand(int minimumArguments, String command, String usage, String description, List<String> aliases) {
        super(minimumArguments, command, usage, description, aliases);
        utilities = Utilities.getInstance();
        itemUtil = utilities.getItemUtil();
        blockUtil = utilities.getBlockUtil();
        chatUtil = utilities.getChatUtil();
    }


    @Override
    public void execute(String[] args) throws NotEnoughArgumentsException {
        super.execute(args);
        String target = args[0];
        chatUtil.sendMessage(String.format("Placing signs from %s.txt, please wait...", target));
        boolean breakBlocks = true;
        if (args.length > 1) {
            try {
                breakBlocks = Boolean.parseBoolean(args[1]);
            } catch (Exception e) {
                chatUtil.sendMessage(new LiteralText("Please provide boolean argument 2!!! (hacker)_ gbhnjbgngbhngbyhnjmvgbyhnujmgbyhnjmfvgbhjfvgb cv cfvgbnvgbnvgbyhnjmbhjk,bjhkgyuvgyubyuilhnol;h.miujmmmmmmmmjmjkjjjmdsfkkk").formatted(Formatting.RED));
                return;
            }
        }
        MinecraftClient client = MinecraftClient.getInstance();
        File folder = new File(String.valueOf(Paths.get(DeviousModClient.workingDirectory, "signs")));
        File file = new File(String.valueOf(Paths.get(folder.getPath(), target + ".txt")));
        boolean finalBreakBlocks = breakBlocks;
        new Thread(() -> {
            try {
                List<String> lines = Files.readAllLines(Path.of(file.getPath()));
                String text = Strings.join(lines, " ");
                Iterable<String> signLines = Splitter.fixedLength(60).split(text);

                itemUtil.giveItem("glass", (byte) 1, 0);
                for (String signLine : signLines) {
                    Thread.sleep(10L);
                    if (client.world == null) return;
                    Vec3d currentLocation = client.player.getPos();
                    for (int x = -3; x <= 3; x++) {
                        for (int y = -3; y <= 3; y++) {
                            for (int z = 1; z <= 3; z++) {
                                if (!finalBreakBlocks) break;
                                blockUtil.breakBlock(currentLocation.add(x, y, z));
                            }
                        }

                    }
                    Thread.sleep(100L);
                    client.player.setYaw(180);
                    client.player.setPitch(0);
                    client.player.setVelocity(Vec3d.ZERO);
                    //"{BlockEntityTag:{}}";
                    Iterable<String> subSignLine = Splitter.fixedLength(15).split(signLine);
                    NbtCompound compound = new NbtCompound();

                    int i = 0;
                    for (String s : subSignLine) {
                        i++;
                        compound.putString(String.format("Text%s", i), String.format("{\"text\":\"%s\"}", s.replaceAll("\"", "\\\\\"")));
                    }
                    itemUtil.giveItem("oak_sign", (byte) 1, String.format("{BlockEntityTag:%s}", compound), 1);
                    blockUtil.placeBlock(currentLocation.add(0, -2, 1), 36);
                    Thread.sleep(100L);
                    blockUtil.placeBlock(currentLocation.add(0, -1, 1), 37);
                    Vec3d newLocation = currentLocation.add(0, 0, 1);
                    client.player.setPosition(Math.floor(newLocation.x), Math.floor(newLocation.y), Math.floor(newLocation.z));
                    client.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.Full(Math.floor(newLocation.x), Math.floor(newLocation.y), Math.floor(newLocation.z), client.player.getYaw(), client.player.getPitch(), client.player.isOnGround()));
                }
            } catch (Exception e) {

            }
        }).start();

    }
}
