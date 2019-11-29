package com.kravchenkopavlo.examplesBot;

import com.kravchenkopavlo.examplesBot.bot.BotService;

public class Main {
    public static void main(String[] args) {
        new BotService().start(
                "BarbequeMakerBot",
                "885202064:AAHsTnSiwqjlVuhsfZ9SVALNC78qlv4kBo8");
    }
}
