package com.losnazar.musicbot.service.handlers.command;

import com.losnazar.musicbot.config.BotConfig;
import com.losnazar.musicbot.model.MusicBotCommand;
import com.losnazar.musicbot.model.db.User;
import com.losnazar.musicbot.repository.UserRepository;
import com.losnazar.musicbot.service.MenuService;
import com.vdurmont.emoji.EmojiParser;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.time.LocalDateTime;
import java.util.ArrayList;

@Log4j2
@Component
public class StartCommandHandler extends CommandHandler {
    private final UserRepository userRepository;
    private final MenuService menuService;

    public StartCommandHandler(UserRepository userRepository,
                               MenuService menuService,
                               SetWebhook setWebhook,
                               BotConfig botConfig) {
        super(MusicBotCommand.START, setWebhook, botConfig);
        this.userRepository = userRepository;
        this.menuService = menuService;
    }

    @Override
    public void getResponse(Message message) throws TelegramApiException {
        Long chatId = message.getChatId();

        log.info(String.format("/start command was invoked, chat_id: %s", chatId));

        if (!userRepository.existsById(chatId)) {
            User user = new User();
            user.setChatId(chatId);
            user.setFirstName(message.getChat().getFirstName());
            user.setLastName(message.getChat().getLastName());
            user.setUserName(message.getChat().getUserName());
            user.setRegisterAt(LocalDateTime.now());
            user.setAudioList(new ArrayList<>());
            userRepository.save(user);
        }

        String text = String.format("Hello, %s, glad to see you! :smiley: \n" +
                        "Please choose command :arrow_down: ",
                message.getChat().getFirstName());

        execute(menuService.getMainMenuMessage(chatId,
                EmojiParser.parseToUnicode(text)));
    }
}
