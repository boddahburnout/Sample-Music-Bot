package org.djbot.Utils;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
import org.djbot.config.ConfigManager;
import org.simpleyaml.configuration.file.YamlFile;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

public class YouTubeSearcher {
    private final String apiKey;
    private final YouTube youTubeService;

    public YouTubeSearcher() {
        YamlFile botConfig = new ConfigManager().accessConfig();
        this.apiKey = botConfig.getString("Global.Youtube-Token");

        try {
            this.youTubeService = new YouTube.Builder(
                    GoogleNetHttpTransport.newTrustedTransport(),
                    JacksonFactory.getDefaultInstance(),
                    null
            ).setApplicationName("Bartender Bot").build();
        } catch (GeneralSecurityException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public List<SearchResult> search(String query, long maxResults) {
        YouTube.Search.List request = null;
        try {
            request = youTubeService.search().list("id,snippet");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        SearchListResponse response = null;
        try {
            response = request.setKey(apiKey)
                    .setQ(query)
                    .setType("video")
                    .setMaxResults(maxResults)
                    .execute();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return response.getItems();
    }
}
