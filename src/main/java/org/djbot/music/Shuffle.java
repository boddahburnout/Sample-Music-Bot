package org.djbot.music;

import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.djbot.Utils.helper.EmbedWrapper;
import org.djbot.Utils.music.GuildMusicManager;
import org.djbot.Utils.music.PlayerManager;
import org.djbot.Utils.music.TrackResult;
import org.djbot.category.BotCategories;

import java.util.Collections;


public class Shuffle extends SlashCommand {
    private final boolean isEphemeral = true;

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


        // 1. Defer the reply IMMEDIATELY. This sends a "Thinking..." message.
        e.deferReply().setEphemeral(isEphemeral).queue();

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
