package com.kravchenkopavlo.examplesBot.bot.actions;

import com.kravchenkopavlo.examplesBot.bot.RespondService;
import com.kravchenkopavlo.examplesBot.bot.Utils;
import org.telegram.telegrambots.meta.api.objects.Update;

public abstract class AbstractAction {
    private final RespondService respondService;
    protected Update update;

    AbstractAction(RespondService respondService) {
        this.respondService = respondService;
    }

    public boolean fits(Update update) {
        this.update = update;
        return globalChecks() && check();
    }

    private boolean globalChecks() {
        return true;
    }

    abstract boolean check();
    abstract public boolean execute();

    boolean updateHasText() {
        return update.hasMessage() && update.getMessage().hasText();
    }

    Respond respond() {
        return respondService.respond(Utils.getChatId(update));
    }

    Respond reply(String text) {
        return respondService.respond(Utils.getChatId(update)).append(text);
    }

    boolean error(String text) {
        reply(text);
        return false;
    }

    String messageText() {
        return update.getMessage().getText();
    }
}

