package me.allink.deviousmod.mixin.network;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.TimeoutException;
import java.time.Instant;
import me.allink.deviousmod.client.DeviousModClient;
import me.allink.deviousmod.managers.ModuleManager;
import me.allink.deviousmod.util.ChatUtil;
import me.allink.deviousmod.util.Utilities;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketEncoderException;
import net.minecraft.network.packet.c2s.play.ResourcePackStatusC2SPacket;
import net.minecraft.network.packet.s2c.play.ResourcePackSendS2CPacket;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientConnection.class)
public class ClientConnectionMixin {
    @Shadow
    private Channel channel;

    @Shadow
    private boolean errored;

    //@Inject(method = "exceptionCaught(Lio/netty/channel/ChannelHandlerContext;Ljava/lang/Throwable;)V", at = @At("HEAD"), cancellable = true)
    public void exceptionCaught(ChannelHandlerContext context, Throwable ex, CallbackInfo ci) {
        ci.cancel();
        Utilities utilities = Utilities.getInstance();
        ChatUtil chatUtil = utilities.getChatUtil();
        if (!(ex instanceof PacketEncoderException)) {
            if (this.channel.isOpen()) {
                if (ex instanceof TimeoutException) {
                    boolean bl = !this.errored;
                    this.errored = true;
                    ((ClientConnection) (Object) this).disconnect(new TranslatableText("disconnect.timeout"));
                } else {
                    chatUtil.sendMessage(new LiteralText("Avoided invalid packet disconnect. (dbg info: exceptioncaught)").formatted(Formatting.GOLD));
                }

            }
        }
    }

    @Inject(method = "channelRead0(Lio/netty/channel/ChannelHandlerContext;Lnet/minecraft/network/Packet;)V", at = @At("HEAD"), cancellable = true)
    public void channelRead0(ChannelHandlerContext channelHandlerContext, Packet<?> packet, CallbackInfo ci) {
        DeviousModClient.timeSinceLastPacket = Instant.now();
        DeviousModClient.lastPacket = packet;
        MinecraftClient client = MinecraftClient.getInstance();

        if (packet instanceof ResourcePackSendS2CPacket && ModuleManager.getModule("NoSRP").isToggled()) {
            ci.cancel();
            new Thread(() -> {
                try {
                    Thread.sleep(100L);
                    if (client.world == null) return;
                    client.getNetworkHandler().sendPacket(new ResourcePackStatusC2SPacket(ResourcePackStatusC2SPacket.Status.ACCEPTED));
                    Thread.sleep(5000L);
                    if (client.world == null) return;
                    client.getNetworkHandler().sendPacket(new ResourcePackStatusC2SPacket(ResourcePackStatusC2SPacket.Status.SUCCESSFULLY_LOADED));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }


    //@Redirect(method = "channelRead0(Lio/netty/channel/ChannelHandlerContext;Lnet/minecraft/network/Packet;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/ClientConnection;disconnect(Lnet/minecraft/text/Text;)V"))
    public void disconnect(ClientConnection instance, Text disconnectReason) {
        Utilities utilities = Utilities.getInstance();
        ChatUtil chatUtil = utilities.getChatUtil();
        chatUtil.sendMessage(new LiteralText("Avoided invalid packet disconnect. (dbg info: channelread0)").formatted(Formatting.GOLD));
        return;
    }
}
