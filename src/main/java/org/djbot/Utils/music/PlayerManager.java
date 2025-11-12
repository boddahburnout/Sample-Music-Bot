package org.djbot.Utils.music;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
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
            manager = new GuildMusicManager(playerManager, guildId);
            musicManagers.put(guildId, manager);
        }
        jda.getGuildById(guildId).getAudioManager().setSendingHandler(manager.getSendHandler());

        return manager;
    }

    /**
     * Loads a track and plays it immediately, putting the currently playing track (if any)
     * at the front of the queue.
     */
    public LoadResult loadAndPlayNow(long guildId, String trackUrl, Consumer<TrackResult> callback) {
        GuildMusicManager musicManager = getGuildMusicManager(guildId);
        LoadResult result = new LoadResult();

        // Get and clone the currently playing track INSIDE the callback
        // to prevent race conditions and NullPointerExceptions.

        playerManager.loadItemOrdered(musicManager, trackUrl, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {
                AudioTrack currentlyPlaying = musicManager.player.getPlayingTrack();
                AudioTrack trackToReQueue = null;

                if (currentlyPlaying != null) {
                    trackToReQueue = currentlyPlaying.makeClone(); // 1. Clone
                }

                // 2. Start the new track (safely stops the old one)
                musicManager.player.startTrack(track, false);

                if (trackToReQueue != null) {
                    musicManager.scheduler.queueFirst(trackToReQueue); // 3. Queue the CLONE
                }

                result.tracks.add(track);
                result.message = "Playing now: " + track.getInfo().title;
                callback.accept(new TrackResult(TrackResult.Status.TRACK_LOADED, track, null, null));
                //musicManager.scheduler.updateQueueSnapshot(); // Call snapshot update
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                // --- FIX for Playlist ---
                AudioTrack currentlyPlaying = musicManager.player.getPlayingTrack();
                AudioTrack trackToReQueue = null;

                if (currentlyPlaying != null) {
                    trackToReQueue = currentlyPlaying.makeClone(); // 1. CLONE
                }

                AudioTrack firstTrack = playlist.getTracks().get(0);

                // 2. Start the new track
                musicManager.player.startTrack(firstTrack, false);

                // 3. Queue the rest of the playlist (if it's not a search result)
                if (!playlist.isSearchResult()) {
                    result.tracks.add(firstTrack);
                    for (int i = 1; i < playlist.getTracks().size(); i++) {
                        AudioTrack nextTrack = playlist.getTracks().get(i);
                        musicManager.scheduler.queue(nextTrack);
                        result.tracks.add(nextTrack);
                    }
                    result.message = "Playing playlist: " + playlist.getName();
                } else {
                    result.tracks.add(firstTrack);
                    result.message = "Playing search result: " + firstTrack.getInfo().title;
                }

                // 4. Queue the cloned old track
                if (trackToReQueue != null) {
                    musicManager.scheduler.queueFirst(trackToReQueue);
                }
                // --- END FIX ---

                if (playlist.isSearchResult()) {
                    callback.accept(new TrackResult(TrackResult.Status.SEARCH_RESULT, firstTrack, playlist,  null));
                } else {
                    callback.accept(new TrackResult(TrackResult.Status.PLAYLIST_LOADED, null, playlist, null));
                }
                //musicManager.scheduler.updateQueueSnapshot(); // Call snapshot update
            }

            @Override
            public void noMatches() {
                callback.accept(new TrackResult(TrackResult.Status.NO_MATCHES, null, null, null));
            }

            @Override
            public void loadFailed(FriendlyException exception) {
                callback.accept(new TrackResult(TrackResult.Status.LOAD_FAILED, null, null, null));
            }
        });
        return result;
    }


    /**
     * Loads a track and adds it to the queue. If the player is idle, it starts playing.
     */
    public LoadResult loadAndPlay(long guildId, String trackUrl, Consumer<TrackResult> callback) {
        GuildMusicManager musicManager = getGuildMusicManager(guildId);
        LoadResult result = new LoadResult();

        playerManager.loadItemOrdered(musicManager, trackUrl, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {

                // --- FIX for "Idle Player" Bug ---
                // Try to start the track. If the player is busy (returns false), then queue it.
                if (!musicManager.player.startTrack(track, true)) {
                    musicManager.scheduler.queue(track);
                }
                // --- END FIX ---

                result.tracks.add(track);
                result.message = "Added to queue: " + track.getInfo().title;
                callback.accept(new TrackResult(TrackResult.Status.TRACK_LOADED, track, null, null));
                //musicManager.scheduler.updateQueueSnapshot(); // Call snapshot update
            }


            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                if (playlist.isSearchResult()) {
                    AudioTrack firstTrack = playlist.getTracks().get(0);

                    // --- FIX for "Idle Player" Bug ---
                    if (!musicManager.player.startTrack(firstTrack, true)) {
                        musicManager.scheduler.queue(firstTrack);
                    }
                    // --- END FIX ---

                    result.tracks.add(firstTrack);
                    result.message = "Search result added to queue: " + firstTrack.getInfo().title;
                    callback.accept(new TrackResult(TrackResult.Status.SEARCH_RESULT, firstTrack, playlist, null));
                } else {
                    List<AudioTrack> tracks = playlist.getTracks();

                    // --- FIX for "Idle Player" Bug ---
                    // Try to start the first track of the playlist
                    if (!musicManager.player.startTrack(tracks.get(0), true)) {
                        // Player is busy, so just queue the first track
                        musicManager.scheduler.queue(tracks.get(0));
                    }

                    // Queue the rest of the tracks
                    for (int i = 1; i < tracks.size(); i++) {
                        musicManager.scheduler.queue(tracks.get(i));
                        result.tracks.add(tracks.get(i)); // Add to result list
                    }
                    result.tracks.add(tracks.get(0)); // Add the first track to the result list
                    // --- END FIX ---

                    result.message = "Playlist added: " + playlist.getName() +
                            " (" + playlist.getTracks().size() + " tracks)";
                    callback.accept(new TrackResult(TrackResult.Status.PLAYLIST_LOADED, null, playlist, null));
                }
                //musicManager.scheduler.updateQueueSnapshot(); // Call snapshot update
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