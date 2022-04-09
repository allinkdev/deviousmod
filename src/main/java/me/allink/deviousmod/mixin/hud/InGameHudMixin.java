package me.allink.deviousmod.mixin.hud;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;
import com.mojang.blaze3d.systems.RenderSystem;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import me.allink.deviousmod.client.DeviousModClient;
import me.allink.deviousmod.managers.ExperimentsManager;
import me.allink.deviousmod.managers.ModuleManager;
import me.allink.deviousmod.module.ModuleBase;
import me.allink.deviousmod.modules.AmogusEditionModule;
import me.allink.deviousmod.modules.DeviousWatermarkModule;
import me.allink.deviousmod.modules.ModuleListModule;
import me.allink.deviousmod.modules.TimingsModule;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.AbstractInventoryScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.StatusEffectSpriteManager;
import net.minecraft.client.util.Window;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public abstract class InGameHudMixin {

    DeviousWatermarkModule deviousWatermarkModule = (DeviousWatermarkModule) ModuleManager.getModule(
        "DeviousWatermark");
    ModuleListModule moduleListModule = (ModuleListModule) ModuleManager.getModule("ModuleList");
    List<Identifier> amogus = new ArrayList<>();
    List<Integer> decimalColors = new ArrayList<>();
    int ticks;

    @Shadow
    private int scaledWidth;
    @Shadow
    @Final
    private MinecraftClient client;
    @Shadow
    private int scaledHeight;

    @Shadow
    public abstract TextRenderer getTextRenderer();

    @Shadow
    public abstract void render(MatrixStack matrices, float tickDelta);

    @Inject(at = @At("TAIL"), method = "<init>(Lnet/minecraft/client/MinecraftClient;)V")
    public void init(MinecraftClient client, CallbackInfo ci) {
        //decimalColors = List.of(11141120, 16733525, 16755200, 16777045, 43520, 5635925, 5636095, 43690, 5592575, 16733695, 11141290);
        for (int i = 1; i <= 6; i++) {
            String filename = String.format("sussybaka%s.png", i);
            amogus.add(new Identifier("deviousmod", filename));
        }
    }

    @Inject(at = @At("TAIL"), method = "tick(Z)V")
    public void tick(boolean paused, CallbackInfo ci) {
        if (ticks >= 20) {
            ticks = 0;
        } else {
            ticks++;
        }
    }

    /**
     * @author Allink
     * @reason Offset/disable the status effect overlay
     */
    @Overwrite
    public void renderStatusEffectOverlay(MatrixStack matrices) {
        if (!ExperimentsManager.isExperimentEnabled("b8f315ac-2b22-433f-8a8c-697b5503d1a7")) {
            return;
        }
        InGameHud thiz = ((InGameHud) (Object) this);
        Collection collection;
        label40:
        {
            collection = this.client.player.getStatusEffects();
            if (!collection.isEmpty()) {
                Screen var4 = this.client.currentScreen;
                if (!(var4 instanceof AbstractInventoryScreen)) {
                    break label40;
                }

                AbstractInventoryScreen abstractInventoryScreen = (AbstractInventoryScreen) var4;
                if (!abstractInventoryScreen.hideStatusEffectHud()) {
                    break label40;
                }
            }

            return;
        }

        RenderSystem.enableBlend();
        int abstractInventoryScreen = 0;
        int i = 0;
        StatusEffectSpriteManager statusEffectSpriteManager = this.client.getStatusEffectSpriteManager();
        List<Runnable> list = Lists.newArrayListWithExpectedSize(collection.size());
        RenderSystem.setShaderTexture(0, HandledScreen.BACKGROUND_TEXTURE);
        Iterator var7 = Ordering.natural().reverse().sortedCopy(collection).iterator();

        while (var7.hasNext()) {
            StatusEffectInstance statusEffectInstance = (StatusEffectInstance) var7.next();
            StatusEffect statusEffect = statusEffectInstance.getEffectType();
            if (statusEffectInstance.shouldShowIcon()) {
                int j = this.scaledWidth;
                int k = 1;
                if (ModuleManager.getModule("ModuleList").isToggled()) {
                    k = 52 + 4 + ((ModuleManager.getModule("DeviousWatermark").isToggled()) ? 10
                        : 0);
                }

                if (this.client.isDemo()) {
                    k += 15;
                }

                if (statusEffect.isBeneficial()) {
                    ++abstractInventoryScreen;
                    j -= 25 * abstractInventoryScreen;
                } else {
                    ++i;
                    j -= 25 * i;
                    k += 26;
                }

                RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
                float f = 1.0F;
                if (statusEffectInstance.isAmbient()) {
                    thiz.drawTexture(matrices, j, k, 165, 166, 24, 24);
                } else {
                    thiz.drawTexture(matrices, j, k, 141, 166, 24, 24);
                    if (statusEffectInstance.getDuration() <= 200) {
                        int l = 10 - statusEffectInstance.getDuration() / 20;
                        f = MathHelper.clamp(
                            (float) statusEffectInstance.getDuration() / 10.0F / 5.0F * 0.5F, 0.0F,
                            0.5F) + MathHelper.cos(
                            (float) statusEffectInstance.getDuration() * 3.1415927F / 5.0F)
                            * MathHelper.clamp((float) l / 10.0F * 0.25F, 0.0F, 0.25F);
                    }
                }

                Sprite l = statusEffectSpriteManager.getSprite(statusEffect);
                int finalJ = j;
                int finalK = k;
                float finalF = f;
                list.add(() -> {
                    RenderSystem.setShaderTexture(0, l.getAtlas().getId());
                    RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, finalF);
                    DrawableHelper.drawSprite(matrices, finalJ + 3, finalK + 3, thiz.getZOffset(),
                        18, 18, l);
                });
            }
        }

        list.forEach(Runnable::run);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
    }

    @Inject(at = @At("HEAD"), method = "render(Lnet/minecraft/client/util/math/MatrixStack;F)V")
    public void render(MatrixStack matrices, float tickDelta, CallbackInfo ci) {
        boolean f3open = MinecraftClient.getInstance().options.debugEnabled;
        int offset = 4;
        int increment = 10;
        if (!f3open) {
            if (deviousWatermarkModule.isToggled()) {
                offset = increment + offset;
                float x = System.currentTimeMillis() % 2000 / 1000F;
                float red = 0.5F + 0.5F * MathHelper.sin(x * (float) Math.PI);
                float green =
                    0.5F + 0.5F * MathHelper.sin((x + 4F / 3F) * (float) Math.PI);
                float blue =
                    0.5F + 0.5F * MathHelper.sin((x + 8F / 3F) * (float) Math.PI);
                int textColor = 0x04 << 16 | (int) (red * 256) << 16
                    | (int) (green * 256) << 8 | (int) (blue * 256);
                String version = DeviousModClient.version;
                Text deviousText = new LiteralText("deviousmod " + version).getWithStyle(
                    Style.EMPTY.withFont(new Identifier("deviousmod", "roboto"))).get(0);
                getTextRenderer().drawWithShadow(matrices, deviousText,
                    this.scaledWidth - this.getTextRenderer().getWidth(deviousText) - 4, 4,
                    textColor);
            }

            if (moduleListModule.isToggled()) {
                List<String> enabledModules = new ArrayList<>();

                for (ModuleBase module : ModuleManager.getModules()) {
                    if (module.isToggled()) {
                        enabledModules.add(module.name);
                    }
                }

                enabledModules.sort(Comparator.comparingInt(String::length));
                enabledModules = ImmutableList.copyOf(enabledModules).reverse();

                for (String module : enabledModules) {
                    String originalText = String.format("%s <", module);
                    Text text = new LiteralText(originalText).getWithStyle(
                        Style.EMPTY.withFont(new Identifier("deviousmod", "roboto"))).get(0);
                    Random random = new Random();
                    long seed = 0;
                    for (byte aByte : originalText.getBytes()) {
                        seed += aByte;
                    }
                    random.setSeed(seed);
                    //int textColor = ColorHelper.Argb.getArgb(255, random.nextInt(140, 255), random.nextInt(140, 255), random.nextInt(140, 255));
                    //int textColor = decimalColors.get(random.nextInt(0, decimalColors.size()));
                    int textColor = ColorHelper.Argb.getArgb(255, 255, 255, 255);
                    getTextRenderer().drawWithShadow(matrices, text,
                        this.scaledWidth - this.getTextRenderer().getWidth(text) - 4, offset,
                        textColor);
                    offset += increment;
                }
            }
        }

        AmogusEditionModule amogos = (AmogusEditionModule) ModuleManager.getModule("AmogusEdition");

        if (amogos.isToggled()) {
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glDisable(GL11.GL_CULL_FACE);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

            RenderSystem.setShaderColor(1, 1, 1, 1);
            RenderSystem.setShaderTexture(0, amogus.get(ticks / 4));
            Window sr = client.getWindow();
            int x = sr.getScaledWidth() / 2 - 77 + 76;
            int y = sr.getScaledHeight() - 77 - 19;
            int w = 77;
            int h = 77;
            DrawableHelper.drawTexture(matrices, x, y, 0, 0, w, h, w, h);

            GL11.glEnable(GL11.GL_CULL_FACE);
            GL11.glDisable(GL11.GL_BLEND);
        }

        int packetOffset = 10;

        if (this.client.currentScreen != null) {
            if (this.client.currentScreen.getClass().equals(ChatScreen.class)) {
                packetOffset += 14;
            }
        }

        TimingsModule timingsModule = (TimingsModule) ModuleManager.getModule("Timings");

        if (timingsModule.isToggled()) {
            String lastPacketClassName = DeviousModClient.lastPacket.getClass().getSimpleName();
            String mappedName = DeviousModClient.getInstance().classMapping.get(
                lastPacketClassName);

            if (mappedName == null) {
                mappedName = lastPacketClassName;
            }

            Duration duration = Duration.between(DeviousModClient.timeSinceLastPacket,
                Instant.now());
            String packetText = String.format(
                "Time since last packet of type %s: %sms (%ss or %sm)",
                mappedName,
                duration.toMillis(), duration.toSeconds(), duration.toMinutes());
            getTextRenderer().drawWithShadow(matrices, packetText,
                this.scaledWidth - this.getTextRenderer().getWidth(packetText) - 4,
                this.scaledHeight - packetOffset, ColorHelper.Argb.getArgb(255, 255, 255, 255));
            packetOffset += 10;
            duration = Duration.between(DeviousModClient.timeSinceLastTick, Instant.now());
            packetText = String.format("Time since last time update: %sms (%ss or %sm)",
                duration.toMillis(), duration.toSeconds(), duration.toMinutes());
            getTextRenderer().drawWithShadow(matrices, packetText,
                this.scaledWidth - this.getTextRenderer().getWidth(packetText) - 4,
                this.scaledHeight - packetOffset, ColorHelper.Argb.getArgb(255, 255, 255, 255));
        }
    }

    @Inject(at = @At("HEAD"), method = "renderStatusBars(Lnet/minecraft/client/util/math/MatrixStack;)V", cancellable = true)
    public void renderStatusBars(MatrixStack matrices, CallbackInfo ci) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player.getMaxHealth() < 1 || Float.isNaN(client.player.getMaxHealth())) {
            ci.cancel();
        }
    }
}
