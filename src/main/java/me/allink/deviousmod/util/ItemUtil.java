package me.allink.deviousmod.util;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.StringNbtReader;

public class ItemUtil {

	MinecraftClient client;

	public ItemUtil(MinecraftClient client) {
		System.out.println("ItemUtil initialized");
		this.client = client;
	}

	/**
	 * Give the player an item
	 *
	 * @param name The type of item
	 */
	public void giveItem(String name) {
		giveItem(name, (byte) 1);
	}

	/**
	 * Give the player an item
	 *
	 * @param name  The type of item
	 * @param count The count of the item
	 */
	public void giveItem(String name, byte count) {
		giveItem(name, count, new NbtCompound());
	}

	/**
	 * Give the player an item
	 *
	 * @param name  The type of item
	 * @param count The count of the item
	 * @param slot  The hotbar slot
	 */
	public void giveItem(String name, byte count, int slot) {
		giveItem(name, count, new NbtCompound(), slot);
	}

	/**
	 * Give the player an item
	 *
	 * @param name  The type of item
	 * @param count The count of the item
	 * @param nbt   The stringified NBT of the item
	 * @throws CommandSyntaxException Throws CommandSyntaxException when the NBT provided is
	 *                                invalid
	 */
	public void giveItem(String name, byte count, String nbt) throws CommandSyntaxException {
		giveItem(name, count, StringNbtReader.parse(nbt));
	}

	/**
	 * Give the player an item
	 *
	 * @param name  The type of item
	 * @param count The count of the item
	 * @param nbt   The NBT compound of the item
	 */
	public void giveItem(String name, byte count, NbtCompound nbt) {
		MinecraftClient client = MinecraftClient.getInstance();
		ClientPlayNetworkHandler networkHandler = client.getNetworkHandler();
		PlayerInventory inventory = client.player.getInventory();

		giveItem(name, count, nbt, inventory.getEmptySlot());
	}

	/**
	 * Give the player an item
	 *
	 * @param name  The type of item
	 * @param count The count of the item
	 * @param nbt   The stringified NBT of the item
	 * @param slot  The hotbar slot
	 * @throws CommandSyntaxException Throws CommandSyntaxException when the NBT provided is
	 *                                invalid
	 */
	public void giveItem(String name, byte count, String nbt, int slot)
		throws CommandSyntaxException {
		giveItem(name, count, StringNbtReader.parse(nbt), slot);
	}

	/**
	 * Give the player an item
	 *
	 * @param name  The type of item
	 * @param count The count of the item
	 * @param nbt   The NBT compound of the item
	 * @param slot  The slot of the item
	 */
	public void giveItem(String name, byte count, NbtCompound nbt, int slot) {
		ClientPlayerInteractionManager interactionManager = client.interactionManager;
		PlayerInventory inventory = client.player.getInventory();

		NbtCompound itemTag = new NbtCompound();
		itemTag.putString("id", name);
		itemTag.putByte("Count", count);
		itemTag.put("tag", nbt);
		interactionManager.clickCreativeStack(ItemStack.fromNbt(itemTag), 36 + slot);
	}
}
