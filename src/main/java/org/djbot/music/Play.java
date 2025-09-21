package org.djbot.music;

import com.google.api.services.youtube.model.SearchResult;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import org.djbot.Utils.*;
import org.djbot.category.BotCategories;
import org.djbot.config.ConfigManager;
import org.simpleyaml.configuration.file.YamlFile;

import java.util.List;
import java.util.Objects;

public class Play extends Command {
    public Play() {
        this.name = "play";
        this.help = "Play music";
        this.category = new BotCategories().MusicCat();
    }

    @Override
    protected void execute(CommandEvent e) {
        Guild guild = e.getGuild();
        TextChannel textChannel = e.getTextChannel();
        Member member = e.getMember();
        Member selfMember = e.getSelfMember();
        PlayerManager playerManager = PlayerManager.getInstance();
        boolean state = Objects.requireNonNull(member.getVoiceState()).inAudioChannel();
        boolean selfState = Objects.requireNonNull(selfMember.getVoiceState()).inAudioChannel();
        boolean isDeaf = member.getVoiceState().isDeafened();
        String args = e.getArgs();
        YamlFile botConfig = new ConfigManager().accessConfig();
        if (!state) {
            textChannel.sendMessageEmbeds(new EmbedWrapper().EmbedMessage(guild.getJDA().getSelfUser().getName(), null, null, new EmbedWrapper().GetGuildEmbedColor(guild), "Join the voice channel first!", null, null, guild.getJDA().getSelfUser().getEffectiveAvatarUrl(), null)).queue();
            return;
        }
        if (isDeaf) {
            textChannel.sendMessageEmbeds(new EmbedWrapper().EmbedMessage(guild.getJDA().getSelfUser().getName(), null, null, new EmbedWrapper().GetGuildEmbedColor(guild), "Your not even listening your opinion does not matter", null, null, guild.getJDA().getSelfUser().getEffectiveAvatarUrl(), null)).queue();
            return;
        }
        if (!selfState) {
            textChannel.sendMessageEmbeds(new EmbedWrapper().EmbedMessage(guild.getJDA().getSelfUser().getName(), null, null, new EmbedWrapper().GetGuildEmbedColor(guild), "Ask me to join the voice channel first!", null, null, guild.getJDA().getSelfUser().getEffectiveAvatarUrl(), null)).queue();
            return;
        }
        try {
            if (e.getArgs().startsWith("http")) {
                playerManager.loadAndPlay(guild.getIdLong(), e.getArgs(), loadResult -> {
                    TrackResult.Status status = loadResult.getStatus();
                    response(status, textChannel, loadResult.getTrack(), loadResult.getPlaylist());
                });
            } else {
                YouTubeSearcher yt = new YouTubeSearcher();
                List<SearchResult> results = yt.search(args, 5);
                if (results != null && !results.isEmpty()) {
                    SearchResult firstResult = results.get(0);
                    String videoId = firstResult.getId().getVideoId();
                    String videoUrl = "https://www.youtube.com/watch?v=" + videoId;
                    playerManager.loadAndPlay(guild.getIdLong(), videoUrl, loadResult -> {
                        TrackResult.Status status = loadResult.getStatus();
                        response(status, textChannel, loadResult.getTrack(), loadResult.getPlaylist());
                    });
                }
                int volume = botConfig.getInt("Settings.Guilds." + guild.getId() + ".Volume", 50);
                playerManager.getGuildMusicManager(guild.getIdLong()).player.setVolume(volume);
            }
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
    public void response(TrackResult.Status status, TextChannel textChannel, AudioTrack audioTrack, AudioPlaylist audioPlaylist) {
        if (status == TrackResult.Status.LOAD_FAILED) {
            textChannel.sendMessageEmbeds(new EmbedWrapper().EmbedMessage(textChannel.getJDA().getSelfUser().getName(), null, null, new EmbedWrapper().GetGuildEmbedColor(textChannel.getGuild()),  "Could not play the Requested track", null, null, textChannel.getJDA().getSelfUser().getEffectiveAvatarUrl(), null)).queue();
        }
        if (status == TrackResult.Status.PLAYLIST_LOADED) {
            textChannel.sendMessageEmbeds(new EmbedWrapper().EmbedMessage(textChannel.getJDA().getSelfUser().getName(), null, null, new EmbedWrapper().GetGuildEmbedColor(textChannel.getGuild()), "Added playlist: **" + audioPlaylist.getName() + "** with **" + audioPlaylist.getTracks().size() + "** tracks.", null, null, textChannel.getJDA().getSelfUser().getEffectiveAvatarUrl(), null)).queue();
        }
        if (status == TrackResult.Status.NO_MATCHES) {
            textChannel.sendMessageEmbeds(new EmbedWrapper().EmbedMessage(textChannel.getJDA().getSelfUser().getName(), null, null, new EmbedWrapper().GetGuildEmbedColor(textChannel.getGuild()),  "Nothing found by " + audioTrack.getInfo().uri, null, null, textChannel.getJDA().getSelfUser().getEffectiveAvatarUrl(), null)).queue();
        }
        if (status == TrackResult.Status.TRACK_LOADED) {
            textChannel.sendMessageEmbeds(new EmbedWrapper().EmbedMessage(textChannel.getJDA().getSelfUser().getName(), null, null, new EmbedWrapper().GetGuildEmbedColor(textChannel.getGuild()),  "Adding to queue " + audioTrack.getInfo().title, null, null, new Thumbnail().Thumbnail(audioTrack), null)).queue();
        }
        if (status == TrackResult.Status.SEARCH_RESULT) {
            textChannel.sendMessageEmbeds(new EmbedWrapper().EmbedMessage(textChannel.getJDA().getSelfUser().getName(), null, null, new EmbedWrapper().GetGuildEmbedColor(textChannel.getGuild()), "Adding to queue (search result): " + audioTrack.getInfo().title, null, null, new Thumbnail().Thumbnail(audioTrack), null)).queue();
        }
    }
}
