package org.djbot.Utils;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class TrackScheduler extends AudioEventAdapter {
    private final AudioPlayer player;
    private final BlockingQueue<AudioTrack> queue;
    private boolean autoplay = false;

    public TrackScheduler(AudioPlayer player) {
        this.player = player;
        this.queue = new LinkedBlockingQueue<>();
    }

    /** Add a track to the queue or play immediately if idle */
    public void queue(AudioTrack track) {
        if (!player.startTrack(track, true)) {
            queue.offer(track);
        }
    }

    /** Start next track in queue */
    public void nextTrack() {
        AudioTrack next = queue.poll();
        if (next != null) {
            player.startTrack(next, false);
        } else if (autoplay) {
            // TODO: implement autoplay logic (load related track)
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

    /** Called automatically when a track ends */
    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        if (endReason.mayStartNext) {
            nextTrack();
        }
    }
}
