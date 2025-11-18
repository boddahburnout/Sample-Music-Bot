package org.djbot.commands.slashcommands.gemini;

import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.djbot.Main;
import org.djbot.utils.bot.config.ConfigData;
import org.djbot.utils.discord.helpers.EmbedWrapper;

import java.util.Collections;

public class AiWelcomePrompt extends SlashCommand {
    public AiWelcomePrompt() {
        this.name = "aiwelcomeprompt";
        this.help = "Set the prompt used when generating ai welcome messages";
        this.options = Collections.singletonList(new OptionData(OptionType.STRING, "prompt", "Prompt used when welcoming new members to the server", true));
        this.ownerCommand = true;
    }
    @Override
    protected void execute(SlashCommandEvent slashCommandEvent) {
        OptionMapping promptOption = slashCommandEvent.getOption("prompt");
        ConfigData configData = Main.getConfigData();
        configData.setGuildWelcomePrompt(slashCommandEvent.getGuild().getIdLong(), promptOption.getAsString());
        long guildId = slashCommandEvent.getGuild().getIdLong();
        boolean isEphemeral = false;
        if (configData.isEphemeral(guildId)) {
            isEphemeral = configData.getEphemeral(guildId);
        }
        slashCommandEvent.replyEmbeds(EmbedWrapper.createInfo("Welcome gemini prompt set to "+promptOption.getAsString(), new EmbedWrapper().GetGuildEmbedColor(slashCommandEvent.getGuild()))).setEphemeral(isEphemeral).queue();
    }
}
