package org.djbot.commands.slashcommands.gemini;

import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.djbot.Main;
import org.djbot.utils.bot.config.ConfigData;
import org.djbot.utils.discord.helpers.EmbedWrapper;
import org.djbot.utils.music.GuildMusicManager;
import org.djbot.utils.music.PlayerManager;
import org.djbot.utils.music.TrackScheduler;
import org.djbot.utils.discord.category.BotCategories;

import java.util.Collections;

public class AiAutoPlay extends SlashCommand {
    public AiAutoPlay() {
        this.name = "autoplay";
        this.help = "Check or set the autoplay status";
        this.category = new BotCategories().MusicCat();
        this.options = Collections.singletonList(new OptionData(OptionType.STRING, "toggle", "change Ai Autoplay setting using toggle ", false));
    }

    @Override
    protected void execute(SlashCommandEvent e) {
        PlayerManager playerManager = PlayerManager.getInstance();
        GuildMusicManager guildMusicManager = playerManager.getGuildMusicManager(e.getGuild().getIdLong());
        TrackScheduler trackScheduler = guildMusicManager.scheduler;
        boolean autoplay = trackScheduler.isAutoplay();
        OptionMapping option = e.getOption("toggle");
        ConfigData configData = Main.getConfigData();
        long guildId = e.getGuild().getIdLong();
        boolean isEphemeral = false;
        if (configData.isEphemeral(guildId)) {
            isEphemeral = configData.getEphemeral(guildId);
        }
        if (option == null) {
            if (autoplay) {
                e.replyEmbeds(EmbedWrapper.createInfo("Autoplay is Enabled", new EmbedWrapper().GetGuildEmbedColor(e.getGuild()))).setEphemeral(isEphemeral).queue();
            } else {
                e.replyEmbeds(EmbedWrapper.createInfo("Autoplay is disabled", new EmbedWrapper().GetGuildEmbedColor(e.getGuild()))).setEphemeral(isEphemeral).queue();
            }
        } else {
            String args = option.getAsString();
            if (args.contains("toggle")) {
                trackScheduler.toggleAutoplay();
                autoplay = trackScheduler.isAutoplay();
                if (autoplay) {
                    e.replyEmbeds(EmbedWrapper.createInfo("Autoplay has been enabled", new EmbedWrapper().GetGuildEmbedColor(e.getGuild()))).setEphemeral(isEphemeral).queue();
                } else {
                    e.replyEmbeds(EmbedWrapper.createInfo("Autoplay has been disabled", new EmbedWrapper().GetGuildEmbedColor(e.getGuild()))).setEphemeral(isEphemeral).queue();
                }
            }
        }
    }
}
