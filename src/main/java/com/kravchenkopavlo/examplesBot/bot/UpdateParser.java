package com.kravchenkopavlo.examplesBot.bot;

import com.kravchenkopavlo.examplesBot.bot.actions.AbstractAction;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.HashSet;
import java.util.Set;

class UpdateParser {

    private final Set<AbstractAction> actionSet;

    UpdateParser() {
        this.actionSet = new HashSet<>();
    }

    UpdateParser addAction(AbstractAction action) {
        synchronized (actionSet) {
            actionSet.add(action);
        }
        return this;
    }

    void proceed(Update update) {
        if (update == null) return;
        for (AbstractAction action : actionSet) {
            if (action.fits(update)) {
                final boolean success = action.execute();
                if (success) break;
            }
        }
    }
}
