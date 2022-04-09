package me.allink.deviousmod.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

public class BlockUtil {
    MinecraftClient client;

    public BlockUtil(MinecraftClient client) {
        System.out.println("BlockUtil initialized");
        this.client = client;
    }

    /**
     * Place a block from your hotbar
     *
     * @param target Where you want to place the block
     * @param slot   The hotbar slot of the block
     */
    public void placeBlock(BlockPos target, int slot) {
        placeBlock(new Vec3d(target.getX(), target.getY(), target.getZ()), slot);
    }

    /**
     * Place a block from your hotbar
     *
     * @param target Where you want to place the block
     * @param slot   The hotbar slot of the block
     */
    public void placeBlock(Vec3d target, int slot) {
        if (client.world.isAir(new BlockPos(target.x, target.y, target.z))) {
            PlayerInventory inventory = client.player.getInventory();
            inventory.selectedSlot = slot - 36;
            client.interactionManager.interactBlock(client.player, client.world, Hand.MAIN_HAND, new BlockHitResult(target, Direction.DOWN, new BlockPos(target.x, target.y, target.z), false));
        }
    }

    /**
     * Break block
     *
     * @param target The position of the block you want to break
     */
    public void breakBlock(Vec3d target) {
        breakBlock(new BlockPos(Math.floor(target.x), Math.floor(target.y), Math.floor(target.z)));
    }

    /**
     * Break block
     *
     * @param target The position of the block you want to break
     */
    public void breakBlock(BlockPos target) {
        client.getNetworkHandler().sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.START_DESTROY_BLOCK, target, Direction.DOWN));
    }
}
