package me.allink.deviousmod.modules;

import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import me.allink.deviousmod.json.language.Lang;
import me.allink.deviousmod.managers.ModuleManager;
import me.allink.deviousmod.module.ModuleBase;

public class ChatFilterModule extends ModuleBase {
    public static List<String> words;

    public ChatFilterModule(String name, String description, String category,
        ModuleManager manager) {
        super(name, description, category, manager);
    }


    @Override
    public void onEnabled() {
        ClassLoader classLoader = getClass().getClassLoader();
        InputStream inputStream = classLoader.getResourceAsStream("lang.json");
        StringBuilder json = new StringBuilder();

        try (InputStreamReader streamReader =
                     new InputStreamReader(inputStream, StandardCharsets.UTF_8);
             BufferedReader reader = new BufferedReader(streamReader)) {

            String line;
            while ((line = reader.readLine()) != null) {
                json.append(line);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        words = new Gson().fromJson(json.toString(), Lang.class).words;
        System.out.println(words.size());
    }
}
