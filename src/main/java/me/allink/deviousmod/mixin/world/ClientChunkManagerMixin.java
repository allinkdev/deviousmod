package me.allink.deviousmod.mixin.world;

import java.util.Map;
import java.util.function.Consumer;
import me.allink.deviousmod.client.DeviousModClient;
import me.allink.deviousmod.managers.ModuleManager;
import me.allink.deviousmod.module.ModuleBase;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.world.ClientChunkManager;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.s2c.play.ChunkData;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.WorldChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientChunkManager.class)
public class ClientChunkManagerMixin {
    @Inject(method = "loadChunkFromPacket(IILnet/minecraft/network/PacketByteBuf;Lnet/minecraft/nbt/NbtCompound;Ljava/util/function/Consumer;)Lnet/minecraft/world/chunk/WorldChunk;", at = @At("RETURN"))
    public void loadChunkFromPacket(int x, int z, PacketByteBuf buf, NbtCompound nbt, Consumer<ChunkData.BlockEntityVisitor> consumer, CallbackInfoReturnable<WorldChunk> cir) {
        ModuleBase commandBlockScannerModule = ModuleManager.getModule("CommandBlockScanner");
        if (commandBlockScannerModule.isToggled()) {
            WorldChunk worldChunk = cir.getReturnValue();
            Map<BlockPos, BlockEntity> blockPosBlockEntityMap = worldChunk.getBlockEntities();
            for (BlockEntity entity : blockPosBlockEntityMap.values()) {
                if (entity.getType() == BlockEntityType.COMMAND_BLOCK) {
                    DeviousModClient.discoverCommandBlock(entity.getPos(), entity);
                }
            }
        }

    }
}
