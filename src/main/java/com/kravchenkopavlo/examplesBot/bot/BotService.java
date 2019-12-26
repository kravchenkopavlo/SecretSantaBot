package com.kravchenkopavlo.examplesBot.bot;

import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.BotSession;

public class BotService {

    private BotSession telegramBotSession;

    public void start(String username, String token) {
        ApiContextInitializer.init();
        final TelegramBotsApi telegramBotsAPI = new TelegramBotsApi();
        final Bot telegramBot = new Bot(username, token);
        try {
            telegramBotSession = telegramBotsAPI.registerBot(telegramBot);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public boolean stop() {
        if (telegramBotSession != null) {
            telegramBotSession.stop();
            telegramBotSession = null;
            return true;
        } else {
            return false;
        }
    }
}
