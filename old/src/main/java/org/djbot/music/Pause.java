package org.djbot.music;

import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import org.djbot.utils.discord.helpers.EmbedWrapper;
import org.djbot.utils.music.PlayerManager;
import org.djbot.utils.discord.category.BotCategories;

import java.util.Objects;

public class Pause extends SlashCommand {
    private final boolean isEphemeral = true;
    public Pause() {
        this.name = "pause";
        this.help = "Pause the audio";
        this.category = new BotCategories().MusicCat();
    }
    @Override
    protected void execute(SlashCommandEvent e) {
        Guild guild = e.getGuild();
        Member member = e.getMember();
        boolean state = Objects.requireNonNull(member.getVoiceState()).inAudioChannel();
        boolean selfState = Objects.requireNonNull(e.getGuild().getSelfMember().getVoiceState()).inAudioChannel();
        if (!state) {
            e.replyEmbeds(EmbedWrapper.createInfo("You aren't even connect to the channel!", new EmbedWrapper().GetGuildEmbedColor(guild))).setEphemeral(isEphemeral).queue();
        }
        if (!selfState) {
            e.replyEmbeds(EmbedWrapper.createInfo("Ask me to join the voice channel first!", new EmbedWrapper().GetGuildEmbedColor(guild))).setEphemeral(isEphemeral).queue();
        }
        PlayerManager player = PlayerManager.getInstance();
        AudioPlayer audioPlayer = player.getGuildMusicManager(guild.getIdLong()).player;
        if (!audioPlayer.isPaused()) {
            audioPlayer.setPaused(true);
            e.replyEmbeds(EmbedWrapper.createInfo("Music has been paused", new EmbedWrapper().GetGuildEmbedColor(guild))).setEphemeral(false).queue();
        } else {
            audioPlayer.setPaused(false);
            e.replyEmbeds(EmbedWrapper.createInfo("Music has been resumed", new EmbedWrapper().GetGuildEmbedColor(guild))).setEphemeral(false).queue();
        }
    }
}
