package org.djbot.music;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import org.djbot.Utils.EmbedWrapper;

import java.util.Objects;

public class Leave extends Command {
    public Leave() {
        this.name = "leave";
        this.help = "Leave the VC";
    }
    @Override
    protected void execute(CommandEvent e) {
        Guild guild = e.getGuild();
        MessageChannel messageChannel = e.getChannel();
        VoiceChannel voiceChannel = Objects.requireNonNull(guild.getSelfMember().getVoiceState()).getChannel().asVoiceChannel();
        if (voiceChannel == null) {
            messageChannel.sendMessageEmbeds(new EmbedWrapper().EmbedMessage(guild.getJDA().getSelfUser().getName(), null, null, new EmbedWrapper().GetGuildEmbedColor(guild), "I am not connected to a voice channel!", null, null, guild.getJDA().getSelfUser().getEffectiveAvatarUrl(), null)).queue();
            return;
        }
        guild.getAudioManager().closeAudioConnection();
        messageChannel.sendMessageEmbeds(new EmbedWrapper().EmbedMessage(guild.getJDA().getSelfUser().getName(), null, null, new EmbedWrapper().GetGuildEmbedColor(guild), "Disconnected from the voice channel!", null, null, guild.getJDA().getSelfUser().getEffectiveAvatarUrl(), null)).queue();
    }
}
