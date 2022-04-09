package me.allink.deviousmod.util;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.Timer;
import java.util.TimerTask;

public class CommandBlockUtil {
    MinecraftClient client;
    Utilities utilities;
    ItemUtil itemUtil;
    BlockUtil blockUtil;

    public CommandBlockUtil(MinecraftClient client) {
        System.out.println("CommandBlockUtil initialized");
        this.client = client;
        this.utilities = Utilities.getInstance();
        itemUtil = utilities.getItemUtil();
        blockUtil = utilities.getBlockUtil();
    }

    /**
     * Place a repeating command block
     *
     * @param command The command block's command
     */
    public void placeRepeating(String command) {
        PlayerEntity player = client.player;
        BlockPos blockPos = client.player.getBlockPos();
        placeRepeating(command, blockPos);
    }

    /**
     * Place a repeating command block
     *
     * @param command  The command block's command
     * @param location The location of the command block
     */
    public void placeRepeating(String command, Vec3d location) {
        placeRepeating(command, new BlockPos(location.x, location.y, location.z));
    }


    /**
     * Place a repeating command block
     *
     * @param command  The command block's command
     * @param location The location of the command block
     */
    public void placeRepeating(String command, BlockPos location) {
        placeRepeating(command, location, false);
    }

    /**
     * Place a repeating command block
     *
     * @param command  The command block's command
     * @param location The location of the command block
     */
    public void placeRepeating(String command, BlockPos location, boolean breakBlocks) {
        final PlayerEntity player = client.player;

        try {
            itemUtil.giveItem("repeating_command_block", (byte) 1, String.format("{BlockEntityTag:{auto:1b, Command:\"%s\", CustomName:\"{\\\"text\\\":\\\"%s\\\"}\"}}", command.replaceAll("\"", "\\\\\""), client.player.getName().getString()), 0);
        } catch (CommandSyntaxException e) {
            e.printStackTrace();
        }

        blockUtil.breakBlock(location.add(0, -3, 0));
        Timer t = new Timer();

        t.schedule(new TimerTask() {
            @Override
            public void run() {
                blockUtil.placeBlock(location.add(0, -3, 0), 36);

            }
        }, 110L);
        t.schedule(new TimerTask() {
            @Override
            public void run() {
                itemUtil.giveItem("air", (byte) 1, 0);
            }
        }, 110L + 55L);
        if(breakBlocks) {
            t.schedule(new TimerTask() {
                @Override
                public void run() {
                    blockUtil.breakBlock(location.add(0, -3, 0));
                }
            }, 110L + 55L * 2);
        }
    }

    /**
     * Destroy the cloop at the player
     */
    public void destroyLooping() {
        final PlayerEntity player = client.player;
        destroyLooping(player.getBlockPos());
    }

    /**
     * Destroy a cloop
     *
     * @param location The location of the cloop
     */
    public void destroyLooping(Vec3d location) {
        destroyLooping(new BlockPos(location.x, location.y, location.z));
    }

    /**
     * Destroy a cloop
     *
     * @param location The location of the cloop
     */
    public void destroyLooping(BlockPos location) {
        final ClientPlayerInteractionManager interactionManager = client.interactionManager;

        interactionManager.cancelBlockBreaking();
        itemUtil.giveItem("redstone_block", (byte) 1, 0);

        final Timer t = new Timer();
        t.schedule(new TimerTask() {
            @Override
            public void run() {
                blockUtil.breakBlock(location.add(1, -1, 4));
            }
        }, 55L);

        t.schedule(new TimerTask() {
            @Override
            public void run() {
                blockUtil.placeBlock(location.add(1, -1, 4), 36);
            }
        }, 200L);

        t.schedule(new TimerTask() {
            @Override
            public void run() {
                blockUtil.breakBlock(location.add(1, -1, 4));
                blockUtil.breakBlock(location.add(1, -1, 2));
            }
        }, 290L);
    }

    /**
     * Place a cloop at the specified location
     *
     * @param command The command the cloop should execute
     */
    public void placeLooping(String command) {
        final PlayerEntity player = client.player;
        placeLooping(command, player.getBlockPos());
    }

    /**
     * Place a cloop at the specified location
     *
     * @param command  The command the cloop should execute
     * @param location Where you want the cloop to be placed
     */
    public void placeLooping(String command, BlockPos location) {
        placeLooping(command, location, true);
    }

