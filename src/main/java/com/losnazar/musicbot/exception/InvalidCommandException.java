package com.losnazar.musicbot.exception;

public class InvalidCommandException extends MusicBotException {
    public InvalidCommandException(String message, Long id) {
        super(message, id);
    }
}
