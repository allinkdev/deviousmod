package me.allink.deviousmod.commands;

import java.util.List;
import me.allink.deviousmod.client.DeviousModClient;
import me.allink.deviousmod.command.ChatCommandBase;
import me.allink.deviousmod.exception.NotEnoughArgumentsException;
import me.allink.deviousmod.util.ChatUtil;
import me.allink.deviousmod.util.Utilities;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Formatting;

public class EggTargetCommand extends ChatCommandBase {
	Utilities utilities;
	ChatUtil chatUtil;

	public EggTargetCommand(int minimumArguments, String command, String usage,
		String description, List<String> aliases) {
		super(minimumArguments, command, usage, description, aliases);
		this.utilities = Utilities.getInstance();
		this.chatUtil = utilities.getChatUtil();
	}

	@Override
	public void execute(String[] args) throws NotEnoughArgumentsException {
		super.execute(args);
		String username = String.join(" ", args);
		DeviousModClient.CONFIG.eggTarget = username;
		DeviousModClient.getInstance().saveConfig();
		chatUtil.sendMessage(new LiteralText("Egg target set to " + username + "!").formatted(
			Formatting.GREEN));
	}
}
