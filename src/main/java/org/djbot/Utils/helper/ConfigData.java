package org.djbot.Utils.helper;

import org.djbot.config.ConfigManager;
import org.simpleyaml.configuration.file.YamlFile;

import java.io.IOException;

public class ConfigData {
    YamlFile Config = new ConfigManager().accessConfig();
    public ConfigData() {
        new ConfigManager().setConfig();
    }
    public String getJDAToken() {
        return Config.getString("Global.Bot-Token");
    }
    public String getGeminiToken() {
        return Config.getString("Global.Gemini-Token");
    }
    public int getGuildVolume(long guildId) {
        return Config.getInt("Settings.Guilds." + guildId + ".Volume", 100);
    }
    public void setGuildVolume(long guildId, int volume) {
        try {
        Config.set("Settings.Guilds." + guildId + ".Volume", volume);
        Config.save();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public String getOwnerId() {
        return Config.getString("Settings.Owner-Id");
    }
    public String getActivity() {
        return Config.getString("Settings.Activity");
    }
    public String getGuildColor(long guildId) {
        return Config.getString("Settings.Guilds." + guildId + ".Color");
    }
    public void setGuildColor(long guildId, String color) {
        try {
            Config.set("Settings.Guilds." + guildId + ".Color", color);
            Config.save();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void reload() {
        Config = new ConfigManager().accessConfig();
    }
}