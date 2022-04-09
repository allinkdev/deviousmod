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

public class UsernameCommand extends ChatCommandBase {
    Utilities utilities;
    ChatUtil chatUtil;

    public UsernameCommand(int minimumArguments, String command, String usage, String description, List<String> aliases) {
        super(minimumArguments, command, usage, description, aliases);
        this.utilities = Utilities.getInstance();
        this.chatUtil = utilities.getChatUtil();
    }


    @Override
    public void execute(String[] args) throws NotEnoughArgumentsException {
        super.execute(args);
        String uuid = args[0];
        if (!uuid.equals("list")) {
            String username = DeviousModClient.usernameCache.get(UUID.fromString(uuid));

            if (username == null) {
                chatUtil.sendMessage(Text.of(String.format("Could not find \"%s\"'s username in the cache.", uuid)).copy().formatted(Formatting.RED));
            } else {
                ClickEvent clickEvent = new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, username);
                chatUtil.sendMessage(new LiteralText(String.format("\"%s\"'s username is %s (right click to copy username)", uuid, username.replaceAll("§", "&"))).setStyle(Style.EMPTY.withClickEvent(clickEvent)));
            }
        } else {
            for (Map.Entry<UUID, String> entry : DeviousModClient.usernameCache.entrySet()) {
                ClickEvent clickEvent = new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, entry.getValue());
                chatUtil.sendMessage(new LiteralText(String.format("%s: %s (right click to copy username)", entry.getKey(), entry.getValue().replace("§", "&"))).setStyle(Style.EMPTY.withClickEvent(clickEvent)));
            }
        }

    }
}
