package org.djbot.utils.bot.config;

import org.simpleyaml.configuration.file.YamlFile;

import java.io.IOException;

public class ConfigData {
    YamlFile Config = new ConfigManager().accessConfig();
    private final String settingsPath = "Settings.";
    private final String guildSettingsPath = "Settings.Guilds.";
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
        return Config.getInt(guildSettingsPath + guildId + ".Volume", 100);
    }

    public String getOwnerId() {
        return Config.getString(settingsPath+"Owner-Id");
    }
    public String getActivity() {
        return Config.getString(settingsPath+"Activity");
    }
    public String getGuildColor(long guildId) {
        return Config.getString(guildSettingsPath + guildId + ".Color");
    }
    public boolean isGuildColor(long guildId) { return Config.isSet(guildSettingsPath + guildId + ".Color"); }
    public String getGuildWelcomeChannel(long guildId) { return Config.getString(guildSettingsPath+guildId+".Welcome-Channel");}
    public boolean isGuildWelcomeChannel(long guildId) { return Config.isSet(guildSettingsPath+guildId+".Welcome-Channel"); }
    public String getGuildJoinRole(long guildId) { return Config.getString(guildSettingsPath+guildId+".Join-Role"); }
    public boolean isGuildJoinRole(long guildId) { return Config.isSet(guildSettingsPath+guildId+".Join-Role"); }
    public String getWelcomePrompt(long guildId) { return Config.getString(guildSettingsPath+guildId+".Welcome-Prompt"); }
    public boolean isWelcomePrompt(long guildId) { return Config.isSet(guildSettingsPath+guildId+".Welcome-Prompt"); }
    public boolean isEphemeral(long guildId) { return Config.isSet(guildSettingsPath+guildId+".Ephemeral"); }
    public boolean getEphemeral(long guildId) { return Config.getBoolean(guildSettingsPath+guildId+".Ephemeral"); }
    public boolean isGuildVolume(long guildId) { return Config.getBoolean( guildSettingsPath+guildId+".Volume");}
    public void setGuildVolume(long guildId, int volume) {
        try {
        Config.set(guildSettingsPath + guildId + ".Volume", volume);
        Config.save();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public void setGuildColor(long guildId, String color) {
        try {
            Config.set(guildSettingsPath + guildId + ".Color", color);
            Config.save();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void setGuildWelcomeChannel(long guildId, String channelId) {
        try {
            Config.set(guildSettingsPath + guildId + ".Welcome-Channel", channelId);
            Config.save();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void setGuildJoinRole(long guildId, String roleId) {
        try {
            Config.set(guildSettingsPath+guildId+".Join-Role", roleId);
            Config.save();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void setGuildWelcomePrompt(long guildId, String prompt) {
        try {
            Config.set(guildSettingsPath+guildId+".Welcome-Prompt", prompt);
            Config.save();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void setEphemeral(long guildId, boolean ephemeral) {
        try {
            Config.set(guildSettingsPath+guildId+".Ephemeral", ephemeral);
            Config.save();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void reload() {
        Config = new ConfigManager().accessConfig();
    }
}