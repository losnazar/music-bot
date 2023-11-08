package com.losnazar.musicbot.service.handlers.command;

import com.losnazar.musicbot.config.BotConfig;
import com.losnazar.musicbot.model.MusicBotCommand;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
public class HelpCommandHandler extends CommandHandler {
    private static final String HELP_TEXT = """
            I am music-bot, which is supposed to help you to download your favourite music!
            My special functions:
            - Downloading music by youtube-url.
            - Providing your download statistic.""";

    public HelpCommandHandler(SetWebhook setWebhook, BotConfig botConfig) {
        super(MusicBotCommand.HELP, setWebhook, botConfig);
    }

    @Override
    public void getResponse(Message message) throws TelegramApiException {
        sendMessage(message.getChatId(), HELP_TEXT);
    }
}
