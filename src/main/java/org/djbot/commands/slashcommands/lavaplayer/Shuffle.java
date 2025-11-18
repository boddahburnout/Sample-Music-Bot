package org.djbot.commands.slashcommands.lavaplayer;

import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.djbot.Main;
import org.djbot.utils.bot.config.ConfigData;
import org.djbot.utils.discord.category.BotCategories;
import org.djbot.utils.discord.helpers.EmbedWrapper;
import org.djbot.utils.music.GuildMusicManager;
import org.djbot.utils.music.PlayerManager;
import org.djbot.utils.music.TrackResult;

import java.util.Collections;
import java.util.Objects;


public class Shuffle extends SlashCommand {

    public Shuffle() {
        this.name = "shuffle";
        this.help = "Shuffle the queue";
        this.category = new BotCategories().MusicCat();
        this.options = Collections.singletonList(new OptionData(OptionType.STRING, "song", "Shuffle a song into the current queue", false));
    }

    @Override
    protected void execute(SlashCommandEvent e) {
        Guild guild = e.getGuild();
        PlayerManager playerManager = PlayerManager.getInstance();
        GuildMusicManager guildMusicManager = playerManager.getGuildMusicManager(guild.getIdLong());
        OptionMapping songArg = e.getOption("song");
        ConfigData configData = Main.getConfigData();

        boolean isEphemeral = false;
        if (configData.isEphemeral(guild.getIdLong())) {
            isEphemeral = configData.isEphemeral(guild.getIdLong());
        }


        // 1. Defer the reply IMMEDIATELY. This sends a "Thinking..." message.
        e.deferReply().setEphemeral(isEphemeral).queue();

        Member member = e.getMember();
        boolean state = Objects.requireNonNull(member.getVoiceState()).inAudioChannel();
        boolean isDeaf = member.getVoiceState().isDeafened();

        if (!state) {
            e.replyEmbeds(EmbedWrapper.createInfo("Join the voice channel first!", new EmbedWrapper().GetGuildEmbedColor(guild))).setEphemeral(isEphemeral).queue();
            return;
        }
        if (isDeaf) {
            e.replyEmbeds(EmbedWrapper.createInfo("Your not even listening your opinion does not matter", new EmbedWrapper().GetGuildEmbedColor(guild))).setEphemeral(isEphemeral).queue();
            return;
        }

        if (guildMusicManager.scheduler.getQueue().isEmpty()) {
            e.getHook().sendMessageEmbeds(EmbedWrapper.createInfo(
                    "There are no songs in the queue",
                    new EmbedWrapper().GetGuildEmbedColor(guild)
            )).queue();
            return;
        }

        if (songArg == null) {
            // This is the simple /shuffle
            guildMusicManager.scheduler.shuffle();

            // 2. Use the "hook" to send your reply
            e.getHook().sendMessageEmbeds(EmbedWrapper.createInfo(
                    "The queue has been shuffled!",
                    new EmbedWrapper().GetGuildEmbedColor(guild))
            ).queue();

        } else {
            String args = songArg.getAsString();
            // This is the /shuffle [song]
            if (!args.startsWith("http")) { args = "ytsearch:" + args; }
            playerManager.loadAndPlay(guild.getIdLong(), args, loadResult -> {

                // We are now inside the asynchronous callback
                TrackResult.Status status = loadResult.getStatus();

                if (status == TrackResult.Status.TRACK_LOADED || status == TrackResult.Status.SEARCH_RESULT) {
                    // 3. The track was loaded. NOW shuffle the queue.
                    guildMusicManager.scheduler.shuffleQueue();

                    // 4. Send ONE final success message.
                    e.getHook().sendMessageEmbeds(EmbedWrapper.createInfo(
                            "Added: **" + loadResult.getTrack().getInfo().title + "** and shuffled the queue! 🔀",
                            new EmbedWrapper().GetGuildEmbedColor(guild))
                    ).queue();

                } else if (status == TrackResult.Status.PLAYLIST_LOADED) {
                    // 3. The playlist was loaded. NOW shuffle.
                    guildMusicManager.scheduler.shuffleQueue();

                    // 4. Send ONE final success message for the playlist.
                    e.getHook().sendMessageEmbeds(EmbedWrapper.createInfo(
                            "Added playlist: **" + loadResult.getPlaylist().getName() + "** and shuffled the queue! 🔀",
                            new EmbedWrapper().GetGuildEmbedColor(guild))
                    ).queue();

                } else {
                    // 5. The load failed. Report the error using the hook.
                    // DO NOT use loadResult.response() as it will try to reply again.
                    //String errorMsg = "Could not load track: " + args;
                    if (status == TrackResult.Status.NO_MATCHES) {
                        //errorMsg = "Nothing found for: " + args;
                    }
                    //e.getHook().sendMessageEmbeds(EmbedWrapper.createInfo(
                    //        errorMsg,
                    //        new EmbedWrapper().GetGuildEmbedColor(guild))
                    //).queue();
                }
            });
            // 6. REMOVED the synchronous e.replyEmbeds(...) from here.
        }
    }
}
