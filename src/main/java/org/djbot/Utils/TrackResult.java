package org.djbot.Utils;

import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

public class TrackResult {
    public enum Status {
        TRACK_LOADED,
        PLAYLIST_LOADED,
        NO_MATCHES,
        LOAD_FAILED,
        SEARCH_RESULT
    }

    private final Status status;
    private final AudioTrack track;
    private final AudioPlaylist playlist;
    private final FriendlyException exception;

    public TrackResult(Status status, AudioTrack track, AudioPlaylist playlist, FriendlyException exception) {
        this.status = status;
        this.track = track;
        this.playlist = playlist;
        this.exception = exception;
    }

    public Status getStatus() { return status; }
    public AudioTrack getTrack() { return track; }
    public AudioPlaylist getPlaylist() { return playlist; }
    public FriendlyException getException() { return exception; }
}
