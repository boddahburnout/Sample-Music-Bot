package org.djbot.Utils.helper;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

public class Thumbnail {
    public String Thumbnail(AudioTrack track) {
    String uri = track.getInfo().uri; // e.g. https://www.youtube.com/watch?v=abcd1234
    String videoId = null;

    if (uri != null && uri.contains("youtube.com/watch?v=")) {
        videoId = uri.substring(uri.indexOf("v=") + 2);
        int amp = videoId.indexOf("&");
        if (amp != -1) {
            videoId = videoId.substring(0, amp);
        }
    } else if (uri != null && uri.contains("youtu.be/")) {
        videoId = uri.substring(uri.indexOf("youtu.be/") + 9);
        int q = videoId.indexOf("?");
        if (q != -1) {
            videoId = videoId.substring(0, q);
        }
    }

    return videoId != null
            ? "https://img.youtube.com/vi/" + videoId + "/hqdefault.jpg"
            : null;
    }
}
