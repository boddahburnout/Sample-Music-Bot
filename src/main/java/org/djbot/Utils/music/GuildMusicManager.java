package org.djbot.Utils.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import net.dv8tion.jda.api.audio.AudioSendHandler;
import org.djbot.Utils.handlers.AudioPlayerSendHandler;

public class GuildMusicManager {
    public final AudioPlayer player;
    public final TrackScheduler scheduler;
    private final AudioPlayerSendHandler sendHandler;

    public GuildMusicManager(AudioPlayerManager manager, long guildId) {
        this.player = manager.createPlayer();
        this.scheduler = new TrackScheduler(player, guildId);
        this.player.addListener(scheduler);
        this.sendHandler = new AudioPlayerSendHandler(player);
    }

    public AudioSendHandler getSendHandler() {
        return sendHandler;
    }
}
