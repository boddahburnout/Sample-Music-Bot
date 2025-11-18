package org.djbot.commands.slashcommands.settings;

import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.djbot.Main;
import org.djbot.utils.bot.config.ConfigData;
import org.djbot.utils.discord.helpers.EmbedWrapper;

import java.awt.*;
import java.util.Collections;

public class SetColor extends SlashCommand {
    public SetColor() {
        this.name = "setcolor";
        this.help = "Set a hex color to embedded messages";
        this.options = Collections.singletonList(new OptionData(OptionType.STRING, "hexcolor", "Hex color (Example: 0xFFFFFF)", true));
        this.ownerCommand = true;
    }
    @Override
    protected void execute(SlashCommandEvent slashCommandEvent) {
         OptionMapping optionMapping = slashCommandEvent.getOption("hexcolor");
         if (optionMapping != null) {
             ConfigData configData = Main.getConfigData();
             String hexColor = optionMapping.getAsString();
             long guildId = slashCommandEvent.getGuild().getIdLong();
             boolean isEphemeral = false;
             if (configData.isEphemeral(guildId)) {
                 isEphemeral = configData.getEphemeral(guildId);
             }
             try {
                 configData.setGuildColor(slashCommandEvent.getGuild().getIdLong(), hexColor);
                 slashCommandEvent.replyEmbeds(EmbedWrapper.createInfo("Message color has been set", Color.decode(hexColor))).setEphemeral(isEphemeral).queue();
             } catch (NumberFormatException e) {
                 configData.setGuildColor(slashCommandEvent.getGuild().getIdLong(), "0xFFFFFF");
                 slashCommandEvent.replyEmbeds(EmbedWrapper.createInfo("Message color has been set", Color.decode( "0xFFFFFF"))).setEphemeral(isEphemeral).queue();
             }
         }
    }
}
