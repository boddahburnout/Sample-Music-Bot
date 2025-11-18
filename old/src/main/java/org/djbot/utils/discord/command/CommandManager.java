package org.djbot.utils.discord.command;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.SlashCommand;
import org.reflections.Reflections;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class CommandManager {
    // --- UPDATED ---
    // Define the specific packages to scan
    private static final String SLASH_COMMANDS_PACKAGE = "org.djbot.commands.slashcommands";
    private static final String TEXT_COMMANDS_PACKAGE = "org.djbot.commands.textcommands";
    // --- END UPDATE ---

    private final List<Command> textCommands;
    private final List<SlashCommand> slashCommands;

    public CommandManager() {
        textCommands = new ArrayList<>();
        slashCommands = new ArrayList<>();

        // --- NEW LOGIC ---

        // 1. Scan for all Slash Commands
        Reflections slashReflections = new Reflections(SLASH_COMMANDS_PACKAGE);
        Set<Class<? extends SlashCommand>> slashCommandClasses = slashReflections.getSubTypesOf(SlashCommand.class);

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

        // 2. Scan for all Text Commands
        Reflections textReflections = new Reflections(TEXT_COMMANDS_PACKAGE);
        Set<Class<? extends Command>> textCommandClasses = textReflections.getSubTypesOf(Command.class);

        for (Class<? extends Command> cmdClass : textCommandClasses) {
            if (Modifier.isAbstract(cmdClass.getModifiers()) || cmdClass.isInterface()) {
                continue;
            }

            // Make sure we don't accidentally register a SlashCommand as a TextCommand
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
        // --- END NEW LOGIC ---

        System.out.println("Successfully loaded " + textCommands.size() + " text commands and " + slashCommands.size() + " slash commands.");
    }

    public Command[] getTextCommands() {
        return textCommands.toArray(new Command[0]);
    }

    public SlashCommand[] getSlashCommands() {
        return slashCommands.toArray(new SlashCommand[0]);
    }
}