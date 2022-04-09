package me.allink.deviousmod.commands;

import me.allink.deviousmod.command.ChatCommandBase;
import me.allink.deviousmod.exception.NotEnoughArgumentsException;
import me.allink.deviousmod.mixin.client.MinecraftClientAccessor;
import me.allink.deviousmod.screen.SetUsernameScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.Session;
import net.minecraft.text.LiteralText;
import org.apache.commons.lang3.RandomStringUtils;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class SetUsernameCommand extends ChatCommandBase {
    public SetUsernameCommand(int minimumArguments, String command, String usage, String description, List<String> aliases) {
        super(minimumArguments, command, usage, description, aliases);
    }

    @Override
    public void execute(String[] args) throws NotEnoughArgumentsException {
        super.execute(args);
        String newUsername = String.join(" ", args);
        MinecraftClient client = MinecraftClient.getInstance();
        ((MinecraftClientAccessor) client).setSession(new Session(newUsername, UUID.nameUUIDFromBytes(String.format("OfflinePlayer:%s", newUsername).getBytes()).toString(), RandomStringUtils.randomAlphanumeric(16), Optional.empty(), Optional.empty(), Session.AccountType.LEGACY));
        if (client.world != null) {
            client.world.disconnect();
            client.disconnect(new SetUsernameScreen(new LiteralText(""), newUsername));
        }
    }
}
