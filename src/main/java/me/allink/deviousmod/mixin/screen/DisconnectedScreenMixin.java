package me.allink.deviousmod.mixin.screen;

import java.util.Objects;
import me.allink.deviousmod.managers.ModuleManager;
import me.allink.deviousmod.modules.ClassicDisconnectedScreenModule;
import net.minecraft.client.font.MultilineText;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.DisconnectedScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.math.ColorHelper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DisconnectedScreen.class)
public class DisconnectedScreenMixin extends Screen {

	@Shadow private MultilineText reasonFormatted;

	@Shadow private int reasonHeight;

	@Shadow @Final private Screen parent;

	protected DisconnectedScreenMixin(Text title) {
		super(title);
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if(keyCode == 256) {
			this.client.setScreen(this.parent);
			return true;
		}

		return false;
	}

	@Inject(method = "render", at = @At("HEAD"), cancellable = true)
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta,
		CallbackInfo ci) {
		ClassicDisconnectedScreenModule module = (ClassicDisconnectedScreenModule) ModuleManager.getModule("ClassicDisconnectedScreen");
		if(module.isToggled()) {
			ci.cancel();
		} else {
			return;
		}
		this.fillGradient(matrices, 0, 0, this.width, this.height, -12574688, -11530224);
		TextRenderer textRenderer = this.textRenderer;
		int centerWidth = this.width / 2;
		int centeredReason = this.height / 2 - this.reasonHeight / 2;
		Objects.requireNonNull(this.textRenderer);
		drawCenteredText(matrices, textRenderer, "Disconnected!", centerWidth, centeredReason - 9 * 2,
			ColorHelper.Argb.getArgb(255, 255, 255, 255));
		this.reasonFormatted.drawCenterWithShadow(matrices, this.width / 2, this.height / 2 - this.reasonHeight / 2);
		super.render(matrices, mouseX, mouseY, delta);
	}
}