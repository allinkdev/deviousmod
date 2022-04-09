package me.allink.deviousmod.json.config;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Config {
    public List<String> enabled;
    public List<String> ignore;
    public List<String> chatCommands;
    public List<String> banList;
    public String kcpHash;
    public String hbotKey;
    public String sbotKey;
    public String key;
    public String prefix;
    public String mainMenuText;
    public String inGameText;
    public String iconText;
    public String bigIconText;
    public String autoEncryptPrefix;
    public String encryptionChar;
    public String chatPrefix = "";
    public String chatSuffix = "";
    public String eggTarget;
    public List<String> experiments;

    public boolean showIP;
}
