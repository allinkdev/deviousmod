package me.allink.deviousmod.commands;

import com.mojang.authlib.GameProfile;
import me.allink.deviousmod.client.DeviousModClient;
import me.allink.deviousmod.command.ChatCommandBase;
import me.allink.deviousmod.exception.NotEnoughArgumentsException;
import me.allink.deviousmod.util.ChatUtil;
import me.allink.deviousmod.util.Utilities;
import net.minecraft.text.Text;

import java.util.List;

public class RealNameCommand extends ChatCommandBase {
    Utilities utilities;
    ChatUtil chatUtil;

    public RealNameCommand(int minimumArguments, String command, String usage, String description, List<String> aliases) {
        super(minimumArguments, command, usage, description, aliases);
        this.utilities = Utilities.getInstance();
        this.chatUtil = utilities.getChatUtil();
    }


    @Override
    public void execute(String[] args) throws NotEnoughArgumentsException {
        super.execute(args);
        String fakeName = args[0];
        for (GameProfile profile : DeviousModClient.tabPlayers) {
            if (profile.getName().equals(fakeName)) {
                try {
                    chatUtil.sendMessage(String.format("%s's real name is %s and their UUID is %s", fakeName, DeviousModClient.usernameCache.get(profile.getId()), profile.getId()));
                    return;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        chatUtil.sendMessage(Text.of(String.format("Couldn't find %s's real name. Please insert among us sussy credits to continue.", fakeName)));
    }
}
