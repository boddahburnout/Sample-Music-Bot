package org.djbot.Utils.command;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.SlashCommand;
import org.reflections.Reflections;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class CommandManager {

    // This is the package your commands MUST be in.
    private static final String COMMANDS_PACKAGE = "org.djbot.music";

    private final List<Command> textCommands;
    private final List<SlashCommand> slashCommands;

    public CommandManager() {
        textCommands = new ArrayList<>();
        slashCommands = new ArrayList<>();

        Reflections reflections = new Reflections(COMMANDS_PACKAGE);

        Set<Class<? extends Command>> textCommandClasses = reflections.getSubTypesOf(Command.class);

        for (Class<? extends Command> cmdClass : textCommandClasses) {
            if (Modifier.isAbstract(cmdClass.getModifiers()) || cmdClass.isInterface()) {
                continue;
            }

            if (SlashCommand.class.isAssignableFrom(cmdClass)) {
                continue;
            }

            try {
                Command command = cmdClass.getConstructor().newInstance();
                textCommands.add(command);
            } catch (Exception e) {
                System.err.println("Error instantiating text command: " + cmdClass.getName());
                e.printStackTrace();
            }
        }

        Set<Class<? extends SlashCommand>> slashCommandClasses = reflections.getSubTypesOf(SlashCommand.class);

        for (Class<? extends SlashCommand> cmdClass : slashCommandClasses) {
            if (Modifier.isAbstract(cmdClass.getModifiers()) || cmdClass.isInterface()) {
                continue;
            }

            try {
                SlashCommand command = cmdClass.getConstructor().newInstance();
                slashCommands.add(command);
            } catch (Exception e) {
                System.err.println("Error instantiating slash command: " + cmdClass.getName());
                e.printStackTrace();
            }
        }
        System.out.println("Successfully registered " + textCommands.size() + " text commands and " + slashCommands.size() + " slash commands.");
    }

    public Command[] getTextCommands() {
        return textCommands.toArray(new Command[0]);
    }

    public SlashCommand[] getSlashCommands() {
        return slashCommands.toArray(new SlashCommand[0]);
    }
}