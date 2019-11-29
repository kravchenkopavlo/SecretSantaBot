package com.kravchenkopavlo.examplesBot.bot;

import com.kravchenkopavlo.examplesBot.bot.actions.SayHello;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

public class Bot extends TelegramLongPollingBot {

    private String username;
    private String token;

    private final UpdateParser   updateParser;
    private final RespondService respondService;

    Bot(String username, String token) {
        this.username = username;
        this.token = token;

        this.respondService = new RespondService(this);
        this.updateParser = new UpdateParser()
                .addAction(new SayHello(respondService));
    }

    public void onUpdateReceived(Update update) {
        updateParser.proceed(update);
        respondService.sendAll();
    }

    public String getBotUsername() {
        return this.username;
    }

    public String getBotToken() {
        return this.token;
    }
}
