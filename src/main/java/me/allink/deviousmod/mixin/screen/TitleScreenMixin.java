package me.allink.deviousmod.mixin.screen;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
import me.allink.deviousmod.manager.Managers;
import me.allink.deviousmod.managers.ModuleManager;
import me.allink.deviousmod.modules.AmogusEditionModule;
import me.allink.deviousmod.modules.RetroTitleModule;
import me.allink.deviousmod.screen.CommandBarScreen;
import net.minecraft.SharedConstants;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerWarningScreen;
import net.minecraft.client.gui.screen.option.OptionsScreen;
import net.minecraft.client.gui.screen.pack.PackScreen;
import net.minecraft.client.gui.screen.world.SelectWorldScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.resource.ResourcePackProfile;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3f;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TitleScreen.class)
public abstract class TitleScreenMixin extends Screen {
    @Mutable
    @Shadow
    @Final
    private static Identifier MINECRAFT_TITLE_TEXTURE;
    @Shadow
    @Final
    private static Identifier PANORAMA_OVERLAY;
    RetroTitleModule retroTitleModule;
    @Mutable
    @Shadow
    @Final
    private boolean isMinceraft;
    @Mutable
    @Shadow
    @Final
    private boolean doBackgroundFade;
    @Shadow
    private long backgroundFadeStart;
    @Shadow
    @Nullable
    private String splashText;

    protected TitleScreenMixin(Text title) {
        super(title);
    }

    @Inject(method = "loadTexturesAsync(Lnet/minecraft/client/texture/TextureManager;Ljava/util/concurrent/Executor;)Ljava/util/concurrent/CompletableFuture;", at = @At("HEAD"))
    private static void loadTexturesAsync(TextureManager textureManager, Executor executor, CallbackInfoReturnable<CompletableFuture<Void>> cir) {

    }

    @Shadow
    protected abstract void initWidgetsNormal(int y, int spacingY);

    @Inject(method = "<init>(Z)V", at = @At("TAIL"))
    public void init(boolean doBackgroundFade, CallbackInfo ci) {
        retroTitleModule = (RetroTitleModule) ModuleManager.getModule("RetroTitle");
        if (!retroTitleModule.isToggled()) return;
        this.doBackgroundFade = false;
    }

    @Inject(method = "render(Lnet/minecraft/client/util/math/MatrixStack;IIF)V", at = @At(value = "HEAD"), cancellable = true)
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        Managers.getDiscordRPCManager().onMainMenu();
        if (!retroTitleModule.isToggled()) return;
        ci.cancel();
        TitleScreen thiz = ((TitleScreen) (Object) this);
        TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
        if (this.backgroundFadeStart == 0L && this.doBackgroundFade) {
            this.backgroundFadeStart = Util.getMeasuringTimeMs();
        }

        int vOffset = 0;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
        RenderSystem.setShaderTexture(0, OPTIONS_BACKGROUND_TEXTURE);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        float f = 32.0F;
        bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
        bufferBuilder.vertex(0.0D, thiz.height, 0.0D).texture(0.0F, (float) thiz.height / 32.0F + (float) vOffset).color(64, 64, 64, 255).next();
        bufferBuilder.vertex(thiz.width, thiz.height, 0.0D).texture((float) thiz.width / 32.0F, (float) thiz.height / 32.0F + (float) vOffset).color(64, 64, 64, 255).next();
        bufferBuilder.vertex(thiz.width, 0.0D, 0.0D).texture((float) thiz.width / 32.0F, (float) vOffset).color(64, 64, 64, 255).next();
        bufferBuilder.vertex(0.0D, 0.0D, 0.0D).texture(0.0F, (float) vOffset).color(64, 64, 64, 255).next();
        tessellator.draw();

