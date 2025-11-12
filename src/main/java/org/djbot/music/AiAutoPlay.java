package org.djbot.music;

import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.djbot.Utils.music.GuildMusicManager;
import org.djbot.Utils.music.PlayerManager;
import org.djbot.Utils.music.TrackScheduler;
import org.djbot.category.BotCategories;

import java.util.Collections;

public class AiAutoPlay extends SlashCommand {
    private final boolean isEphemeral = false;
    public AiAutoPlay() {
        this.name = "autoplay";
        this.help = "Check or set the autoplay status";
        this.category = new BotCategories().MusicCat();
        this.options = Collections.singletonList(new OptionData(OptionType.STRING, "toggle", "change Ai Autoplay setting using toggle ", true));
    }

    @Override
    protected void execute(SlashCommandEvent e) {
        PlayerManager playerManager = PlayerManager.getInstance();
        GuildMusicManager guildMusicManager = playerManager.getGuildMusicManager(e.getGuild().getIdLong());
        TrackScheduler trackScheduler = guildMusicManager.scheduler;
        boolean autoplay = trackScheduler.isAutoplay();
        String args = e.getOption("toggle").getAsString();
        if (args.isEmpty()) {
            if (autoplay) {
                e.reply("Autoplay is enabled").setEphemeral(isEphemeral).queue();
            } else {
                e.reply("Autoplay is disabled").setEphemeral(isEphemeral).queue();
            }
        } else {
            if (args.contains("toggle")) {
                trackScheduler.toggleAutoplay();
                autoplay = trackScheduler.isAutoplay();
                if (autoplay) {
                    e.reply("Autoplay has been enabled").setEphemeral(isEphemeral).queue();
                } else {
                    e.reply("Autoplay has been disabled").setEphemeral(isEphemeral).queue();
                }
            }
        }
    }
}
