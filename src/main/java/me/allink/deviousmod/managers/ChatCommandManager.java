package me.allink.deviousmod.managers;

import java.util.ArrayList;
import java.util.List;
import me.allink.deviousmod.client.DeviousModClient;
import me.allink.deviousmod.command.ChatCommandBase;
import me.allink.deviousmod.commands.AmongUs2Command;
import me.allink.deviousmod.commands.AmongUsCommand;
import me.allink.deviousmod.commands.AutoSignCommand;
import me.allink.deviousmod.commands.BanCommand;
import me.allink.deviousmod.commands.BundleCommand;
import me.allink.deviousmod.commands.CLoopCommand;
import me.allink.deviousmod.commands.ChestCommand;
import me.allink.deviousmod.commands.ClearCommand;
import me.allink.deviousmod.commands.CommandBlockCommand;
import me.allink.deviousmod.commands.CommandBlocksCommand;
import me.allink.deviousmod.commands.CrashCommand;
import me.allink.deviousmod.commands.DestroyCLoopCommand;
import me.allink.deviousmod.commands.EggTargetCommand;
import me.allink.deviousmod.commands.EncryptArmorStandCommand;
import me.allink.deviousmod.commands.EncryptBookCommand;
import me.allink.deviousmod.commands.ExperimentsCommand;
import me.allink.deviousmod.commands.FireballCommand;
import me.allink.deviousmod.commands.GiveCommand;
import me.allink.deviousmod.commands.HValCommand;
import me.allink.deviousmod.commands.HelpCommand;
import me.allink.deviousmod.commands.IgnoreCommand;
import me.allink.deviousmod.commands.JoinCommandsCommand;
import me.allink.deviousmod.commands.KValCommand;
import me.allink.deviousmod.commands.KickCommand;
import me.allink.deviousmod.commands.LagArmorstandCommand;
import me.allink.deviousmod.commands.ModuleCommand;
import me.allink.deviousmod.commands.NBTCommand;
import me.allink.deviousmod.commands.NBTTPCommand;
import me.allink.deviousmod.commands.PlayersCommand;
import me.allink.deviousmod.commands.RPCCommand;
import me.allink.deviousmod.commands.RandomTPCommand;
import me.allink.deviousmod.commands.RealNameCommand;
import me.allink.deviousmod.commands.SValCommand;
import me.allink.deviousmod.commands.SetChatPrefixCommand;
import me.allink.deviousmod.commands.SetChatSuffixCommand;
import me.allink.deviousmod.commands.SetEncryptionKeyCommand;
import me.allink.deviousmod.commands.SetPrefixCommand;
import me.allink.deviousmod.commands.SetUsernameCommand;
import me.allink.deviousmod.commands.SpawnerCommand;
import me.allink.deviousmod.commands.SpectateCommand;
import me.allink.deviousmod.commands.SpectatorTPCommand;
import me.allink.deviousmod.commands.StealItemCommand;
import me.allink.deviousmod.commands.SteganographyCommand;
import me.allink.deviousmod.commands.SussywussyCommand;
import me.allink.deviousmod.commands.UUIDCommand;
import me.allink.deviousmod.commands.UnbanCommand;
import me.allink.deviousmod.commands.UnignoreCommand;
import me.allink.deviousmod.commands.UnspectateCommand;
import me.allink.deviousmod.commands.UpdateHBotKeyCommand;
import me.allink.deviousmod.commands.UpdateSBotKeyCommand;
import me.allink.deviousmod.commands.UpdateTokenCommand;
import me.allink.deviousmod.commands.UsernameCommand;
import me.allink.deviousmod.exception.NotEnoughArgumentsException;
import me.allink.deviousmod.manager.ManagerBase;
import me.allink.deviousmod.util.ChatUtil;
import me.allink.deviousmod.util.Utilities;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Formatting;

public class ChatCommandManager extends ManagerBase {
    public final List<ChatCommandBase> commands = new ArrayList<>();

