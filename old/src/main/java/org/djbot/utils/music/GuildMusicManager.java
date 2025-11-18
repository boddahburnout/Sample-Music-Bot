package org.djbot.utils.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import net.dv8tion.jda.api.audio.AudioSendHandler;
import org.djbot.Main;
import org.djbot.utils.musicplayer.PlayerUIManager;

public class GuildMusicManager {
    public final AudioPlayer player;
    public final TrackScheduler scheduler;
    private final AudioPlayerSendHandler sendHandler;
    private final PlayerUIManager uiManager;

    public GuildMusicManager(AudioPlayerManager manager, long guildId) {
        this.player = manager.createPlayer();
        this.scheduler = new TrackScheduler(player, guildId);
        this.player.addListener(scheduler);
        this.sendHandler = new AudioPlayerSendHandler(player);
        this.uiManager = new PlayerUIManager(this, Main.getJdaInstance().getGuildById(guildId));
        this.scheduler.setPlayerUIManager(this.uiManager);
    }

    public AudioSendHandler getSendHandler() {
        return sendHandler;
    }

    public PlayerUIManager getPlayerUIManager() {
        return uiManager;
    }
}
