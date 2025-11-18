package org.djbot.commands.slashcommands.lavaplayer;

import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.managers.AudioManager;
import org.djbot.utils.discord.category.BotCategories;
import org.djbot.utils.discord.helpers.EmbedWrapper;
import org.djbot.utils.music.GuildMusicManager;
import org.djbot.utils.music.PlayerManager;

public class Leave extends SlashCommand {
    private final boolean isEphemeral = true;
    public Leave() {
        this.name = "leave";
        this.help = "Leave the VC";
        this.category = new BotCategories().MusicCat();
    }

    @Override
    protected void execute(SlashCommandEvent e) {
        Guild guild = e.getGuild();
        AudioManager audioManager = guild.getAudioManager();
        Boolean voiceChannel = audioManager.isConnected();
        PlayerManager playerManager = PlayerManager.getInstance();
        GuildMusicManager guildMusicManager = playerManager.getGuildMusicManager(guild.getIdLong());
        if (!voiceChannel) {
            e.replyEmbeds(EmbedWrapper.createInfo("I am not connected to a voice channel!", new EmbedWrapper().GetGuildEmbedColor(guild))).setEphemeral(isEphemeral).queue();
        } else {
            if (e.getMember().getVoiceState().getChannel().asVoiceChannel().equals(e.getGuild().getSelfMember().getVoiceState().getChannel().asVoiceChannel())) {
                guild.getAudioManager().closeAudioConnection();
                guildMusicManager.scheduler.clearQueue();
                e.replyEmbeds(EmbedWrapper.createInfo("Disconnected from the voice channel!", new EmbedWrapper().GetGuildEmbedColor(guild))).setEphemeral(isEphemeral).queue();
            } else {
                e.replyEmbeds(EmbedWrapper.createInfo("You aren't connected to the VC!", new EmbedWrapper().GetGuildEmbedColor(guild))).setEphemeral(isEphemeral).queue();
            }
        }
    }
}
