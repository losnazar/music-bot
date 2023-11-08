package com.losnazar.musicbot.model;

import lombok.Getter;

@Getter
public enum MusicBotCommand {
    START("/start"),
    STATISTIC("/statistic"),
    HELP("/help"),
    DOWNLOAD("/download");

    private final String value;

    MusicBotCommand(String value) {
        this.value = value;
    }
}
