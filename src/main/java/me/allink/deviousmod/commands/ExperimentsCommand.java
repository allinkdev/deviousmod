package me.allink.deviousmod.commands;

import java.util.List;
import me.allink.deviousmod.command.ChatCommandBase;
import me.allink.deviousmod.manager.Managers;
import me.allink.deviousmod.managers.ExperimentsManager;
import me.allink.deviousmod.util.Utilities;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Formatting;

public class ExperimentsCommand extends ChatCommandBase {

	public ExperimentsCommand(int minimumArguments, String command, String usage,
		String description, List<String> aliases) {
		super(minimumArguments, command, usage, description, aliases);
	}

	@Override
	public void execute(String[] args) {
		Utilities.getInstance().getChatUtil().sendMessage(new LiteralText("hi, this is mr allink, plz don't complain to me if something here is broken, they're experimental for a reason, so be warned lol").formatted(
			Formatting.RED, Formatting.BOLD));

		String id = args[0].toLowerCase();
		boolean value = ExperimentsManager.isExperimentEnabled(id);
		ExperimentsManager.setExperimentEnabled(id, !value);

		switch(id) {
			case "8a907415-d2d4-4d57-95d2-870ab4fa011c":
				Managers.getChatCommandManager().commands.add(new CannonCommand(0, "cannon", "cannon", "boom", List.of("boom", "handcannon", "pewpew")));
				break;
		}

		Utilities.getInstance().getChatUtil().sendMessage(String.valueOf(!value));
	}
}
