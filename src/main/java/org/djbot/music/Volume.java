package org.djbot.music;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import org.djbot.Utils.EmbedWrapper;
import org.djbot.Utils.GuildMusicManager;
import org.djbot.Utils.PlayerManager;
import org.djbot.category.BotCategories;
import org.djbot.config.ConfigManager;
import org.simpleyaml.configuration.file.YamlFile;

public class Volume extends Command {
    public Volume() {
        this.name = "volume";
        this.help = "Set the global volume";
        this.category = new BotCategories().MusicCat();
    }
    @Override
    protected void execute(CommandEvent e) {
        PlayerManager playerManager = PlayerManager.getInstance();
        Guild guild = e.getGuild();
        TextChannel textChannel = e.getTextChannel();
        GuildMusicManager guildMusicManager = playerManager.getGuildMusicManager(guild.getIdLong());
        YamlFile botConfig = (new ConfigManager()).accessConfig();
        Integer volume = botConfig.getInt("Settings.Guilds."+guild.getId()+".Volume");
        String[] args = e.getArgs().split(" ");
        if (e.getArgs().isEmpty()) {
            textChannel.sendMessageEmbeds(new EmbedWrapper().EmbedMessage("Volume", null, null, new EmbedWrapper().GetGuildEmbedColor(guild), "Volume is set to "+ volume, null,null,e.getSelfUser().getAvatarUrl(),null)).queue();
            return;
        }
        botConfig.set("Settings.Guilds."+guild.getId()+".Volume", args[0]);
        guildMusicManager.player.setVolume(Integer.parseInt(args[0]));
        textChannel.sendMessageEmbeds(new EmbedWrapper().EmbedMessage("Volume set", null, null, new EmbedWrapper().GetGuildEmbedColor(guild), "Volume has been set to "+args[0], null,null,e.getSelfUser().getAvatarUrl(),null)).queue();
    }
}
