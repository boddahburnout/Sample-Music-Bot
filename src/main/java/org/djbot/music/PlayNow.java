package org.djbot.music;

import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.djbot.Main;
import org.djbot.Utils.helper.ConfigData;
import org.djbot.Utils.helper.EmbedWrapper;
import org.djbot.Utils.music.GuildMusicManager;
import org.djbot.Utils.music.PlayerManager;
import org.djbot.Utils.music.TrackResult;
import org.djbot.category.BotCategories;

import java.util.Collections;
import java.util.Objects;

public class PlayNow extends SlashCommand {
    private final boolean isEphemeral = true;

    public PlayNow() {
        this.name = "playnow";
        this.help = "Play a song now";
        this.category = new BotCategories().MusicCat();
        this.options = Collections.singletonList(new OptionData(OptionType.STRING, "song", "Name or url of a song", true));
    }

    @Override
    protected void execute(SlashCommandEvent e) {
        Guild guild = e.getGuild();
        Member member = e.getMember();
        Member selfMember = e.getGuild().getSelfMember();
        PlayerManager playerManager = PlayerManager.getInstance();
        boolean state = Objects.requireNonNull(member.getVoiceState()).inAudioChannel();
        boolean selfState = Objects.requireNonNull(selfMember.getVoiceState()).inAudioChannel();
        boolean isDeaf = member.getVoiceState().isDeafened();
        String args = e.getOption("song").getAsString();
        ConfigData configData = Main.getConfigData();
        if (!state) {
            e.replyEmbeds(EmbedWrapper.createInfo("Join the voice channel first!", new EmbedWrapper().GetGuildEmbedColor(guild))).setEphemeral(isEphemeral).queue();
            return;
        }
        if (isDeaf) {
            e.replyEmbeds(EmbedWrapper.createInfo("Your not even listening your opinion does not matter", new EmbedWrapper().GetGuildEmbedColor(guild))).setEphemeral(isEphemeral).queue();
            return;
        }
        if (!selfState) {
            e.replyEmbeds(EmbedWrapper.createInfo("Ask me to join the voice channel first!", new EmbedWrapper().GetGuildEmbedColor(guild))).setEphemeral(isEphemeral).queue();
            return;
        }
        //AudioTrack currentTrack = playerManager.getGuildMusicManager(guild.getIdLong()).player.getPlayingTrack();
        try {
            if (!args.startsWith("http")) {
                args = "ytsearch:" + args;
            }

            GuildMusicManager guildMusicManager = playerManager.getGuildMusicManager(guild.getIdLong());
            guildMusicManager.scheduler.setTextChannel(e.getTextChannel());

            playerManager.loadAndPlayNow(guild.getIdLong(), args, loadResult -> {
                TrackResult.Status status = loadResult.getStatus();
                loadResult.response(status, e, loadResult.getTrack().makeClone(), loadResult.getPlaylist());
            });

            //if (currentTrack != null) {
            //    guildMusicManager.scheduler.queueFirst(currentTrack.makeClone());
            //}

            int volume = configData.getGuildVolume(guild.getIdLong());
            guildMusicManager.player.setVolume(volume);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}
