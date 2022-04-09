package me.allink.deviousmod.commands;

import joptsimple.internal.Strings;
import me.allink.deviousmod.client.DeviousModClient;
import me.allink.deviousmod.command.ChatCommandBase;
import me.allink.deviousmod.exception.NotEnoughArgumentsException;
import me.allink.deviousmod.util.ChatUtil;
import me.allink.deviousmod.util.Utilities;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Formatting;

import java.util.List;

public class SetChatPrefixCommand extends ChatCommandBase {
    Utilities utilities;
    ChatUtil chatUtil;

    public SetChatPrefixCommand(int minimumArguments, String command, String usage, String description, List<String> aliases) {
        super(minimumArguments, command, usage, description, aliases);
        utilities = Utilities.getInstance();
        chatUtil = utilities.getChatUtil();
    }

    @Override
    public void execute(String[] args) throws NotEnoughArgumentsException {
        super.execute(args);
        String prefix = Strings.join(args, " ").trim();
        if (prefix.equalsIgnoreCase("off")) {
            prefix = "";
        } else if (prefix.equalsIgnoreCase("reset")) {
            prefix = "Ｄｅｖｉｏｕｓ Ｍｏｄ «";
        }

        DeviousModClient.CONFIG.chatPrefix = prefix;
        DeviousModClient.getInstance().saveConfig(DeviousModClient.CONFIG);
        chatUtil.sendMessage(new LiteralText(String.format("Updated your chat prefix to \"%s\"", prefix)).formatted(Formatting.GREEN));

    }
}
