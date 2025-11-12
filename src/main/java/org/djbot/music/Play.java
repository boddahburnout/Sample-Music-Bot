package org.djbot.music;

import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
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

public class Play extends SlashCommand {
    private final boolean isEphemeral = true;
    public Play() {
        this.name = "play";
        this.help = "Play music";
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
            e.replyEmbeds(EmbedWrapper.createInfo("Ask me to join the voice channel first!", new EmbedWrapper().GetGuildEmbedColor(guild))).setEphemeral(false).queue();
            return;
        }
        try {
            if (args.startsWith("http")) {
                playerManager.loadAndPlay(guild.getIdLong(), args, loadResult -> {
                    TrackResult.Status status = loadResult.getStatus();
                    loadResult.response(status, e, loadResult.getTrack(), loadResult.getPlaylist());
                });
            } else {
                String searchQuery = "ytsearch:" + args;
                GuildMusicManager guildMusicManager = playerManager.getGuildMusicManager(guild.getIdLong());
                guildMusicManager.scheduler.setTextChannel(e.getTextChannel());
                playerManager.loadAndPlay(guild.getIdLong(), searchQuery, loadResult -> {
                    TrackResult.Status status = loadResult.getStatus();
                    loadResult.response(status, e, loadResult.getTrack(), loadResult.getPlaylist());
                });
                int volume = configData.getGuildVolume(guild.getIdLong());
                guildMusicManager.player.setVolume(volume);
            }
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}
