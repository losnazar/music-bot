package com.losnazar.musicbot.controller;

import com.losnazar.musicbot.exception.MusicBotException;
import com.losnazar.musicbot.service.MusicBot;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {
    private final MusicBot musicBot;

    public GlobalExceptionHandler(@Lazy MusicBot musicBot) {
        this.musicBot = musicBot;
    }

    @ExceptionHandler(value = {MusicBotException.class})
    public void handleMusicBotException(MusicBotException e) {
        musicBot.sendMessage(e.getChatId(), e.getMessage());
    }
}
