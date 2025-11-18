package org.djbot.music;

import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.djbot.Main;
import org.djbot.utils.discord.helpers.ConfigData;
import org.djbot.utils.discord.helpers.EmbedWrapper;
import org.djbot.utils.music.GuildMusicManager;
import org.djbot.utils.music.PlayerManager;
import org.djbot.utils.discord.category.BotCategories;

import java.awt.Color;
import java.util.Collections;

public class Volume extends SlashCommand {
    private final boolean isEphemeral = true;

    public Volume() {
        this.name = "volume";
        this.help = "Set or check the player volume";
        this.category = new BotCategories().MusicCat();

        this.options = Collections.singletonList(
                new OptionData(OptionType.INTEGER, "level", "Set volume. Leave blank to check.", false)
        );
    }

    @Override
    protected void execute(SlashCommandEvent e) {
        PlayerManager playerManager = PlayerManager.getInstance();
        Guild guild = e.getGuild();
        if (guild == null) {
            e.replyEmbeds(EmbedWrapper.createError("This command must be used in a server.", Color.RED)).setEphemeral(isEphemeral).queue();
            return;
        }

        GuildMusicManager guildMusicManager = playerManager.getGuildMusicManager(guild.getIdLong());

        Color embedColor = new EmbedWrapper().GetGuildEmbedColor(guild);
        ConfigData configData = Main.getConfigData();

        OptionMapping volumeOption = e.getOption("level");

        if (volumeOption == null) {
            int currentVolume = configData.getGuildVolume(guild.getIdLong());
            e.replyEmbeds(EmbedWrapper.createInfo("Current volume is set to: " + currentVolume, embedColor)).setEphemeral(isEphemeral).queue();

        } else {
            int newVolume = volumeOption.getAsInt();

            if (newVolume < 0 || newVolume > 150) {
                e.replyEmbeds(EmbedWrapper.createError("Volume must be between 0 and 150.", embedColor)).setEphemeral(isEphemeral).queue();
                return;
            }

            configData.setGuildVolume(guild.getIdLong(), newVolume);

            guildMusicManager.player.setVolume(newVolume);

            e.replyEmbeds(EmbedWrapper.createInfo("Volume has been set to: " + newVolume, embedColor)).setEphemeral(isEphemeral).queue();
        }
    }
}