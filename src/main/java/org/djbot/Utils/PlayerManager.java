package org.djbot.Utils;

import com.sedmelluq.discord.lavaplayer.player.*;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.*;
import dev.lavalink.youtube.YoutubeAudioSourceManager;
import net.dv8tion.jda.api.JDA;
import org.djbot.Main;

import java.util.*;
import java.util.function.Consumer;

public class PlayerManager {

    private static PlayerManager INSTANCE;
    private final AudioPlayerManager playerManager;
    private final Map<Long, GuildMusicManager> musicManagers;

    public static class LoadResult {
        public final List<AudioTrack> tracks = new ArrayList<>();
        public String message = "";
    }

    private PlayerManager() {
        this.musicManagers = new HashMap<>();
        this.playerManager = new DefaultAudioPlayerManager();

        // Register sources
        playerManager.registerSourceManager(new YoutubeAudioSourceManager());
        AudioSourceManagers.registerRemoteSources(playerManager);
        AudioSourceManagers.registerLocalSource(playerManager);
    }

    public static synchronized PlayerManager getInstance() {
        if (INSTANCE == null) INSTANCE = new PlayerManager();
        return INSTANCE;
    }

    public synchronized GuildMusicManager getGuildMusicManager(long guildId) {
        GuildMusicManager manager = musicManagers.get(guildId);
        JDA jda = Main.getJdaInstance();
        if (manager == null) {
            manager = new GuildMusicManager(playerManager);
            musicManagers.put(guildId, manager);
        }
        jda.getGuildById(guildId).getAudioManager().setSendingHandler(manager.getSendHandler());

        return manager;
    }

    public LoadResult loadAndPlay(long guildId, String trackUrl, Consumer<TrackResult> callback) {
        GuildMusicManager musicManager = getGuildMusicManager(guildId);
        LoadResult result = new LoadResult();

        playerManager.loadItemOrdered(musicManager, trackUrl, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {
                musicManager.scheduler.queue(track);
                result.tracks.add(track);
                result.message = "Added to queue: " + track.getInfo().title;
                callback.accept(new TrackResult(TrackResult.Status.TRACK_LOADED, track, null, null));
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                if (playlist.isSearchResult()) {
                    AudioTrack firstTrack = playlist.getTracks().get(0);
                    musicManager.scheduler.queue(firstTrack);
                    result.tracks.add(firstTrack);
                    result.message = "Search result added to queue: " + firstTrack.getInfo().title;
                    callback.accept(new TrackResult(TrackResult.Status.SEARCH_RESULT, firstTrack, playlist, null));
                } else {
                    for (AudioTrack track : playlist.getTracks()) {
                        musicManager.scheduler.queue(track);
                        result.tracks.add(track);
                    }
                    result.message = "Playlist added: " + playlist.getName() +
                            " (" + playlist.getTracks().size() + " tracks)";
                    callback.accept(new TrackResult(TrackResult.Status.PLAYLIST_LOADED, null, playlist, null));
                }
            }

            @Override
            public void noMatches() {
                result.message = "No tracks found for: " + trackUrl;
                callback.accept(new TrackResult(TrackResult.Status.NO_MATCHES, null, null, null));
            }

            @Override
            public void loadFailed(FriendlyException exception) {
                result.message = "Failed to load track: " + exception.getMessage();
                callback.accept(new TrackResult(TrackResult.Status.LOAD_FAILED, null, null, null));
            }
        });

        return result;
    }
}
