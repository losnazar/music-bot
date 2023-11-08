package com.losnazar.musicbot.util;

import org.springframework.stereotype.Component;

@Component
public class AudioInfoParser {

    public String getArtist(String title) {
        return title.split(" - ")[0];
    }

    public String getTitle(String title) {
        return title.split(" - ")[1]
                .replaceAll("([(\\[][A-z]+\\s[A-z]+[)\\]])", "");
    }
}
