package com.losnazar.musicbot.service;

import com.losnazar.musicbot.config.BotConfig;
import com.losnazar.musicbot.exception.InvalidCommandException;
import com.losnazar.musicbot.model.MusicBotCommand;
import com.losnazar.musicbot.service.handlers.CallbackQueryHandler;
import com.losnazar.musicbot.service.handlers.command.CommandHandler;
import com.losnazar.musicbot.strategy.CommandSupplier;
import com.losnazar.musicbot.util.UrlOperator;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendAudio;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.starter.SpringWebhookBot;
import java.io.File;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Log4j2
public class MusicBot extends SpringWebhookBot {
    private static final String START_COMMAND = "/start";
    private static final String DOWNLOAD_COMMAND = "/download";
    private static final String HELP_COMMAND = "/help";
    private static final String STATISTIC_COMMAND = "/statistic";

    private static final String START_DESCRIPTION = "START_DESCRIPTION";
    private static final String DOWNLOAD_DESCRIPTION = "DOWNLOAD_DESCRIPTION";
    private static final String HELP_DESCRIPTION = "HELP_DESCRIPTION";
    private static final String STATISTIC_DESCRIPTION = "STATISTIC_DESCRIPTION";
    private final BotConfig botConfig;
    @Autowired
    private CallbackQueryHandler callbackQueryHandler;
    @Autowired
    private CommandSupplier commandSupplier;
    @Autowired
    private MusicBotCommandParser musicBotCommandParser;
    @Autowired
    private UrlOperator urlOperator;

    public MusicBot(SetWebhook setWebhook,
                    DefaultBotOptions botOptions,
                    BotConfig botConfig) {
        super(botOptions, setWebhook);
        this.botConfig = botConfig;
        getMenuKeyboard();
        log.info("MusicBot was constructed. " + LocalDateTime.now());
    }

    public MusicBot(SetWebhook setWebhook,
                    BotConfig botConfig) {
        super(setWebhook);
        this.botConfig = botConfig;

        getMenuKeyboard();
        log.info("MusicBot was constructed. " + LocalDateTime.now());
    }



    @Override
    public String getBotUsername() {
        return botConfig.getBotName();
    }

    @Override
    public String getBotToken() {
        return botConfig.getBotToken();
    }

    @Override
    public String getBotPath() {
        return botConfig.getWebhookPath();
    }

    @Override
    public BotApiMethod<?> onWebhookUpdateReceived(Update update) {
        try {
            this.handleUpdate(update);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e.getMessage(), e.getCause());
        }
        return new SendMessage(update.getChatMember().getChat().getId().toString(), "Message");
    }

    public void handleUpdate(Update update) throws InvalidCommandException,
            TelegramApiException {
        if (update.hasCallbackQuery()) {
            callbackQueryHandler
                    .processCallbackQuery(update.getCallbackQuery());
        }
        handleInputMessage(update.getMessage());
    }

    public void handleInputMessage(Message message) throws InvalidCommandException {
        CommandHandler commandHandler;
        if (!urlOperator.isURL(message.getText())) {
            MusicBotCommand musicBotCommand = musicBotCommandParser.parse(message);
            commandHandler = commandSupplier.get(musicBotCommand);
        } else {
            commandHandler = commandSupplier.get(MusicBotCommand.DOWNLOAD);
            try {
                commandHandler.getResponse(message);
            } catch (TelegramApiException e) {
                throw new RuntimeException(e);
            }
        }
        try {
            commandHandler.getResponse(message);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendMessage(Long chatId, String text) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(text);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            throw new RuntimeException("Can't send message", e.getCause());
        }
    }

    @SneakyThrows
    public void sendAudio(Long chatId, String audioPath) {
        Path path = Path.of(audioPath);
        File audio = path.toFile();
        InputFile inputAudio = new InputFile(audio);
        SendAudio sendAudio = new SendAudio();
        sendAudio.setChatId(chatId);
        sendAudio.setAudio(inputAudio);
        execute(sendAudio);
        log.info(String.format("Audio by path: %s, was sent to chat_id: %s",
                audioPath, chatId));
    }

    public void getMenuKeyboard() {
        List<BotCommand> menu = new ArrayList<>();
        menu.add(new BotCommand(START_COMMAND, START_DESCRIPTION));
        menu.add(new BotCommand(DOWNLOAD_COMMAND, DOWNLOAD_DESCRIPTION));
        menu.add(new BotCommand(HELP_COMMAND, HELP_DESCRIPTION));
        menu.add(new BotCommand(STATISTIC_COMMAND, STATISTIC_DESCRIPTION));
        try {
            execute(new SetMyCommands(menu,
                    new BotCommandScopeDefault(), null));
        } catch (TelegramApiException e) {
            throw new RuntimeException("Can't initialize menu");
        }
    }
}
