package org.djbot.music;

import com.google.api.services.youtube.model.SearchResult;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import org.djbot.Utils.*;
import org.djbot.category.BotCategories;
import org.djbot.config.ConfigManager;
import org.simpleyaml.configuration.file.YamlFile;
import java.util.List;
import java.util.Objects;

public class PlayNow extends Command {
    public PlayNow() {
        this.name = "playnow";
        this.help = "Play a song now before the current queue";
        this.category = new BotCategories().MusicCat();
    }
    @Override
    protected void execute(CommandEvent e) {
        Guild guild = e.getGuild();
        TextChannel textChannel = e.getTextChannel();
        Member member = e.getMember();
        PlayerManager playerManager = PlayerManager.getInstance();
        GuildMusicManager guildMusicManager = playerManager.getGuildMusicManager(guild.getIdLong());

        boolean state = Objects.requireNonNull(member.getVoiceState()).inAudioChannel();
        String args = e.getArgs();

        if (!state) {
            textChannel.sendMessageEmbeds(new EmbedWrapper().EmbedMessage(
                    guild.getJDA().getSelfUser().getName(),
                    null, null,
                    new EmbedWrapper().GetGuildEmbedColor(guild),
                    "Join the voice channel first!",
                    null, null,
                    guild.getJDA().getSelfUser().getEffectiveAvatarUrl(),
                    null)).queue();
            return;
        }

        // Save the currently playing track if any
        AudioTrack currentTrack = guildMusicManager.player.getPlayingTrack();
        if (currentTrack != null) {
            currentTrack = currentTrack.makeClone();
            //guildMusicManager.player.stopTrack();
            guildMusicManager.scheduler.getQueue().remove(currentTrack);
        }

        if (args.startsWith("http")) {
            // Play the link immediately
            AudioTrack finalCurrentTrack = currentTrack;
            playerManager.loadAndPlayNow(guild.getIdLong(), args, loadResult -> {
                TrackResult.Status status = loadResult.getStatus();
                new Play().response(status, textChannel, loadResult.getTrack(), loadResult.getPlaylist());

                // Re-add the old track at the top of the queue
                if (finalCurrentTrack != null) {
                    guildMusicManager.scheduler.queueFirst(finalCurrentTrack.makeClone());
                }
            });
        } else {
            // Use YouTube search
            YouTubeSearcher yt = new YouTubeSearcher();
            List<SearchResult> results = yt.search(args, 5);

            if (results != null && !results.isEmpty()) {
                SearchResult firstResult = results.get(0);
                String videoId = firstResult.getId().getVideoId();
                String videoUrl = "https://www.youtube.com/watch?v=" + videoId;

                AudioTrack finalCurrentTrack = currentTrack;
                playerManager.loadAndPlayNow(guild.getIdLong(), videoUrl, loadResult -> {
                    TrackResult.Status status = loadResult.getStatus();
                    new Play().response(status, textChannel, loadResult.getTrack(), loadResult.getPlaylist());

                    // Re-add the old track at the top of the queue
                    if (finalCurrentTrack != null) {
                        guildMusicManager.scheduler.queueFirst(finalCurrentTrack.makeClone());
                    }
                });
            }
        }

        // Ensure safe volume setting
        YamlFile botConfig = new ConfigManager().accessConfig();
        int volume = botConfig.getInt("Settings.Guilds." + guild.getId() + ".Volume", 50);
        guildMusicManager.player.setVolume(volume);
    }
}
