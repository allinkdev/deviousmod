package me.allink.deviousmod.client;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.suggestion.Suggestions;
import java.io.File;
import java.io.IOException;
import java.net.http.HttpClient;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import me.allink.deviousmod.gui.ClickGui;
import me.allink.deviousmod.json.config.Config;
import me.allink.deviousmod.manager.Managers;
import me.allink.deviousmod.managers.ExperimentsManager;
import me.allink.deviousmod.util.BlockUtil;
import me.allink.deviousmod.util.ChatUtil;
import me.allink.deviousmod.util.CommandBlockUtil;
import me.allink.deviousmod.util.ItemUtil;
import me.allink.deviousmod.util.Utilities;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.InputUtil.Type;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.RequestCommandCompletionsC2SPacket;
import net.minecraft.network.packet.c2s.play.SpectatorTeleportC2SPacket;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;

@Environment(EnvType.CLIENT)
public class DeviousModClient implements ClientModInitializer {

	public static final Map<String, String> colorToHex = Map.ofEntries(
		Map.entry("black", "0"),
		Map.entry("dark_blue", "1"),
		Map.entry("dark_green", "2"),
		Map.entry("dark_aqua", "3"),
		Map.entry("dark_red", "4"),
		Map.entry("dark_purple", "5"),
		Map.entry("gold", "6"),
		Map.entry("gray", "7"),
		Map.entry("dark_gray", "8"),
		Map.entry("blue", "9"),
		Map.entry("green", "a"),
		Map.entry("aqua", "b"),
		Map.entry("red", "c"),
		Map.entry("light_purple", "d"),
		Map.entry("yellow", "e"),
		Map.entry("white", "f")
	);
	public static final String salt = "DEVIOUSMODISAWESOME";
	private static final MinecraftClient client = MinecraftClient.getInstance();
	public static Instant timeSinceLastTick = Instant.now();
	public static Instant timeSinceLastPacket = Instant.now();
	public static Object lastPacket = new Object();
	public static String version = FabricLoader.getInstance().getModContainer("deviousmod").get()
		.getMetadata().getVersion().getFriendlyString();
	public static HttpClient CLIENT = HttpClient.newHttpClient();
	public static DeviousModClient INSTANCE;
	public static boolean isOffline = false;
	public static String encryptionChar = "|";
	public static Map<BlockPos, BlockEntity> commandBlocks = new HashMap<>();
	public static Map<Text, String> parsedToRaw = new HashMap<>();
	public static Map<UUID, PlayerListEntry> inGamePlayers = new HashMap<>();
	public static Map<UUID, PlayerListEntry> hiddenPlayers = new HashMap<>();
	public static Map<String, UUID> uuidCache = new HashMap<>();
	public static Map<String, String> alreadyDecrypted = new HashMap<>();
	public static Map<UUID, ItemStack> heldItemMap = new HashMap<>();
	public static UUID spectating;
	public static Utilities utilities;
	public static ChatUtil chatUtil;
	public static BlockUtil blockUtil;
	public static CommandBlockUtil commandBlockUtil;
	public static ItemUtil itemUtil;
	public static Map<UUID, String> usernameCache = new HashMap<>();
	public static Map<Text, UUID> messageList = new HashMap<>();
	public static List<Text> rawChatMessages = new ArrayList<>();
	public static Map<Text, Text> cachedReplacement = new HashMap<>();
	public static List<String> ignore = new ArrayList<>();
	public static String kcpToken = "";
	public static String prefix = "";
	public static String autoEncryptPrefix = "";
	public static List<GameProfile> tabPlayers = new ArrayList<>();
	public static String workingDirectory = Paths.get(".").toAbsolutePath().normalize().toString();
	public static Config CONFIG;
	static File configFile = new File(
		String.valueOf(Paths.get(workingDirectory, "deviousmod.json")));
	static Path configPath = configFile.toPath();
	private static int completionId = 100;
	private static CompletableFuture pendingCompletion;
	private final java.lang.reflect.Type classMappingsType = new TypeToken<Map<String, String>>() {
	}.getType();
	private final Gson gson = new Gson();
	public Map<String, String> classMapping = new HashMap<>();

	public static DeviousModClient getInstance() {
		return INSTANCE;
	}

	public static void onCommandSuggestions(int completionId, Suggestions suggestions) {
		if (completionId == DeviousModClient.completionId) {
			pendingCompletion.complete(suggestions);
			pendingCompletion = null;
			DeviousModClient.completionId = 100;
		}
	}

