package me.allink.deviousmod.screen;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.logging.LogUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import me.allink.deviousmod.exception.NotEnoughArgumentsException;
import me.allink.deviousmod.manager.Managers;
import me.allink.deviousmod.managers.ChatCommandManager;
import net.minecraft.client.gui.hud.ChatHudLine;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.ChatMessages;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.slf4j.Logger;

public class CommandBarScreen extends Screen {
    private static final Logger LOGGER = LogUtils.getLogger();
    static CommandBarScreen INSTANCE;
    private final List<ChatHudLine<Text>> messages = Lists.newArrayList();
    private final List<ChatHudLine<OrderedText>> visibleMessages = Lists.newArrayList();
    TextFieldWidget commandInputWidget;
    private boolean hasUnreadNewMessages;
    private int scrolledLines;

    public CommandBarScreen(Text title) {
        super(title);
    }

    private static double getMessageOpacityMultiplier(int age) {
        double d = (double) age / 200.0D;
        d = 1.0D - d;
        d *= 10.0D;
        d = MathHelper.clamp(d, 0.0D, 1.0D);
        d *= d;
        return d;
    }

    public static CommandBarScreen getInstance() {
        return INSTANCE;
    }

    public static int getHeight(double heightOption) {
        return MathHelper.floor(heightOption * 160.0D + 20.0D);
    }

    public void reset() {
        this.visibleMessages.clear();
        this.resetScroll();

        for (int i = this.messages.size() - 1; i >= 0; --i) {
            ChatHudLine<Text> chatHudLine = this.messages.get(i);
            this.addMessage(chatHudLine.getText(), chatHudLine.getId(), chatHudLine.getCreationTick(), true);
        }

    }

    public int getHeight() {
        return getHeight(1.0D);
    }

    public void resetScroll() {
        this.scrolledLines = 0;
        this.hasUnreadNewMessages = false;
    }

    public void scroll(int i) {
        this.scrolledLines += i;
        int j = this.visibleMessages.size();
        if (this.scrolledLines > j - this.getVisibleLineCount()) {
            this.scrolledLines = j - this.getVisibleLineCount();
        }

        if (this.scrolledLines <= 0) {
            this.scrolledLines = 0;
            this.hasUnreadNewMessages = false;
        }

    }

    public int getVisibleLineCount() {
        return getHeight() / 4;
    }

    public void processText() {
        ArrayList<String> args = new ArrayList<>(Arrays.asList(this.commandInputWidget.getText().split(" ")));
        String command = args.get(0);
        args.remove(0);
        ChatCommandManager chatCommandManager = Managers.getChatCommandManager();

        try {
            chatCommandManager.executeIfExists(command, args.toArray(new String[0]));
        } catch (NotEnoughArgumentsException ignored) {
            addMessage("Not enough arguments!");
        } catch (Exception exception) {
            addMessage("Try running the command in-game.");
        }

        commandInputWidget.setText("");
    }

    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        amount = MathHelper.clamp(amount, -1.0D, 1.0D);
        if (!hasShiftDown()) {
            amount *= 7.0D;
        }

