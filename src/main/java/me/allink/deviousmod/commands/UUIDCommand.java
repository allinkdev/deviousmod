package me.allink.deviousmod.commands;

import me.allink.deviousmod.client.DeviousModClient;
import me.allink.deviousmod.command.ChatCommandBase;
import me.allink.deviousmod.exception.NotEnoughArgumentsException;
import me.allink.deviousmod.util.ChatUtil;
import me.allink.deviousmod.util.Utilities;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class UUIDCommand extends ChatCommandBase {
    Utilities utilities;
    ChatUtil chatUtil;

    public UUIDCommand(int minimumArguments, String command, String usage, String description, List<String> aliases) {
        super(minimumArguments, command, usage, description, aliases);
        this.utilities = Utilities.getInstance();
        this.chatUtil = utilities.getChatUtil();
    }


    @Override
    public void execute(String[] args) throws NotEnoughArgumentsException {
        super.execute(args);
        String username = args[0];
        if (!username.equals("list")) {
            UUID uuid = DeviousModClient.uuidCache.get(username);

            if (uuid == null) {
                chatUtil.sendMessage(Text.of(String.format("Could not find \"%s\"'s UUID in the cache.", username)).copy().formatted(Formatting.RED));
            } else {
                ClickEvent clickEvent = new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, uuid.toString());
                chatUtil.sendMessage(new LiteralText(String.format("\"%s\"'s UUID is %s (right click to copy UUID)", username.replaceAll("§", "&"), uuid)).setStyle(Style.EMPTY.withClickEvent(clickEvent)));
            }
        } else {
            for (Map.Entry<String, UUID> entry : DeviousModClient.uuidCache.entrySet()) {
                ClickEvent clickEvent = new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, entry.getValue().toString());
                chatUtil.sendMessage(new LiteralText(String.format("%s: %s (right click to copy UUID)", entry.getKey().replaceAll("§", "&"), entry.getValue())).setStyle(Style.EMPTY.withClickEvent(clickEvent)));
            }
        }

    }
}
