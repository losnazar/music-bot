package com.losnazar.musicbot.util;

import com.losnazar.musicbot.model.db.Audio;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class AudioSlicer {
    private final String PART_NAME_FORMAT = "_part%01d.m4a";
    private final String SLICE_CMD = "ffmpeg -hide_banner -i '%s' " +
            "-reset_timestamps 1 -f segment -segment_time %d -c copy '%s'";

    private final ShellProcessExecutor processExecutor;

    public AudioSlicer(ShellProcessExecutor processExecutor) {
        this.processExecutor = processExecutor;
    }

    public List<Audio> getAudioParts(int partDuration, Audio audio) {
        final String OPENING_FOR_WRITING_EXP = ".*Opening '.*' for writing$";
        String audioPath = audio.getPath();
        List<String> processOutput =
                processExecutor.runProcess(List.of(SLICE_CMD, audioPath,
                        String.valueOf(partDuration), cutExtension(audioPath)));

        List<Audio> parts = new ArrayList<>();
        return parts;
    }

    private String cutExtension(String audioPath) {
        return null;
    }
}
