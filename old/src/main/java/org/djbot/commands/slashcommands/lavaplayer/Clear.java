package org.djbot.commands.slashcommands.lavaplayer;

import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import org.djbot.Main;
import org.djbot.utils.bot.config.ConfigData;
import org.djbot.utils.discord.helpers.EmbedWrapper;
import org.djbot.utils.music.GuildMusicManager;
import org.djbot.utils.music.PlayerManager;
import org.djbot.utils.discord.category.BotCategories;

import java.util.Objects;

public class Clear extends SlashCommand {
    public Clear() {
        this.name = "clear";
        this.category = new BotCategories().MusicCat();
        this.help = "Clear all music in queue";
    }
    @Override
    protected void execute(SlashCommandEvent slashCommandEvent) {
        PlayerManager playerManager = PlayerManager.getInstance();
        ConfigData configData = Main.getConfigData();
        long guildId = slashCommandEvent.getGuild().getIdLong();
        boolean isEphemeral = false;
        if (configData.isEphemeral(guildId)) {
            isEphemeral = configData.getEphemeral(guildId);
        }
        GuildMusicManager guildMusicManager = playerManager.getGuildMusicManager(Objects.requireNonNull(slashCommandEvent.getGuild()).getIdLong());
        guildMusicManager.scheduler.clearQueue();
        slashCommandEvent.replyEmbeds(EmbedWrapper.createInfo("Queue has been cleared", new EmbedWrapper().GetGuildEmbedColor(slashCommandEvent.getGuild()))).setEphemeral(isEphemeral).queue();
    }
}
