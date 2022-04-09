package me.allink.deviousmod.mixin.world;

import me.allink.deviousmod.client.DeviousModClient;
import me.allink.deviousmod.managers.ModuleManager;
import me.allink.deviousmod.module.ModuleBase;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(World.class)
public class WorldMixin {
    @Inject(at = @At("TAIL"), method = "setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;II)Z")
    public void setBlockState(BlockPos pos, BlockState state, int flags, int maxUpdateDepth, CallbackInfoReturnable<Boolean> cir) {
        ModuleBase commandBlockScannerModule = ModuleManager.getModule("CommandBlockScanner");
        if (commandBlockScannerModule.isToggled()) {
            World world = MinecraftClient.getInstance().world;
            if (world != null) {
                if (!world.isAir(pos)) {
                    if (world.getBlockEntity(pos) != null) {
                        if (world.getBlockEntity(pos).getType() == BlockEntityType.COMMAND_BLOCK) {
                            DeviousModClient.discoverCommandBlock(pos, world.getBlockEntity(pos));
                        }
                    }
                }
            }
        }
    }
}
