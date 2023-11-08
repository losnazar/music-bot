package com.losnazar.musicbot.service;

import com.losnazar.musicbot.model.AudioDetails;
import com.losnazar.musicbot.model.db.Audio;
import com.losnazar.musicbot.model.ShellQuery;
import com.losnazar.musicbot.util.AudioInfoParser;
import com.losnazar.musicbot.util.ShellProcessExecutor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import java.util.List;

@Log4j2
@Component
public class AudioDownloader {
    private static final String GET_TITLE_OPTION = "--get-title";
    private static final String GET_DURATION_OPTION = "--get-duration";
    private static final String SET_DOWNLOAD_PATH_OPTION = "-o";

    private static final String AUDIO_FILE_TYPE = "bestaudio[ext=m4a]";

    private String url;
    private final ShellProcessExecutor processExecutor;
    private final AudioInfoParser infoParser;

    public AudioDownloader(ShellProcessExecutor processExecutor,
                           AudioInfoParser infoParser) {
        this.processExecutor = processExecutor;
        this.infoParser = infoParser;
    }

    public AudioDetails getAudioDetails(String url) {
        this.url = url;

        ShellQuery shellQuery = new ShellQuery(url);
        shellQuery.setOption(GET_TITLE_OPTION);
        shellQuery.setOption(GET_DURATION_OPTION);

        List<String> commands = shellQuery.getCommands();
        List<String> processOutput = processExecutor.runProcess(commands);

        AudioDetails details = new AudioDetails();
        details.setUrl(url);
        if (isExecuted(processOutput)) {
            String title = processOutput.get(0);
            int duration = convertToSeconds(processOutput.get(1));
            details.setTitle(title);
            details.setDuration(duration);
            details.setAvailable(true);
        } else {
            details.setAvailable(false);
        }
        return details;
    }

    public Audio download(AudioDetails audioDetails) {
        String title = infoParser.getTitle(audioDetails.getTitle());
        String artist = infoParser.getArtist(audioDetails.getTitle());
        String path = artist + "-" + title;

        ShellQuery shellQuery = new ShellQuery(url);
        shellQuery.setFileType(AUDIO_FILE_TYPE);
        shellQuery.setOption(SET_DOWNLOAD_PATH_OPTION,
                ShellProcessExecutor.CMD_DIRECTORY + String.format("\\%s.m4a", path));

        List<String> commands = shellQuery.getCommands();
        List<String> processOutput = processExecutor.runProcess(commands);

        if (!isExecuted(processOutput)) {
            log.error(String.format("Downloading by url: %s, was failed. " +
                    "Youtube-dl output: ", url));
            processOutput.forEach(log::error);
        }
        Audio audio = new Audio();
        audio.setTitle(title);
        audio.setArtist(artist);
        audio.setDuration(audioDetails.getDuration());
        audio.setPath(ShellProcessExecutor.CMD_DIRECTORY
                + String.format("\\%s.m4a", path));
        audio.setUrl(url);
        return audio;
    }

    private int convertToSeconds(String duration) {
        String[] time = duration.split(":");
        return switch (time.length) {
            case 1 -> Integer.parseInt(time[0]);
            case 2 -> Integer.parseInt(time[0]) * 60 + Integer.parseInt(time[1]);
            case 3 -> Integer.parseInt(time[0]) * 3600
                    + Integer.parseInt(time[1]) * 60 + Integer.parseInt(time[0]);
            default -> 0;
        };
    }

    public boolean isExecuted(List<String> output) {
        return output.get(output.size() -1).equals("0");
    }
}
