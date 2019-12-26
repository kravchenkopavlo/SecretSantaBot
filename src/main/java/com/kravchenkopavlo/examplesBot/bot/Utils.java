package com.kravchenkopavlo.examplesBot.bot;

import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

public class Utils {

    public static Long getChatId(Update update) {
        if (update.hasMessage()) return update.getMessage().getChatId();
        if (update.hasCallbackQuery()) return update.getCallbackQuery().getMessage().getChatId();
        if (update.hasEditedMessage()) return update.getEditedMessage().getChatId();
        if (update.hasChannelPost()) return update.getChannelPost().getChatId();
        return null;
    }

    public static String getSenderClickableName(Update update) {
        final User sender = getSender(update);
        if (sender == null) return null;

        final String senderUserName = sender.getUserName();
        if (senderUserName != null) return "@"+senderUserName;

        final StringBuilder sb = new StringBuilder();
        final String senderFirstName = sender.getFirstName();
        final String senderLastName = sender.getLastName();

        if (senderFirstName != null) sb.append(senderFirstName).append(" ");
        if (senderLastName != null) sb.append(senderLastName).append(" ");

        if (sb.length() == 0) sb.append("User?id=").append(sender.getId());

        return sb.toString();
    }

    public static User getSender(Update update) {
        if (update.hasMessage()) return update.getMessage().getFrom();
        if (update.hasCallbackQuery()) return update.getCallbackQuery().getFrom();
        if (update.hasEditedMessage()) return update.getEditedMessage().getFrom();
        if (update.hasChannelPost()) return update.getChannelPost().getFrom();
        return null;
    }
}
