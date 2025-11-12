package org.djbot.music;

import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.djbot.Utils.helper.EmbedWrapper;
import org.djbot.Utils.music.GuildMusicManager;
import org.djbot.Utils.music.PlayerManager;
import org.djbot.category.BotCategories;

import java.util.Collections;

public class Remove extends SlashCommand {
    private final boolean isEphemeral = true;
    public Remove() {
        this.name = "remove";
        this.help = "Remove a song from the queue";
        this.category = new BotCategories().MusicCat();
        this.options = Collections.singletonList(new OptionData(OptionType.STRING, "target", "Track pos", true));
    }

    @Override
    protected void execute(SlashCommandEvent e) {
        Guild guild = e.getGuild();
        int queuePOS = e.getOption("target").getAsInt();
        GuildMusicManager guildMusicManager = PlayerManager.getInstance().getGuildMusicManager(guild.getIdLong());
        AudioTrack audioTrack = guildMusicManager.scheduler.remove(queuePOS);
        e.replyEmbeds(EmbedWrapper.createInfo(audioTrack.getInfo().title + " has been skipped!", new EmbedWrapper().GetGuildEmbedColor(guild))).setEphemeral(isEphemeral).queue();
    }
}