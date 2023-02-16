package bot.service.command.manicure;

import bot.service.ResponseService;
import bot.managers.KeyboardsManager;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class CallbackSelectWeek extends ResponseService {

    @Override
    public List<PartialBotApiMethod<? extends Serializable>> responseAction(Update update) {
        return List.of(selectWeek(update));
    }


    private EditMessageText selectWeek(Update update) {
        long messageId = update.hasCallbackQuery()? update.getCallbackQuery().getMessage().getMessageId(): update.getMessage().getChatId();
        long chatId = update.hasCallbackQuery()? update.getCallbackQuery().getMessage().getChatId(): update.getMessage().getChatId();

        EditMessageText new_message = new EditMessageText();
        String answer = "В какие дни Вы бы хотели записаться?";
        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        List<InlineKeyboardButton> rowInline;

        String mtype = update.getCallbackQuery().getData().replace("BSReg_", "");
        var manicureObject = dataManager.getManicureRegObject(chatId);
        manicureObject.setManicureType(mtype);
        manicureObject.setCost(dataManager.getServicePrice(mtype));

        var temp = dataManager.getRegWeeks();
        for (var s : temp.entrySet()) {
            rowInline = new ArrayList<>();
            rowInline.add(KeyboardsManager.getInlineKeyboardButton(s.getKey(), "BSReg_week_"+s.getValue()));
            rowsInline.add(rowInline);
        }
        rowInline = new ArrayList<>();
        rowInline.add(KeyboardsManager.getInlineKeyboardButton("Возможно позже", "BSReg_later"));
        rowsInline.add(rowInline);

        markupInline.setKeyboard(rowsInline);
        new_message.setReplyMarkup(markupInline);
        new_message.setChatId(chatId);
        new_message.setMessageId((int) messageId);
        new_message.setText(answer);

        return new_message;
    }
}
