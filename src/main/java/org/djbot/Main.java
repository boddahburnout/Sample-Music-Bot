package org.djbot;

import com.jagrosh.jdautilities.command.CommandClientBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.djbot.Utils.command.CommandManager;
import org.djbot.Utils.helper.ConfigData;

public abstract class Main {
    private static JDA jda;
    private static final ConfigData configData = new ConfigData();
    public static void main(String[] args) {
        CommandClientBuilder clientBuilder = new CommandClientBuilder();
        CommandManager commandManager = new CommandManager();

        clientBuilder.setPrefix(".");

        clientBuilder.setOwnerId(configData.getOwnerId());
        clientBuilder.setActivity(Activity.playing(configData.getActivity()));

        clientBuilder.addSlashCommands(commandManager.getSlashCommands());
        try {
            jda = JDABuilder.createDefault(configData.getJDAToken())
                    .enableIntents(GatewayIntent.MESSAGE_CONTENT)
                    .addEventListeners(
                            clientBuilder.build()
                    )
                    .build();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    public static JDA getJdaInstance() {
        return jda;
    }
    public static ConfigData getConfigData() {
        return configData;
    }
}