    /**
     * Place a cloop at the specified location
     *
     * @param command     The command the cloop should execute
     * @param location    Where you want the cloop to be placed
     * @param breakBlocks Whether or not to break the blocks
     */
    public void placeLooping(String command, BlockPos location, boolean breakBlocks) {
        client.player.setYaw(0);
        client.player.setPitch(0);
        client.player.setVelocity(Vec3d.ZERO);

        try {
            itemUtil.giveItem("chain_command_block", (byte) 1, String.format("{BlockEntityTag:{auto:1b, Command:\"%s\"}}", command.replaceAll("\"", "\\\\\"").trim()), 0);
            itemUtil.giveItem("repeating_command_block", (byte) 1, "{BlockEntityTag:{auto:0b, Command:\"/clone ~ ~ ~2 ~ ~ ~1 ~ ~ ~-1\"}}", 1);
            itemUtil.giveItem("redstone_block", (byte) 1, 2);
        } catch (Exception e) {
            e.printStackTrace();
        }

        client.interactionManager.cancelBlockBreaking();

        final Timer t = new Timer();
        t.schedule(new TimerTask() {
            @Override
            public void run() {
                if (breakBlocks) {
                    blockUtil.breakBlock(location.add(0, -1, 1));
                    blockUtil.breakBlock(location.add(0, -1, 2));
                    blockUtil.breakBlock(location.add(0, -1, 3));
                    blockUtil.breakBlock(location.add(0, -1, 4));
                    blockUtil.breakBlock(location.add(1, -1, 2));
                }
            }
        }, 55L);


        t.schedule(new TimerTask() {
            @Override
            public void run() {
                blockUtil.placeBlock(location.add(0, -1, 1), 36);
                blockUtil.placeBlock(location.add(0, -1, 2), 37);
                blockUtil.placeBlock(location.add(0, -1, 3), 36);
                blockUtil.placeBlock(location.add(0, -1, 4), 37);
                blockUtil.placeBlock(location.add(1, -1, 2), 38);
            }
        }, 290L);

        t.schedule(new TimerTask() {
            @Override
            public void run() {
                for (int i = 0; i <= 2; i++) {
                    itemUtil.giveItem("minecraft:air", (byte) 1, i);
                }
            }
        }, 290L + 55L);
    }

    /**
     * Place a cloop at the specified location
     *
     * @param commands    The commands the cloop should execute
     * @param location    Where you want the cloop to be placed
     * @param breakBlocks Whether or not to break the blocks
     */
    public void placeMultiLooping(String[] commands, BlockPos location, boolean breakBlocks) {
        client.player.setYaw(0);
        client.player.setPitch(0);
        client.player.setVelocity(Vec3d.ZERO);

        try {
            itemUtil.giveItem("repeating_command_block", (byte) 1, String.format("{BlockEntityTag:{auto:0b, Command:\"/clone ~ ~ ~%d ~ ~ ~1 ~ ~ ~-%d\"}}", commands.length + 1, commands.length), 0);
            itemUtil.giveItem("redstone_block", (byte) 1, 1);
            for (int i = 0; i < commands.length; i++) {
                itemUtil.giveItem("chain_command_block", (byte) 1, String.format("{BlockEntityTag:{auto:1b, Command:\"%s\"}}", commands[i].replaceAll("\"", "\\\\\"").trim()), 2 + i);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        client.interactionManager.cancelBlockBreaking();

        final Timer t = new Timer();

        t.schedule(new TimerTask() {
            @Override
            public void run() {
                blockUtil.placeBlock(location.add(0, -1, 1), 36);
                for (int i = 0; i < commands.length; i++) {
                    blockUtil.placeBlock(location.add(0, -1, 2 + i), 38 + i);
                }
                blockUtil.placeBlock(location.add(0, -1, 3 + commands.length), 36);
                for (int i = 0; i < commands.length; i++) {
                    blockUtil.placeBlock(location.add(0, -1, (2 + i) + commands.length), 38 + i);
                }
                blockUtil.placeBlock(location.add(0, -1, 3 + commands.length * 2), 36);
                //redstone block
                blockUtil.placeBlock(location.add(1, -1, 2), 37);
            }
        }, 290L);
    }
}
