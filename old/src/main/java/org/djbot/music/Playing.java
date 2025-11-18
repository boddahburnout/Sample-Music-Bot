package org.djbot.music;

import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.entities.Guild;
import org.djbot.utils.discord.helpers.EmbedWrapper;
import org.djbot.utils.music.PlayerManager;
import org.djbot.utils.discord.category.BotCategories;

public class Playing extends SlashCommand {
    private final boolean isEphemeral = true;
    public Playing() {
        this.name = "playing";
        this.help = "See what's playing";
        this.category = new BotCategories().MusicCat();
    }

    @Override
    protected void execute(SlashCommandEvent e) {
        Guild guild = e.getGuild();
        PlayerManager playerManager = PlayerManager.getInstance();

        AudioTrack audioTrack = playerManager.getGuildMusicManager(guild.getIdLong()).player.getPlayingTrack();
        try {
            e.replyEmbeds(EmbedWrapper.createInfo("The song playing is " + audioTrack.getInfo().title, new EmbedWrapper().GetGuildEmbedColor(guild))).setEphemeral(isEphemeral).queue();
        } catch (NullPointerException ex) {
            e.replyEmbeds(EmbedWrapper.createInfo("Nothing is playing!", new EmbedWrapper().GetGuildEmbedColor(guild))).setEphemeral(isEphemeral).queue();
        }
    }
}