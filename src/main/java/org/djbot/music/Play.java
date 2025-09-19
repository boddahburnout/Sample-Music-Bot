package org.djbot.music;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import org.djbot.Utils.EmbedWrapper;
import org.djbot.Utils.TrackResult;
import org.djbot.Utils.PlayerManager;
import org.djbot.Utils.Thumbnail;
import org.djbot.config.ConfigManager;
import org.simpleyaml.configuration.file.YamlFile;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;
import java.util.Objects;

public class Play extends Command {
    public Play() {
        this.name = "play";
        this.help = "Play music";
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
        String token = botConfig.getString("Global.Youtube-Token");
        JacksonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
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
            YouTube youTubeService = new YouTube.Builder(
                    GoogleNetHttpTransport.newTrustedTransport(),
                    JSON_FACTORY,
                    null
            ).setApplicationName("Bartender Bot").build();

            YouTube.Search.List request = youTubeService.search().list("id,snippet");
            SearchListResponse response = request.setKey(token)
                    .setQ(args)
                    .setType("video")
                    .setMaxResults(5L)
                    .execute();

            if (e.getArgs().startsWith("http")) {
                playerManager.loadAndPlay(guild.getIdLong(), e.getArgs(), trackResult -> {
                    TrackResult.Status status = trackResult.getStatus();
                    response(status, textChannel, trackResult.getTrack(), trackResult.getPlaylist());
                });
                playerManager.getGuildMusicManager(guild.getIdLong()).player.setVolume(10);
            } else {
                List<SearchResult> results = response.getItems();
                if (results != null && !results.isEmpty()) {
                    SearchResult firstResult = results.get(0);
                    String videoId = firstResult.getId().getVideoId();
                    String videoUrl = "https://www.youtube.com/watch?v=" + videoId;
                    playerManager.loadAndPlay(guild.getIdLong(), videoUrl, trackResult -> {
                        TrackResult.Status status = trackResult.getStatus();
                        response(status, textChannel, trackResult.getTrack(), trackResult.getPlaylist());
                    });
                    playerManager.getGuildMusicManager(guild.getIdLong()).player.setVolume(10);
                }
            }
        } catch (GeneralSecurityException ex) {
            throw new RuntimeException(ex);
        } catch (IOException ex) {
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
