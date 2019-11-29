package com.kravchenkopavlo.examplesBot.bot;

import com.kravchenkopavlo.examplesBot.bot.actions.Respond;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RespondService {
    private final Bot bot;
    private final Map<Long, Respond> respondsMap;

    public RespondService(Bot bot) {
        this.bot = bot;
        respondsMap = new HashMap<>();
    }

    public Respond respond(Long chatId) {
        Respond respond;
        synchronized (respondsMap) {
            if (respondsMap.containsKey(chatId)) {
                respond = respondsMap.get(chatId);
            } else {
                respond = new Respond(bot, chatId);
                respondsMap.put(chatId, respond);
            }
        }
        return respond;
    }

    void sendAll() {
        synchronized (respondsMap) {
            respondsMap.values().forEach(respond ->
                    respond.getSendBuffer().forEach(botApiMethod -> {
                        try {
                            fixMethod(botApiMethod);
                            bot.execute(botApiMethod);
                        } catch (TelegramApiException exception) {
                            System.out.println("Exception while executing: " + botApiMethod.toString());
                            exception.printStackTrace();
                        }
                    })
            );
            respondsMap.clear();
        }
    }

    private void fixMethod(BotApiMethod method) {
        if (method instanceof SendMessage) {
            final String text = ((SendMessage) method).getText();
            ((SendMessage) method).setText(fixText(text));
        } else if (method instanceof EditMessageText) {
            final String text = ((EditMessageText) method).getText();
            ((EditMessageText) method).setText(fixText(text));
        }
    }

    private String fixText(String txt) {
        final String fixedText = txt.replaceAll("\\$","&#36;");
        final StringBuilder sb = new StringBuilder();
        final Matcher matcher = Pattern.compile("(<b>|<strong>|<i>|<em>|<pre>|<code>|<a +href ?= ?\"\\S+\">)").matcher(fixedText);
        int position = 0;
        while (true) {
            if (matcher.find()) {
                final String openTag = matcher.group(1);
                if (matcher.start() >= position) {
                    sb.append(removeBraces(fixedText.substring(position, matcher.start(1))));
                    final String closeTag = openTag.matches("<a +href ?= ?\"\\S+\">") ? "</a>"
                            : openTag.substring(0, 1) + "/" + openTag.substring(1);
                    final Matcher matcher2 = Pattern.compile("(" + closeTag + ")").matcher(fixedText.substring(matcher.end(1)));
                    if (matcher2.find()) {
                        sb.append(openTag);
                        sb.append(removeBraces(fixedText.substring(matcher.end(1), matcher.end(1) + matcher2.start(1))));
                        sb.append(closeTag);
                        position = matcher.end(1) + matcher2.end(1);
                    } else {
                        sb.append(removeBraces(openTag));
                        position = matcher.end(1);
                    }
                }
            } else {
                sb.append(removeBraces(fixedText.substring(position)));
                break;
            }
        }
        return sb.toString();
    }

    private String removeBraces(String text) {
        return text.replaceAll("<","&#60;").replaceAll(">","&#62;");
    }
}
