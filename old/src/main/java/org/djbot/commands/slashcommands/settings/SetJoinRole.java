package org.djbot.commands.slashcommands.settings;

import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.djbot.Main;
import org.djbot.utils.bot.config.ConfigData;
import org.djbot.utils.discord.category.BotCategories;
import org.djbot.utils.discord.helpers.EmbedWrapper;
import java.util.Collections;

public class SetJoinRole extends SlashCommand {
    public SetJoinRole() {
        this.name = "setjoinrole";
        this.help = "Set the role automatically assigned when a new user joins";
        this.category = new BotCategories().MusicCat();
        this.options = Collections.singletonList(new OptionData(OptionType.ROLE, "role", "Role to use when a user joins", true));
        this.ownerCommand = true;
    }
    @Override
    protected void execute(SlashCommandEvent slashCommandEvent) {
        OptionMapping roleOption = slashCommandEvent.getOption("role");
        Role joinRole = roleOption.getAsRole();
        String joinRoleId = joinRole.getId();

        ConfigData configData = Main.getConfigData();
        boolean isEphemeral = false;
        if (configData.isEphemeral(slashCommandEvent.getGuild().getIdLong())) {
            isEphemeral = configData.getEphemeral(slashCommandEvent.getGuild().getIdLong());
        }
        configData.setGuildJoinRole(slashCommandEvent.getGuild().getIdLong(), joinRoleId);
        slashCommandEvent.replyEmbeds(EmbedWrapper.createInfo(joinRole.getAsMention()+" set as join role", new EmbedWrapper().GetGuildEmbedColor(slashCommandEvent.getGuild()))).setEphemeral(isEphemeral).queue();
    }
}
