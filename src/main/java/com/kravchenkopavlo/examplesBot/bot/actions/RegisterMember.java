package com.kravchenkopavlo.examplesBot.bot.actions;

import com.kravchenkopavlo.examplesBot.bot.RespondService;
import com.kravchenkopavlo.examplesBot.bot.Utils;
import org.telegram.telegrambots.meta.api.objects.User;

import java.util.ArrayList;
import java.util.List;

public class RegisterMember extends AbstractAction {

    public RegisterMember(RespondService respondService) {
        super(respondService);
    }

    @Override
    boolean check() {
        return updateHasCallBackData() && callbackData().equals("ADD");
    }

    @Override
    public boolean execute() {
        final List<String> alreadyAdded = new ArrayList<>();
        final String msgText = messageText();
        if (msgText == null) return false;
        for (String line : msgText.split("\n")) {
            if (!line.matches("^(Записываемся!|Добавлен:|Записаны:).*")) {
                alreadyAdded.add(line);
            }
        }

        final User sender = Utils.getSender(update);
        if (sender == null) return false;
        final Integer senderId = sender.getId();

        if (!messageText().contains("("+senderId+")")) {
            final StringBuilder sb = new StringBuilder();
            sb.append("Добавлен: ").append(Utils.getSenderClickableName(update)).append("\n").append("Записаны:\n");
            alreadyAdded.forEach(line -> sb.append(line).append("\n"));
            sb.append(alreadyAdded.size()+1)
                    .append(". ")
                    .append(Utils.getSenderClickableName(update))
                    .append("(")
                    .append(Utils.getSender(update).getId())
                    .append(")\n");
            edit(sb.toString(),"Записаться%data%ADD%btn%Разыграть%data%ROLL");
            return true;
        }
        return false;
    }
}
