package org.djbot.events;

import net.dv8tion.jda.api.entities.Mentions;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.SelfUser;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.djbot.utils.bot.gemini.GeminiClient;

public class MessageReceivedHandler extends ListenerAdapter {
    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) return;
        Message message = event.getMessage();
        if (message.getMentions().isMentioned(event.getJDA().getSelfUser())) {
            sendGemini(event.getJDA().getSelfUser(), message);
        }
    }

    public void sendGemini(SelfUser botSelfUser, Message message) {
        String mention = botSelfUser.getAsMention();
        Mentions mentions = message.getMentions();
        if (message.getMentions().isMentioned(botSelfUser)) {
            if (!mentions.mentionsEveryone()) {
                if (!message.getContentRaw().trim().equals(mention)) {
                    String reply = null;
                    try {
                        reply = new GeminiClient().chat(message, botSelfUser);
                        message.getChannel().sendMessage(reply).queue();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
    }
}
