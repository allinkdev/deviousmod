package me.allink.deviousmod.mixin.screen;

import me.allink.deviousmod.manager.Managers;
import me.allink.deviousmod.screen.ExportServersScreen;
import me.allink.deviousmod.screen.ImportServersScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MultiplayerScreen.class)
public class MultiplayerScreenMixin extends Screen {
    protected MultiplayerScreenMixin(Text title) {
        super(title);
    }

    @Inject(at = @At("TAIL"), method = "init()V")
    public void init(CallbackInfo ci) {
        addDrawableChild(new ButtonWidget(width / 2 - 154 - 100 - 4, height - 52, 100,
                20, new LiteralText("Import servers..."), (b) -> {
            client.setScreen(new ImportServersScreen(new LiteralText("Import Servers"), ((MultiplayerScreen) (Object) this)));
        }
        ));

        addDrawableChild(new ButtonWidget(width / 2 - 154 - 100 - 4, height - 28, 100,
                20, new LiteralText("Export servers..."), (b) -> {
            client.setScreen(new ExportServersScreen(new LiteralText("Export Servers"), ((MultiplayerScreen) (Object) this)));
        }));

        Managers.getDiscordRPCManager().onMainMenu();
    }
}
