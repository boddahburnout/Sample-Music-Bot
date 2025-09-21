package org.djbot;

import com.jagrosh.jdautilities.command.CommandClientBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.djbot.config.ConfigManager;
import org.djbot.music.*;
import org.simpleyaml.configuration.file.YamlFile;

public abstract class Main {
    private static JDA jda;
    public static void main(String[] args) {
        CommandClientBuilder clientBuilder = new CommandClientBuilder();

        clientBuilder.setPrefix(".");

        clientBuilder.setOwnerId("292484423658766346");

        clientBuilder.addCommands(
                new Join(),
                new Leave(),
                new Pause(),
                new Play(),
                new Playing(),
                new Queue(),
                new Remove(),
                new Shuffle(),
                new Skip(),
                new Volume(),
                new PlayNow()
        );
        try {
            new ConfigManager().setConfig();
            //Access the config
            YamlFile Config = new ConfigManager().accessConfig();
            //Sets token to a string
            String token = Config.getString("Global.Bot-Token");

            jda = JDABuilder.createDefault(token)
                    .enableIntents(GatewayIntent.MESSAGE_CONTENT)
                    .setActivity(Activity.playing("Smoking hella dank at the Deathstar"))
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
}