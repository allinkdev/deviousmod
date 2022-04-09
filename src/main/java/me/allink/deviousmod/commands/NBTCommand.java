package me.allink.deviousmod.commands;

import java.io.File;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import joptsimple.internal.Strings;
import me.allink.deviousmod.client.DeviousModClient;
import me.allink.deviousmod.command.ChatCommandBase;
import me.allink.deviousmod.util.ChatUtil;
import me.allink.deviousmod.util.Utilities;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.network.packet.c2s.play.CreativeInventoryActionC2SPacket;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class NBTCommand extends ChatCommandBase {

	Utilities utilities;
	ChatUtil chatUtil;

	public NBTCommand(int minimumArguments, String command, String usage, String description,
		List<String> aliases) {
		super(minimumArguments, command, usage, description, aliases);
		utilities = Utilities.getInstance();
		chatUtil = utilities.getChatUtil();
	}


	@Override
	public void execute(String[] args) {
		MinecraftClient client = MinecraftClient.getInstance();

		if (client.player == null) {
			return;
		}

		File nbtDirectory = new File(DeviousModClient.workingDirectory, "items");
		if (!nbtDirectory.exists()) {
			nbtDirectory.mkdir();
		}

		final PlayerInventory inventory = client.player.getInventory();
		final ItemStack mainItemStack = inventory.getMainHandStack();

		if (args.length == 0) {
			chatUtil.sendMessage("get\nsave [filename]\nload <filename>\nset <nbt>\nlist");
			return;
		}

		switch (args[0].toLowerCase()) {
			case "get":
				if (mainItemStack.hasNbt()) {
					chatUtil.sendMessage(new LiteralText(String.format("%s has NBT %s",
						mainItemStack.getItem().getName().getString().replaceAll("§", "§§r&"),
						mainItemStack.getNbt().toString().replaceAll("§", "§§r&"))));
				} else {
					chatUtil.sendMessage(
						new LiteralText("Item stack has no NBT.").formatted(Formatting.RED));
				}

				break;
			case "save":
				String fileName = String.format("%s_%s", mainItemStack.getItem().toString(),
					UUID.randomUUID().toString().replaceAll("-", "").substring(0, 3));

				if (args.length != 1) {
					fileName = args[1];
				}

				File nbtFile = new File(nbtDirectory, fileName + ".txt");

				NbtCompound itemNbt = new NbtCompound();
				itemNbt.putString("id",
					String.format("minecraft:%s", mainItemStack.getItem().toString()));
				itemNbt.putByte("Count", (byte) 1);
				itemNbt.put("tag", mainItemStack.getNbt());

				try {
					String itemNbtString = itemNbt.toString();
					Files.writeString(Path.of(nbtFile.getPath()), itemNbtString);
					chatUtil.sendMessage(new LiteralText(
						String.format("Successfully saved NBT to %s.", fileName)).formatted(
						Formatting.GREEN));
				} catch (Exception e) {
					chatUtil.sendMessage(new LiteralText(
						String.format("Failed to save NBT: %s", e.getMessage())).formatted(
						Formatting.RED));
				}

				break;
			case "load":
				if (args.length == 1) {
					chatUtil.sendMessage(new LiteralText(
						"Please supply a filename without the extension to load the item from.").formatted(
						Formatting.RED));
				} else {
					nbtFile = new File(nbtDirectory, args[1] + ".txt");

					try {
						itemNbt = StringNbtReader.parse(
							Files.readString(Path.of(nbtFile.getPath())));
						ItemStack itemStack = ItemStack.fromNbt(itemNbt);
						client.player.networkHandler.sendPacket(
							new CreativeInventoryActionC2SPacket(36 + inventory.selectedSlot,
								itemStack));

						chatUtil.sendMessage(new LiteralText("Successfully loaded NBT.").formatted(
							Formatting.GREEN));
					} catch (Exception e) {
						chatUtil.sendMessage(new LiteralText(
							String.format("Failed to load NBT: %s", e.getMessage())).formatted(
							Formatting.RED));
					}
				}

				break;
			case "set":
				if (args.length == 1) {
					chatUtil.sendMessage(
						new LiteralText("Supply the new NBT.").formatted(Formatting.RED));
				} else {
					ArrayList<String> nbtArguments = new ArrayList<>(Arrays.asList(args));
					nbtArguments.remove(0);

					try {
						itemNbt = new NbtCompound();
						itemNbt.putString("id",
							String.format("minecraft:%s", mainItemStack.getItem().toString()));
						itemNbt.putByte("Count", (byte) 1);
						itemNbt.put("tag", StringNbtReader.parse(Strings.join(nbtArguments, " ")));

						client.player.networkHandler.sendPacket(
							new CreativeInventoryActionC2SPacket(36 + inventory.selectedSlot,
								ItemStack.fromNbt(itemNbt)));

						chatUtil.sendMessage(new LiteralText(
							String.format("Successfully set the NBT to %s", itemNbt)).formatted(
							Formatting.GREEN));
					} catch (Exception e) {
						chatUtil.sendMessage(
							new LiteralText(e.getMessage()).formatted(Formatting.RED));
					}
				}

				break;
			case "copy":
				client.keyboard.setClipboard(mainItemStack.getNbt().toString());
				chatUtil.sendMessage(
					new LiteralText("Copied NBT to clipboard.").formatted(Formatting.GREEN));
				break;
			case "hash":
				try {
					MessageDigest md = MessageDigest.getInstance("SHA-256");
					byte[] hash = md.digest(mainItemStack.getNbt().toString().getBytes());
					BigInteger big_int = new BigInteger(1,
						Arrays.copyOfRange(hash, 0, hash.length));
					String strHash = big_int.toString(16);
					chatUtil.sendMessage(new LiteralText(String.format(
						"The hash of the item's NBT you're currently holding is %s (click to copy)",
						strHash)).setStyle(Style.EMPTY.withClickEvent(
						new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, strHash))));
				} catch (Exception e) {
					chatUtil.sendMessage(new LiteralText(e.getMessage()).formatted(Formatting.RED));
					e.printStackTrace();
				}
				break;
			case "list":
			default:
				chatUtil.sendMessage(new LiteralText("Saved item data").formatted(Formatting.BLUE));

				File[] files = nbtDirectory.listFiles();

				for (File file : files) {
					try {
						String name = file.getName().replaceAll(".txt", "");
						ClickEvent clickEvent = new ClickEvent(ClickEvent.Action.RUN_COMMAND,
							String.format("%snbt load %s", DeviousModClient.prefix, name));
						HoverEvent hoverEvent = HoverEvent.Action.SHOW_TEXT.buildHoverEvent(
							new LiteralText(String.format("NBT: %s",
								Files.readString(Path.of(file.getPath())))).formatted(
								Formatting.BLUE));
						Style style = Style.EMPTY;
						style = style.withClickEvent(clickEvent).withHoverEvent(hoverEvent);
						Text text = new LiteralText(name).formatted(Formatting.GREEN)
							.getWithStyle(style).get(0);
						chatUtil.sendMessage(text);
					} catch (Exception ignored) {
					}
				}

				chatUtil.sendMessage(new LiteralText(
					"Hover over an entry to display the NBT load the item.").formatted(
					Formatting.BLUE));

				break;
		}
	}
}