        this.scroll((int) amount);
        return true;
    }

    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 266) {
            this.scroll(-getVisibleLineCount() - 1);
            return true;
        } else if (keyCode == 267) {
            this.scroll(-getVisibleLineCount() + 1);
            return true;
        } else if (!this.commandInputWidget.active || this.getFocused() != this.commandInputWidget ||
                keyCode != 257 && keyCode != 335) {
            return super.keyPressed(keyCode, scanCode, modifiers);
        } else {
            processText();
            return true;
        }
    }

    public void init() {
        INSTANCE = this;
        int executeButtonTextWidth = textRenderer.getWidth("Execute") + 35;
        this.commandInputWidget = new TextFieldWidget(this.textRenderer, 3, this.height - 23, this.width - (6 + executeButtonTextWidth), 20, new LiteralText("Enter command here..."));
        this.commandInputWidget.setMaxLength(Integer.MAX_VALUE);
        this.addDrawableChild(new ButtonWidget(this.width - (1 + executeButtonTextWidth), this.height - 23, executeButtonTextWidth, 20, new LiteralText("Execute"), (button) -> {
            processText();
        }));
        this.addDrawableChild(new ButtonWidget(3, 3, 20, 20, new LiteralText("<"), (button) -> {
            this.client.setScreen(new TitleScreen());
        }));

        this.setInitialFocus(commandInputWidget);
    }

    public void addMessage(String message) {
        this.addMessage(new LiteralText(message));
    }

    public void addMessage(Text message) {
        this.addMessage(message, 0);
    }

    private void addMessage(Text message, int messageId) {
        this.addMessage(message, messageId, this.client.inGameHud.getTicks(), false);
        LOGGER.info("[COMMAND BAR] {}", message.getString().replaceAll("\r", "\\\\r").replaceAll("\n", "\\\\n"));
    }

    private void removeMessage(int messageId) {
        this.visibleMessages.removeIf((message) -> {
            return message.getId() == messageId;
        });
        this.messages.removeIf((message) -> {
            return message.getId() == messageId;
        });
    }

    private void addMessage(Text message, int messageId, int timestamp, boolean refresh) {
        if (messageId != 0) {
            this.removeMessage(messageId);
        }

        int i = MathHelper.floor((double) this.width / 1.0f);
        List<OrderedText> list = ChatMessages.breakRenderedChatMessageLines(message, i, this.client.textRenderer);
        boolean bl = true;

        OrderedText orderedText;
        for (Iterator var8 = list.iterator(); var8.hasNext(); this.visibleMessages.add(0, new ChatHudLine(timestamp, orderedText, messageId))) {
            orderedText = (OrderedText) var8.next();
            if (bl && this.scrolledLines > 0) {
                this.hasUnreadNewMessages = true;
                this.scroll(1);
            }
        }

        while (this.visibleMessages.size() > 100) {
            this.visibleMessages.remove(this.visibleMessages.size() - 1);
        }

        if (!refresh) {
            this.messages.add(0, new ChatHudLine(timestamp, message, messageId));

            while (this.messages.size() > 100) {
                this.messages.remove(this.messages.size() - 1);
            }
        }

    }

    public void clear() {
        this.visibleMessages.clear();
        this.messages.clear();
    }

    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        int vOffset = 0;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
        RenderSystem.setShaderTexture(0, new Identifier("textures/misc/enchanted_item_glint.png"));
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        float f = 32.0F;
        bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
        bufferBuilder.vertex(0.0D, this.height, 0.0D).texture(0.0F, (float) this.height / 32.0F + (float) vOffset).color(64, 64, 64, 255).next();
        bufferBuilder.vertex(this.width, this.height, 0.0D).texture((float) this.width / 32.0F, (float) this.height / 32.0F + (float) vOffset).color(64, 64, 64, 255).next();
        bufferBuilder.vertex(this.width, 0.0D, 0.0D).texture((float) this.width / 32.0F, (float) vOffset).color(64, 64, 64, 255).next();
        bufferBuilder.vertex(0.0D, 0.0D, 0.0D).texture(0.0F, (float) vOffset).color(64, 64, 64, 255).next();
        tessellator.draw();


        super.render(matrices, mouseX, mouseY, delta);
        this.commandInputWidget.render(matrices, mouseX, mouseY, delta);

        // Fortnite battle pass
        int i = this.getVisibleLineCount();
        int j = this.visibleMessages.size();
        if (j > 0) {
            boolean bl = true;

            f = 1.0f;
            int k = MathHelper.ceil((float) getHeight() / f);
            matrices.push();
            matrices.translate(4.0D, 8.0D, 0.0D);
            matrices.scale(f, f, 1.0F);
            double d = this.client.options.chatOpacity * 0.8999999761581421D + 0.10000000149011612D;
            double e = this.client.options.textBackgroundOpacity;
            double g = 9.0D * (this.client.options.chatLineSpacing + 1.0D);
            double h = -8.0D * (this.client.options.chatLineSpacing + 1.0D) + 4.0D * this.client.options.chatLineSpacing;
            int l = 0;

            int m;
            int n;
            int p;
            int q;
            for (m = 0; m + this.scrolledLines < this.visibleMessages.size() && m < i; ++m) {
                ChatHudLine<OrderedText> chatHudLine = this.visibleMessages.get(m + this.scrolledLines);
                if (chatHudLine != null) {
                    n = (int) (delta - chatHudLine.getCreationTick());
                    if (n < 200 || bl) {
                        double o = bl ? 1.0D : getMessageOpacityMultiplier(n);
                        p = (int) (255.0D * o * d);
                        q = (int) (255.0D * o * e);
                        ++l;
                        if (p > 3) {
                            double s = (double) (-m) * g;
                            matrices.push();
                            matrices.translate(0.0D, 0.0D, 50.0D);
                            //fill(matrices, -4, (int) (s - g), 0 + k + 4, (int) s, q << 24);
                            //fill(matrices, -4, (int) (s - g), 0 + k + 4, (int) s, q << 24);
                            RenderSystem.enableBlend();
                            matrices.translate(0.0D, 0.0D, 50.0D);
                            this.client.textRenderer.drawWithShadow(matrices, chatHudLine.getText(), 0.0F, (float) (((this.height - this.visibleMessages.size() * this.client.options.chatLineSpacing * 20.0f) - 20 * 2) + (int) (s + h)), 16777215 + (p << 24));
                            RenderSystem.disableBlend();
                            matrices.pop();
                        }
                    }
                }
            }

            int chatHudLine;

            Objects.requireNonNull(this.client.textRenderer);
            m = 9;
            chatHudLine = j * m;
            n = l * m;
            int o = this.scrolledLines * n / j;
            int t = n * n / chatHudLine;
            if (chatHudLine != n) {
                p = o > 0 ? 170 : 96;
                q = this.hasUnreadNewMessages ? 13382451 : 3355562;
                matrices.translate(-4.0D, 0.0D, 0.0D);
                fill(matrices, 0, -o, 2, -o - t, q + (p << 24));
                fill(matrices, 2, -o, 1, -o - t, 13421772 + (p << 24));
            }

            matrices.pop();
        }

    }
}
