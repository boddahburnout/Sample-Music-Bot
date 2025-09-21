package org.djbot.music;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import org.djbot.Utils.EmbedWrapper;
import org.djbot.Utils.GuildMusicManager;
import org.djbot.Utils.PlayerManager;
import org.djbot.Utils.TrackResult;
import org.djbot.category.BotCategories;


public class Shuffle extends Command {
    public Shuffle() {
        this.name = "shuffle";
        this.help = "Shuffle the queue";
        this.category = new BotCategories().MusicCat();
    }

    @Override
    protected void execute(CommandEvent e) {
        Guild guild = e.getGuild();
        TextChannel textChannel = e.getTextChannel();
        Member selfMember = e.getSelfMember();
        PlayerManager.getInstance();
        PlayerManager playerManager = PlayerManager.getInstance();
        GuildMusicManager guildMusicManager = playerManager.getGuildMusicManager(guild.getIdLong());
        String args = e.getArgs();
        if (e.getArgs().isEmpty()) {
            guildMusicManager.scheduler.shuffle();
            textChannel.sendMessageEmbeds(new EmbedWrapper().EmbedMessage(guild.getJDA().getSelfUser().getName(), null, null, new EmbedWrapper().GetGuildEmbedColor(guild), "The queue has been shuffled!", null, null, selfMember.getAvatarUrl(), null)).queue();
        } else {
            playerManager.loadAndPlay(guild.getIdLong(), args, loadResult -> {
                TrackResult.Status status = loadResult.getStatus();
                new Play().response(status, textChannel, loadResult.getTrack(), loadResult.getPlaylist());
                guildMusicManager.scheduler.shuffleQueue();
            });
            textChannel.sendMessageEmbeds(new EmbedWrapper().EmbedMessage(
                    guild.getJDA().getSelfUser().getName(),
                    null, null,
                    new EmbedWrapper().GetGuildEmbedColor(guild),
                    "Added to queue and shuffled! 🔀",
                    null, null,
                    guild.getJDA().getSelfUser().getEffectiveAvatarUrl(),
                    null
            )).queue();
        }
    }
}
