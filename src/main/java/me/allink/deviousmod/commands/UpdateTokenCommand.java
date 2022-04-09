package me.allink.deviousmod.commands;

import me.allink.deviousmod.client.DeviousModClient;
import me.allink.deviousmod.command.ChatCommandBase;
import me.allink.deviousmod.exception.NotEnoughArgumentsException;
import me.allink.deviousmod.util.ChatUtil;
import me.allink.deviousmod.util.Utilities;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Formatting;

import java.util.List;

public class UpdateTokenCommand extends ChatCommandBase {
    Utilities utilities;
    ChatUtil chatUtil;

    public UpdateTokenCommand(int minimumArguments, String command, String usage, String description, List<String> aliases) {
        super(minimumArguments, command, usage, description, aliases);
        this.utilities = Utilities.getInstance();
        this.chatUtil = utilities.getChatUtil();
    }


    @Override
    public void execute(String[] args) throws NotEnoughArgumentsException {
        super.execute(args);
        DeviousModClient.kcpToken = args[0];
        DeviousModClient.getInstance().tokenUpdate();
        chatUtil.sendMessage(new LiteralText(String.format("Updated your KCP token to: %s", args[0])).formatted(Formatting.GREEN));
    }
}