        boolean i = true;
        int j = thiz.width / 2 - 137;
        boolean k = true;
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, PANORAMA_OVERLAY);
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, this.doBackgroundFade ? (float) MathHelper.ceil(MathHelper.clamp(f, 0.0F, 1.0F)) : 1.0F);
        DrawableHelper.drawTexture(matrices, 0, 0, thiz.width, thiz.height, 0.0F, 0.0F, 16, 128, 16, 128);
        float g = this.doBackgroundFade ? MathHelper.clamp(f - 1.0F, 0.0F, 1.0F) : 1.0F;
        int l = MathHelper.ceil(g * 255.0F) << 24;
        if ((l & -67108864) != 0) {
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            int width = 522 / 2;
            int height = 165 / 2;
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, g);

            AmogusEditionModule amogus = (AmogusEditionModule) ModuleManager.getModule("AmogusEdition");
            if (!amogus.isToggled()) {
                RenderSystem.setShaderTexture(0, MINECRAFT_TITLE_TEXTURE);

                if (this.isMinceraft) {
                    thiz.drawWithOutline(j, 30, (x, y) -> {
                        thiz.drawTexture(matrices, x, y, 0, 0, 99, 44);
                        thiz.drawTexture(matrices, x + 99, y, 129, 0, 27, 44);
                        thiz.drawTexture(matrices, x + 99 + 26, y, 126, 0, 3, 44);
                        thiz.drawTexture(matrices, x + 99 + 26 + 3, y, 99, 0, 26, 44);
                        thiz.drawTexture(matrices, x + 155, y, 0, 45, 155, 44);
                    });
                } else {
                    thiz.drawWithOutline(j, 30, (x, y) -> {
                        thiz.drawTexture(matrices, x, y, 0, 0, 155, 44);
                        thiz.drawTexture(matrices, x + 155, y, 0, 45, 155, 44);
                    });
                }

                if (splashText != null) {
                    matrices.push();
                    matrices.translate(thiz.width / 2 + 90, 70.0D, 0.0D);
                    matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(-20.0F));
                    float h = 1.8F - MathHelper.abs(MathHelper.sin((float) (Util.getMeasuringTimeMs() % 1000L) / 1000.0F * 6.2831855F) * 0.1F);
                    h = h * 100.0F / (float) (textRenderer.getWidth(this.splashText) + 32);
                    matrices.scale(h, h, h);
                    DrawableHelper.drawCenteredText(matrices, textRenderer, this.splashText, 0, -8, 16776960 | l);
                    matrices.pop();
                }

                String h = "Minecraft " + SharedConstants.getGameVersion().getName();
                DrawableHelper.drawStringWithShadow(matrices, textRenderer, h, 2, 1, ColorHelper.Argb.getArgb(255, 80, 80, 80) | l);
            } else {
                RenderSystem.setShaderTexture(0, new Identifier("deviousmod", "amogus.png"));
                thiz.drawWithOutline(j, 30, (x, y) -> {
                    drawTexture(matrices, x, y, 0, 0, width, height, width, height);
                });

                String h = "v" + SharedConstants.getGameVersion().getName();
                DrawableHelper.drawStringWithShadow(matrices, textRenderer, h, 2, 1, ColorHelper.Argb.getArgb(255, 255, 255, 255) | l);

            }


            int copyrightTextWidth = this.textRenderer.getWidth("Copyright Mojang AB. Do not distribute!");
            int copyrightTextX = this.width - copyrightTextWidth - 2;
            DrawableHelper.drawStringWithShadow(matrices, textRenderer, "Copyright Mojang AB. Do not distribute!", copyrightTextX, thiz.height - 10, 16777215 | l);
            if (mouseX > copyrightTextX && mouseX < copyrightTextX + copyrightTextWidth && mouseY > thiz.height - 10 && mouseY < thiz.height) {
                fill(matrices, copyrightTextX, thiz.height - 1, copyrightTextX + copyrightTextWidth, thiz.height, 16777215 | l);
            }

            Iterator var12 = thiz.children().iterator();

            while (var12.hasNext()) {
                Element element = (Element) var12.next();
                if (element instanceof ClickableWidget) {
                    ((ClickableWidget) element).setAlpha(g);
                }
            }

            super.render(matrices, mouseX, mouseY, delta);
        }
    }

    @Inject(method = "init()V", at = @At("HEAD"), cancellable = true)
    public void init(CallbackInfo ci) {
        if (!retroTitleModule.isToggled()) return;
        ci.cancel();
        if (this.splashText == null) {
            this.splashText = this.client.getSplashTextLoader().get();
        }

        boolean i = true;
        int j = this.height / 4 + 48;
        initWidgetsNormal(j, 24);


        this.addDrawableChild(new ButtonWidget(this.width / 2 - 100, j + 24 * 3, 200, 20, new TranslatableText("menu.options"), (button) -> {
            this.client.setScreen(new OptionsScreen(this, this.client.options));
        }));
        this.client.setConnectedToRealms(false);
    }

    @Inject(method = "initWidgetsNormal(II)V", at = @At("HEAD"), cancellable = true)
    public void initWidgetsNormal(int y, int spacingY, CallbackInfo ci) {
        int width = textRenderer.getWidth("Command Bar") + 35;
        this.addDrawableChild(new ButtonWidget(this.width - width, this.height - 33, width, 20, new LiteralText("Command Bar"), (button) -> {
            this.client.setScreen(new CommandBarScreen(new LiteralText("")));
        }));
        if (!retroTitleModule.isToggled()) return;
        ci.cancel();
        GameOptions settings = MinecraftClient.getInstance().options;
        TitleScreen thiz = ((TitleScreen) (Object) this);
        this.addDrawableChild(new ButtonWidget(this.width / 2 - 100, y, 200, 20, new TranslatableText("menu.singleplayer"), (button) -> {
            this.client.setScreen(new SelectWorldScreen(this));
        }));
        boolean bl = this.client.isMultiplayerEnabled();
        ButtonWidget.TooltipSupplier tooltipSupplier = bl ? ButtonWidget.EMPTY : new ButtonWidget.TooltipSupplier() {
            private final Text MULTIPLAYER_DISABLED_TEXT = new TranslatableText("title.multiplayer.disabled");

            public void onTooltip(ButtonWidget buttonWidget, MatrixStack matrixStack, int i, int j) {
                if (!buttonWidget.active) {
                    thiz.renderOrderedTooltip(matrixStack, client.textRenderer.wrapLines(this.MULTIPLAYER_DISABLED_TEXT, Math.max(thiz.width / 2 - 43, 170)), i, j);
                }

            }

            public void supply(Consumer<Text> consumer) {
                consumer.accept(this.MULTIPLAYER_DISABLED_TEXT);
            }
        };
        this.addDrawableChild(new ButtonWidget(this.width / 2 - 100, y + spacingY * 1, 200, 20, new TranslatableText("menu.multiplayer"), (button) -> {
            Screen screen = this.client.options.skipMultiplayerWarning ? new MultiplayerScreen(this) : new MultiplayerWarningScreen(this);
            this.client.setScreen(screen);
        }, tooltipSupplier)).active = bl;
        this.addDrawableChild(new ButtonWidget(this.width / 2 - 100, y + spacingY * 2, 200, 20, new LiteralText("Mods and Texture Packs"), (button) -> {
            this.client.setScreen(new PackScreen(this, this.client.getResourcePackManager(), (resourcePackManager) -> {
                List<String> list = ImmutableList.copyOf(settings.resourcePacks);
                settings.resourcePacks.clear();
                settings.incompatibleResourcePacks.clear();
                Iterator var3 = resourcePackManager.getEnabledProfiles().iterator();

                while (var3.hasNext()) {
                    ResourcePackProfile resourcePackProfile = (ResourcePackProfile) var3.next();
                    if (!resourcePackProfile.isPinned()) {
                        settings.resourcePacks.add(resourcePackProfile.getName());
                        if (!resourcePackProfile.getCompatibility().isCompatible()) {
                            settings.incompatibleResourcePacks.add(resourcePackProfile.getName());
                        }
                    }
                }

                settings.write();
                List<String> list2 = ImmutableList.copyOf(settings.resourcePacks);
                if (!list2.equals(list)) {
                    this.client.reloadResources();
                }
            }, this.client.getResourcePackDir(), new TranslatableText("resourcePack.title")));
        }, tooltipSupplier)).active = bl;
    }
}
