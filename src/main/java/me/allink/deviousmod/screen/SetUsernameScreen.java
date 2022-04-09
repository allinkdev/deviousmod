package me.allink.deviousmod.screen;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

public class SetUsernameScreen extends Screen {
    String username;

    protected SetUsernameScreen(Text title) {
        super(title);
    }

    public SetUsernameScreen(Text title, String username) {
        super(title);
        this.username = username;
    }

    protected void init() {

    }

    public boolean shouldCloseOnEsc() {
        return false;
    }

    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices);
        drawCenteredText(matrices, this.textRenderer, String.format("Your username has been set to %s.", username), this.width / 2, this.height / 2, 5635925);
        super.render(matrices, mouseX, mouseY, delta);
    }
}
