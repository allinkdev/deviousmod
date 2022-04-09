package me.allink.deviousmod.gui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import me.allink.deviousmod.gui.util.RGB;
import me.allink.deviousmod.managers.ModuleManager;
import me.allink.deviousmod.module.ModuleBase;
import me.allink.deviousmod.util.Utilities;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class ClickGui extends Screen {
	private String selected = null;

	public ClickGui(Text title) {
		super(title);
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		if(selected != null) {
			boolean newValue = ModuleManager.getModule(selected).toggle();
			Utilities.getInstance().getChatUtil().sendMessage(Text.of(
					String.format("%s %s successfully.", (newValue) ? "Enabled" : "Disabled", selected))
				.copy().formatted((newValue) ? Formatting.GREEN : Formatting.RED));
			return true;
		}

		return false;
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		super.render(matrices, mouseX, mouseY, delta);
		selected = null;
		List<ModuleBase> rawModules = ModuleManager.getModules();
		List<String> categories = new ArrayList<>();
		Map<String, String[]> modules = new HashMap<>();
		for (ModuleBase rawModule : rawModules) {
			if(!categories.contains(rawModule.category)) {
				categories.add(rawModule.category);
			}
			if(!modules.containsKey(rawModule.category)) {
				modules.put(rawModule.category, new String[]{rawModule.name});
			} else {
				// add module into modules array of category
				String[] moduleArray = modules.get(rawModule.category);
				String[] newModuleArray = new String[moduleArray.length + 1];
				for (int i = 0; i < moduleArray.length; i++) {
					newModuleArray[i] = moduleArray[i];
				}
				newModuleArray[moduleArray.length] = rawModule.name;
				modules.put(rawModule.category, newModuleArray);
			}
		}

		categories.sort(String::compareToIgnoreCase);
		final RGB baseColor = new RGB(255, 63, 63, 63);
		final RGB moduleColor = new RGB(255, 43, 43, 43);
		int paddingX = 15;
		int paddingY = 30;
		int offset = 0;

		for (String category : categories) {
			// render background
			int biggestModule = 0;
			String[] modulesInCategory = modules.get(category);

			for (String module : modulesInCategory) {
				if (textRenderer.getWidth(module) > biggestModule) {
					biggestModule = textRenderer.getWidth(module);
				}
			}

			int textWidth = textRenderer.getWidth(category) + biggestModule;
			fill(matrices, paddingX + offset - 5, paddingY - 3, (paddingX + offset) + textWidth + 5, paddingY + 10, baseColor.getIntRGB());
			textRenderer.drawWithShadow(matrices, category, paddingX + offset, paddingY, 0xFFFFFFFF);

			// render modules
			int moduleOffset = 14;
			for (String module : modulesInCategory) {
				RGB thisParticularFuckingModuleColour = (ModuleManager.isEnabled(module))? new RGB(255,2, 89, 15) : moduleColor;
				boolean active = false;

				int paddingY4Module = 14;

				int x1 = paddingX + offset - 5;
				int y1 = paddingY + moduleOffset - 3;
				int x2 = (paddingX + offset) + textWidth + 5;
				int y2 = (paddingY + moduleOffset) + paddingY4Module;

				if(mouseX >= x1 && mouseX <= x2 && mouseY >= y1 && mouseY <= y2 && selected == null) {
					active = true;
					selected = module;
				}

				fill(matrices, x1, y1, x2 ,y2 , (active) ? ((thisParticularFuckingModuleColour.equals(moduleColor)) ? moduleColor.subtract(0, 10, 10, 10).getIntRGB() : new RGB(255, 2, 68, 12).getIntRGB()) : thisParticularFuckingModuleColour.getIntRGB());
				textRenderer.drawWithShadow(matrices, module, paddingX + offset, paddingY + moduleOffset, 0xFFFFFFFF);
				moduleOffset += paddingY4Module;
			}

			offset += paddingX + textWidth;
		}

		if(selected != null) {
			this.renderTooltip(matrices, new LiteralText(ModuleManager.getModule(selected).description), mouseX, mouseY);
		}

		//fill(matrices, mouseX - width, mouseY - height, mouseX + width, mouseY + height, baseColor.getIntRGB());
	}

	@Override
	protected void init() {
		super.init();
	}
}
