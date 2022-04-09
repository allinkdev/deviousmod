package me.allink.deviousmod.modules;

import java.util.List;
import java.util.Objects;
import me.allink.deviousmod.client.DeviousModClient;
import me.allink.deviousmod.managers.ModuleManager;
import me.allink.deviousmod.module.ModuleBase;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.OtherClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;

public class EggAuraModule extends ModuleBase {

	//long timeSinceLastPlace = 0;

	public EggAuraModule(String name, String description, String category,
		ModuleManager manager) {
		super(name, description, category, manager);
	}

	@Override
	public void tick(MinecraftClient client) {
		super.tick(client);
		if (client.world == null || client.player == null) return;
		if (!client.player.isHolding(Items.AXOLOTL_SPAWN_EGG)) return;
		//if(System.currentTimeMillis() - timeSinceLastPlace < 1000) return;

		int radius = 10;

		List<Entity> otherPlayers = client.world.getOtherEntities(client.player, new Box(client.player.getPos().subtract(radius, radius, radius), client.player.getPos().add(radius, radius, radius)), (entity -> entity instanceof OtherClientPlayerEntity));
		if (otherPlayers.size() > 0) {
			for (Entity asEntity : otherPlayers) {
				OtherClientPlayerEntity otherPlayer = (OtherClientPlayerEntity) asEntity;
				if (Objects.equals(DeviousModClient.CONFIG.eggTarget, "*")) {
					client.interactionManager.interactBlock(client.player, client.world, Hand.MAIN_HAND, new BlockHitResult(otherPlayer.getPos().add(0, 1, 0), Direction.DOWN, otherPlayer.getBlockPos().add(0, 1, 0), false));
				} else {
					if (otherPlayer.getEntityName().equals(DeviousModClient.CONFIG.eggTarget)) {
						client.interactionManager.interactBlock(client.player, client.world, Hand.MAIN_HAND, new BlockHitResult(otherPlayer.getPos().add(0, 1, 0), Direction.DOWN, otherPlayer.getBlockPos().add(0, 1, 0), false));
					}
				}
			}
			//client.interactionManager.interactItem(client.player, client.world, Hand.MAIN_HAND);
		}
	}

}
