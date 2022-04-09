package me.allink.deviousmod.modules;

import me.allink.deviousmod.managers.ModuleManager;
import me.allink.deviousmod.module.ModuleBase;
import net.minecraft.client.MinecraftClient;

public class FlightModule extends ModuleBase {
	public FlightModule(String name, String description, String category,
		ModuleManager manager) {
		super(name, description, category, manager);
	}

	@Override
	public void onEnabled() {
		MinecraftClient client = MinecraftClient.getInstance();
		if(client.player != null) {
			client.player.getAbilities().allowFlying = true;
			client.player.getAbilities().setFlySpeed(0.05F * 3);
		}
	}

	@Override
	public void onDisabled() {
		MinecraftClient client = MinecraftClient.getInstance();
		if(client.player != null) {
			client.player.getAbilities().flying = false;
			client.player.getAbilities().allowFlying = client.player.isCreative();
			client.player.getAbilities().setFlySpeed(0.05F);
		}
	}

	@Override
	public void tick(MinecraftClient client) {
		if(client.player != null) {
			client.player.getAbilities().flying = true;
		}
	}
}
