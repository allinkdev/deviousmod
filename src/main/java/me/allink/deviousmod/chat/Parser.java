package me.allink.deviousmod.chat;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import me.allink.deviousmod.client.DeviousModClient;

import java.util.Map;

public class Parser {
    public static final Map<String, String> colorMap = Map.ofEntries(
            Map.entry("black", "§0"),
            Map.entry("dark_blue", "§1"),
            Map.entry("dark_green", "§2"),
            Map.entry("dark_aqua", "§3"),
            Map.entry("dark_red", "§4"),
            Map.entry("dark_purple", "§5"),
            Map.entry("gold", "§6"),
            Map.entry("gray", "§7"),
            Map.entry("dark_gray", "§8"),
            Map.entry("blue", "§9"),
            Map.entry("green", "§a"),
            Map.entry("aqua", "§b"),
            Map.entry("red", "§c"),
            Map.entry("light_purple", "§d"),
            Map.entry("yellow", "§e"),
            Map.entry("white", "§f"),
            Map.entry("bold", "§l"),
            Map.entry("italic", "§o"),
            Map.entry("underline", "§n"),
            Map.entry("strikethrough", "§m"),
            Map.entry("obfuscated", "§k"),
            Map.entry("reset", "§r")
    );

    public static <T> T coalesce(T... ts) {
        for (T t : ts)
            if (t != null)
                return t;

        return null;
    }

    public static String parse(JsonObject extra, boolean isTopLevel, int depth) {
        StringBuilder text = new StringBuilder();

        String color = null;
        if (extra.has("color")) {
            color = coalesce(DeviousModClient.colorToHex.get(extra.get("color").getAsString()), '§' + extra.get("color").getAsString());
        } else if (!isTopLevel) {
            color = DeviousModClient.colorToHex.get("reset");
        }

        if (color != null) text.append(color);

        if (extra.has("bold")) if (extra.get("bold").getAsBoolean()) text.append(colorMap.get("bold"));
        if (extra.has("italic")) if (extra.get("italic").getAsBoolean()) text.append(colorMap.get("italic"));
        if (extra.has("underlined")) if (extra.get("underlined").getAsBoolean()) text.append(colorMap.get("underline"));
        if (extra.has("strikethrough"))
            if (extra.get("strikethrough").getAsBoolean()) text.append(colorMap.get("strikethrough"));
        if (extra.has("obfuscated"))
            if (extra.get("obfuscated").getAsBoolean()) text.append(colorMap.get("obfuscated"));

        text.append(coalesce(extra.get("text").getAsString(), ""));

        /*if (extra.translate && !extra.text) {
            const translate = language[extra.translate] || extra.translate;
            const parsedWith = [];
            if (extra.with) {
                for (const subExtra of extra.with) {
                    const parsedSubExtra = parse(subExtra, true, depth + 1);
                    parsedWith.push(parsedSubExtra + (color || colorMap.reset));
                }
            }

            text += util.format(translate, ...parsedWith);

        }*/

        if (extra.has("extra") && depth < 10) {
            JsonArray array = extra.getAsJsonArray("extra");
            for (int i = 0; i < array.size(); i++) {
                text.append(parse(array.get(i).getAsJsonObject(), false, depth + 1));
            }
        }

        return text.toString();
    }
}
