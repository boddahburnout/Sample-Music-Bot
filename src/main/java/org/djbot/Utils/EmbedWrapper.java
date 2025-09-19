package org.djbot.Utils;


import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.simpleyaml.configuration.file.YamlFile;

import java.awt.*;

public class EmbedWrapper {
    EmbedBuilder eb = new EmbedBuilder();

    public MessageEmbed EmbedMessage(String title, String Author, String url, Color color, String message, String autorurl, String imageurl, String thumb, String image) {
        eb.setTitle(title, url);
        eb.setColor(color);
        eb.setDescription(message);
        eb.setAuthor(Author, autorurl, imageurl);
        eb.setThumbnail(thumb);
        eb.setImage(image);
        return eb.build();
    }

    public Color GetGuildEmbedColor(Guild guild) {
        YamlFile botConfig = null;
        //try {
            //botConfig = new ConfigManager().accessConfig();
            //String RGB = botConfig.getString("Settings.Guilds."+guild.getId()+".Color");
            //if (!(RGB == null)) {
                //Color embedcolor = ColorFactory.valueOf(RGB);
                //return embedcolor;
            //} else {
                //Color embedcolor = new Color(50, 86, 168);
                //return embedcolor;
            //}
        //} catch (IOException e) {
            //throw new RuntimeException(e);
        //}
        return Color.YELLOW;
    }
}