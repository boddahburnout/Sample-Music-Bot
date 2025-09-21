package org.djbot.music;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.managers.AudioManager;
import org.djbot.Utils.EmbedWrapper;
import org.djbot.Utils.GuildMusicManager;
import org.djbot.Utils.PlayerManager;
import org.djbot.category.BotCategories;

public class Leave extends Command {
    public Leave() {
        this.name = "leave";
        this.help = "Leave the VC";
        this.category = new BotCategories().MusicCat();
    }

    @Override
    protected void execute(CommandEvent e) {
        Guild guild = e.getGuild();
        MessageChannel messageChannel = e.getChannel();
        AudioManager audioManager = guild.getAudioManager();
        Boolean voiceChannel = audioManager.isConnected();
        PlayerManager playerManager = PlayerManager.getInstance();
        GuildMusicManager guildMusicManager = playerManager.getGuildMusicManager(guild.getIdLong());
        if (!voiceChannel) {
            messageChannel.sendMessageEmbeds(new EmbedWrapper().EmbedMessage(guild.getJDA().getSelfUser().getName(), null, null, new EmbedWrapper().GetGuildEmbedColor(guild), "I am not connected to a voice channel!", null, null, guild.getJDA().getSelfUser().getEffectiveAvatarUrl(), null)).queue();
        } else {
            guild.getAudioManager().closeAudioConnection();
            guildMusicManager.scheduler.getQueue().clear();
            messageChannel.sendMessageEmbeds(new EmbedWrapper().EmbedMessage(guild.getJDA().getSelfUser().getName(), null, null, new EmbedWrapper().GetGuildEmbedColor(guild), "Disconnected from the voice channel!", null, null, guild.getJDA().getSelfUser().getEffectiveAvatarUrl(), null)).queue();
        }
    }
}
