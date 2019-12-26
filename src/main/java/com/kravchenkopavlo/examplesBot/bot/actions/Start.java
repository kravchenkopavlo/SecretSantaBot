package com.kravchenkopavlo.examplesBot.bot.actions;

import com.kravchenkopavlo.examplesBot.bot.RespondService;
import com.kravchenkopavlo.examplesBot.bot.Utils;

import java.util.regex.Pattern;

public class Start extends AbstractAction {

    private static final Pattern start = Pattern.compile("^/?start.*",Pattern.CASE_INSENSITIVE);

    public Start(RespondService respondService) {
        super(respondService);
    }

    @Override
    boolean check() {
        return updateHasText() && start.matcher(messageText()).matches() && Utils.getSender(update).getId().equals(368098997);
    }

    @Override
    public boolean execute() {
        reply("Записываемся!").addInlineKeyboard("Записаться%data%ADD");
        return true;
    }
}
