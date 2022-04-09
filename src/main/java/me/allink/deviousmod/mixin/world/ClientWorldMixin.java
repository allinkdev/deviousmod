package me.allink.deviousmod.mixin.world;

import me.allink.deviousmod.client.DeviousModClient;
import me.allink.deviousmod.manager.Managers;
import me.allink.deviousmod.managers.DiscordRPCManager;
import me.allink.deviousmod.managers.ModuleManager;
import me.allink.deviousmod.module.ModuleBase;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientWorld.class)
public class ClientWorldMixin {
    @Shadow
    @Final
    private MinecraftClient client;

    @Shadow
    @Final
    private WorldRenderer worldRenderer;

    @Inject(at = @At("TAIL"), method = "addEntityPrivate(ILnet/minecraft/entity/Entity;)V")
    public void addEntityPrivate(int id, Entity entity, CallbackInfo ci) {
        if (entity.getType() == EntityType.FIREBALL) {
            client.interactionManager.attackEntity(client.player, entity);
        }
    }

    @Inject(at = @At("TAIL"), method = "disconnect()V")
    public void disconnect(CallbackInfo ci) {
        DeviousModClient.commandBlocks.clear();
        DeviousModClient.inGamePlayers.clear();
        DeviousModClient.hiddenPlayers.clear();
        DeviousModClient.spectating = null;
        ModuleBase commandBlockScannerModule = ModuleManager.getModule("CommandBlockScanner");
        commandBlockScannerModule.setToggled(false);
        DiscordRPCManager rpcManager = Managers.getDiscordRPCManager();
        rpcManager.onPlayGame();
        Managers.getModuleManager().onLeave(this.client);
    }
}
