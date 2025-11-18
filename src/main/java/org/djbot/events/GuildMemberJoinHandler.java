package org.djbot.events;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.djbot.Main;
import org.djbot.utils.bot.config.ConfigData;
import org.djbot.utils.bot.gemini.GeminiClient;
import org.djbot.utils.discord.helpers.EmbedWrapper;
import org.json.simple.parser.ParseException;

import java.awt.*;
import java.io.IOException;

public class GuildMemberJoinHandler extends ListenerAdapter implements EventListener {
    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent guildMemberJoinEvent) {
        Guild guild = guildMemberJoinEvent.getGuild();
        Member member = guildMemberJoinEvent.getMember();
        try {
            welcomeMember(guild, member);
        } catch (IOException | ParseException exception) {
            exception.printStackTrace();
        }
    }
    public void welcomeMember(Guild guild, Member member) throws IOException, ParseException {
        ConfigData configData = Main.getConfigData();
        if (configData.isGuildWelcomeChannel(guild.getIdLong())) {
            TextChannel channel = guild.getTextChannelById(configData.getGuildWelcomeChannel(guild.getIdLong()));
            if (channel != null) {
                Color color = new EmbedWrapper().GetGuildEmbedColor(guild);
                GeminiClient geminiClient = new GeminiClient();
                channel.sendMessageEmbeds(EmbedWrapper.createInfo(
                        geminiClient.geminiWelcome(member, guild.getIdLong()),
                        color,
                        "Welcome to "+guild.getName(),
                        member.getEffectiveAvatarUrl())).queue();
            }
        }

        // ✅ Proper role assignment
        if (configData.isGuildJoinRole(guild.getIdLong())) {
            Role role = guild.getRoleById(configData.getGuildJoinRole(guild.getIdLong()));
            if (role != null) {
                guild.addRoleToMember(member, role).queue(
                        success -> System.out.println(),
                        error -> error.printStackTrace()
                );
            }
        }
    }
}