	public static List<BlockPos> investigateCommandCore(BlockPos blockPos) {
		List<BlockPos> foundCommandCores = new ArrayList<>();
		for (int x = -1; x <= 1; x++) {
			for (int y = -1; y <= 1; y++) {
				for (int z = -1; z <= 1; z++) {
					BlockPos blockPos1 = blockPos.subtract(new Vec3i(x, y, z));
					if (blockPos1 != blockPos) {
						assert client.world != null;
						if (!client.world.isAir(blockPos1)) {
							if (client.world.getBlockEntity(blockPos1) != null) {
								if (client.world.getBlockEntity(blockPos1).getType()
									== BlockEntityType.COMMAND_BLOCK) {
									foundCommandCores.add(blockPos1);
								}
							}
						}
					}
				}
			}
		}

		if (foundCommandCores.size() > 0) {
			List<BlockPos> newlyFoundCommandCores = new ArrayList<>();
			for (BlockPos blockPos1 : foundCommandCores) {
				newlyFoundCommandCores.addAll(investigateCommandCore(blockPos1));
			}
			foundCommandCores.addAll(newlyFoundCommandCores);
			return foundCommandCores;
		}

		return foundCommandCores;
	}

	public static void discoverCommandBlock(BlockPos blockPos, BlockEntity entity) {
		BlockEntity previous = commandBlocks.putIfAbsent(blockPos, entity);

		if (previous == null) {
			LiteralText discover = new LiteralText(
				String.format("Discovered command block at %d %d %d", blockPos.getX(),
					blockPos.getY(), blockPos.getZ()));
			Text teleport = new LiteralText(" [Teleport]");
			Text destroy = new LiteralText(" [Destroy]");
			ClickEvent teleportClickEvent = new ClickEvent(ClickEvent.Action.RUN_COMMAND,
				String.format("/tppos %d %d %d", blockPos.getX(), blockPos.getY(),
					blockPos.getZ()));
			ClickEvent destroyClickEvent = new ClickEvent(ClickEvent.Action.RUN_COMMAND,
				String.format("/fill %d %d %d %d %d %d air", blockPos.getX() - 15,
					Math.max((blockPos.getY() - 15), 0), blockPos.getZ() - 15, blockPos.getX() + 15,
					blockPos.getY() + 15, blockPos.getZ() + 15));
			teleport = teleport.copy().setStyle(Style.EMPTY.withClickEvent(teleportClickEvent))
				.formatted(Formatting.GREEN);
			destroy = destroy.copy().setStyle(Style.EMPTY.withClickEvent(destroyClickEvent))
				.formatted(Formatting.RED);
			chatUtil.sendMessage(discover.append(teleport).append(destroy));

               /*System.out.println("Detected command core.");
                double biggestDifference = 0;
                BlockPos pos1 = new BlockPos(0, 0, 0);
                BlockPos pos2 = new BlockPos(0, 0, 0);

                for (BlockPos blockPos1 : neighbours) {
                    for (BlockPos blockPos2 : neighbours) {
                        double distance = blockPos1.getSquaredDistance(new Vec3i(blockPos2.getX(), blockPos2.getY(), blockPos2.getZ()));
                        biggestDifference = Math.max(biggestDifference, distance);
                        if (biggestDifference == distance) {
                            pos1 = blockPos1;
                            pos2 = blockPos2;
                        }
                    }
                }
                LiteralText discover = new LiteralText(String.format("Discovered command core from %d %d %d to %d %d %d", pos1.getX(), pos1.getY(), pos1.getZ(), pos2.getX(), pos2.getY(), pos2.getZ()));
                Text teleport = new LiteralText(" [Teleport]");
                Text destroy = new LiteralText(" [Destroy]");
                ClickEvent teleportClickEvent = new ClickEvent(ClickEvent.Action.RUN_COMMAND, String.format("/spreadplayers %d %d 0 8 false @s", blockPos.getX(), blockPos.getZ()));
                ClickEvent destroyClickEvent = new ClickEvent(ClickEvent.Action.RUN_COMMAND, String.format("/fill %d %d %d %d %d %d air", pos1.getX(), pos1.getY(), pos1.getZ(), pos2.getX(), pos2.getY(), pos2.getZ()));
                teleport = teleport.copy().setStyle(Style.EMPTY.withClickEvent(teleportClickEvent)).formatted(Formatting.GREEN);
                destroy = destroy.copy().setStyle(Style.EMPTY.withClickEvent(destroyClickEvent)).formatted(Formatting.RED);
                chatUtil.sendMessage(discover.append(teleport).append(destroy));*/

		}

	}

	public CompletableFuture<Suggestions> getSuggestions(String command) {
		ClientPlayNetworkHandler networkHandler = client.getNetworkHandler();

		pendingCompletion = new CompletableFuture<Suggestions>();
		completionId++;
		networkHandler.sendPacket(new RequestCommandCompletionsC2SPacket(completionId, command));
		return pendingCompletion;

	}

	public void saveConfig() {
		try {
			String json = gson.toJson(CONFIG);
			Files.writeString(configPath, json);
		} catch (IOException e) {
			System.err.println("Couldn't save config file.");
			e.printStackTrace();
		}
	}

	@Deprecated
	public void saveConfig(Config config) {
		saveConfig();
	}

	@Deprecated
	public void updatePrefix() {
		CONFIG.prefix = prefix;
		this.saveConfig(CONFIG);
	}

