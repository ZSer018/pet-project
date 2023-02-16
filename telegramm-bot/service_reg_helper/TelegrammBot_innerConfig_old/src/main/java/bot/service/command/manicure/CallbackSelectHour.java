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

public class CallbackSelectHour extends ResponseService {

    @Override
    public List<PartialBotApiMethod<? extends Serializable>> responseAction(Update update) {
        return List.of(selectHour(update));
    }

    private EditMessageText selectHour(Update update) {
        long message_id = update.getCallbackQuery().getMessage().getMessageId();
        long chat_id = update.getCallbackQuery().getMessage().getChatId();

        EditMessageText new_message = new EditMessageText();
        String answer = "Выберите пожалуйста время";
        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        List<InlineKeyboardButton> rowInline;

        var manicureObject = dataManager.getManicureRegObject(chat_id);
        if (manicureObject.getDate() == null) {
            manicureObject.setDate(update.getCallbackQuery().getData().replaceAll("BSReg_day_", ""));
        }

        var temp = dataManager.getRegHours(chat_id);
        for (var s : temp) {
            rowInline = new ArrayList<>();
            rowInline.add(KeyboardsManager.getInlineKeyboardButton(s, "BSReg_hour_"+s));
            rowsInline.add(rowInline);
        }
        rowInline = new ArrayList<>();
        rowInline.add(KeyboardsManager.getInlineKeyboardButton("Возможно позже", "BSReg_later"));
        rowsInline.add(rowInline);

        markupInline.setKeyboard(rowsInline);
        new_message.setReplyMarkup(markupInline);
        new_message.setChatId(chat_id);
        new_message.setMessageId((int) message_id);
        new_message.setText(answer);


        return new_message;
    }

}