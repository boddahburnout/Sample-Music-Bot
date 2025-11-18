package org.djbot.commands.slashcommands.discord;

import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.djbot.Main;
import org.djbot.utils.bot.config.ConfigData;
import org.djbot.utils.discord.helpers.EmbedWrapper;

import java.awt.*;
import java.util.List;

public class Guilds extends SlashCommand {
    public Guilds() {
        this.name = "guilds";
        this.category = new Category("User");
        this.help = "List guilds bot is active in";
        this.guildOnly = false;
    }

    @Override
    protected void execute(SlashCommandEvent event) {
        JDA jda = event.getJDA();

        List<Guild> guilds = jda.getGuilds();
        int serverCount = guilds.size();

        StringBuilder sb = new StringBuilder();
        sb.append("I'm serving users in **" + serverCount + "** guilds:");

        // Set a limit to avoid exceeding Discord's embed description limit (4096 chars)
        int guildsListed = 0;
        final int maxGuildsToShow = 50; // Show up to 50 guilds

        for (Guild guild : guilds) {
            if (guildsListed >= maxGuildsToShow) {
                break; // Stop if we're showing too many
            }

            // Format as: "- Guild Name (ID: 12345)"
            String line = "\n- " + guild.getName() + " (ID: " + guild.getId() + ")";

            // Check if adding this line would exceed the limit
            if (sb.length() + line.length() > MessageEmbed.DESCRIPTION_MAX_LENGTH - 100) { // -100 for a buffer
                break;
            }

            sb.append(line);
            guildsListed++;
        }

        // Add a "... and X more" message if we truncated the list
        if (guilds.size() > guildsListed) {
            sb.append("\n... and ").append(guilds.size() - guildsListed).append(" more.");
        }
        // --- END UPDATED LOGIC ---

        Color color = new EmbedWrapper().GetGuildEmbedColor(event.getGuild());

        // Use the new StringBuilder string
        MessageEmbed embed = EmbedWrapper.createInfo(sb.toString(), color);
        ConfigData configData = Main.getConfigData();
        long guildId = event.getGuild().getIdLong();
        boolean isEphemeral = false;
        if (configData.isEphemeral(guildId)) {
            isEphemeral = configData.getEphemeral(guildId);
        }
        event.replyEmbeds(embed).setEphemeral(isEphemeral).queue();
    }
}
