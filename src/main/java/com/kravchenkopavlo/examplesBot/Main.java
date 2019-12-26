package com.kravchenkopavlo.examplesBot;

import com.kravchenkopavlo.examplesBot.bot.BotService;

public class Main {
    public static void main(String[] args) {
        new BotService().start(
                "simpleSecretSantaBot",
                "758387571:AAFiUzzkVaFi8EvW8hKm7_gY14SJtATs5K4");
    }
}
