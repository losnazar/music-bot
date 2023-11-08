package com.losnazar.musicbot.service.handlers.command;

import com.losnazar.musicbot.config.BotConfig;
import com.losnazar.musicbot.model.MusicBotCommand;
import com.losnazar.musicbot.model.db.Audio;
import com.losnazar.musicbot.repository.AudioRepository;
import com.losnazar.musicbot.repository.UserRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import java.util.ArrayList;
import java.util.List;

@Log4j2
@Component
public class StatisticCommandHandler extends CommandHandler {
    public static final String YOUR_SONGS_BUTTON = "Your Songs";
    public static final String ARTISTS_BUTTON = "Artists";
    private final AudioRepository audioRepository;

    public StatisticCommandHandler(UserRepository userRepository,
                                   AudioRepository audioRepository,
                                   SetWebhook setWebhook,
                                   BotConfig botConfig) {
        super(MusicBotCommand.STATISTIC, setWebhook, botConfig);
        this.audioRepository = audioRepository;
    }

    @Override
    public void getResponse(Message message) throws TelegramApiException {
        execute(getStatisticMenu(message.getChatId()));
    }

    public SendMessage getStatisticMenu(Long chatId) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText("Choose type of statistics: ");

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        List<InlineKeyboardButton> firstRow = new ArrayList<>();

        InlineKeyboardButton allSongsButton = new InlineKeyboardButton();
        allSongsButton.setText("Your songs");
        allSongsButton.setCallbackData(YOUR_SONGS_BUTTON);
        firstRow.add(allSongsButton);

        InlineKeyboardButton allArtistsButton = new InlineKeyboardButton();
        allArtistsButton.setText("Artists");
        allArtistsButton.setCallbackData(ARTISTS_BUTTON);
        firstRow.add(allArtistsButton);

        rows.add(firstRow);

        inlineKeyboardMarkup.setKeyboard(rows);
        sendMessage.setReplyMarkup(inlineKeyboardMarkup);

        return sendMessage;
    }

    public List<Audio> getAudiosByArtist(String artist) {
        return audioRepository.findAllByArtist(artist);
    }
}