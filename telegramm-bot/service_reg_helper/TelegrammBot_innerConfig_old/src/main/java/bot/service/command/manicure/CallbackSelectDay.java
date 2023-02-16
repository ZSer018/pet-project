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

public class CallbackSelectDay extends ResponseService {

    @Override
    public List<PartialBotApiMethod<? extends Serializable>> responseAction(Update update) {
        return List.of(selectDay(update));
    }

    private EditMessageText selectDay(Update update) {
        long message_id = update.getCallbackQuery().getMessage().getMessageId();
        long chat_id = update.getCallbackQuery().getMessage().getChatId();

        EditMessageText new_message = new EditMessageText();
        String answer = "Выберите пожалуйста интересующий Вас день";
        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        List<InlineKeyboardButton> rowInline;

        var manicureObject = dataManager.getManicureRegObject(chat_id);
        if (manicureObject.getChosenWeek() == null) {
            manicureObject.setChosenWeek(update.getCallbackQuery().getData().replaceAll("BSReg_week_", ""));
        }

        var temp = dataManager.getRegDays(chat_id);
        for (var s : temp.entrySet()) {
            rowInline = new ArrayList<>();
            rowInline.add(KeyboardsManager.getInlineKeyboardButton(s.getValue(), "BSReg_day_"+s.getKey()));
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
