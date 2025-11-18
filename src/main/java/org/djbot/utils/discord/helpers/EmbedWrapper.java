package org.djbot.utils.discord.helpers;


import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.SelfUser;
import org.djbot.Main;

import java.awt.*;

public class EmbedWrapper {
    public Color GetGuildEmbedColor(Guild guild) {


        // 1. Get the hex string from the config, providing a default color
        String hexColor = Main.getConfigData().getGuildColor(guild.getIdLong());
        if (hexColor == null) return Color.decode("#FFFFFF");
        try {
            // 2. Parse the hex string directly into a Color object
            return Color.decode(hexColor);
        } catch (NumberFormatException e) {
            // Fallback in case the string in the config is invalid
            return Color.decode("#FFFFFF");
        }
    }
    public static MessageEmbed createInfo(String message, Color color) {
        EmbedBuilder eb = new EmbedBuilder();
        JDA jda = Main.getJdaInstance();
        SelfUser selfUser = jda.getSelfUser();
        String title = selfUser.getName();
        String thumbnail = selfUser.getEffectiveAvatarUrl();
        eb.setTitle(title);
        eb.setThumbnail(thumbnail);
        eb.setDescription(message);
        eb.setColor(color);

        return eb.build();
    }

    public static MessageEmbed createInfo(String message, Color color, String title, String thumbnail) {
        EmbedBuilder eb = new EmbedBuilder();
        JDA jda = Main.getJdaInstance();
        eb.setTitle(title);
        eb.setThumbnail(thumbnail);
        eb.setDescription(message);
        eb.setColor(color);

        return eb.build();
    }

    public static MessageEmbed createError(String message, Color color) {
        // EmbedBuilder is created *inside* the method.
        // This is fresh, clean, and thread-safe every time.
        EmbedBuilder eb = new EmbedBuilder();

        eb.setTitle("❌ Error");
        eb.setDescription(message);
        eb.setColor(color);

        return eb.build();
    }

    public static MessageEmbed createTrackLoaded(String title, String trackTitle, String trackUrl, String author, String artworkUrl, Color color) {
        EmbedBuilder eb = new EmbedBuilder();

        eb.setTitle(title);
        // Create a markdown link for the title
        eb.setDescription("[" + trackTitle + "](" + trackUrl + ")\nby " + author);
        eb.setColor(color);

        if (artworkUrl != null) {
            eb.setThumbnail(artworkUrl);
        }

        return eb.build();
    }
}