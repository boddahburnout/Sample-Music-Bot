package org.djbot.music;

import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.entities.Guild;
import org.djbot.Utils.helper.EmbedWrapper;
import org.djbot.Utils.music.PlayerManager;
import org.djbot.category.BotCategories;

import java.util.List;

public class Queue extends SlashCommand {
    private final boolean isEphemeral = true;
    public Queue() {
        this.name = "queue";
        this.help = "List the upcoming songs";
        this.category = new BotCategories().MusicCat();
     }
    @Override
    protected void execute(SlashCommandEvent e) {
        Guild guild = e.getGuild();
        PlayerManager playerManager = PlayerManager.getInstance();

        List<AudioTrack> queue = playerManager.getGuildMusicManager(guild.getIdLong())
                .scheduler.getQueue();

        if (queue.isEmpty()) {
            e.reply("The queue is currently empty.").setEphemeral(isEphemeral).queue();
            return;
        }

        int trackCount = Math.min(queue.size(), 10); // Show up to 10 songs
        List<AudioTrack> tracks = queue.stream().limit(trackCount).toList();

        StringBuilder sb = new StringBuilder("**Up Next:**\n");
        for (int i = 0; i < tracks.size(); i++) {
            AudioTrack track = tracks.get(i);
            sb.append("`")
                    .append(i + 1)
                    .append(".` ")
                    .append(track.getInfo().title)
                    .append(" - ")
                    .append(track.getInfo().author)
                    .append("\n");
        }

        if (queue.size() > trackCount) {
            sb.append("... and ").append(queue.size() - trackCount).append(" more.");
        }
        e.replyEmbeds(EmbedWrapper.createInfo(sb.toString(), new EmbedWrapper().GetGuildEmbedColor(guild))).setEphemeral(isEphemeral).queue();
    }
}
