package com.losnazar.musicbot.strategy;

import com.losnazar.musicbot.service.handlers.command.CommandHandler;
import com.losnazar.musicbot.model.MusicBotCommand;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class CommandStrategy implements CommandSupplier {
    private final List<CommandHandler> commandHandlers;

    public CommandStrategy(List<CommandHandler> commandHandlers) {
        this.commandHandlers = commandHandlers;
    }

    @Override
    public CommandHandler get(MusicBotCommand musicBotCommand) {
        return commandHandlers.stream()
                .filter(commandHandler -> commandHandler.corresponds(musicBotCommand))
                .findFirst()
                .get();
    }
}