    public ChatCommandManager() {
    }


    public void executeIfExists(String command, String[] args) throws NotEnoughArgumentsException {
        boolean executed = false;
        Utilities utilities = Utilities.getInstance();
        ChatUtil chatUtil = utilities.getChatUtil();
        for (ChatCommandBase chatCommand : commands) {
            if (chatCommand.command.equalsIgnoreCase(command) || chatCommand.aliases.contains(command.toLowerCase())) {
                try {
                    executed = true;
                    chatCommand.execute(args);
                } catch (Exception e) {
                    chatUtil.sendMessage(new LiteralText("Exception while executing command: " + e.getMessage()).formatted(Formatting.RED));
                    chatUtil.sendMessage(new LiteralText(DeviousModClient.prefix + chatCommand.usage).formatted(Formatting.RED));
                }
            }
        }
        if (!executed) {
            chatUtil.sendMessage(String.format("Command %s not found.", command));
        }
    }

    @Override
    public void init() {
        commands.add(new CLoopCommand(1, "cloop", "cloop <command>", "Creates a cloop at your location.", List.of("commandloop", "loop", "loopc", "l")));
        //commands.add(new CLoopBulkCommand(1, "cloopbulk"));
        commands.add(new CommandBlockCommand(1, "cmd", "cmd <command>", "Places a repeating command block underneath you.", List.of("command", "cblock", "cb", "c")));
        commands.add(new DestroyCLoopCommand(0, "dcloop", "dcloop", "Destroys the cloop at your location.", List.of("dcloop", "destroycloop", "cloopd", "cloopdestroy", "dloop", "destroycloop", "cloopdestroy", "cloopd", "rcloop", "removecloop", "cloopr", "cloopremove", "cr", "rc", "lr", "ld", "dc", "cd")));
        commands.add(new GiveCommand(1, "give", "give <item> [nbt]", "Gives you an item, optionally with NBT.", List.of("item", "gib", "g", "i")));
        commands.add(new IgnoreCommand(1, "ignore", "ignore <string>", "Ignores all messages in chat that contain the string you provided.", List.of("block", "ig", "remove")));
        commands.add(new UnignoreCommand(1, "unignore", "unignore <string>", "Stops ignoring messages that contain the string you provided.", List.of("unblock", "ub", "unremove")));
        commands.add(new ModuleCommand(1, "modules", "modules <toggle/enable/disable/list/help> <name if toggle,enable,disable>", "Manipulates modules.", List.of("mods", "module", "mod")));
        commands.add(new SpawnerCommand(1, "spawner", "spawner <entity> [nbt]", "Gives you a custom spawner.", List.of("spawn", "s")));
        commands.add(new UUIDCommand(1, "uuid", "uuid <username>", "Gives you the UUID of a username.", List.of("id")));
        commands.add(new UsernameCommand(1, "username", "username <uuid>", "Gives you the username of a UUID.", List.of("user", "name")));
        commands.add(new RealNameCommand(1, "realname", "realname <username>", "Gives you the first observed username of a username.", List.of("rname", "realusername", "realuser")));
        commands.add(new UpdateTokenCommand(1, "updatetoken", "updatetoken <kcpToken>", "Updates your KCP token.", List.of("settoken", "setoken", "setkcp", "updatekcp", "setkcptoken")));
        commands.add(new UpdateHBotKeyCommand(1, "updatehbotkey", "updatehbot <hbotKey>", "Updates your HBot key.", List.of("sethbot", "updatehbot")));
        commands.add(new UpdateSBotKeyCommand(1, "updatesbotkey", "updatesbotkey <sbotKey>", "Updates your SBot key.", List.of("updatesbot", "setsbot", "updatesbotkey")));
        commands.add(new HelpCommand(0, "help", "help", "Displays this message.", List.of("cmds")));
        commands.add(new CommandBlocksCommand(0, "cmbs", "cmbs", "Lists all command blocks detect with CommandBlockScanner.", List.of("commandblocks", "cbs")));
        commands.add(new LagArmorstandCommand(0, "lstand", "lstand", "Gives you an armour stand that lags other player's clients.", List.of("lagstand", "standl", "standlag", "lag")));
        commands.add(new SpectatorTPCommand(1, "spectatortp", "spectatortp <uuid/username>", "Teleports to another player in spectator mode (even if they're vanished!)", List.of("stp", "tpspec", "tps", "tp")));
        commands.add(new SpectateCommand(1, "spectate", "spectate <uuid/username>", "Spams the SpectatorTP packet to create the illusion of vanilla spectating (also works if they're vanished!)", List.of("spectate", "spectator", "watch")));
        commands.add(new UnspectateCommand(0, "unspectate", "unspectate", "Stops spectating player.", List.of("stopspectate", "unsp")));
        commands.add(new SetPrefixCommand(1, "prefix", "setprefix <prefix>", "Changes the command prefix.", List.of("prefix", "updateprefix")));
        commands.add(new StealItemCommand(1, "steal", "steal <player>", "Steals the currently held item from a player.", List.of("stealitem", "copyitem", "copy", "si", "ci")));
        commands.add(new PlayersCommand(0, "list", "list", "Lists all players currently visible to you.", List.of("players", "pl", "lp", "list")));
        commands.add(new RPCCommand(0, "rpc", "rpc <mainmenutext/ingametext/icontext/bigicontext/showip> <value>", "Modifies your Discord Rich Presence.", List.of("discord", "setrpc", "setdiscord", "presence")));
        commands.add(new SteganographyCommand(1, "encrypt", "encrypt <message>", "Encrypts your message, encodes the encrypted bytes into hex, and then chats it in a colourful manner! ☺", List.of("encrypt", "steg")));
        commands.add(new EncryptArmorStandCommand(1, "estand", "estand <message>", "The same as the steg command but instead of a chat message, it's the name of an armour stand.", List.of("encryptstand", "standencrypt")));
        commands.add(new RandomTPCommand(0, "rtp", "rtp", "Randomly teleports you (this was taken from HClient, which isn't compatible with 1.18.1)", List.of("randomtp", "random", "tpr", "wild")));
        commands.add(new EncryptBookCommand(1, "ebook", "ebook <message>", "The same as the steg command but instead of a chat message, it's the pages of a book.", List.of("encryptbook", "bookencrypt")));
        commands.add(new CrashCommand(1, "crash", "crash <nocompacket/nocomsign/essentials/book/help>", "Executes the server crash exploit of your choosing.", List.of("exploit")));
        commands.add(new ChestCommand(1, "chest", "chest <item> [nbt]", "Gives you a fancy-looking chest containing an item with optional NBT.", List.of("deviouschest", "givechest", "dchest", "gc", "givechest")));
        commands.add(new BundleCommand(1, "bundle", "bundle <item> [nbt]", "Gives you a bundle containing an item with optional NBT. (not fancy looking ☹)", List.of("deviousbundle", "givebundle", "gb")));
        commands.add(new NBTTPCommand(1, "ntp", "ntp <player>", "Uses a lectern containing a book to force teleport to a player (works on Kaboom)", List.of("ntp", "nbtp", "tpn", "booktp", "tpbook", "tpb")));
        commands.add(new AutoSignCommand(1, "autosign", "autosign <filename> [breakBlocks]", "Starts writing out the content of your specified file in the .minecraft/signs directory, if you want to use this on TotalFreedom you MUST set breakBlocks to false or risk an auto-ban/kick.", List.of("signauto", "autosign", "asign", "sauto", "sa", "as")));
        commands.add(new AmongUsCommand(0, "amongus", "amongus", "Because Minecraft just isn't sussy enough", List.of("sussy", "wussy", "sussywussy", "sus", "impostor")));
        commands.add(new NBTCommand(1, "nbt", "nbt <get/save/load/list/set> [filename/nbt]", "Modifies, loads, saves and gets the held item's NBT. (if you use load with nothing in your hands it'll still work)", List.of("saved", "saveditems", "items")));
        commands.add(new JoinCommandsCommand(1, "commands", "commands <add/remove/set/list/help> [index/command]", "Modifies what commands the JoinCommands module will run on join. I recommend using list for a more interactive experience (click on the pencil to edit and click on the cross to delete).", List.of("joincommands", "jc")));
        commands.add(new AmongUs2Command(0, "amongus2", "amongus2", "The sequel to SUSSY among us Command!", List.of("amogus2", "sussy2", "wussy2", "sussywussy2", "sus2", "impostor2")));
        commands.add(new FireballCommand(0, "fireball", "fireball", "Summons sussy impostor fireball egg (rite click to use)", List.of("fb", "fball", "ball", "clickfireball", "clickball", "clickmyballs")));
        commands.add(new ClearCommand(0, "clear", "clear", "Clears the chat and/or command bar.", List.of("cc")));
        commands.add(new KValCommand(1, "kval", "kval <command>", String.format("Execute BanBot commands (requires KCP token to be set with %supdatetoken)", DeviousModClient.prefix), List.of("kcpval", "kcpeval", "kcp", "k")));
        commands.add(new HValCommand(1, "hval", "hval <command>", String.format("Execute HBot commands (requires HBot key to be set with %supdatehbot)", DeviousModClient.prefix), List.of("hbotval", "hbot", "hb", "h")));
        commands.add(new SValCommand(1, "sval", "sval <command>", String.format("Execute SBot commands (requires SBot key to be set with %supdatesbot)", DeviousModClient.prefix), List.of("s", "sbot")));
        commands.add(new SetChatPrefixCommand(1, "setchatprefix", "setchatprefix <prefix/reset/off>", "Set chat prefix (SuffixPrefix module MUST be enabled!)", List.of("updatechatprefix")));
        commands.add(new SetChatSuffixCommand(1, "setchatsuffix", "setchatsuffix <suffix/reset/off>", "Set chat suffix (SuffixPrefix module MUST be enabled!)", List.of("updatechatsuffix", "updatesuffix", "setsuffix", "suffix")));
        commands.add(new KickCommand(1, "kick", "kick <name/uuid>", "Kicks a username/uuid from the game.", List.of("k", "disconnect")));
        commands.add(new BanCommand(1, "ban", "ban <namer/uuid>", "Bans a username/uuid from the game (you must have the entity's chunk loaded or it WILL not work).", List.of("b", "banish")));
        commands.add(new UnbanCommand(1, "unban", "unban <name/uuid>", "Unbans a username/uuid.", List.of("ub", "pardon", "unbanish")));
        commands.add(new SetEncryptionKeyCommand(1, "setencryptionkey", "setencryptionkey <key>", "Sets the encryption key (you must set to use encryption-related commands)", List.of("setencrypt", "updatekey")));
        commands.add(new SetUsernameCommand(1, "setusername", "setusername <username...>", "Set your username to something new. Only supports offline accounst at the moment.", List.of()));
        //commands.add(new CannonCommand(0, "cannon", "cannon", "boom", List.of("boom", "handcannon", "pewpew")));
        commands.add(new SussywussyCommand(0, "soos", "", "", List.of()));
        commands.add(new EggTargetCommand(1, "eggtarget", "eggtarget <username/*>", "Sets the target of the EggAura module.", List.of("et", "eggtar", "targetegg")));
        commands.add(new ExperimentsCommand(1, "experiment", "experiment <id>", "Toggles an experiment (THESE MAY BREAK YOUR GAME!)", List.of("e", "experiments")));
        //commands.add(new SilentNTPCommand(1, "sntp"));
        //commands.add(new TestCommand(0, "test", "test", "test", List.of()));
        //commands.add(new QueryBlockCommand(3, "queryblock"));
    }
}
