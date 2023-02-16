package bot.simplemessage;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;


public class SimpleSendMessage {

    private final String messageText;
    private long userId = 0;

    public SimpleSendMessage(String messageText, long userId) {
        this.messageText = messageText;
        this.userId = userId;
    }

    public SendMessage getNewMessage(Update update) {
        if (userId == 0) {
            userId = update.hasCallbackQuery() ? update.getCallbackQuery().getMessage().getChatId() : update.getMessage().getChatId();
        }
        SendMessage message = new SendMessage();
        message.setChatId(userId);
        message.setText(messageText);
        return message;
    }
}
