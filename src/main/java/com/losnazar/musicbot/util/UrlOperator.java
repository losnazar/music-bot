package com.losnazar.musicbot.util;

import org.springframework.stereotype.Component;

import java.net.MalformedURLException;
import java.net.URL;

@Component
public class UrlOperator {
    private static final String YOUTUBE_REGEX = "http(?:s?):\\/\\/((?:www\\.)|(?:m\\.))" +
            "?youtu(?:be\\.com\\/watch\\?v=|\\.be\\/)([\\w\\-\\_]*)(&(amp;)?[\\w\\?\u200C\u200B=]*)?";

    public boolean isYoutubeUrl(String url) {
        return url.matches(YOUTUBE_REGEX);
    }

    public boolean isURL(String url) {
        try {
            new URL(url);
        } catch (MalformedURLException e) {
            return false;
        }
        return true;
    }

    public String deleteArguments(String url) {
        return url.split("&")[0];
    }
}
