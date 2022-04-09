package me.allink.deviousmod.screen;

import me.allink.deviousmod.mixin.screen.MultiplayerScreenAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.math.ColorHelper;

import java.nio.file.Files;
import java.nio.file.Path;

public class ExportServersScreen extends Screen {

    private static final Text ENTER_IP_TEXT = new LiteralText("Server Export Path");
    private final MultiplayerScreen parent;
    private String errorText = "";
    private ButtonWidget exportServersButton;
    private TextFieldWidget pathField;

    public ExportServersScreen(Text title, MultiplayerScreen parent) {
        super(title);
        this.parent = parent;
    }

    public void tick() {
        this.pathField.tick();
    }

    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (!this.exportServersButton.active || this.getFocused() != this.pathField || keyCode != 257 && keyCode != 335) {
            return super.keyPressed(keyCode, scanCode, modifiers);
        } else {
            this.exportAndClose();
            return true;
        }
    }

    protected void init() {
        this.client.keyboard.setRepeatEvents(true);
        this.exportServersButton = this.addDrawableChild(new ButtonWidget(this.width / 2 - 100, this.height / 4 + 96 + 12, 200, 20, new LiteralText("Export"), (button) -> {
            this.exportAndClose();
        }));
        this.addDrawableChild(new ButtonWidget(this.width / 2 - 100, this.height / 4 + 120 + 12, 200, 20, ScreenTexts.CANCEL, (x) -> client.setScreen(parent)));
        this.pathField = new TextFieldWidget(this.textRenderer, this.width / 2 - 100, 116, 200, 20, new LiteralText("Path"));
        this.pathField.setMaxLength(Integer.MAX_VALUE);
        this.pathField.setTextFieldFocused(true);
        this.pathField.setChangedListener((text) -> {
            this.onPathFieldChanged();
        });
        this.addSelectableChild(this.pathField);
        this.setInitialFocus(this.pathField);
        this.onPathFieldChanged();
    }

    public void resize(MinecraftClient client, int width, int height) {
        String string = this.pathField.getText();
        this.init(client, width, height);
        this.pathField.setText(string);
    }

    private void exportAndClose() {
        String path = pathField.getText().replace("~", System.getProperties().getProperty("user.home"));
        try {
            MultiplayerScreenAccessor accessor = (MultiplayerScreenAccessor) parent;
            StringBuilder exportText = new StringBuilder();

            for (int i = 0; i < accessor.getServerList().size(); i++) {
                ServerInfo serverInfo = accessor.getServerList().get(i);
                exportText.append(serverInfo.address + "\n");
            }

            Files.writeString(Path.of(path), exportText.toString().trim());

            client.setScreen(parent);
        } catch (Exception e) {
            e.printStackTrace();
            errorText = "An error occurred. Check the logs for details.";
        }
    }

    public void close() {
        this.client.setScreen(this.parent);
    }

    public void removed() {
        this.client.keyboard.setRepeatEvents(false);
    }

    private void onPathFieldChanged() {
        this.exportServersButton.active = !this.pathField.getText().isEmpty();
    }

    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices);
        drawCenteredText(matrices, this.textRenderer, this.title, this.width / 2, 20, 16777215);
        drawTextWithShadow(matrices, this.textRenderer, ENTER_IP_TEXT, this.width / 2 - 100, 100, 10526880);
        drawTextWithShadow(matrices, this.textRenderer, new LiteralText(errorText), this.width / 2 - 100, 145, ColorHelper.Argb.getArgb(255, 255, 85, 85));
        this.pathField.render(matrices, mouseX, mouseY, delta);
        super.render(matrices, mouseX, mouseY, delta);
    }
}
