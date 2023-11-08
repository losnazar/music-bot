package com.losnazar.musicbot.util;

import org.springframework.stereotype.Component;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
public class ShellProcessExecutor {
    public static final String CMD_DIRECTORY = "C:\\Users\\User\\Downloads\\music-bot";
    private static final int TIMEOUT = 300;

    public List<String> runProcess(List<String> commands) {
        List<String> processOutput = new ArrayList<>();

        try {
            Process process = new ProcessBuilder(commands)
                    .directory(new File(CMD_DIRECTORY))
                    .redirectErrorStream(true)
                    .start();

            BufferedReader reader =
                    new BufferedReader(new InputStreamReader(process.getInputStream()));

            String line;

            while ((line = reader.readLine()) != null) {
                processOutput.add(line);
            }

            process.waitFor(TIMEOUT, TimeUnit.SECONDS);
            processOutput.add(String.valueOf(process.exitValue()));

        } catch (IOException e) {
            processOutput.add("I/O exception occurred when running command: %s, "
                    + String.join("", commands));
            processOutput.add(e.getMessage());
        } catch (InterruptedException e) {
            processOutput.add("Interrupted exception occurred when running command: %s, "
                    + String.join("", commands));
            processOutput.add(e.getMessage());
        }
        return processOutput;
    }
}
