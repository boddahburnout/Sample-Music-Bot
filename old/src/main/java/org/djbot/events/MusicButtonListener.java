package org.djbot.events;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.djbot.utils.music.GuildMusicManager;
import org.djbot.utils.music.PlayerManager;
import org.jetbrains.annotations.NotNull;

public class MusicButtonListener extends ListenerAdapter {

    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
        String buttonId = event.getComponentId();
        Guild guild = event.getGuild();

        if (guild == null || !buttonId.startsWith("music:")) {
            return;
        }

        // 1. Acknowledge the interaction with deferEdit().
        // This is a "silent" acknowledgment. It prevents the "Interaction failed"
        // error and lets the scheduler update the message.
        event.deferEdit().queue();

        GuildMusicManager musicManager = PlayerManager.getInstance().getGuildMusicManager(guild.getIdLong());

        switch (buttonId) {
            case "music:playpause":
                boolean isPaused = musicManager.player.isPaused();
                musicManager.player.setPaused(!isPaused);
                // --- MANUAL UI UPDATE ---
                // We still need to manually update for pause/autoplay,
                // as these don't fire a track event.
                if (musicManager.player.getPlayingTrack() != null) {
                    musicManager.getPlayerUIManager().onTrackStart(
                            musicManager.player.getPlayingTrack(),
                            event.getChannel()
                    );
                }
                break;

            case "music:skip":
                musicManager.scheduler.nextTrack();
                // No update needed, scheduler will fire onTrackStart/onQueueEmpty
                break;

            case "music:shuffle": // You added this
                musicManager.scheduler.shuffleQueue();
                // No update needed, scheduler will fire onTrackStart
                break;

            case "music:stop":
                musicManager.scheduler.clearQueue();
                musicManager.player.stopTrack();
                // No update needed, scheduler will fire onTrackEnd -> onQueueEmpty
                break;

            case "music:autoplay":
                musicManager.scheduler.toggleAutoplay();
                // --- MANUAL UI UPDATE ---
                if (musicManager.player.getPlayingTrack() != null) {
                    musicManager.getPlayerUIManager().onTrackStart(
                            musicManager.player.getPlayingTrack(),
                            event.getChannel()
                    );
                }
                break;
        }

        // --- REMOVED ---
        // We no longer edit the hook here. The scheduler handles all updates.
        // --- END REMOVED ---
    }
}