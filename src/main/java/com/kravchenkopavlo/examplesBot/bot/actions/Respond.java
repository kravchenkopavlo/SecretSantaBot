package com.kravchenkopavlo.examplesBot.bot.actions;

import com.kravchenkopavlo.examplesBot.bot.Bot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.ForwardMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ForceReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Respond {

    private Bot bot;
    private Long chatId;
    private final List<BotApiMethod> sendBuffer;

    public Respond(Bot bot, Long chatId) {
        this.bot = bot;
        this.chatId = chatId;
        this.sendBuffer = new ArrayList<>();
    }

    public List<BotApiMethod> getSendBuffer() {
        List<BotApiMethod> result;
        synchronized (sendBuffer) {
            result = new ArrayList<>(this.sendBuffer);
            this.sendBuffer.clear();
        }
        return result;
    }

    //////////////////////////////////////////////////////////////////////////////////////////
    //// addSendMessage
    //////////////////////////////////////////////////////////////////////////////////////////

    Respond addSendMessage(String text, ReplyKeyboard replyKeyboard) {
        if (text!=null) {
            synchronized (sendBuffer) {
                sendBuffer.add(new SendMessage()
                        .setChatId(chatId)
                        .setText(text)
                        .setReplyMarkup(replyKeyboard)
                        .setParseMode("HTML"));
            }
            return this;
        } else return null;
    }

    Respond addSendMessage(String text) {
        return addSendMessage(text, null);
    }

    Respond append(String text) {
        synchronized (sendBuffer) {
            if (text != null && sendBuffer.size() > 0) {
                BotApiMethod botApiMethod = sendBuffer.get(sendBuffer.size()-1);
                if (botApiMethod instanceof SendMessage) {
                    ((SendMessage) botApiMethod).setText(((SendMessage) botApiMethod).getText()+text);
                } else {
                    return addSendMessage(text);
                }
            } else return addSendMessage(text);
        }
        return this;
    }

    Respond addInlineKeyboard(String keyboardAsText) {
        synchronized (sendBuffer) {
            if (sendBuffer.size() > 0) {
                final BotApiMethod previousAddedMethod = sendBuffer.get(sendBuffer.size() - 1);
                if (previousAddedMethod == null) return this;
                if (previousAddedMethod instanceof SendMessage) {
                    ((SendMessage) previousAddedMethod).setReplyMarkup(makeInlineKeyboard(keyboardAsText));
                }
            }
        }
        return this;
    }

    Respond addReplyKeyboard(String keyboardAsText) {
        synchronized (sendBuffer) {
            if (sendBuffer.size() > 0) {
                final BotApiMethod previousAddedMethod = sendBuffer.get(sendBuffer.size() - 1);
                if (previousAddedMethod == null) return this;
                if (previousAddedMethod instanceof SendMessage) {
                    ((SendMessage) previousAddedMethod).setReplyMarkup(makeReplyKeyboard(keyboardAsText));
                }
            }
        }
        return this;
    }

    Respond forceReply() {
        synchronized (sendBuffer) {
            if (sendBuffer.size() > 0) {
                final BotApiMethod previousAddedMethod = sendBuffer.get(sendBuffer.size() - 1);
                if (previousAddedMethod == null) return this;
                if (previousAddedMethod instanceof SendMessage) {
                    ((SendMessage) previousAddedMethod).setReplyMarkup(new ForceReplyKeyboard());
                }
            }
        }
        return this;
    }

    Respond enableOneTimeKeyboard() {
        synchronized (sendBuffer) {
            if (sendBuffer.size() > 0) {
                BotApiMethod previousAddedMethod = sendBuffer.get(sendBuffer.size() - 1);
                if (previousAddedMethod == null) return this;
                if (previousAddedMethod instanceof SendMessage) {
                    if (((SendMessage) previousAddedMethod).getReplyMarkup() instanceof ReplyKeyboardMarkup) {
                        final ReplyKeyboardMarkup rkm = (ReplyKeyboardMarkup) ((SendMessage) previousAddedMethod).getReplyMarkup();
                        rkm.setOneTimeKeyboard(true);
                        ((SendMessage) previousAddedMethod).setReplyMarkup(rkm);
                    }
                }
            }
        }
        return this;
    }

    private ReplyKeyboardMarkup makeReplyKeyboard(String text){
        final List<KeyboardRow> keyboard = new ArrayList<>();
        if (text.length() > 0) {
            KeyboardRow line;
            for (String rowString : text.split("%row%")) {
                if (rowString.length() > 0) {
                    line = new KeyboardRow();
                    for (String btnString : rowString.split("%btn%")) {
                        if (btnString.length() > 0) {
                            line.add(btnString);
                        }
                    }
                    keyboard.add(line);
                }
            }
        }
        return new ReplyKeyboardMarkup().setKeyboard(keyboard);
    }

    private InlineKeyboardMarkup makeInlineKeyboard(String text) {
        final List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        if (text != null && text.length() > 0) {
            List<InlineKeyboardButton> line;
            for (String rowString : text.split("%row%")) {
                if (rowString.length() > 0) {
                    line = new ArrayList<>();
                    for (String btnString : rowString.split("%btn%")) {
                        if (btnString.length() > 0) {
                            String[] data = btnString.split("%data%");
                            line.add(new InlineKeyboardButton(data[0]).setCallbackData(data[1]));
                        }
                    }
                    keyboard.add(line);
                }
            }
        }
        return new InlineKeyboardMarkup().setKeyboard(keyboard);
    }

    Respond editMessage(Update update, String newText) {
        return editMessage(update, newText, null);
    }

    Respond editMessage(Update update, String newText, String keyboardAsString) {
        Message message = update.hasCallbackQuery() ? update.getCallbackQuery().getMessage() : update.hasMessage() ? update.getMessage() : null;
        if (message != null) {
            synchronized (sendBuffer) {
                sendBuffer.add(new EditMessageText()
                        .setChatId(message.getChatId())
                        .setMessageId(message.getMessageId())
                        .setParseMode("HTML")
                        .setReplyMarkup(makeInlineKeyboard(keyboardAsString))
                        .setText(newText));
            }
        }
        return this;
    }

    Respond editKeyboard(Update update, String newKeyboard) {
        final Message message = update.hasCallbackQuery() ? update.getCallbackQuery().getMessage() : update.hasMessage() ? update.getMessage() : null;
        if (message != null) {
            synchronized (sendBuffer) {
                sendBuffer.add(new EditMessageReplyMarkup()
                        .setChatId(message.getChatId())
                        .setMessageId(message.getMessageId())
                        .setReplyMarkup(makeInlineKeyboard(newKeyboard)));
            }
        }
        return this;
    }

    Respond sendFile(String filename) {
        if (filename != null) {
            File file = new File(filename);
            return sendFile(file);
        }
        return this;
    }

    Respond sendFile(File file) {
        if (file.exists()) try {
            bot.execute(new SendDocument().setDocument(new InputFile(file,file.getName())).setChatId(chatId));
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
        return this;
    }

    Respond forwardMessage(Message message) {
        synchronized (sendBuffer) {
            sendBuffer.add(new ForwardMessage()
                    .setChatId(chatId)
                    .setMessageId(message.getMessageId())
                    .setFromChatId(message.getChatId()));
        }
        return this;
    }

    Respond deleteMessage(Message message) {
        synchronized (sendBuffer) {
            sendBuffer.add(new DeleteMessage()
                    .setChatId(chatId)
                    .setMessageId(message.getMessageId()));
        }
        return this;
    }
}

