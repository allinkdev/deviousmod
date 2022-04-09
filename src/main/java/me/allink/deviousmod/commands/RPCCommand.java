package me.allink.deviousmod.commands;

import java.util.List;
import java.util.Locale;
import me.allink.deviousmod.client.DeviousModClient;
import me.allink.deviousmod.command.ChatCommandBase;
import me.allink.deviousmod.exception.NotEnoughArgumentsException;
import me.allink.deviousmod.manager.Managers;
import me.allink.deviousmod.managers.DiscordRPCManager;
import me.allink.deviousmod.managers.ModuleManager;
import me.allink.deviousmod.module.ModuleBase;
import me.allink.deviousmod.util.ChatUtil;
import me.allink.deviousmod.util.Utilities;

public class RPCCommand extends ChatCommandBase {
    Utilities utilities;
    ChatUtil chatUtil;

    public RPCCommand(int minimumArguments, String command, String usage, String description, List<String> aliases) {
        super(minimumArguments, command, usage, description, aliases);
        utilities = Utilities.getInstance();
        chatUtil = utilities.getChatUtil();
    }


    @Override
    public void execute(String[] args) throws NotEnoughArgumentsException {
        super.execute(args);
        List<ModuleBase> modules = ModuleManager.getModules();
        String moduleName;
        ModuleBase module;
        List<String> values = List.of(new String[]{"mainMenuText", "inGameText", "iconText", "bigIconText", "showIP"});
        if (args.length <= 1) {
            for (String value : values) {
                chatUtil.sendMessage(value);
            }
        } else {
            String toSet = args[0];
            String value;
            if (args.length > 2) {
                List<String> argsList = new java.util.ArrayList<>(List.of(args));
                argsList.remove(0);
                value = String.join(" ", argsList);
            } else {
                value = args[1];
            }
            switch (toSet.toLowerCase(Locale.ROOT)) {
                case "mainmenutext":
                    DeviousModClient.CONFIG.mainMenuText = value;
                    chatUtil.sendMessage(String.format("Set mainMenuText to %s", value));
                    break;
                case "ingametext":
                    DeviousModClient.CONFIG.inGameText = value;
                    chatUtil.sendMessage(String.format("Set inGameText to %s", value));
                    break;
                case "icontext":
                    DeviousModClient.CONFIG.iconText = value;
                    chatUtil.sendMessage(String.format("Set iconText to %s", value));
                    break;
                case "bigicontext":
                    DeviousModClient.CONFIG.bigIconText = value;
                    chatUtil.sendMessage(String.format("Set bigIconText to %s", value));
                    break;
                case "showip":
                    DeviousModClient.CONFIG.showIP = Boolean.parseBoolean(value);
                    chatUtil.sendMessage(String.format("Set showIP to %s", value));
                    break;
                default:
                    for (String val : values) {
                        chatUtil.sendMessage(val);
                    }
                    break;
            }
            DeviousModClient.getInstance().saveConfig(DeviousModClient.CONFIG);
            DiscordRPCManager rpcManager = Managers.getDiscordRPCManager();
            rpcManager.onEnable();
        }
    }
}
