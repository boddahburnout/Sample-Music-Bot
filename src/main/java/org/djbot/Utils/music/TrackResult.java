package org.djbot.Utils.music;

import com.jagrosh.jdautilities.command.SlashCommandEvent;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import org.djbot.Utils.helper.EmbedWrapper;
import org.djbot.Utils.helper.Thumbnail;

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
    private final boolean isEphemeral = true;

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


    public void response(TrackResult.Status status, SlashCommandEvent slashCommandEvent, AudioTrack audioTrack, AudioPlaylist audioPlaylist) {
        if (status == TrackResult.Status.LOAD_FAILED) {
            slashCommandEvent.replyEmbeds(EmbedWrapper.createInfo("Could not play the Requested track", new EmbedWrapper().GetGuildEmbedColor(slashCommandEvent.getGuild()))).setEphemeral(isEphemeral).queue();
        }
        if (status == TrackResult.Status.PLAYLIST_LOADED) {
            slashCommandEvent.replyEmbeds(EmbedWrapper.createInfo("Added playlist: **" + audioPlaylist.getName() + "** with **" + audioPlaylist.getTracks().size() + "** tracks.", new EmbedWrapper().GetGuildEmbedColor(slashCommandEvent.getGuild()))).setEphemeral(isEphemeral).queue();
        }
        if (status == TrackResult.Status.NO_MATCHES) {
            slashCommandEvent.replyEmbeds(EmbedWrapper.createInfo("Nothing found by " + audioTrack.getInfo().uri, new EmbedWrapper().GetGuildEmbedColor(slashCommandEvent.getGuild()))).setEphemeral(isEphemeral).queue();
        }
        if (status == TrackResult.Status.TRACK_LOADED) {
            slashCommandEvent.replyEmbeds(EmbedWrapper.createTrackLoaded(slashCommandEvent.getGuild().getSelfMember().getEffectiveName(),audioTrack.getInfo().title,audioTrack.getInfo().uri,audioTrack.getInfo().author, new Thumbnail().Thumbnail(audioTrack),new EmbedWrapper().GetGuildEmbedColor(slashCommandEvent.getGuild()))).setEphemeral(isEphemeral).queue();
        }
        if (status == TrackResult.Status.SEARCH_RESULT) {
            slashCommandEvent.replyEmbeds(EmbedWrapper.createTrackLoaded(slashCommandEvent.getGuild().getSelfMember().getEffectiveName(),audioTrack.getInfo().title,audioTrack.getInfo().uri, audioTrack.getInfo().author, new Thumbnail().Thumbnail(audioTrack),new EmbedWrapper().GetGuildEmbedColor(slashCommandEvent.getGuild()))).setEphemeral(isEphemeral).queue();
        }
    }
}