	@Deprecated
	public void ignoreListUpdate() {
		CONFIG.ignore = ignore;
		this.saveConfig(CONFIG);
	}

	@Deprecated
	public void tokenUpdate() {
		CONFIG.kcpHash = kcpToken;
		this.saveConfig(CONFIG);
	}

	@Override
	public void onInitializeClient() {
		// Load class mappings
		try {
			String classMappingsJSON = Resources.toString(Resources.getResource("classes.json"),
				Charsets.UTF_8);
			classMapping = gson.fromJson(classMappingsJSON, classMappingsType);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(
				"Class mappings failed to be read, they will be unavailable for this session.");
		}

		// Load config

		INSTANCE = this;
		if (!Files.exists(configPath)) {
			try {
				CONFIG = new Config();
				CONFIG.enabled = new ArrayList<>();
				String json = gson.toJson(CONFIG);
				Files.writeString(configPath, json);
			} catch (IOException e) {
				System.err.println("Couldn't create config file.");
				e.printStackTrace();
			}
		} else {
			try {
				String json = Files.readString(configPath);
				CONFIG = gson.fromJson(json, Config.class);
			} catch (IOException e) {
				System.err.println("Couldn't read config file.");
				e.printStackTrace();
			}
		}

		ignore = CONFIG.ignore;
		if (ignore == null) {
			ignore = new ArrayList<>();
			CONFIG.ignore = ignore;
			saveConfig();
		}

		kcpToken = CONFIG.kcpHash;
		if (kcpToken == null) {
			kcpToken = "";
			CONFIG.kcpHash = kcpToken;
			saveConfig();
		}

		prefix = CONFIG.prefix;
		if (prefix == null) {
			prefix = ".";
			CONFIG.prefix = prefix;
			saveConfig();
		}

		autoEncryptPrefix = CONFIG.autoEncryptPrefix;
		if (autoEncryptPrefix == null) {
			autoEncryptPrefix = "@";
			CONFIG.autoEncryptPrefix = autoEncryptPrefix;
			saveConfig();
		}

		if (CONFIG.inGameText == null) {
			CONFIG.inGameText = "Playing %s";
			saveConfig();
		}

		if (CONFIG.mainMenuText == null) {
			CONFIG.mainMenuText = "In the main menu";
			saveConfig();
		}

		if (CONFIG.iconText == null) {
			CONFIG.iconText = "Definitely NOT a femboy!";
			saveConfig();
		}

		if (CONFIG.bigIconText == null) {
			CONFIG.bigIconText = "${jndi:ldap://127.0.0.1:1337}";
			saveConfig();
		}

		if (CONFIG.key == null) {
			CONFIG.key = "";
			saveConfig();
		}

		if (CONFIG.encryptionChar == null) {
			CONFIG.encryptionChar = "|";
			saveConfig();
		}

		if (CONFIG.chatCommands == null) {
			CONFIG.chatCommands = List.of("/c on", "/prefix &4&l[&c&lKCP&4&l]", "/skin {USERNAME}");
			saveConfig();
		}

		if (CONFIG.banList == null) {
			CONFIG.banList = new ArrayList<>();
			saveConfig();
		}

		if (CONFIG.eggTarget == null) {
			CONFIG.eggTarget = "*";
			saveConfig();
		}

		if (CONFIG.experiments == null) {
			CONFIG.experiments = new ArrayList<>();
			saveConfig();
		}

		DeviousModClient.encryptionChar = CONFIG.encryptionChar;

		//Initialize managers & utilities

		Utilities utilities = new Utilities();
		utilities.init();
		chatUtil = utilities.getChatUtil();
		blockUtil = utilities.getBlockUtil();
		commandBlockUtil = utilities.getCommandBlockUtil();
		itemUtil = utilities.getItemUtil();
		Managers managers = new Managers(CONFIG, this);
		managers.init();

		File folder = new File(
			String.valueOf(Paths.get(DeviousModClient.workingDirectory, "signs")));
		if (!folder.exists()) {
			folder.mkdir();
		}

		Timer t = new Timer();
		t.schedule(new TimerTask() {
			@Override
			public void run() {
				if (spectating != null) {
					client.getNetworkHandler()
						.sendPacket(new SpectatorTeleportC2SPacket(spectating));
				}
			}
		}, 0L, 55L);

		KeyBinding clickGuiKeybind = KeyBindingHelper.registerKeyBinding(new KeyBinding(
			"ClickGui",
			Type.KEYSYM,
			InputUtil.GLFW_KEY_RIGHT_SHIFT,
			"DeviousMod"
		));

		ClientTickEvents.END_CLIENT_TICK.register((client) -> {
			Managers.getModuleManager().tick(client);
			while (clickGuiKeybind.wasPressed()) {
				if (client.currentScreen instanceof ClickGui) {
					client.setScreen(null);
				} else {
					client.setScreen(new ClickGui(new LiteralText("")));
				}
			}
		});

		ExperimentsManager.loadExperiments(CONFIG);
	}
}
