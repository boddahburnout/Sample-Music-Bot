package org.djbot.music;

import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import org.djbot.Utils.helper.EmbedWrapper;
import org.djbot.Utils.music.GuildMusicManager;
import org.djbot.Utils.music.PlayerManager;
import org.djbot.category.BotCategories;

import java.util.Objects;

public class Clear extends SlashCommand {
    public final boolean isEphemeral = true;
    public Clear() {
        this.name = "clear";
        this.category = new BotCategories().MusicCat();
        this.help = "Clear all music in queue";
    }
    @Override
    protected void execute(SlashCommandEvent slashCommandEvent) {
        PlayerManager playerManager = PlayerManager.getInstance();
        GuildMusicManager guildMusicManager = playerManager.getGuildMusicManager(Objects.requireNonNull(slashCommandEvent.getGuild()).getIdLong());
        guildMusicManager.scheduler.clearQueue();
        slashCommandEvent.replyEmbeds(EmbedWrapper.createInfo("Queue has been cleared", new EmbedWrapper().GetGuildEmbedColor(slashCommandEvent.getGuild()))).setEphemeral(isEphemeral).queue();
    }
}
