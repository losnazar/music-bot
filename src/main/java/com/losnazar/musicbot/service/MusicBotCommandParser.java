package com.losnazar.musicbot.service;

import com.losnazar.musicbot.exception.InvalidCommandException;
import com.losnazar.musicbot.model.MusicBotCommand;
import com.vdurmont.emoji.EmojiParser;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;

@Component
public class MusicBotCommandParser {
    public static final String DOWNLOAD = EmojiParser.parseToUnicode(":inbox_tray: Download");
    public static final String STATISTIC = EmojiParser.parseToUnicode(":bar_chart: Statistic");
    public static final String HELP = EmojiParser.parseToUnicode(":question: Help");
    public static final String EXCEPTION_MESSAGE = ":no_entry: Invalid command!";

    public MusicBotCommand parse(Message message) throws InvalidCommandException {
        String text = message.getText();
        if (text.equals(DOWNLOAD)) {
            return MusicBotCommand.DOWNLOAD;
        } else if (text.equals(STATISTIC)) {
            return MusicBotCommand.STATISTIC;
        } else if (text.equals(HELP)) {
            return MusicBotCommand.HELP;
        } else {
            String commandString = text;
            if (text.contains("/")) {
                commandString = text.substring(text.indexOf("/") + 1);
            }
            try {
                return MusicBotCommand.valueOf(commandString.toUpperCase());
            } catch (Exception e) {
                throw new InvalidCommandException(EmojiParser.parseToUnicode(EXCEPTION_MESSAGE),
                        message.getChatId());
            }
        }
    }
}
