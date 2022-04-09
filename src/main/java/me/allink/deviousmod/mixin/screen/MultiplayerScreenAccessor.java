package me.allink.deviousmod.mixin.screen;

import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerServerListWidget;
import net.minecraft.client.option.ServerList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(MultiplayerScreen.class)
public interface MultiplayerScreenAccessor {
    @Accessor("serverListWidget")
    MultiplayerServerListWidget getServerListWidget();

    @Accessor("serverList")
    ServerList getServerList();

    @Invoker("addEntry")
    void addEntry(boolean confirmedAction);
}
