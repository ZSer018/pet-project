package bot.service.command.signup;

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

public class CallbackPleaseConfirmData extends ResponseService {

    @Override
    public List<PartialBotApiMethod<? extends Serializable>> responseAction(Update update) {
        return List.of(
                KeyboardsManager.getSignCancelKeyboard("Завершающий этап", update),
                pleaseConfirmData(update))
                ;
    }

    public SendMessage pleaseConfirmData(Update update) {
        long chatId = update.getMessage().getChatId();
        String[] data = update.getMessage().getText().replaceAll("\\s{2,}", " ").split(" ");
        String tgUsername = update.getMessage().getChat().getUserName();
        if (tgUsername == null) {
            tgUsername = "Не установлено";
        } else {
            tgUsername = "@" + tgUsername;
        }

        var customer = dataManager.getCustomerObject(chatId);
        if (data.length == 1) {
            customer.setName(data[0]);
            customer.setPhone("-");
            customer.setTgUsername(tgUsername);
        } else {
            customer.setName(data[0]);
            customer.setPhone(data[1]);
            customer.setTgUsername(tgUsername);
        }
        customer.setTelegramId(0);

        String answer = "Этап 2: проверка введенных Вами данных для завершения регистрации\n\nПравильно ли введены Ваши данные? \n"+ customer;

        SendMessage message = new SendMessage();
        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();

        List<InlineKeyboardButton> rowInline = new ArrayList<>();
        InlineKeyboardButton okButton = new InlineKeyboardButton();
        okButton.setText("Да, все верно");
        okButton.setCallbackData("signUp_confirmOk");
        InlineKeyboardButton editButton = new InlineKeyboardButton();
        editButton.setText("Изменить данные");
        editButton.setCallbackData("signUp_editData");
        rowInline.add(okButton);
        rowInline.add(editButton);
        rowsInline.add(rowInline);
        markupInline.setKeyboard(rowsInline);
        message.setReplyMarkup(markupInline);
        message.setChatId(chatId);
        message.setText(answer);
        return message;
    }
}
