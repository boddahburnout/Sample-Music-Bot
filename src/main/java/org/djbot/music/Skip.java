package org.djbot.music;

import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import org.djbot.Utils.helper.EmbedWrapper;
import org.djbot.Utils.music.GuildMusicManager;
import org.djbot.Utils.music.PlayerManager;
import org.djbot.category.BotCategories;

import java.util.Objects;

public class Skip extends SlashCommand {
    private final boolean isEphemeral = true;
    public Skip() {
        this.name = "skip";
        this.help = "Skip the playing song";
        this.category = new BotCategories().MusicCat();
    }

    @Override
    protected void execute(SlashCommandEvent e) {
        Guild guild = e.getGuild();
        Member member = e.getMember();
        PlayerManager playerManager = PlayerManager.getInstance();
        GuildMusicManager guildMusicManager = playerManager.getGuildMusicManager(guild.getIdLong());
        boolean state = Objects.requireNonNull(member.getVoiceState()).inAudioChannel();
        boolean isDeaf = member.getVoiceState().isDeafened();
        if (!state) { e.replyEmbeds(EmbedWrapper.createInfo("Join the voice channel first!", new EmbedWrapper().GetGuildEmbedColor(guild))).setEphemeral(isEphemeral).queue(); }
        if (isDeaf) { e.replyEmbeds(EmbedWrapper.createInfo("Your not even listening your opinion does not matter", new EmbedWrapper().GetGuildEmbedColor(guild))).setEphemeral(isEphemeral).queue(); }
        if (guildMusicManager.player.getPlayingTrack() == null) { e.replyEmbeds(EmbedWrapper.createInfo("We are cutting you off for tonight, nothing is even playing.", new EmbedWrapper().GetGuildEmbedColor(guild))).setEphemeral(isEphemeral).queue(); }
        e.replyEmbeds(EmbedWrapper.createInfo(guildMusicManager.player.getPlayingTrack().getInfo().title + " has been skipped!", new EmbedWrapper().GetGuildEmbedColor(guild))).setEphemeral(isEphemeral).queue();
        if (guildMusicManager.scheduler.getQueue().isEmpty()) {
            guildMusicManager.player.stopTrack();
        } else {
            guildMusicManager.scheduler.nextTrack();
        }
    }
}
