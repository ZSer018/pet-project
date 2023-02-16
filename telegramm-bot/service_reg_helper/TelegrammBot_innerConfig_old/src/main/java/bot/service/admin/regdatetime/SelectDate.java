package bot.service.admin.regdatetime;

import bot.managers.KeyboardsManager;
import bot.service.ResponseService;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SelectDate extends ResponseService {

    public static enum Action {OPEN_DATE, CLOSE_DATE}
    public static enum Type {DATE, HOURS}

    private final Type type;
    private final Action action;

    public SelectDate(Type type, Action action) {
        this.type = type;
        this.action = action;
    }

    @Override
    public List<PartialBotApiMethod<? extends Serializable>> responseAction(Update update) {
        return List.of(selectDay(update));
    }


    private SendMessage selectDay(Update update) {
        long chat_id = update.getMessage().getChatId();

        SendMessage new_message = new SendMessage();
        String answer = "Выберите необходимый день";
        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        List<InlineKeyboardButton> rowInline;

        var temp = dataManager.getServiceCalendar();

        String callbackType = null;

        if (type == Type.DATE) {
            callbackType = action == Action.CLOSE_DATE ? "adminSelectCloseDay_" : "adminSelectOpenDay_";
        } else {
            callbackType = "adminSelectHours_";
        }

        for (var entry: temp.entrySet()) {
            rowInline = new ArrayList<>();
            rowInline.add(KeyboardsManager.getInlineKeyboardButton(entry.getKey(), callbackType+entry.getValue()));
            rowsInline.add(rowInline);
        }
        rowInline = new ArrayList<>();
        rowInline.add(KeyboardsManager.getInlineKeyboardButton("Возможно позже", "adminSelectCancel"));
        rowsInline.add(rowInline);

        markupInline.setKeyboard(rowsInline);
        new_message.setReplyMarkup(markupInline);
        new_message.setChatId(chat_id);
        new_message.setText(answer);

        return new_message;
    }
}
