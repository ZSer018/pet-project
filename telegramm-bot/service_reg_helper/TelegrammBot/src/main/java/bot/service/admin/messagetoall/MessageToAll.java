package bot.service.admin.messagetoall;

import bot.managers.KeyboardsManager;
import bot.service.ResponseService;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MessageToAll extends ResponseService {
    @Override
    public List<PartialBotApiMethod<? extends Serializable>> responseAction(Update update) {
        dataManager.admin_stringMessageToAll = update.getMessage().getText();

        return List.of( pleaseConfirmSending(update) );
    }

    private SendMessage pleaseConfirmSending(Update update) {
        long chatId = update.hasCallbackQuery()? update.getCallbackQuery().getMessage().getChatId(): update.getMessage().getChatId();

        SendMessage message = new SendMessage();
        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();

        List<InlineKeyboardButton> rowInline = new ArrayList<>();
        rowInline.add(KeyboardsManager.getInlineKeyboardButton("Да, отправить!", "sendMessageToAll_yes"));
        rowsInline.add(rowInline);

        rowInline = new ArrayList<>();
        rowInline.add(KeyboardsManager.getInlineKeyboardButton("Нет, не надо", "sendMessageToAll_later"));
        rowsInline.add(rowInline);


        markupInline.setKeyboard(rowsInline);
        message.setReplyMarkup(markupInline);
        message.setChatId(chatId);
        message.setText( "Отправить всем сообщение\n\n"+dataManager.admin_stringMessageToAll+"\n\nвсем вашим клиентам?");
        return message;
    }
}
