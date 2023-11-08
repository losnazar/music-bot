package com.losnazar.musicbot.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AudioDetails {
    private String url;
    private String title;
    private int duration;
    private boolean isAvailable;
}
