package com.kravchenkopavlo.examplesBot.bot.actions;

import com.kravchenkopavlo.examplesBot.bot.RespondService;
import com.kravchenkopavlo.examplesBot.bot.Utils;

public class SayHello extends AbstractAction {

    public SayHello(RespondService respondService) {
        super(respondService);
    }

    @Override
    boolean check() {
        return Utils.getChatId(this.update) != null;
    }

    @Override
    public boolean execute() {
        reply("Привет!");
        return true;
    }
}
