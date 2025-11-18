package org.djbot.commands.slashcommands.settings;

import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.djbot.Main;
import org.djbot.utils.bot.config.ConfigData;
import org.djbot.utils.discord.category.BotCategories;
import org.djbot.utils.discord.helpers.EmbedWrapper;

import java.util.Collections;

public class SetEphemeral extends SlashCommand {
    public SetEphemeral() {
        this.name = "setephemeral";
        this.help = "make the bots commands visible/invisible";
        this.category = new BotCategories().AdminCat();
        this.options = Collections.singletonList(new OptionData(OptionType.BOOLEAN, "ephemeral", "set if bot response's are private or not", true));
        this.ownerCommand = true;
    }
    @Override
    protected void execute(SlashCommandEvent event) {
        ConfigData configData = Main.getConfigData();
        boolean isEphemeral = event.getOption("ephemeral").getAsBoolean();
        configData.setEphemeral(event.getGuild().getIdLong(), isEphemeral);;
        event.replyEmbeds(EmbedWrapper.createInfo("Private messages has been set to "+isEphemeral, new EmbedWrapper().GetGuildEmbedColor(event.getGuild()))).setEphemeral(isEphemeral).queue();
    }
}
