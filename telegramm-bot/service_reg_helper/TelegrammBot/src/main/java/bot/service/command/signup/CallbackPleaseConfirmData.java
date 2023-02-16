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
import java.util.Arrays;
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
        //String[] data = update.getMessage().getText().replaceAll("\\s{2,}", " ").split(" ");

        String name = null;
        String phoneNum = null;

        if (update.getMessage().getText().indexOf(" ") > 1) {
            name = update.getMessage().getText().substring(0, update.getMessage().getText().indexOf(" "));
            phoneNum = update.getMessage().getText().substring(update.getMessage().getText().indexOf(" ") + 1);
        } else {
            name = update.getMessage().getText();
            phoneNum = "-не задан-";
        }
        String tgUsername = update.getMessage().getChat().getUserName();

        if (tgUsername == null) {
            tgUsername = "Не установлено";
        } else {
            tgUsername = "@" + tgUsername;
        }

//        if (data.length == 1) {
            dataManager.setUserName(chatId, name);
            dataManager.setUserPhone(chatId,phoneNum);
            dataManager.setCustomerTgUsername(chatId, tgUsername);
/*        } else {
            dataManager.setUserName(chatId, data[0]);
            dataManager.setUserPhone(chatId,data[1]);
            dataManager.setCustomerTgUsername(chatId, tgUsername);
        }*/
        dataManager.setUserTgId(chatId,0);

        String answer = "Этап 2: проверка введенных Вами данных для завершения регистрации\n\nПравильно ли введены Ваши данные? \n"
                + "Имя: "+ dataManager.getUserName(chatId) + "\n"
                + "Телефон: "+ dataManager.getUserPhone(chatId) + "\n"
                + "Telegram: "+ dataManager.getCustomerTgUsername(chatId) + "\n";

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
