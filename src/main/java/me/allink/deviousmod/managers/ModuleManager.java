package me.allink.deviousmod.managers;

import java.util.ArrayList;
import java.util.List;
import me.allink.deviousmod.client.DeviousModClient;
import me.allink.deviousmod.json.config.Config;
import me.allink.deviousmod.manager.ManagerBase;
import me.allink.deviousmod.module.ModuleBase;
import me.allink.deviousmod.modules.AmogusEditionModule;
import me.allink.deviousmod.modules.AutoClearModule;
import me.allink.deviousmod.modules.BetterTabModule;
import me.allink.deviousmod.modules.BookFillModule;
import me.allink.deviousmod.modules.ChatFilterModule;
import me.allink.deviousmod.modules.ClassicDisconnectedScreenModule;
import me.allink.deviousmod.modules.ClownFishAuraModule;
import me.allink.deviousmod.modules.ComicSansEverywhereModule;
import me.allink.deviousmod.modules.CommandBlockScannerModule;
import me.allink.deviousmod.modules.DeathPosModule;
import me.allink.deviousmod.modules.DeviousWatermarkModule;
import me.allink.deviousmod.modules.DiscordRPCModule;
import me.allink.deviousmod.modules.EggAuraModule;
import me.allink.deviousmod.modules.FastUseModule;
import me.allink.deviousmod.modules.FlightModule;
import me.allink.deviousmod.modules.HandsR4N00bsModule;
import me.allink.deviousmod.modules.InterEverywhereModule;
import me.allink.deviousmod.modules.ItemLoggerModule;
import me.allink.deviousmod.modules.JoinCommandsModule;
import me.allink.deviousmod.modules.KillYourselfModule;
import me.allink.deviousmod.modules.LoginNameModule;
import me.allink.deviousmod.modules.ModuleListModule;
import me.allink.deviousmod.modules.NoNBTFuckeryModule;
import me.allink.deviousmod.modules.NoRenderModule;
import me.allink.deviousmod.modules.NoSRPModule;
import me.allink.deviousmod.modules.RainbowArmourModule;
import me.allink.deviousmod.modules.ReallyNotRenderModule;
import me.allink.deviousmod.modules.RetroTitleModule;
import me.allink.deviousmod.modules.RobotoEverywhereModule;
import me.allink.deviousmod.modules.SuffixPrefixModule;
import me.allink.deviousmod.modules.SuperAdvancedTooltipsModule;
import me.allink.deviousmod.modules.TimingsModule;
import me.allink.deviousmod.modules.VanishDetectorModule;
import me.allink.deviousmod.modules.VanishHiderModule;
import me.allink.deviousmod.util.ChatUtil;
import me.allink.deviousmod.util.Utilities;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.Nullable;

public class ModuleManager extends ManagerBase {
    static List<String> enabledModuleNames = new ArrayList<>();
    static List<ModuleBase> modules = new ArrayList<>();
    private final List<KeyBinding> keyBindings = new ArrayList<>();
    Config config;
    DeviousModClient deviousMod;
    boolean ignoreModuleToggles = true;

    public ModuleManager(Config config, DeviousModClient deviousMod) {
        this.config = config;
        this.deviousMod = deviousMod;
    }

    public static boolean isEnabled(String name) {
        return enabledModuleNames.contains(name);
    }

    @Nullable
    public static ModuleBase getModule(String name) {
        for (ModuleBase module : modules) {
            if (module.name.equalsIgnoreCase(name)) {
                return module;
            }
        }
        return null;
    }

    public static List<ModuleBase> getModules() {
        return modules;
    }

    public void tick(MinecraftClient client) {
        for (ModuleBase module : modules) {
            if (module.isToggled()) {
                module.tick(client);
            }
        }
    }


    public void onJoin(MinecraftClient client) {
        for (ModuleBase module : modules) {
            if (module.isToggled()) {
                module.onJoin(client);
            }
        }
    }

    public void onLeave(MinecraftClient client) {
        for (ModuleBase module : modules) {
            if (module.isToggled()) {
                module.onLeave(client);
            }
        }
    }

    public void listenForKeypresses() {
        ChatUtil chatUtil = Utilities.getInstance().getChatUtil();
        ClientTickEvents.END_CLIENT_TICK.register((client) -> {
            for (KeyBinding keyBinding : keyBindings) {
                if (keyBinding.wasPressed()) {
                    ModuleBase module = ModuleManager.getModule(keyBinding.getTranslationKey());
                    boolean newValue = module.toggle();
                    chatUtil.sendMessage(Text.of(String.format("%s %s successfully.", (newValue) ? "Enabled" : "Disabled", module.name)).copy().formatted((newValue) ? Formatting.GREEN : Formatting.RED));
                }
            }
        });
    }

