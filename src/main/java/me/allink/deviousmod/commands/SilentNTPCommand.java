package me.allink.deviousmod.commands;

import me.allink.deviousmod.command.ChatCommandBase;
import me.allink.deviousmod.exception.NotEnoughArgumentsException;
import me.allink.deviousmod.util.BlockUtil;
import me.allink.deviousmod.util.ChatUtil;
import me.allink.deviousmod.util.ItemUtil;
import me.allink.deviousmod.util.Utilities;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Formatting;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class SilentNTPCommand extends ChatCommandBase {
    Utilities utilities;
    ChatUtil chatUtil;
    ItemUtil itemUtil;
    BlockUtil blockUtil;

    public SilentNTPCommand(int minimumArguments, String command, String usage, String description, List<String> aliases) {
        super(minimumArguments, command, usage, description, aliases);
        utilities = Utilities.getInstance();
        chatUtil = utilities.getChatUtil();
        itemUtil = utilities.getItemUtil();
    }


    @Override
    public void execute(String[] args) throws NotEnoughArgumentsException {
        super.execute(args);

        MinecraftClient client = MinecraftClient.getInstance();
        String target = args[0];
        String nbt = String.format("{title:\"DeviousMod\",author:\"Allink\",pages:['{\"nbt\":\"Pos\",\"entity\":\"%s\"}'],resolved:0b}", target);
        try {
            itemUtil.giveItem("written_book", (byte) 1, nbt, 0);
            Timer t = new Timer(); // account for network lag and such
            t.schedule(new TimerTask() {
                @Override
                public void run() {
                    System.out.println(client.player.getInventory().getStack(0).getNbt());
                }
            }, 1000L);
        } catch (Exception e) {
            chatUtil.sendMessage(new LiteralText(e.getMessage()).formatted(Formatting.RED));
        }
    }

}
