package org.djbot.commands.textcommands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.ApplicationInfo;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import org.djbot.utils.discord.helpers.EmbedWrapper;

import java.awt.*;
import java.util.List;
import java.util.Set;


public class Invite extends Command {
    public Invite() {
        this.help = "Get an invite link to add the bot to your guild";
        this.category = new Category("User");
        this.name = "invite";
        this.guildOnly = false;
    }

    @Override
    protected void execute(CommandEvent event) {
        JDA jda = event.getJDA();
        MessageChannel channel = event.getChannel();
        Color color = new EmbedWrapper().GetGuildEmbedColor(event.getGuild());

        List<Permission> perms = List.of(
                Permission.VIEW_CHANNEL,
                Permission.MESSAGE_SEND,
                Permission.MESSAGE_EMBED_LINKS,
                Permission.VOICE_CONNECT,
                Permission.VOICE_SPEAK,
                Permission.USE_APPLICATION_COMMANDS,
                Permission.MANAGE_EVENTS
        );

        Set<String> scopes = Set.of("bot", "applications.commands");

        ApplicationInfo appInfo = jda.retrieveApplicationInfo().complete();
        String inviteUrl = appInfo.setRequiredScopes(scopes).getInviteUrl(perms);
        channel.sendMessageEmbeds(EmbedWrapper.createInfo("You can invite me with \n"+ inviteUrl, color)).queue();
    }
}
