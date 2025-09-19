package org.djbot.music;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.managers.AudioManager;
import org.djbot.Utils.EmbedWrapper;

import java.util.Objects;

public class Join extends Command {
    public Join() {
        this.name = "join";
        this.help = "Join the VC";
    }

    @Override
    protected void execute(CommandEvent e) {
        Guild guild = e.getGuild();
        TextChannel channel = e.getTextChannel();
        Member member = e.getMember();
        VoiceChannel connectedChannel = Objects.requireNonNull(member.getVoiceState()).getChannel().asVoiceChannel();
        if (connectedChannel == null) {
            channel.sendMessageEmbeds(new EmbedWrapper().EmbedMessage(guild.getJDA().getSelfUser().getName(), null, null, new EmbedWrapper().GetGuildEmbedColor(guild), "You are not connected to a voice channel!", null, null, guild.getJDA().getSelfUser().getEffectiveAvatarUrl(), null)).queue();
        } else {
            AudioManager audioManager = guild.getAudioManager();
            audioManager.openAudioConnection(connectedChannel);
            channel.sendMessageEmbeds(new EmbedWrapper().EmbedMessage(guild.getJDA().getSelfUser().getName(), null, null, new EmbedWrapper().GetGuildEmbedColor(guild), "Connected to the voice channel!", null, null, guild.getJDA().getSelfUser().getEffectiveAvatarUrl(), null)).queue();
        }
    }
}
