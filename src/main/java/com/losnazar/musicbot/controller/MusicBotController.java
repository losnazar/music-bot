package com.losnazar.musicbot.controller;

import com.losnazar.musicbot.service.MusicBot;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;

@Log4j2
@RestController
public class MusicBotController {
    private final MusicBot musicBot;

    public MusicBotController(MusicBot musicBot) {
        this.musicBot = musicBot;
    }

    @PostMapping("/")
    public BotApiMethod<?> onUpdateReceived(@RequestBody Update update) {
        if (update.getMessage() != null) {
            log.info(String.format("Update from chat_id: %s was received on controller.",
                update.getMessage().getChatId()));
        } else {
            log.info("Processing callback. ");
        }
        return musicBot.onWebhookUpdateReceived(update);
    }
}
