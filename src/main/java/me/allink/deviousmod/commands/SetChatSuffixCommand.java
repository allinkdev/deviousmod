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

public class SetChatSuffixCommand extends ChatCommandBase {
    Utilities utilities;
    ChatUtil chatUtil;

    public SetChatSuffixCommand(int minimumArguments, String command, String usage, String description, List<String> aliases) {
        super(minimumArguments, command, usage, description, aliases);
        utilities = Utilities.getInstance();
        chatUtil = utilities.getChatUtil();
    }

    @Override
    public void execute(String[] args) throws NotEnoughArgumentsException {
        super.execute(args);
        String suffix = Strings.join(args, " ").trim();

        if (suffix.equalsIgnoreCase("off")) {
            suffix = "";
        } else if (suffix.equalsIgnoreCase("reset")) {
            suffix = "» Ｄｅｖｉｏｕｓ Ｍｏｄ";
        }

        DeviousModClient.CONFIG.chatSuffix = suffix;
        DeviousModClient.getInstance().saveConfig(DeviousModClient.CONFIG);
        chatUtil.sendMessage(new LiteralText(String.format("Updated your chat suffix to \"%s\"", suffix)).formatted(Formatting.GREEN));
    }
}
