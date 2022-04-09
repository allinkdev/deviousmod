package me.allink.deviousmod.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import joptsimple.internal.Strings;
import me.allink.deviousmod.client.DeviousModClient;
import me.allink.deviousmod.command.ChatCommandBase;
import me.allink.deviousmod.exception.NotEnoughArgumentsException;
import me.allink.deviousmod.managers.ModuleManager;
import me.allink.deviousmod.module.ModuleBase;
import me.allink.deviousmod.util.ChatUtil;
import me.allink.deviousmod.util.Utilities;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class ModuleCommand extends ChatCommandBase {
    Utilities utilities;
    ChatUtil chatUtil;

    public ModuleCommand(int minimumArguments, String command, String usage, String description, List<String> aliases) {
        super(minimumArguments, command, usage, description, aliases);
        this.utilities = Utilities.getInstance();
        this.chatUtil = utilities.getChatUtil();
    }


    @Override
    public void execute(String[] args) throws NotEnoughArgumentsException {
        super.execute(args);
        List<ModuleBase> modules = ModuleManager.getModules();
        String moduleName;
        ModuleBase module;
        if (args.length > 0) {
            switch (args[0].toLowerCase(Locale.ROOT)) {
                case "list":
                    for (ModuleBase module1 : modules) {
                        if (MinecraftClient.getInstance().getSession().getUsername().equalsIgnoreCase("riawo")) {
                            if (module1.name.contains("amogus".toLowerCase()) || module1.name.contains("among".toLowerCase()) || module1.description.contains("amogus".toLowerCase()) || module1.description.contains("among".toLowerCase())) {
                                continue;
                            }
                        }
                        chatUtil.sendMessage(String.format("§2%s§b: §a%s §b(enabled: %b§b)", module1.name, module1.description, module1.toggled));
                    }
                    break;
                case "toggle":
                    moduleName = args[1];
                    module = ModuleManager.getModule(args[1].toLowerCase(Locale.ROOT));
                    if (module == null) {
                        List<String> similarModules = new ArrayList<>();
                        chatUtil.sendMessage(Text.of(String.format("Module \"%s\" not found.", moduleName)).copy().formatted(Formatting.RED));
                        for (ModuleBase moduleBase : modules) {
                            if (moduleBase.name.toLowerCase(Locale.ROOT).contains(moduleName.toLowerCase(Locale.ROOT)) || moduleName.toLowerCase(Locale.ROOT).contains(moduleBase.name)) {
                                similarModules.add(moduleBase.name);
                            }
                        }

                        if (similarModules.size() > 0) {
                            chatUtil.sendMessage(Text.of(String.format("Did you mean %s?", Strings.join(similarModules, " or "))));
                        }
                        return;
                    } else {
                        boolean newValue = module.toggle();
                        chatUtil.sendMessage(Text.of(String.format("%s %s successfully.", (newValue) ? "Enabled" : "Disabled", module.name)).copy().formatted((newValue) ? Formatting.GREEN : Formatting.RED));
                    }
                    break;
                case "enable":
                    moduleName = args[1];
                    module = ModuleManager.getModule(args[1].toLowerCase(Locale.ROOT));
                    if (module == null) {
                        List<String> similarModules = new ArrayList<>();
                        chatUtil.sendMessage(Text.of(String.format("Module \"%s\" not found.", moduleName)).copy().formatted(Formatting.RED));
                        for (ModuleBase moduleBase : modules) {
                            if (moduleBase.name.toLowerCase(Locale.ROOT).contains(moduleName.toLowerCase(Locale.ROOT)) || moduleName.toLowerCase(Locale.ROOT).contains(moduleBase.name)) {
                                similarModules.add(moduleBase.name);
                            }
                        }

                        if (similarModules.size() > 0) {
                            chatUtil.sendMessage(Text.of(String.format("Did you mean %s?", Strings.join(similarModules, " or "))));
                        }
                        return;
                    } else {
                        module.setToggled(true);
                        chatUtil.sendMessage(Text.of(String.format("Enabled %s successfully.", module.name)).copy().formatted(Formatting.GREEN));
                    }
                    break;
                case "disable":
                    moduleName = args[1];
                    module = ModuleManager.getModule(args[1].toLowerCase(Locale.ROOT));
                    if (module == null) {
                        List<String> similarModules = new ArrayList<>();
                        chatUtil.sendMessage(Text.of(String.format("Module \"%s\" not found.", moduleName)).copy().formatted(Formatting.RED));
                        for (ModuleBase moduleBase : modules) {
                            if (moduleBase.name.toLowerCase(Locale.ROOT).contains(moduleName.toLowerCase(Locale.ROOT)) || moduleName.toLowerCase(Locale.ROOT).contains(moduleBase.name)) {
                                similarModules.add(moduleBase.name);
                            }
                        }

                        if (similarModules.size() > 0) {
                            chatUtil.sendMessage(Text.of(String.format("Did you mean %s?", Strings.join(similarModules, " or "))));
                        }
                        return;
                    } else {
                        module.setToggled(false);
                        chatUtil.sendMessage(Text.of(String.format("Disabled %s successfully.", module.name)).copy().formatted(Formatting.RED));
                    }
                    break;
                default:
                case "help":
                    chatUtil.sendMessage(String.format("%senable\n%sdisable\n%stoggle\n%slist\n%shelp", DeviousModClient.prefix, DeviousModClient.prefix, DeviousModClient.prefix, DeviousModClient.prefix, DeviousModClient.prefix));
                    break;
            }
        }

    }
}
