package com.losnazar.musicbot.config;

import com.losnazar.musicbot.service.MusicBot;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook;

@Configuration
public class AppConfig {
    private final BotConfig botConfig;

    public AppConfig(BotConfig botConfig) {
        this.botConfig = botConfig;
    }

    @Bean
    public SetWebhook setWebhookInstance() {
        return SetWebhook.builder()
                .url(botConfig.getWebhookPath())
                .build();
    }

    @Bean
    @Primary
    public MusicBot springWebhookBot(SetWebhook setWebhook,
                                     BotConfig botConfig) {
        return new MusicBot(setWebhook, botConfig);
    }
}
