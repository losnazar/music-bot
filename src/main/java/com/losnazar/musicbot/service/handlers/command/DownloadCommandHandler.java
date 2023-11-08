package com.losnazar.musicbot.service.handlers.command;

import com.losnazar.musicbot.config.BotConfig;
import com.losnazar.musicbot.exception.DataRetrievingException;
import com.losnazar.musicbot.model.AudioDetails;
import com.losnazar.musicbot.model.db.Audio;
import com.losnazar.musicbot.model.MusicBotCommand;
import com.losnazar.musicbot.model.db.User;
import com.losnazar.musicbot.repository.UserRepository;
import com.losnazar.musicbot.service.AudioDownloader;
import com.losnazar.musicbot.service.MenuService;
import com.losnazar.musicbot.service.MusicBotCommandParser;
import com.losnazar.musicbot.util.UrlOperator;
import com.vdurmont.emoji.EmojiParser;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import java.util.List;

@Log4j2
@Component
public class DownloadCommandHandler extends CommandHandler {
    private final MenuService menuService;
    private final UserRepository userRepository;
    private final AudioDownloader audioDownloader;
    private final UrlOperator urlOperator;

    public DownloadCommandHandler(MenuService menuService,
                                  UserRepository userRepository,
                                  AudioDownloader audioDownloader,
                                  UrlOperator urlOperator,
                                  SetWebhook setWebhook,
                                  BotConfig botConfig) {
        super(MusicBotCommand.DOWNLOAD, setWebhook, botConfig);
        this.menuService = menuService;
        this.userRepository = userRepository;
        this.audioDownloader = audioDownloader;
        this.urlOperator = urlOperator;
    }

    @Override
    public void getResponse(Message message) throws TelegramApiException {
        Long chatId = message.getChatId();
        String text = message.getText();
        User user = userRepository.findById(chatId)
                .orElseThrow(() -> new DataRetrievingException("Can't find user. ", chatId));

        log.info(String.format("DownloadCommandHandler was invoked. Chat_id: %s.", chatId));

        if (text.equals(MusicBotCommandParser.DOWNLOAD)
                || text.equals("/download")) {
            sendMessage(chatId,
                    EmojiParser.parseToUnicode("Please enter youtube-url, " +
                            "which you want to download :arrow_down:"));
        } else {
            log.info(String.format("Youtube-url was entered to DownloadCommandHandler. Chat_id: %s",
                    chatId));

            String url = message.getText();

            if (urlOperator.isURL(url) && urlOperator.isYoutubeUrl(urlOperator.deleteArguments(url))) {
                AudioDetails audioDetails = audioDownloader.getAudioDetails(url);
                if (audioDetails.isAvailable()) {
                    Audio audio = audioDownloader.download(audioDetails);
                    List<Audio> userAudioList = user.getAudioList();

                    if (userAudioList.stream().noneMatch(a -> a.getUrl().equals(url))) {
                        userAudioList.add(audio);
                        userRepository.save(user);

                        log.info(String.format("User by id: %s, was updated in DB, added audio: %s",
                                chatId, audioDetails.getTitle()));
                    }
                    log.info(String.format("Audio: %s, was saved to DB. ", audioDetails.getTitle()));
                    sendAudio(chatId, audio.getPath());
                } else {
                    sendMessage(chatId,
                            EmojiParser.parseToUnicode("""
                                    Sorry, can't download this audio :disappointed_relieved:
                                    Possible reasons:
                                    1. YouTube marked this video as unacceptable for some users.
                                    2. Private video."""));
                }
            } else {
                sendMessage(chatId,
                        EmojiParser.parseToUnicode("You entered invalid url! Please try again! :arrow_down:"));
            }
        }
        execute(menuService.getMainMenuMessage(chatId,
                EmojiParser.parseToUnicode("Your audio was successfully downloaded! :rocket:")));
    }
}

