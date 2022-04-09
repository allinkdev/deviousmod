package me.allink.deviousmod.mixin.hud;

import com.mojang.authlib.GameProfile;
import me.allink.deviousmod.client.DeviousModClient;
import me.allink.deviousmod.managers.ModuleManager;
import me.allink.deviousmod.modules.BetterTabModule;
import net.minecraft.client.gui.hud.PlayerListHud;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.network.ClientConnection;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerListHud.class)
public class PlayerListHudMixin {
    @Inject(at = @At("HEAD"), method = "getPlayerName(Lnet/minecraft/client/network/PlayerListEntry;)Lnet/minecraft/text/Text;", cancellable = true)
    public void getPlayerName(PlayerListEntry entry, CallbackInfoReturnable<Text> cir) {
        BetterTabModule module = (BetterTabModule) ModuleManager.getModule("BetterTab");

        if(!module.isToggled()) return;

        GameProfile profile = entry.getProfile();
        String profileUUID = DeviousModClient.usernameCache.get(profile.getId());

        if (profileUUID == null) {
            cir.setReturnValue(new LiteralText(profile.getName().replaceAll("§", "&")));
            return;
        }
        Text seperator = new LiteralText(" - ").formatted(Formatting.GRAY);
        Text username = new LiteralText(profile.getName().replaceAll("§", "&"));
        Text uuid = new LiteralText(profileUUID).formatted(Formatting.GRAY);

        if (!profileUUID.equals("Error")) {
            cir.setReturnValue(username.copy().append(seperator).append(uuid));
        }
    }

    @Redirect(method = "render(Lnet/minecraft/client/util/math/MatrixStack;ILnet/minecraft/scoreboard/Scoreboard;Lnet/minecraft/scoreboard/ScoreboardObjective;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/ClientConnection;isEncrypted()Z"))
    public boolean render(ClientConnection instance) {
        return true;
    }

    /*@Redirect(method = "render(Lnet/minecraft/client/util/math/MatrixStack;ILnet/minecraft/scoreboard/Scoreboard;Lnet/minecraft/scoreboard/ScoreboardObjective;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayNetworkHandler;getPlayerList()Ljava/util/Collection;"))
    public Collection<PlayerListEntry> render(ClientPlayNetworkHandler instance) {
        List<PlayerListEntry> masterList = new ArrayList<>();
        masterList.addAll(DeviousModClient.inGamePlayers.values());
        masterList.addAll(DeviousModClient.hiddenPlayers.values());
        return masterList;
    }*/
}
