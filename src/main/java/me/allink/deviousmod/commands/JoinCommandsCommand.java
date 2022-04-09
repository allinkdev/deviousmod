package me.allink.deviousmod.commands;

import joptsimple.internal.Strings;
import me.allink.deviousmod.client.DeviousModClient;
import me.allink.deviousmod.command.ChatCommandBase;
import me.allink.deviousmod.exception.NotEnoughArgumentsException;
import me.allink.deviousmod.json.config.Config;
import me.allink.deviousmod.util.ChatUtil;
import me.allink.deviousmod.util.Utilities;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.*;
import net.minecraft.util.Formatting;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class JoinCommandsCommand extends ChatCommandBase {
    Utilities utilities;
    ChatUtil chatUtil;

    public JoinCommandsCommand(int minimumArguments, String command, String usage, String description, List<String> aliases) {
        super(minimumArguments, command, usage, description, aliases);
        this.utilities = Utilities.getInstance();
        this.chatUtil = utilities.getChatUtil();
    }


    @Override
    public void execute(String[] args) throws NotEnoughArgumentsException {
        super.execute(args);
        Config config = DeviousModClient.CONFIG;
        ArrayList<String> newArgs = new ArrayList<>(Arrays.asList(args));

        switch (args[0]) {
            case "add":
                chatUtil.sendMessage("TIP: You can also use the placeholders {USERNAME}, {UUID} and {RANDOM}.");

                newArgs.remove(0);

                String command = Strings.join(newArgs, " ");
                config.chatCommands.add(command);
                DeviousModClient.getInstance().saveConfig(config);
                chatUtil.sendMessage(String.format("Added %s§r to the list of commands.", command));
                break;
            case "remove":
                try {
                    int index = Integer.parseInt(args[1]);

                    config.chatCommands.remove(index);
                    DeviousModClient.getInstance().saveConfig(config);
                    chatUtil.sendMessage(String.format("Removed %s from the list of commands.", index));
                    MinecraftClient.getInstance().player.sendChatMessage(String.format("%scommands list", DeviousModClient.prefix));
                } catch (Exception e) {
                    chatUtil.sendMessage(new LiteralText(e.getMessage()).formatted(Formatting.RED));
                    e.printStackTrace();
                }
                break;
            case "set":
                try {
                    for (int i = 0; i < 2; i++) {
                        newArgs.remove(0);
                    }

                    String newCommand = Strings.join(newArgs, " ");

                    int index = Integer.parseInt(args[1]);

                    config.chatCommands.set(index, newCommand);
                    DeviousModClient.getInstance().saveConfig(config);
                    chatUtil.sendMessage(String.format("Set %s to %s", index, newCommand));
                    MinecraftClient.getInstance().player.sendChatMessage(String.format("%scommands list", DeviousModClient.prefix));
                } catch (Exception e) {
                    chatUtil.sendMessage(new LiteralText(String.valueOf(e.getMessage())).formatted(Formatting.RED));
                    e.printStackTrace();
                }
                break;
            case "list":
                int i = 0;
                for (String chatCommand : config.chatCommands) {
                    Text editText = new LiteralText("✎").formatted(Formatting.GRAY);
                    ClickEvent editClickEvent = new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, String.format(String.format("%scommands set %s <command>", DeviousModClient.prefix, i)));
                    Style editStyle = Style.EMPTY;
                    editStyle = editStyle.withClickEvent(editClickEvent);
                    editText = editText.getWithStyle(editStyle).get(0);
                    Text deleteText = new LiteralText("❌").formatted(Formatting.RED);
                    ClickEvent deleteClickEvent = new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, String.format(String.format("%scommands remove %s", DeviousModClient.prefix, i)));
                    Style deleteStyle = Style.EMPTY;
                    deleteStyle = deleteStyle.withClickEvent(deleteClickEvent);
                    deleteText = deleteText.getWithStyle(deleteStyle).get(0);

                    System.out.println(editText);
                    System.out.println(deleteText);

                    MutableText message = new LiteralText("[");
                    message = message.append(editText).append("§r");
                    message = message.append("|").append(deleteText).append("§r");
                    message = message.append("] §r").append(String.format("§l%s§r %s", i, chatCommand));

                    chatUtil.sendMessage(message);
                    i++;
                }
                break;
            case "help":
            default:
                chatUtil.sendMessage("add <command>\nremove <index>\nset <index> <command>\nlist");
                break;
        }
    }
}
