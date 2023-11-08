package com.losnazar.musicbot.exception;

public class DataRetrievingException extends MusicBotException {
    public DataRetrievingException(String message, Long id) {
        super(message, id);
    }
}
