package org.djbot.music;

import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.managers.AudioManager;
import org.djbot.utils.discord.helpers.EmbedWrapper;
import org.djbot.utils.discord.category.BotCategories;


import java.util.Objects;

public class Join extends SlashCommand {
    private final boolean isEphemeral = true;
    public Join() {
        this.name = "join";
        this.help = "Join the VC";
        this.category = new BotCategories().MusicCat();
    }

    @Override
    protected void execute(SlashCommandEvent e) {
        Guild guild = e.getGuild();
        Member member = e.getMember();
        VoiceChannel voiceChannel = member.getVoiceState().getChannel().asVoiceChannel();
        boolean connectedChannel = Objects.requireNonNull(member.getVoiceState().inAudioChannel());
        if (!connectedChannel) {
            e.replyEmbeds(EmbedWrapper.createInfo("You are not connected to a voice channel!", new EmbedWrapper().GetGuildEmbedColor(guild))).setEphemeral(isEphemeral).queue();
        } else {
            if (e.getGuild().getSelfMember().getVoiceState().inAudioChannel()) {
                e.replyEmbeds(EmbedWrapper.createInfo("Already connected to the voice channel!", new EmbedWrapper().GetGuildEmbedColor(guild))).setEphemeral(isEphemeral).queue();
            } else {
                AudioManager audioManager = guild.getAudioManager();
                audioManager.openAudioConnection(voiceChannel);
                e.replyEmbeds(EmbedWrapper.createInfo("Connected to the voice channel!", new EmbedWrapper().GetGuildEmbedColor(guild))).setEphemeral(isEphemeral).queue();
            }
        }
    }
}