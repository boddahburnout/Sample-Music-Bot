package org.djbot.events;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.djbot.Main;

public class GuildJoinHandler extends ListenerAdapter {
    @Override
    public void onGuildJoin(GuildJoinEvent guildJoinEvent) {
        Guild guild = guildJoinEvent.getGuild();
        Main.setGuildDefault(guild);
    }
}
