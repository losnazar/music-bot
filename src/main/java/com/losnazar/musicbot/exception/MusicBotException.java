package com.losnazar.musicbot.exception;

public class MusicBotException extends RuntimeException {
    private final Long chatId;
    public MusicBotException(String message, Long id) {
        super(message);
        this.chatId = id;
    }
    public Long getChatId() {
        return chatId;
    }
}
