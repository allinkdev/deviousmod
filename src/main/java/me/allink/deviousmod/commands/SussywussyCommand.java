package me.allink.deviousmod.commands;

import java.util.ArrayList;
import java.util.List;
import me.allink.deviousmod.command.ChatCommandBase;
import net.minecraft.client.MinecraftClient;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.packet.c2s.play.CreativeInventoryActionC2SPacket;
import net.minecraft.util.registry.Registry;

public class SussywussyCommand extends ChatCommandBase {
    public SussywussyCommand(int minimumArguments, String command, String usage, String description, List<String> aliases) {
        super(minimumArguments, command, usage, description, aliases);
    }

    @Override
    public void execute(String[] args) {
        NbtCompound item = new NbtCompound();
        item.putString("id", "minecraft:stone_button");
        item.putByte("Count", (byte) 1);
        NbtCompound tag = new NbtCompound();
        NbtList enchantments = new NbtList();
        List<Enchantment> allEnchantments = new ArrayList<>();

        for (Enchantment enchantment : Registry.ENCHANTMENT) {
            allEnchantments.add(enchantment);
        }

        System.out.println(allEnchantments);

        for (Enchantment allEnchantment : allEnchantments) {
            for (int i = 0; i < 128; i++) {
                NbtCompound enchantmentCompound = new NbtCompound();
                enchantmentCompound.putString("id",
                    String.valueOf(Registry.ENCHANTMENT.getId(allEnchantment)));
                enchantmentCompound.putShort("lvl", (short) i);
                enchantments.add(enchantmentCompound);
            }
        }

        tag.put("Enchantments", enchantments);
        /*NbtList attributes = new NbtList();
        String[] tags = {"minecraft:generic.attack_damage", "minecraft:generic.attack_knockback", "minecraft:generic.armor_toughness", "minecraft:generic.max_health", "minecraft:generic.knockback_resistance", "minecraft:generic.armor"};
        for (String attributeName : tags) {
            NbtCompound attribute = new NbtCompound();
            attribute.putDouble("Amount", Integer.MAX_VALUE);
            attribute.putString("AttributeName", attributeName);
            attribute.putString("Name", "");
            attribute.putInt("Operation", 1);
            attribute.putUuid("UUID", UUID.randomUUID());
            attributes.add(attribute);
        }
        tag.put("AttributeModifiers", attributes);
        */
        item.put("tag", tag);
        System.out.println(item);
        MinecraftClient.getInstance().getNetworkHandler()
            .sendPacket(new CreativeInventoryActionC2SPacket(36, ItemStack.fromNbt(item)));

    }
}
