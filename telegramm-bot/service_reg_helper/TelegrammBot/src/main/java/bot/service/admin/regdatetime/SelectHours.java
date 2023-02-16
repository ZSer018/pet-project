package bot.service.admin.regdatetime;

import bot.managers.KeyboardsManager;
import bot.service.ResponseService;
import bot.simplemessage.SimpleEditMessage;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SelectHours extends ResponseService {

    private final String date;

    public SelectHours(String date) {
        this.date = date;
    }

    @Override
    public List<PartialBotApiMethod<? extends Serializable>> responseAction(Update update) {
        return List.of(
                new SimpleEditMessage("Для отключения записи на конкретное время, выберите его пожалуйста в меню ниже").getNewEditMessage(update),
                selectHour("Вот доступное для выбора время: ", update)
        );
    }


    public SendMessage selectHour(String messageText, Update update) {
        long chatId = update.hasCallbackQuery()? update.getCallbackQuery().getMessage().getChatId(): update.getMessage().getChatId();
        SendMessage message = new SendMessage();

        message.setChatId(chatId);

        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboard = new ArrayList<>();
        KeyboardRow row = null;

        var temp = dataManager.getDateTime(date);

        //unused field date im admin object
        dataManager.setCustomerSignUpDate(dataManager.ADMIN_ID, date);

        if (temp.size() > 0) {

            for (String s: temp) {
                row = new KeyboardRow();
                row.add(s);
                keyboard.add(row);
            }

            row = new KeyboardRow();
            row.add("<< В начало");
            keyboard.add(row);

            keyboardMarkup.setKeyboard(keyboard);
            keyboardMarkup.setResizeKeyboard(true);
            message.setReplyMarkup(keyboardMarkup);
            message.setText(messageText);

        } else {
            message.setText("Нет доступного для выбора времени");
        }
        return message;
    }





}
