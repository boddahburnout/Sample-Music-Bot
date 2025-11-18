package org.djbot.commands.slashcommands.lavaplayer;

import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import net.dv8tion.jda.api.entities.Guild;
import org.djbot.utils.discord.category.BotCategories;
import org.djbot.utils.music.PlayerManager;
import org.djbot.utils.musicplayer.PlayerUIManager;

public class SpawnPlayer extends SlashCommand {

    public SpawnPlayer() {
        this.name = "spawnplayer";
        this.help = "Shows the music player and controls.";
        this.category = new BotCategories().MusicCat();
    }

    @Override
    protected void execute(SlashCommandEvent event) {
        Guild guild = event.getGuild();
        if (guild == null) return;

        PlayerUIManager uiManager = PlayerManager.getInstance().getGuildMusicManager(event.getGuild().getIdLong()).getPlayerUIManager();
        //uiManager.SpawnPlayer(event);
    }
}