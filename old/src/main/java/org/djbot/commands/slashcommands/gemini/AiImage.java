package org.djbot.commands.slashcommands.gemini;

import com.google.genai.Client;
import com.google.genai.types.GenerateImagesConfig;
import com.google.genai.types.GenerateImagesResponse;
import com.google.genai.types.ListModelsConfig;
import com.google.genai.types.Model;
import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.utils.FileUpload;
import org.djbot.utils.bot.gemini.GeminiClient;

import java.util.Collections;

public class AiImage extends SlashCommand {
    public AiImage() {
        this.name = "aiimage";
        this.help = "Generate Ai images";
        this.options = Collections.singletonList(new OptionData(OptionType.STRING, "prompt", "Ai prompt to use", true));
    }

    @Override
    protected void execute(SlashCommandEvent event) {
        String authorName = event.getUser().getName();
        String prompt = event.getOption("prompt").getAsString();
        event.getChannel().sendMessage("Brewing up '" + prompt + "' for you, " + authorName + "...").queue();

        try {
            // *** This is where your Java Gemini API magic happens! ***
            // 1. 'geminiImageClient' would call your Gemini-powered text-to-image
            //    model (or use Gemini to refine a prompt for another TTI API).
            // 2. It should then return a File object (e.g., a temporary PNG/JPG).
            //    Alternatively, if it returns a URL, just send:
            //    event.getChannel().sendMessage(imageUrl).queue();
            Client geminiImageClient = new GeminiClient().getClient();
            GenerateImagesConfig config =
                    GenerateImagesConfig.builder()
                            //.thinkingConfig(ThinkingConfig.builder().thinkingBudget(0))
                            //.systemInstruction(systemInstruction)
                            .build();
            ListModelsConfig listModelsConfig = ListModelsConfig.builder()
                            .build();
            for (Model model : geminiImageClient.models.list(listModelsConfig)) {
                System.out.println(model.name());
            }
            GenerateImagesResponse generatedImage = geminiImageClient.models.generateImages("gemini-2.0-flash-exp", prompt, config); // <-- Implement this!

            event.getChannel().sendFiles(FileUpload.fromData(generatedImage.generatedImages().get().get(0).image().get().imageBytes().get(), "budtender_art.png")).queue(
                    null, // Success (optional)
                    failure -> event.getChannel().sendMessage("Hmm, couldn't send the masterpiece!").queue()
            );

        } catch (Exception e) {
            //event.getChannel().sendMessage("Whoops! My visual mixer sputtered. Couldn't conjure that image for ya!").queue();
             System.out.println(e);
        }
    }
}
