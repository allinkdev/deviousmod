package me.allink.deviousmod.commands;

import joptsimple.internal.Strings;
import me.allink.deviousmod.client.DeviousModClient;
import me.allink.deviousmod.command.ChatCommandBase;
import me.allink.deviousmod.exception.NotEnoughArgumentsException;
import me.allink.deviousmod.util.BanUtil;
import me.allink.deviousmod.util.ChatUtil;
import me.allink.deviousmod.util.Utilities;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Formatting;

import java.util.List;

public class BanCommand extends ChatCommandBase {
    Utilities utilities;
    BanUtil banUtil;
    ChatUtil chatUtil;

    public BanCommand(int minimumArguments, String command, String usage, String description, List<String> aliases) {
        super(minimumArguments, command, usage, description, aliases);
        this.utilities = Utilities.getInstance();
        this.banUtil = utilities.getBanUtil();
        this.chatUtil = utilities.getChatUtil();
    }

    @Override
    public void execute(String[] args) throws NotEnoughArgumentsException {
        super.execute(args);
        String identifier = Strings.join(args, " ");
        if (!DeviousModClient.CONFIG.banList.contains(identifier)) {
            chatUtil.sendMessage(String.format("Banning %s...", identifier));
            banUtil.banPlayer(identifier);
            DeviousModClient.CONFIG.banList.add(identifier);
            DeviousModClient.getInstance().saveConfig();
        } else {
            chatUtil.sendMessage(new LiteralText("That player is already banned, so we're just going to re-make the ban cart...").formatted(Formatting.GOLD));
            banUtil.banPlayer(identifier);
        }
    }
}
