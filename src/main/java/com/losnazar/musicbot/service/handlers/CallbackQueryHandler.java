package com.losnazar.musicbot.service.handlers;

import com.losnazar.musicbot.config.BotConfig;
import com.losnazar.musicbot.exception.DataRetrievingException;
import com.losnazar.musicbot.service.MusicBot;
import com.losnazar.musicbot.service.handlers.command.StatisticCommandHandler;
import com.losnazar.musicbot.model.db.Audio;
import com.losnazar.musicbot.model.db.User;
import com.losnazar.musicbot.repository.UserRepository;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.MessageEntity;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;

@Component
public class CallbackQueryHandler extends MusicBot {
    private static final String BACK_BUTTON = "Back";
    private static final String AUDIO_BY_ARTIST = "GetAudioBy";

    private final UserRepository userRepository;
    private final StatisticCommandHandler statisticCommandHandler;

    public CallbackQueryHandler(UserRepository userRepository,
                                @Lazy StatisticCommandHandler statisticCommandHandler,
                                SetWebhook setWebhook,
                                BotConfig botConfig) {
        super(setWebhook, botConfig);
        this.userRepository = userRepository;
        this.statisticCommandHandler = statisticCommandHandler;
    }

    public void processCallbackQuery(CallbackQuery callbackQuery) throws TelegramApiException {
        Long chatId = callbackQuery.getMessage().getChatId();
        Long userId = callbackQuery.getFrom().getId();
        Integer messageId = callbackQuery.getMessage().getMessageId();
        org.telegram.telegrambots.meta.api.objects.User userTelegram =
                new org.telegram.telegrambots.meta.api.objects.User();
        userTelegram.setId(userId);

        BotApiMethod<?> callBackAnswer = null;
        String[] callbackData = callbackQuery.getData().split("-");
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new DataRetrievingException("Can't find user. ", userId));

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();

        switch (callbackData[0]) {
            case (StatisticCommandHandler.YOUR_SONGS_BUTTON) -> {
                List<Audio> userAudioList = user.getAudioList();
                List<MessageEntity> entities = new ArrayList<>();

                userAudioList.forEach(audio -> {
                    MessageEntity entity = new MessageEntity();
                    String audioString = audio.toString();
                    entity.setText(audioString);
                    entity.setUrl(audio.getUrl());
                    entity.setType("text_link");
                    entity.setOffset(0);
                    entity.setLength(audioString.length());
                    entity.setUser(userTelegram);
                    entities.add(entity);
                });

                List<List<InlineKeyboardButton>> rows = new ArrayList<>();
                rows.add(List.of(getBackButton()));
                markup.setKeyboard(rows);

                EditMessageText editMessageText = new EditMessageText();
                editMessageText.setChatId(chatId);
                editMessageText.setMessageId(messageId);
                editMessageText.setEntities(entities);
                editMessageText.setReplyMarkup(markup);
                callBackAnswer = editMessageText;
            }
            case (StatisticCommandHandler.ARTISTS_BUTTON) -> {
                List<String> artists = user.getAudioList()
                        .stream()
                        .map(Audio::getArtist)
                        .distinct()
                        .toList();

                int rowsAmount = artists.size() < 3 ? 1
                        : artists.size() % 3 != 0
                        ? artists.size() / 3 + 1
                        : artists.size() / 3;

                List<List<InlineKeyboardButton>> rows = new ArrayList<>();

                int rowLength = rowsAmount == 1 ? Math.min(artists.size(), 3) : 3;

                int index = 0;
                for (int i = 0; i < rowsAmount; i++) {
                    List<InlineKeyboardButton> row = new ArrayList<>();
                    if (i == rowsAmount - 1)
                        rowLength = artists.size() % 3;
                    for (int j = 0; j < rowLength; j++) {
                        InlineKeyboardButton artistButton = new InlineKeyboardButton();
                        String artist = artists.get(index);
                        artistButton.setText(artist);
                        artistButton.setCallbackData(String.format("%s-%s",
                                AUDIO_BY_ARTIST, artist));
                        row.add(artistButton);
                        index++;
                    }
                    rows.add(row);
                }
                markup.setKeyboard(rows);

                rows.add(List.of(getBackButton()));
                markup.setKeyboard(rows);

                EditMessageText editMessageText = new EditMessageText();
                editMessageText.setChatId(chatId);
                editMessageText.setMessageId(messageId);
                editMessageText.setText("Choose artist: ");
                editMessageText.setReplyMarkup(markup);
                callBackAnswer = editMessageText;
            }
            case (BACK_BUTTON) -> {
                SendMessage menu = statisticCommandHandler.getStatisticMenu(chatId);
                EditMessageText editMessageText = new EditMessageText();
                editMessageText.setChatId(chatId);
                editMessageText.setMessageId(messageId);
                editMessageText.setText(menu.getText());
                editMessageText.setReplyMarkup((InlineKeyboardMarkup) menu.getReplyMarkup());
                callBackAnswer = editMessageText;
            }
            case (AUDIO_BY_ARTIST) -> {
                List<Audio> audiosByArtist = statisticCommandHandler.getAudiosByArtist(callbackData[1]);
                EditMessageText editMessageText = new EditMessageText();
                editMessageText.setChatId(chatId);
                List<MessageEntity> entities = new ArrayList<>();
                Audio audio = audiosByArtist.get(0);
                MessageEntity entity = new MessageEntity();
                    entity.setType("text_link");
                    entity.setUrl(audio.getUrl());
                    entity.setOffset(0);
                    entity.setLength(4);
                    entities.add(entity);
                editMessageText.setText("Link");
                editMessageText.setEntities(entities);
                callBackAnswer = editMessageText;
            }
        }
        execute(callBackAnswer);
    }

    private InlineKeyboardButton getBackButton() {
        InlineKeyboardButton backButton = new InlineKeyboardButton();
        backButton.setText("Back");
        backButton.setCallbackData(BACK_BUTTON);
        return backButton;
    }
}
