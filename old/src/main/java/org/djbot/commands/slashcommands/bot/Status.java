package org.djbot.commands.slashcommands.bot;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import org.djbot.Main;
import org.djbot.utils.bot.config.ConfigData;
import org.djbot.utils.discord.category.BotCategories;
import org.djbot.utils.discord.helpers.EmbedWrapper;

import java.awt.*;
import java.io.File;
import java.lang.management.ManagementFactory;
import java.text.NumberFormat;

public class Status extends SlashCommand {

    public Status() {
        this.name = "status";
        this.category = new BotCategories().UserCat();
        this.guildOnly = false;
        this.help = "Get status on the bots server hardware";
    }

    @Override
    protected void execute(SlashCommandEvent e) {
        ConfigData configData = Main.getConfigData();
        long guildId = e.getGuild().getIdLong();
        boolean isEphemeral = false;
        if (configData.isEphemeral(guildId)) {
            isEphemeral = configData.getEphemeral(guildId);
        }
        e.deferReply().setEphemeral(isEphemeral);

        Runtime runtime = Runtime.getRuntime();

        NumberFormat format = NumberFormat.getInstance();

        StringBuilder sb = new StringBuilder();
        long duration = ManagementFactory.getRuntimeMXBean().getUptime();
        long processors = runtime.availableProcessors();
        long maxMemory = runtime.maxMemory();
        long allocatedMemory = runtime.totalMemory();
        long freeMemory = runtime.freeMemory();
        sb.append("------Performance------\n");
        sb.append("Uptime: "+ duration/3600000L+" Hours"+"\n\n");
        sb.append("Total Processors: "+ processors+"\n");
        sb.append("max memory: " + format.format(maxMemory / 1073741824) + " GB\n");
        sb.append("total free memory: " + format.format((freeMemory + (maxMemory - allocatedMemory)) / 1073741824) + " GB\n\n");
        File root = new File("/");
        if (!root.exists()) root = new File("C:");
        sb.append(String.format("Total space: %.2f GB\n",
                (double) root.getTotalSpace() / 1073741824));
        sb.append(String.format("Usable space: %.2f GB\n\n",
                (double) root.getUsableSpace() / 1073741824));

        Color color = new EmbedWrapper().GetGuildEmbedColor(e.getGuild());
        MessageEmbed embed = EmbedWrapper.createInfo( sb.toString(), color);
        e.replyEmbeds(embed).setEphemeral(isEphemeral).queue();
    }
}
