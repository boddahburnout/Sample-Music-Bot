package org.djbot.music;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import org.djbot.Utils.EmbedWrapper;
import org.djbot.Utils.GuildMusicManager;
import org.djbot.Utils.PlayerManager;
import org.djbot.Utils.Thumbnail;

import java.awt.*;

public class Remove extends Command {
    public Remove() {
        this.name = "remove";
        this.help = "Remove a song from the queue";
    }

    @Override
    protected void execute(CommandEvent e) {
        Guild guild = e.getGuild();
        TextChannel channel = e.getTextChannel();
        Member selfMember = e.getSelfMember();
        String[] args = e.getArgs().split(" ");
        int queuePOS = Integer.parseInt(args[0]);
        GuildMusicManager guildMusicManager = PlayerManager.getInstance().getGuildMusicManager(guild.getIdLong());
        AudioTrack audioTrack = guildMusicManager.scheduler.remove(queuePOS);
        channel.sendMessageEmbeds(new EmbedWrapper().EmbedMessage(selfMember.getEffectiveName(), null, null, new EmbedWrapper().GetGuildEmbedColor(guild),audioTrack.getInfo().title + " has been skipped!",null,null,new Thumbnail().Thumbnail(audioTrack),null)).queue();
    }
}