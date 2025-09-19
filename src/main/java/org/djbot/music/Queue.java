package org.djbot.music;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import org.djbot.Utils.EmbedWrapper;
import org.djbot.Utils.PlayerManager;

import java.awt.*;
import java.util.List;

public class Queue extends Command {
    public Queue() {
        this.name = "queue";
        this.help = "List the upcoming songs";
     }
    @Override
    protected void execute(CommandEvent e) {
        Guild guild = e.getGuild();
        TextChannel textChannel = e.getTextChannel();
        PlayerManager playerManager = PlayerManager.getInstance();

        List<AudioTrack> queue = playerManager.getGuildMusicManager(guild.getIdLong())
                .scheduler.getQueue();

        if (queue.isEmpty()) {
            textChannel.sendMessage("The queue is currently empty.").queue();
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

        textChannel.sendMessageEmbeds(new EmbedWrapper().EmbedMessage("Queue", null, null, Color.YELLOW, sb.toString(), null, null, e.getSelfUser().getAvatarUrl(),null)).queue();
    }
}
