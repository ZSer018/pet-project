package bot;

import bot.service.command.filter.MainCommandTypeFilter;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.*;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.Serializable;
import java.util.List;


public class Bot extends TelegramLongPollingBot{

    private final String BOT_TOKEN;
    private final String BOT_NAME;

    public Bot(String BOT_TOKEN, String BOT_NAME) {
        this.BOT_TOKEN = BOT_TOKEN;
        this.BOT_NAME = BOT_NAME;
    }

    @Override
    public String getBotUsername() {
        return BOT_NAME;
    }

    @Override
    public String getBotToken() {
        return BOT_TOKEN;
    }

    //TODO логирование


    @Override
    public void onUpdateReceived(Update update) {
        executeMessage(new MainCommandTypeFilter().responseAction(update));
    }

    public void executeMessage(List<PartialBotApiMethod<? extends Serializable>> messages) {
        messages.forEach(message -> {
            String x = message.getClass().getName().substring(message.getClass().getName().lastIndexOf(".") + 1);
            try {
                switch (x) {
                    case "SendPhoto":
                        execute((SendPhoto) message);
                        break;
                    case "SendMediaGroup":
                        execute((SendMediaGroup) message);
                        break;
                    case "SendMessage":
                        execute((SendMessage) message);
                        break;
                    case "EditMessageText":
                        execute((EditMessageText) message);
                        break;
                }
            } catch (TelegramApiException e) {
                throw new IllegalStateException(e);
            }
        });
    }


}