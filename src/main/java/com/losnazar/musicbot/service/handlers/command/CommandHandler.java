package com.losnazar.musicbot.service.handlers.command;

import com.losnazar.musicbot.config.BotConfig;
import com.losnazar.musicbot.model.MusicBotCommand;
import com.losnazar.musicbot.service.MusicBot;
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

abstract public class CommandHandler extends MusicBot {
    private final MusicBotCommand musicBotCommand;

    public CommandHandler(MusicBotCommand musicBotCommand,
                          SetWebhook setWebhook,
                          BotConfig botConfig) {
        super(setWebhook, botConfig);
        this.musicBotCommand = musicBotCommand;
    }

    public abstract void getResponse(Message message) throws TelegramApiException;

    public boolean corresponds(MusicBotCommand musicBotCommand) {
        return this.musicBotCommand.equals(musicBotCommand);
    }
}
