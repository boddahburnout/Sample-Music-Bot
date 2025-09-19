package org.djbot.music;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import org.djbot.Utils.EmbedWrapper;
import org.djbot.Utils.PlayerManager;

import java.util.Objects;

public class Pause extends Command {
    public Pause() {
        this.name = "Pause";
        this.help = "Pause the audio";
    }
    @Override
    protected void execute(CommandEvent e) {
        Guild guild = e.getGuild();
        TextChannel textChannel = e.getTextChannel();
        Member member = e.getMember();
        Member selfMember = e.getSelfMember();
        boolean state = Objects.requireNonNull(member.getVoiceState()).inAudioChannel();
        boolean selfState = Objects.requireNonNull(selfMember.getVoiceState()).inAudioChannel();
        if (!state) {
            textChannel.sendMessageEmbeds(new EmbedWrapper().EmbedMessage(guild.getJDA().getSelfUser().getName(), null, null, new EmbedWrapper().GetGuildEmbedColor(guild), "You aren't even listening smh", null, null, guild.getJDA().getSelfUser().getEffectiveAvatarUrl(), null)).queue();
        }
        if (!selfState) {
            textChannel.sendMessageEmbeds(new EmbedWrapper().EmbedMessage(guild.getJDA().getSelfUser().getName(), null, null, new EmbedWrapper().GetGuildEmbedColor(guild), "Ask me to join the voice channel first!", null, null, guild.getJDA().getSelfUser().getEffectiveAvatarUrl(), null)).queue();
        }
        PlayerManager player = PlayerManager.getInstance();
        AudioPlayer audioPlayer = player.getGuildMusicManager(guild.getIdLong()).player;
        if (!audioPlayer.isPaused()) {
            audioPlayer.setPaused(true);
            textChannel.sendMessageEmbeds(new EmbedWrapper().EmbedMessage(guild.getJDA().getSelfUser().getName(), null, null, new EmbedWrapper().GetGuildEmbedColor(guild), "Music has been paused", null, null, guild.getJDA().getSelfUser().getEffectiveAvatarUrl(), null)).queue();
        } else {
            audioPlayer.setPaused(false);
            textChannel.sendMessageEmbeds(new EmbedWrapper().EmbedMessage(guild.getJDA().getSelfUser().getName(), null, null, new EmbedWrapper().GetGuildEmbedColor(guild), "Music has been resumed", null, null, guild.getJDA().getSelfUser().getEffectiveAvatarUrl(), null)).queue();
        }
    }
}
