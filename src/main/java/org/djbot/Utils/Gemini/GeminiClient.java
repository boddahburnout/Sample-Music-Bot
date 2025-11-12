package org.djbot.Utils.Gemini;

import com.google.genai.errors.ServerException;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.SelfUser;
import org.djbot.Main;
import org.djbot.Utils.music.PlayerManager;
import com.google.genai.types.*;
import com.google.genai.Client;

import java.util.List;

public class GeminiClient {
    private final String API_KEY = Main.getConfigData().getGeminiToken();
    public String chat(Message message, SelfUser selfUser) throws Exception {
        Content systemInstruction = Content.fromParts(Part.fromText("You are a Discord Bot, that plays music. Your name is Budtender Bot. The creator of the discord bot is Darth Kota. Your role as an AI in the discord should be to chat with an entertain users, Keep in mind that you only have 2000 characters to respond with and that discord counts whitespace such as space and new line"));
        String userMessage = message.getContentRaw().replace(selfUser.getAsMention(), "").trim();
        return sendResponse(systemInstruction, userMessage);
    }

    public String[] getPlaylistFromHistory(List<String> trackHistory) {
        StringBuilder promptBuilder = new StringBuilder();

        if (trackHistory != null && !trackHistory.isEmpty()) {
            promptBuilder.append("Here are the songs the user has been listening to:\n");
            for (String track : trackHistory) {
                promptBuilder.append(track).append("\n");
            }
        } else {
            promptBuilder.append("The user hasn't played any songs yet, suggest some popular tracks.");
        }

        Content systemInstruction = Content.fromParts(
                Part.fromText("Respond with 10 song suggestions in format Author: Title, numbered 1-10. "
                        + "Keep the vibe similar to the songs given. Only return suggestions, no extra text.")
        );

        String response = sendResponse(systemInstruction, promptBuilder.toString());

        if (response == null || response.isBlank()) {
            return null;
        }
        return response.split("\\r?\\n");
    }


    public String[] getPlaylistFromPrompt(String message) {
        // We *only* send the user's prompt, no history.
        String prompt = "User's playlist request: '" + message + "'";

        Content systemInstruction = Content.fromParts(
                Part.fromText("You will be given a user's description of a playlist. "
                        + "Respond with 10 song suggestions in format Author: Title, numbered 1-10. "
                        + "Use *only* the user's prompt to generate the list. "
                        + "Only return suggestions, no extra text.")
        );

        String response = sendResponse(systemInstruction, prompt);

        if (response == null || response.isBlank()) {
            return null;
        }
        return response.split("\\r?\\n");
    }

    // ... (keep your other methods: chat, welcome, sendResponse)


    public String geminiWelcome(Member member) {
        Content systemInstruction = Content.fromParts(
                Part.fromText("You are a discord bot made for the server the Deathstar. Your creator's name is darth kota. In the request sent to you here I want you to generate a creative, Starwars themed welcome messages to the new user. I will include info about the user in the attached message, Mention is how you ping the member and the name is just their name in plain text"));

        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("Mention: "+ member.getAsMention()+"\n");
        stringBuilder.append("Name: "+ member.getEffectiveName());

        String response = sendResponse(systemInstruction, stringBuilder.toString());
        return response;
    }

    public String sendResponse(Content systemInstruction, String userMessage) {
        Client client = Client.builder().apiKey(API_KEY)
                .build();
        GenerateContentConfig config =
                GenerateContentConfig.builder()
                        //.thinkingConfig(ThinkingConfig.builder().thinkingBudget(0))
                        .systemInstruction(systemInstruction)
                        .build();
            GenerateContentResponse response =
                    client.models.generateContent("gemini-2.5-flash", userMessage, config);
            return response.text();
    }
}
