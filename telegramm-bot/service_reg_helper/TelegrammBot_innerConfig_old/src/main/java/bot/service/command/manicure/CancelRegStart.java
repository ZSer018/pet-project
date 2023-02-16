package bot.service.command.manicure;

import bot.service.ResponseService;
import bot.managers.KeyboardsManager;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class CancelRegStart extends ResponseService {

    @Override
    public List<PartialBotApiMethod<? extends Serializable>> responseAction(Update update) {
        return List.of(pleaseConfirmCansel(update));
    }

    private SendMessage pleaseConfirmCansel(Update update) {
        long chatId = update.hasCallbackQuery()? update.getCallbackQuery().getMessage().getChatId(): update.getMessage().getChatId();

        SendMessage message = new SendMessage();
        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();

        List<InlineKeyboardButton> rowInline = new ArrayList<>();
        rowInline.add(KeyboardsManager.getInlineKeyboardButton("Да, отменить запись", "manicure_reg_cancel_yes"));
        rowsInline.add(rowInline);

        rowInline = new ArrayList<>();
        rowInline.add(KeyboardsManager.getInlineKeyboardButton("Нет", "manicure_reg_cancel_no"));
        rowsInline.add(rowInline);


        markupInline.setKeyboard(rowsInline);
        message.setReplyMarkup(markupInline);
        message.setChatId(chatId);
        message.setText(dataManager.getCustomerObject(chatId).getName()+ ", Вы уверены что хотите отменить свою запись? \nВы записаны:\n"+dataManager.getUserManicureRegData(chatId));
        return message;
    }
}
