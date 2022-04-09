package me.allink.deviousmod.mixin.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.Session;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.io.File;

@Mixin(MinecraftClient.class)
public interface MinecraftClientAccessor {
    @Mutable
    @Accessor("session")
    void setSession(Session session);
}