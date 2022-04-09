package me.allink.deviousmod.mixin.network.packet;

import me.allink.deviousmod.client.DeviousModClient;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(GameMessageS2CPacket.class)
public class GameMessageS2CPacketMixin {
    @Redirect(method = "<init>(Lnet/minecraft/network/PacketByteBuf;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/PacketByteBuf;readText()Lnet/minecraft/text/Text;"))
    private Text init(PacketByteBuf instance) {
        String json = PacketByteBufs.copy(instance).readString();
        Text text = PacketByteBufs.copy(instance).readText();
        DeviousModClient.parsedToRaw.put(text, json);
        //JsonObject obj = JsonParser.parseString(json).getAsJsonObject();
        //System.out.println(json);
        //System.out.println(Parser.parse(obj, true, 10));
        //t.start();
        return instance.readText();
    }
}
