package me.allink.deviousmod.managers;

import com.google.common.collect.Maps;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import me.allink.deviousmod.client.DeviousModClient;
import me.allink.deviousmod.commands.CannonCommand;
import me.allink.deviousmod.gui.ClickGui;
import me.allink.deviousmod.json.config.Config;
import me.allink.deviousmod.manager.ManagerBase;
import me.allink.deviousmod.manager.Managers;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.InputUtil.Type;
import net.minecraft.text.LiteralText;

public class ExperimentsManager extends ManagerBase {
	public static final Map<String, Object[]> experiments = Maps.newHashMap(Map.ofEntries(
		Map.entry("8a907415-d2d4-4d57-95d2-870ab4fa011c", new Object[]{"Cannon Command", "Gives you a cannon (prone to crashing)", false}),
		Map.entry("b8f315ac-2b22-433f-8a8c-697b5503d1a7", new Object[]{"Offset effects", "Offsets effects displayed in HUD", false})
	));

	public static boolean isExperimentEnabled(String id) {
		try {
			return (boolean) experiments.get(id)[2];
		} catch (Exception e) {
			return false;
		}
	}

	public static void setExperimentEnabled(String id, boolean enabled) {
		try {
			Object[] newValue = experiments.get(id);
			newValue[2] = enabled;
			experiments.put(id, newValue);
		} catch (Exception ignored) {
		}

		List<String> configExperiments = new ArrayList<>();

		for (String s : experiments.keySet()) {
			if((boolean) experiments.get(s)[2]) {
				configExperiments.add(s);
			}
		}

		DeviousModClient.CONFIG.experiments = configExperiments;
		DeviousModClient.getInstance().saveConfig();
	}

	public static void loadExperiments(Config config) {
		for (String experiment : config.experiments) {
			setExperimentEnabled(experiment, true);
		}
		if(isExperimentEnabled("bfb67caf-b55e-4a3b-9938-e1066ad7df8d")) {
			KeyBinding keyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
				"ClickGui",
				Type.KEYSYM,
				InputUtil.GLFW_KEY_RIGHT_SHIFT,
				"DeviousMod"
			));

			ClientTickEvents.END_CLIENT_TICK.register(client -> {
				while (keyBinding.wasPressed()) {
					if (client.currentScreen instanceof ClickGui) {
						client.setScreen(null);
					} else {
						client.setScreen(new ClickGui(new LiteralText("Click Gui")));
					}
				}
			});
		}
		if(isExperimentEnabled("8a907415-d2d4-4d57-95d2-870ab4fa011c")) {
			Managers.getChatCommandManager().commands.add(new CannonCommand(0, "cannon", "cannon", "boom", List.of("boom", "handcannon", "pewpew")));
		}
	}
}
