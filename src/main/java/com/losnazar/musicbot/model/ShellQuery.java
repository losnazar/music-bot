package com.losnazar.musicbot.model;

import java.util.ArrayList;
import java.util.List;

public class ShellQuery {
    private static final String YOUTUBE_DL = "youtube-dl";
    private static final String FILE_TYPE = "-f";

    private final String url;
    private final List<String> commands;

    public ShellQuery(String url) {
        this.url = url;
        commands = new ArrayList<>();
        commands.add(YOUTUBE_DL);
    }

    public void setFileType(String value) {
        commands.add(FILE_TYPE);
        commands.add(value);
    }

    public void setOption(String option) {
        commands.add(option);
    }

    public void setOption(String option, String value){
        commands.add(option);
        commands.add(value);
    }

    public List<String> getCommands() {
        commands.add(url);
        return commands;
    }
}
