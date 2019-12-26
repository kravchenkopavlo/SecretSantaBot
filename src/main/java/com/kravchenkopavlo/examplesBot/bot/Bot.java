package com.kravchenkopavlo.examplesBot.bot;

import com.kravchenkopavlo.examplesBot.bot.actions.RegisterMember;
import com.kravchenkopavlo.examplesBot.bot.actions.SendSecretSanta;
import com.kravchenkopavlo.examplesBot.bot.actions.Start;
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
                .addAction(new Start(respondService))
                .addAction(new RegisterMember(respondService))
                .addAction(new SendSecretSanta(respondService, this));
    }

    @Override
    public void onUpdateReceived(Update update) {
        updateParser.proceed(update);
        respondService.sendAll();
    }

    @Override
    public String getBotUsername() {
        return this.username;
    }

    @Override
    public String getBotToken() {
        return this.token;
    }
}
