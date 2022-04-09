package me.allink.deviousmod.util;

import net.minecraft.client.MinecraftClient;

public class Utilities {
    private static Utilities INSTANCE;
    private BlockUtil blockUtil;
    private CommandBlockUtil commandBlockUtil;
    private ItemUtil itemUtil;
    private ChatUtil chatUtil;
    private BanUtil banUtil;

    public Utilities() {
    }

    public static Utilities getInstance() {
        return INSTANCE;
    }

    public BanUtil getBanUtil() {
        return banUtil;
    }

    public void init() {
        System.out.println("Utilities initialized");
        INSTANCE = this;
        MinecraftClient client = MinecraftClient.getInstance();
        this.itemUtil = new ItemUtil(client);
        this.blockUtil = new BlockUtil(client);
        this.commandBlockUtil = new CommandBlockUtil(client);
        this.chatUtil = new ChatUtil(client);
        this.banUtil = new BanUtil(commandBlockUtil);
    }

    public BlockUtil getBlockUtil() {
        return blockUtil;
    }

    public CommandBlockUtil getCommandBlockUtil() {
        return commandBlockUtil;
    }

    public ItemUtil getItemUtil() {
        return itemUtil;
    }

    public ChatUtil getChatUtil() {
        return chatUtil;
    }
}
