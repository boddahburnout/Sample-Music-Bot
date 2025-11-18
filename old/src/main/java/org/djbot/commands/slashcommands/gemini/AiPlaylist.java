package org.djbot.commands.slashcommands.gemini;

import com.google.genai.errors.ServerException;
import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.djbot.Main;
import org.djbot.utils.bot.config.ConfigData;
import org.djbot.utils.bot.gemini.GeminiClient;
import org.djbot.utils.discord.helpers.EmbedWrapper; // <-- IMPORT ADDED
import org.djbot.utils.music.PlayerManager;
import org.djbot.utils.music.TrackResult;
import org.djbot.utils.discord.category.BotCategories;

import java.awt.Color; // <-- IMPORT ADDED
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class AiPlaylist extends SlashCommand {
    public AiPlaylist() {
        this.name = "aiplaylist";
        this.help = "Generates a playlist from a prompt, or from your history if no prompt is given.";
        this.category = new BotCategories().MusicCat();
        this.options = Collections.singletonList(new OptionData(OptionType.STRING, "prompt", "Description of a playlist to be generated", false));
    }

    @Override
    protected void execute(SlashCommandEvent e) {
        GeminiClient geminiClient = new GeminiClient();
        ConfigData configData = Main.getConfigData();
        long guildId = e.getGuild().getIdLong();
        boolean isEphemeral = false;
        if (configData.isEphemeral(guildId)) {
            isEphemeral = configData.getEphemeral(guildId);
        }
        e.deferReply().setEphemeral(isEphemeral).queue();

        OptionMapping promptOption = e.getOption("prompt");

        String[] aiPlaylist;
        try {
            if (promptOption != null) {
                String userPrompt = promptOption.getAsString();
                aiPlaylist = geminiClient.getPlaylistFromPrompt(userPrompt);

            } else {
                List<String> history = PlayerManager.getInstance()
                        .getGuildMusicManager(guildId)
                        .scheduler
                        .getTrackHistory();

                if (history.isEmpty()) {
                    e.getHook().sendMessage("You have no listening history. Play some songs or provide a prompt!").setEphemeral(true).queue();
                    return;
                }
                aiPlaylist = geminiClient.getPlaylistFromHistory(history);
            }
        } catch (ServerException exception) {
            throw new RuntimeException(exception);
        }

        if (aiPlaylist == null) {
            e.getHook().sendMessage("Gemini didn't return any suggestions.").setEphemeral(true).queue();
            return;
        }
        List<String> searchQueries = new ArrayList<>();
        for (String line : aiPlaylist) {
            //System.out.println("[Gemini Response]: " + line); // Good for debugging
            String suggestion = line.replaceAll("^\\d+\\.\\s*", "").trim();
            String[] trackInfo;
            if (suggestion.contains(":")) {
                trackInfo = suggestion.split(":", 2);
            } else if (suggestion.contains("-")) {
                trackInfo = suggestion.split("-", 2); // Also check for dash
            } else {
                continue; // No separator found
            }
            if (trackInfo.length < 2 || trackInfo[0].isBlank() || trackInfo[1].isBlank()) {
                continue;
            }
            searchQueries.add(trackInfo[0].trim() + " - " + trackInfo[1].trim());
        }
        if (searchQueries.isEmpty()) {
            e.getHook().sendMessage("Gemini responded, but I couldn't parse any songs.").setEphemeral(true).queue();
            return;
        }
        final int totalTracks = searchQueries.size();
        final AtomicInteger processedCount = new AtomicInteger(0);
        final List<String> loadedTitles = Collections.synchronizedList(new ArrayList<>());
        e.getHook().editOriginal("Loading " + totalTracks + " suggestions from Gemini...").queue();
        for (String searchQuery : searchQueries) {
            String lavaplayerSearchQuery = "ytsearch:" + searchQuery;
            PlayerManager.getInstance().loadAndPlay(guildId, lavaplayerSearchQuery, e.getUser(), loadResult -> {
                if (loadResult.getStatus() == TrackResult.Status.TRACK_LOADED || loadResult.getStatus() == TrackResult.Status.SEARCH_RESULT) {
                    loadedTitles.add(loadResult.getTrack().getInfo().title);
                }

                if (processedCount.incrementAndGet() == totalTracks) {
                    StringBuilder summary = new StringBuilder();
                    if (loadedTitles.isEmpty()) {
                        summary.append("Gemini sent suggestions, but none could be loaded.");
                    } else {
                        summary.append("Added **").append(loadedTitles.size()).append("** tracks to the queue:\n");
                        for (int i = 0; i < loadedTitles.size(); i++) {
                            summary.append("`").append(i + 1).append(".` ")
                                    .append(loadedTitles.get(i)).append("\n");
                        }
                    }

                    Color guildColor = new EmbedWrapper().GetGuildEmbedColor(e.getGuild());
                    e.getHook().editOriginalEmbeds(EmbedWrapper.createInfo(summary.toString(), guildColor)).queue();
                }
            });
        }
    }
}