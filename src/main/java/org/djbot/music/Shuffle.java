package org.djbot.music;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import org.djbot.Utils.EmbedWrapper;
import org.djbot.Utils.GuildMusicManager;
import org.djbot.Utils.PlayerManager;

public class Shuffle extends Command {
    public Shuffle() {
        this.name = "shuffle";
        this.help = "Shuffle the queue";
    }
    @Override
    protected void execute(CommandEvent e) {
        Guild guild = e.getGuild();
        TextChannel textChannel = e.getTextChannel();
        Member selfMember = e.getSelfMember();
        GuildMusicManager guildMusicManager = PlayerManager.getInstance().getGuildMusicManager(guild.getIdLong());
        String args = e.getArgs();
        guildMusicManager.scheduler.shuffle();
        textChannel.sendMessageEmbeds(new EmbedWrapper().EmbedMessage(guild.getJDA().getSelfUser().getName(), null, null, new EmbedWrapper().GetGuildEmbedColor(guild), "The queue has been shuffled!", null, null, selfMember.getAvatarUrl(), null)).queue();
    }
}
