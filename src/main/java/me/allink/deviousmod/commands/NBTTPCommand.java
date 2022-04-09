package me.allink.deviousmod.commands;

import me.allink.deviousmod.command.ChatCommandBase;
import me.allink.deviousmod.exception.NotEnoughArgumentsException;
import me.allink.deviousmod.util.BlockUtil;
import me.allink.deviousmod.util.ChatUtil;
import me.allink.deviousmod.util.ItemUtil;
import me.allink.deviousmod.util.Utilities;
import net.minecraft.client.MinecraftClient;
import net.minecraft.nbt.NbtElement;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class NBTTPCommand extends ChatCommandBase {
    Utilities utilities;
    ItemUtil itemUtil;
    BlockUtil blockUtil;
    ChatUtil chatUtil;

    public NBTTPCommand(int minimumArguments, String command, String usage, String description, List<String> aliases) {
        super(minimumArguments, command, usage, description, aliases);
        utilities = Utilities.getInstance();
        itemUtil = utilities.getItemUtil();
        blockUtil = utilities.getBlockUtil();
        chatUtil = utilities.getChatUtil();
    }


    @Override
    public void execute(String[] args) throws NotEnoughArgumentsException {
        super.execute(args);

        MinecraftClient client = MinecraftClient.getInstance();
        String target = args[0];
        String nbt = String.format("{BlockEntityTag:{Book:{Count:1b,id:\"minecraft:written_book\",tag:{title:\"DeviousMod\",author:\"Allink\",pages:['{\"nbt\":\"Pos\",\"entity\":\"%s\"}']}}}}", target);
        try {
            Vec3d location = client.player.getPos().add(0, 3, 0);
            itemUtil.giveItem("lectern", (byte) 1, nbt, 0);
            blockUtil.placeBlock(location, 36);
            itemUtil.giveItem("air", (byte) 1, 0);
            Timer t = new Timer(); // account for network lag and such
            t.schedule(new TimerTask() {
                @Override
                public void run() {
                    client.getNetworkHandler().getDataQueryHandler().queryBlockNbt(new BlockPos(location.getX(), location.getY(), location.getZ()), (nbtCompound) -> {
                        if (nbtCompound.isEmpty()) {
                            return;
                        } else {
                            String text = nbtCompound.getCompound("Book").getCompound("tag").getList("pages", NbtElement.STRING_TYPE).get(0).asString();
                            String asText = Text.Serializer.fromJson(text).getString().replaceAll("\\[", "").replaceAll("\\]", "");
                            String[] asArray = asText.split(",");
                            blockUtil.breakBlock(location);
                            client.player.sendChatMessage(String.format("/tppos %s %s %s", (int) Math.floor(Double.parseDouble(asArray[0])), (int) Math.floor(Double.parseDouble(asArray[1])), (int) Math.floor(Double.parseDouble(asArray[2]))));
                            chatUtil.sendMessage(String.format("Teleporting to %s...", target));
                        }
                    });
                }
            }, 90L);
        } catch (Exception e) {
            chatUtil.sendMessage(new LiteralText(e.getMessage()).formatted(Formatting.RED));
        }
    }
}