    private void initKeybinds() {
        for (ModuleBase module : modules) {
            KeyBinding keyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                    module.name,
                    InputUtil.Type.KEYSYM,
                    InputUtil.UNKNOWN_KEY.getCode(),
                    "DeviousMod Modules"
            ));
            keyBindings.add(keyBinding);
        }

        listenForKeypresses();
    }

    @Override
    public void init() {
        enabledModuleNames = config.enabled;
        modules.add(new LoginNameModule("LoginName", "Matches the player's UUID up with the player's Minecraft account name.", "Utility", this));
        modules.add(new NoRenderModule("NoRender", "Limits & disables rendering of certain UI elements that can be used to generate lag.", "Render", this));
        //modules.add(new TimestampModule("ChatTimestamps", "Add timestamps to chat messages", this));
        //modules.add(new SenderUUIDModule("SenderUUID", "Right click to get the sender of a message's UUID (doesn't work on system messages)", this));
        modules.add(new VanishDetectorModule("VanishDetector", "Detect and store vanished players. Utilize .players.", "Devious", this));
        modules.add(new VanishHiderModule("VanishHider", "Hides the fact that you're in vanish by sending out a fake leave message.", "Devious", this));
        //modules.add(new AntiSpamModule("AntiSpam", "Detects spam and if detected it will not display the message.", this));
        modules.add(new CommandBlockScannerModule("CommandBlockScanner", "Scans chunks when they load for any signs of command blocks. Also applies for block updates. Reload the chunk after toggled.", "Utility", this));
        modules.add(new FastUseModule("FastUse", "Quickly use items.", "Devious", this));
        modules.add(new ItemLoggerModule("ItemLogger", "Log other player's items.", "Devious", this));
        modules.add(new DiscordRPCModule("DiscordRPC", "Tell all your friends and enemies that you're using the most based mod to exist.", "Based", this));
        modules.add(new RetroTitleModule("RetroTitle", "Reverts the title screen back to a simpler time...", "Render", this));
        modules.add(new DeviousWatermarkModule("DeviousWatermark", "Adds a cool in-game reminder that you're one of the select few with access to deviousmod.", "Based", this));
        modules.add(new ChatFilterModule("ChatFilter", "For those days when you don't want to be called the n word over a block game.", "Utility", this));
        modules.add(new RainbowArmourModule("RainbowArmour", "I'm so cool mummy", "Devious", this));
        modules.add(new BookFillModule("BookFill", "Fill those books with lots of data for all your sussy needs", "Devious", this));
        modules.add(new NoSRPModule("NoSRP", "Disables server forcing you to have a resource pack", "Utility", this));
        modules.add(new AutoClearModule("AutoClear", "Automatically clears your inventory with clear ddos packet", "Utility", this));
        modules.add(new ClownFishAuraModule("ClownFishAura", "Crazy totalfreedom hack", "Devious", this));
        modules.add(new JoinCommandsModule("JoinCommands", "Executes commands on join", "Utility", this));
        modules.add(new SuffixPrefixModule("SuffixPrefix", "Prefixes and suffixes your messages.", "Utility", this));
        modules.add(new AmogusEditionModule("AmogusEdition", "Check out dat sussy ass!", "Render", this));
        modules.add(new DeathPosModule("DeathPos", "Snap back to reality", "Devious", this));
        modules.add(new ModuleListModule("ModuleList", "List your current modules in the HUD", "Render", this));
        modules.add(new KillYourselfModule("KillYourself", "Kill yourself, NOW!", "Fun", this));
        modules.add(new RobotoEverywhereModule("RobotoEverywhere", "ROBOTO IS LOVE, ROBOTO IS LIFE.", "Utility", this));
        modules.add(new ComicSansEverywhereModule("ComicSansEverywhere", "The TotalFreedom player's font.", "Utility", this));
        modules.add(new InterEverywhereModule("InterEverywhere", "With thanks to VideoGameSmash12 for suggesting this font.", "Utility", this));
        modules.add(new TimingsModule("Timings", "Displays the time since last tick & time update in the bottom right corner for your lag measuring convenience.", "Utility", this));
        modules.add(new NoNBTFuckeryModule("NoNBTFuckery", "Prevents NBT fuckery, clean and simple.", "Utility", this));
        modules.add(new HandsR4N00bsModule("HandsR4N00bs", "Imagine using hands in 2022, bruh...", "Render", this));
        modules.add(new ReallyNotRenderModule("ReallyNotRender", "Really doesn't render anything.", "Render", this));
        modules.add(new FlightModule("Flight", "Makes you soar through the sky.", "Devious", this));
        //modules.add(new SchizoidModeModule("SchizoidMode", "Disables server pinging & double checks if you want to connect to multiplayer servers.", "Utility", this));
        modules.add(new EggAuraModule("EggAura", "FAJK U YURNI!!!!", "Fun", this));
        modules.add(new ClassicDisconnectedScreenModule("ClassicDisconnectedScreen", "Displays the c0.30 disconnected screen.", "Render", this));
        modules.add(new BetterTabModule("BetterTab",
            "Displays login names and stuff besides a player's displayname.", "Render", this));
        modules.add(new SuperAdvancedTooltipsModule("SuperAdvancedTooltips",
            "Displays size of items & hashcode of their NBT.", "Utility", this));
        //modules.add(new BanListModule("BanList", "Ban players on join", this));

        List<String> mirror = new ArrayList<>(List.copyOf(enabledModuleNames));

        for (String enabledModule : enabledModuleNames) {
            ModuleBase module = getModule(enabledModule);
            if (module != null) {
                getModule(enabledModule).setToggled(true);
            } else {
                System.out.printf("Discarding unknown module %s%n", enabledModule);
                mirror.remove(enabledModule);
            }
        }

        config.enabled = mirror;
        deviousMod.saveConfig(config);

        ignoreModuleToggles = false;

        initKeybinds();
    }

    public void updateModuleToggle(ModuleBase module, boolean toggled) {
        if (ignoreModuleToggles) return;

        if (!toggled) {
            enabledModuleNames.remove(module.name);
        } else {
            enabledModuleNames.add(module.name);
        }
        config.enabled = enabledModuleNames;
        deviousMod.saveConfig(config);
    }
}
