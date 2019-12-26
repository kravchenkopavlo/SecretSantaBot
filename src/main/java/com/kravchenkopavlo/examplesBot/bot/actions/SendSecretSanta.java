package com.kravchenkopavlo.examplesBot.bot.actions;

import com.kravchenkopavlo.examplesBot.bot.Bot;
import com.kravchenkopavlo.examplesBot.bot.RespondService;
import com.kravchenkopavlo.examplesBot.bot.Utils;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SendSecretSanta extends AbstractAction {

    private static final Pattern id = Pattern.compile(".*\\((\\d+)\\).*");
    private final Bot bot;

    public SendSecretSanta(RespondService respondService, Bot bot) {
        super(respondService);
        this.bot = bot;
    }

    @Override
    boolean check() {
        return updateHasCallBackData() && callbackData().equals("ROLL") && Utils.getSender(update).getId().equals(368098997);
    }

    @Override
    public boolean execute() {
        final List<String> persons = new ArrayList<>();
        final String msgText = messageText();
        if (msgText == null) return false;
        for (String line : msgText.split("\n")) {
            if (!line.matches("^(Записываемся!|Добавлен:|Записаны:).*")) {
                persons.add(line);
            }
        }

//        if (!areReady(persons)) return false;

        final List<String> notPicked = new ArrayList<>(persons);
        persons.forEach(person -> {
            final Matcher matcher = id.matcher(person);
            if (matcher.find()) {
                final int userId = Integer.parseInt(matcher.group(1));
                int pick;
                while (true) {
                    final int index = (int) Math.round(Math.random() * (notPicked.size() - 1));
                    if (!notPicked.get(index).contains("("+userId+")")) {
                        pick = index;
                        break;
                    }
                }
                respond(userId).addSendMessage("Поздравляю! Вы теперь тайный дед мороз для:\n<b>"+notPicked.get(pick)+"</b>\nУра! ");
                notPicked.remove(pick);
            }
        });
        return true;
    }

    private boolean areReady(List<String> persons) {
        final StringBuilder sb = new StringBuilder();
        for (String person : persons) {
            final Matcher matcher = id.matcher(person);
            if (matcher.find()) {
                final int id = Integer.parseInt(matcher.group(1));
                final boolean isReady = isReady(id);
                if (!isReady) {
                    sb.append(person).append("\n");
                }
            }
        }
        if (sb.length() > 0) {
            reply("Не могу связаться с:\n"+sb.toString());
            return false;
        }
        return true;
    }

    private boolean isReady(int id) {
        try {
            final SendMessage testMessage = new SendMessage()
                    .setChatId((long)id)
                    .setText("Проверка связи. Если вы видите это сообщение - всё ок.");
            bot.execute(testMessage);
            return true;
        } catch (TelegramApiException e) {
            e.printStackTrace();
            return false;
        }
    }
}
