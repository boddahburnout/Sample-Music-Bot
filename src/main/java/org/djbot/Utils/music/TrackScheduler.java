package org.djbot.Utils.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import net.dv8tion.jda.api.entities.Guild; // <-- IMPORT ADDED
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import org.djbot.Utils.Gemini.GeminiClient;
import org.djbot.Utils.helper.EmbedWrapper; // <-- IMPORT ADDED

import java.awt.Color; // <-- IMPORT ADDED
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class TrackScheduler extends AudioEventAdapter {
    private final AudioPlayer player;
    private final BlockingQueue<AudioTrack> queue;
    private boolean autoplay = false;
    private TextChannel textChannel;
    private long guildId;

    // --- NEW FIELDS FOR HISTORY ---
    private final LinkedList<String> trackHistory = new LinkedList<>();
    private final int HISTORY_LIMIT = 20; // Cache the last 20 songs
    // --- END NEW FIELDS ---


    public TrackScheduler(AudioPlayer player, long guildId) {
        this.player = player;
        this.queue = new LinkedBlockingQueue<>();
        this.guildId = guildId;
    }

    /** Add a track to the queue or play immediately if idle */
    public void queue(AudioTrack track) {
        if (!player.startTrack(track, true)) {
            queue.offer(track);
        }
    }

    // --- NEW HISTORY METHODS ---
    /**
     * Adds a track to the history, maintaining the size limit.
     */
    private void addTrackToHistory(AudioTrack track) {
        // Fix for potential NullPointerException if track info is null
        if (track == null || track.getInfo() == null) return;

        String trackInfo = track.getInfo().author + ": " + track.getInfo().title;

        // Add new track to the end
        this.trackHistory.addLast(trackInfo);

        // Remove the oldest track if we're over the limit
        while (this.trackHistory.size() > HISTORY_LIMIT) {
            this.trackHistory.removeFirst();
        }
    }

    /**
     * Gets a copy of the current track history.
     */
    public List<String> getTrackHistory() {
        return new ArrayList<>(this.trackHistory); // Return a copy
    }

    /**
     * Called by PlayerManager to update the history snapshot for Gemini.
     * This is no longer used by Gemini, but could be useful for other commands.
     */
    public void updateQueueSnapshot() {
        // This method seems to be called by PlayerManager, but trackHistory
        // is now built in onTrackStart. We'll leave it in case
        // other parts of your code (not provided) use it.
    }
    // --- END NEW HISTORY METHODS ---


    public void setTextChannel(TextChannel textChannel) {
        this.textChannel = textChannel;
    }

    public void shuffleQueue() {
        List<AudioTrack> tracks = new ArrayList<>(queue);
        queue.clear();

        AudioTrack current = player.getPlayingTrack();
        if (current != null) {
            tracks.add(current.makeClone()); // put it back into pool
        }

        Collections.shuffle(tracks);
        queue.addAll(tracks);

        // start new first track
        if (!queue.isEmpty()) {
            player.startTrack(queue.poll(), false); // Safely stops current and starts next
        }
    }

    /** Start next track in queue */
    public void nextTrack() {
        AudioTrack next = queue.poll();
        if (next != null) {
            player.startTrack(next, false);
        }
    }

    /** Remove track at index from queue */
    public AudioTrack remove(int index) {
        if (index < 0 || index >= queue.size()) return null;
        List<AudioTrack> temp = new ArrayList<>(queue);
        AudioTrack removed = temp.remove(index);
        queue.clear();
        queue.addAll(temp);
        return removed;
    }

    /** Shuffle the queue */
    public void shuffle() {
        List<AudioTrack> temp = new ArrayList<>(queue);
        Collections.shuffle(temp);
        queue.clear();
        queue.addAll(temp);
    }

    public void queueFirst(AudioTrack track) {
        queue.removeIf(t -> t.getIdentifier().equals(track.getIdentifier()));

        List<AudioTrack> temp = new ArrayList<>();
        temp.add(track);       // new first track
        temp.addAll(queue);    // existing tracks
        queue.clear();
        queue.addAll(temp);
    }

    /** Toggle autoplay */
    public void toggleAutoplay() {
        autoplay = !autoplay;
    }

    public boolean isAutoplay() {
        return autoplay;
    }

    public List<AudioTrack> getQueue() {
        return new ArrayList<>(queue);
    }

    public void clearQueue() {
        queue.clear();
    }

    /** Called automatically when a track starts */
    @Override
    public void onTrackStart(AudioPlayer player, AudioTrack track) {
        addTrackToHistory(track);
    }

    /** Called automatically when a track ends */
    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        if (endReason.mayStartNext) {
            nextTrack();
        }

        // --- UPDATED AUTOPLAY LOGIC ---
        if (queue.isEmpty() && autoplay && endReason.mayStartNext) {
            GeminiClient geminiClient = new GeminiClient();
            List<String> history = getTrackHistory();

            if (history.isEmpty()) {
                if (textChannel != null) {
                    textChannel.sendMessage("🎶 Autoplay has no history to base suggestions on.").queue();
                }
                return;
            }

            String[] results = geminiClient.getPlaylistFromHistory(history);

            if (results == null || results.length == 0) {
                if (textChannel != null) {
                    textChannel.sendMessage("🎶 Autoplay tried to run, but Gemini had no suggestions.").queue();
                }
                return;
            }

            // 1. Pre-process all the tracks
            List<String> searchQueries = new ArrayList<>();
            for (String line : results) {
                String suggestion = line.replaceAll("^\\d+\\.\\s*", "").trim();
                String[] trackInfo;
                if (suggestion.contains(":")) {
                    trackInfo = suggestion.split(":", 2);
                } else if (suggestion.contains("-")) {
                    trackInfo = suggestion.split("-", 2);
                } else {
                    continue;
                }
                if (trackInfo.length < 2 || trackInfo[0].isBlank() || trackInfo[1].isBlank()) {
                    continue;
                }
                searchQueries.add(trackInfo[0].trim() + " - " + trackInfo[1].trim());
            }

            if (searchQueries.isEmpty()) {
                return; // No songs were parsable
            }

            // 2. Set up thread-safe counters
            final int totalTracks = searchQueries.size();
            final AtomicInteger processedCount = new AtomicInteger(0);
            final List<String> loadedTitles = Collections.synchronizedList(new ArrayList<>());

            // 3. Loop and load
            for (String searchQuery : searchQueries) {
                String lavaplayerSearchQuery = "ytsearch:" + searchQuery;
                PlayerManager.getInstance().loadAndPlay(guildId, lavaplayerSearchQuery, loadResult -> {

                    // 4. Inside the callback...
                    // --- THIS IS THE FIX ---
                    if (loadResult.getStatus() == TrackResult.Status.TRACK_LOADED || loadResult.getStatus() == TrackResult.Status.SEARCH_RESULT) {
                        // --- END FIX ---
                        loadedTitles.add(loadResult.getTrack().getInfo().title);
                    }

                    // 5. Check if this is the LAST track
                    if (processedCount.incrementAndGet() == totalTracks && !loadedTitles.isEmpty()) {
                        // It is! Send the final summary message.
                        StringBuilder summary = new StringBuilder();
                        summary.append("🎶 Autoplay enabled! Added **").append(loadedTitles.size()).append("** new tracks:\n");
                        for (int i = 0; i < loadedTitles.size(); i++) {
                            summary.append("`").append(i + 1).append(".` ")
                                    .append(loadedTitles.get(i)).append("\n");
                        }

                        // --- FINAL CHANGE ---
                        if (textChannel != null) {
                            // Get the guild from the textChannel
                            Guild guild = textChannel.getGuild();
                            // Get the color
                            Color guildColor = new EmbedWrapper().GetGuildEmbedColor(guild);
                            // Send the embed
                            textChannel.sendMessageEmbeds(EmbedWrapper.createInfo(summary.toString(), guildColor)).queue();
                        }
                        // --- END FINAL CHANGE ---
                    }
                });
            }
        }
    }
}