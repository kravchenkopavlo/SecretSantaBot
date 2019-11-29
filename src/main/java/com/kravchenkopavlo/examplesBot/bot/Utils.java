package com.kravchenkopavlo.examplesBot.bot;

import org.telegram.telegrambots.meta.api.objects.Update;

public class Utils {

    public static Long getChatId(Update update) {
        if (update.hasMessage()) return update.getMessage().getChatId();
        if (update.hasCallbackQuery()) return update.getCallbackQuery().getMessage().getChatId();
        if (update.hasEditedMessage()) return update.getEditedMessage().getChatId();
        if (update.hasChannelPost()) return update.getChannelPost().getChatId();
        return null;
    }
}
