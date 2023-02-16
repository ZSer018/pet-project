package bot.simplemessage;

import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;

public class SimpleEditMessage{

    private final String editMessageText;

   // private long userId = 0;

    public SimpleEditMessage(String editMessageText) {
        this.editMessageText = editMessageText;
        //this.userId = userId;
    }

    public EditMessageText getNewEditMessage(Update update) {
        long chatId = update.hasCallbackQuery()? update.getCallbackQuery().getMessage().getChatId(): update.getMessage().getChatId();
        int messageId = update.hasCallbackQuery()? update.getCallbackQuery().getMessage().getMessageId(): update.getMessage().getMessageId();

        EditMessageText message = new EditMessageText();
        message.setChatId(chatId);
        message.setMessageId(messageId);
        message.setText(editMessageText);
        return message;
    }
}
