package com.kravchenkopavlo.examplesBot.bot.actions;

import com.kravchenkopavlo.examplesBot.bot.RespondService;
import com.kravchenkopavlo.examplesBot.bot.Utils;
import org.telegram.telegrambots.meta.api.objects.Update;

public abstract class AbstractAction {
    abstract boolean check();
    abstract public boolean execute();

    private final RespondService respondService;

    protected Update update;

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // constructor

    AbstractAction(RespondService respondService) {
        this.respondService = respondService;
    }

    public boolean fits(Update update) {
        this.update = update;
        return globalChecks() && check();
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // always-performed check

    private boolean globalChecks() {
        return true;
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // common checks

    boolean updateHasText() {
        return update.hasMessage() && update.getMessage().hasText();
    }

    boolean updateHasCallBackData() {
        return update.hasCallbackQuery()
                && update.getCallbackQuery().getData() != null
                && update.getCallbackQuery().getData().length() > 0;
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // quick responses

    Respond respond() {
        return respondService.respond(Utils.getChatId(update));
    }

    Respond respond(int id) {
        return respondService.respond((long)id);
    }

    Respond reply(String text) {
        return respond().append(text);
    }

    Respond edit(String updatedText, String keyboard) {
        return respond().editMessage(update, updatedText, keyboard);
    }

    boolean error(String text) {
        reply(text);
        return false;
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // common update's data getters

    String messageText() {
        return updateHasText() ? update.getMessage().getText()
                : updateHasCallBackData() ? update.getCallbackQuery().getMessage().getText()
                : null;
    }

    String callbackData() {
        return update.getCallbackQuery().getData();
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // actions


}

