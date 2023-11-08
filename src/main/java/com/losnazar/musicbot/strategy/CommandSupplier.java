package com.losnazar.musicbot.strategy;

import com.losnazar.musicbot.service.handlers.command.CommandHandler;
import com.losnazar.musicbot.model.MusicBotCommand;

public interface CommandSupplier {
    CommandHandler get(MusicBotCommand musicBotCommand);
}
