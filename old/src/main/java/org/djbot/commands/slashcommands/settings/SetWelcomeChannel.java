package org.djbot.commands.slashcommands.settings;

import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.djbot.Main;
import org.djbot.utils.bot.config.ConfigData;
import org.djbot.utils.discord.category.BotCategories;
import org.djbot.utils.discord.helpers.EmbedWrapper;

import java.util.Collections;

public class SetWelcomeChannel extends SlashCommand {
    public SetWelcomeChannel() {
        this.name = "setwelcomechannel";
        this.help = "Set the channel used when welcoming users";
        this.category = new BotCategories().MusicCat();
        this.options = Collections.singletonList(new OptionData(OptionType.CHANNEL, "channel", "channel to use when a user joins", true).setChannelTypes(ChannelType.TEXT));
        this.ownerCommand = true;
    }
    @Override
    protected void execute(SlashCommandEvent slashCommandEvent) {
        OptionMapping channelOption = slashCommandEvent.getOption("channel");
        GuildChannel joinChannel = channelOption.getAsChannel();
        String joinChannelId = joinChannel.getId();

        ConfigData configData = Main.getConfigData();
        boolean isEphemeral = false;
        if (configData.isEphemeral(slashCommandEvent.getGuild().getIdLong())) {
            isEphemeral = configData.getEphemeral(slashCommandEvent.getGuild().getIdLong());
        }
        configData.setGuildWelcomeChannel(slashCommandEvent.getGuild().getIdLong(), joinChannelId);
        slashCommandEvent.replyEmbeds(EmbedWrapper.createInfo(joinChannel.getAsMention()+" set as join channel", new EmbedWrapper().GetGuildEmbedColor(slashCommandEvent.getGuild()))).setEphemeral(isEphemeral).queue();

    }
}
