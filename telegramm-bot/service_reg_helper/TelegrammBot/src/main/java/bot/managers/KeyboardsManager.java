package bot.managers;

import bot.simplemessage.SimpleSendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class KeyboardsManager {

    private static final DataManager dataManager = DataManager.getInstance();

    public static SendMessage getUserKeyboard(String messageText, Update update){
        long chatId = update.hasCallbackQuery()? update.getCallbackQuery().getMessage().getChatId(): update.getMessage().getChatId();

        if (dataManager.getUserSignUpStatus(chatId) == DataManager.userSignUpStatus.SIGNUP_COMPLETED){
            return getSignInKeyboard(messageText, update);
        }

        if (dataManager.getUserSignUpStatus(chatId) == DataManager.userSignUpStatus.NO_SIGNUP){
            return getStartKeyboard(messageText, update);
        }
        return getStartKeyboard(messageText, update);
    }

    public static SendMessage getStartKeyboard(String messageText, Update update) {
        long chatId = update.hasCallbackQuery()? update.getCallbackQuery().getMessage().getChatId(): update.getMessage().getChatId();
        SendMessage message = new SendMessage();
        message.setText(messageText);

        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboard = new ArrayList<>();
        KeyboardRow row = new KeyboardRow();
        row.add("Услуги и цены");
        keyboard.add(row);

        row = new KeyboardRow();
        row.add("Посмотреть портфолио");
        keyboard.add(row);

        row = new KeyboardRow();
        row.add("Зарегистрироваться");
        keyboard.add(row);

        row = new KeyboardRow();
        row.add("Написать мастеру");
        keyboard.add(row);

        keyboardMarkup.setKeyboard(keyboard);
        keyboardMarkup.setResizeKeyboard(true);
        message.setReplyMarkup(keyboardMarkup);
        message.setChatId(chatId);

        return message;
    }

    public static SendMessage getSignCancelKeyboard(String messageText, Update update) {
        long chatId = update.hasCallbackQuery()? update.getCallbackQuery().getMessage().getChatId(): update.getMessage().getChatId();
        SendMessage message = new SendMessage();

        message.setText(Objects.requireNonNullElse(messageText, "Этап 1:\n\n" +
                "Для регистрации введите пожалуйста через пробел ваше имя и, при желании, телефон. \n" +
                "Он необходим для связи с Вами на случай, если по каким-либо причинам не получится связаться иным способом. \n" +
                "Мы гарантируем, что Ваш телефон не будет передан третьим лицам и/или использован для распространения рекламы\n" +
                "\n\n" +
                "Пример вводимых данных: \nСветлана +7888888888 \n\nИли просто: \nСветлана"));

        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboard = new ArrayList<>();
        KeyboardRow row = new KeyboardRow();

        if (chatId == dataManager.ADMIN_ID) {
            row.add("Закончить");
        } else if (dataManager.customerManicureRegStatus(chatId) == DataManager.manicureRegStatus.NO_REG_ERROR) {
            row.add("Отказаться от регистрации");
        } else {
            row.add("Отказаться от записи");
        }

        keyboard.add(row);
        keyboardMarkup.setKeyboard(keyboard);
        keyboardMarkup.setResizeKeyboard(true);
        message.setReplyMarkup(keyboardMarkup);
        message.setChatId(chatId);

        return message;
    }

    public static SendMessage getSignInKeyboard(String messageText, Update update) {
        long chatId = update.hasCallbackQuery()? update.getCallbackQuery().getMessage().getChatId(): update.getMessage().getChatId();

        SendMessage message = new SendMessage();
        message.setText(messageText);

        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboard = new ArrayList<>();
        KeyboardRow row;

        row = new KeyboardRow();
        row.add("Услуги и цены");
        keyboard.add(row);

        row = new KeyboardRow();
        if (dataManager.customerManicureRegStatus(chatId) == DataManager.manicureRegStatus.REG_COMPLETE) {
            row.add("Напомнить время записи");
            keyboard.add(row);
            row = new KeyboardRow();
            row.add("Отменить запись");
        } else {
            row.add("Записаться на маникюр");
       }
        keyboard.add(row);


        row = new KeyboardRow();
        row.add("Посмотреть портфолио");
        keyboard.add(row);

        row = new KeyboardRow();
        row.add("Написать мастеру");
        keyboard.add(row);

        keyboardMarkup.setKeyboard(keyboard);
        keyboardMarkup.setResizeKeyboard(true);
        message.setReplyMarkup(keyboardMarkup);

        message.setChatId(chatId);
        return message;
    }


    public static InlineKeyboardButton getInlineKeyboardButton(String text, String callBackData){
        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText(text);
        button.setCallbackData(callBackData);
        return button;
    }

    public static SendMessage removeKeyboard(String messageText, Update update){
        long chatId = update.hasCallbackQuery()? update.getCallbackQuery().getMessage().getChatId(): update.getMessage().getChatId();
        SendMessage message = new SendMessage();
        message.setText(messageText);
        message.setChatId(chatId);
        ReplyKeyboardRemove remove = new ReplyKeyboardRemove();
        remove.setRemoveKeyboard(true);
        message.setReplyMarkup(remove);
        return message;
    }




    public static SendMessage adminMainKeyboard(String messageText, Update update) {
        long chatId = update.hasCallbackQuery()? update.getCallbackQuery().getMessage().getChatId(): update.getMessage().getChatId();
        SendMessage message = new SendMessage();
        message.setText(messageText);

        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboard = new ArrayList<>();
        KeyboardRow row = new KeyboardRow();
        row.add("Услуги и цены");
        keyboard.add(row);

        row = new KeyboardRow();
        row.add("Дополнительное сообщение");
        keyboard.add(row);

        row = new KeyboardRow();
        row.add("Фотографии");
        keyboard.add(row);

        row = new KeyboardRow();
        row.add("Календарь записи");
        keyboard.add(row);

        row = new KeyboardRow();
        row.add("Сообщение всем");
        keyboard.add(row);

        row = new KeyboardRow();
        row.add("Настройки");
        keyboard.add(row);

        keyboardMarkup.setKeyboard(keyboard);
        keyboardMarkup.setResizeKeyboard(true);
        message.setReplyMarkup(keyboardMarkup);
        message.setChatId(chatId);

        return message;
    }


    public static SendMessage adminSettingsKeyboard(String messageText, Update update) {
        long chatId = update.hasCallbackQuery()? update.getCallbackQuery().getMessage().getChatId(): update.getMessage().getChatId();
        SendMessage message = new SendMessage();
        message.setText(messageText);

        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboard = new ArrayList<>();
        KeyboardRow row = new KeyboardRow();
        row.add("Вкл/выкл уведомления о записи");
        keyboard.add(row);

        row = new KeyboardRow();
        row.add("Вкл/выкл запись на маникюр");
        keyboard.add(row);

        row = new KeyboardRow();
        row.add("<< В начало");
        keyboard.add(row);


        keyboardMarkup.setKeyboard(keyboard);
        keyboardMarkup.setResizeKeyboard(true);
        message.setReplyMarkup(keyboardMarkup);
        message.setChatId(chatId);

        return message;
    }



    public static SendMessage adminPhotoManagementKeyboard(String messageText, Update update) {
        long chatId = update.hasCallbackQuery()? update.getCallbackQuery().getMessage().getChatId(): update.getMessage().getChatId();
        SendMessage message = new SendMessage();
        message.setText(messageText);

        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboard = new ArrayList<>();
        KeyboardRow row = new KeyboardRow();
        row.add("Посмотреть фотографии");
        keyboard.add(row);

        row = new KeyboardRow();
        row.add("Добавить фотографии");
        keyboard.add(row);

        row = new KeyboardRow();
        row.add("Удалить фотографии");
        keyboard.add(row);

        row = new KeyboardRow();
        row.add("<< В начало");
        keyboard.add(row);

        keyboardMarkup.setKeyboard(keyboard);
        keyboardMarkup.setResizeKeyboard(true);
        message.setReplyMarkup(keyboardMarkup);
        message.setChatId(chatId);

        return message;
    }


    public static SendMessage choosePhotoTypeKeyboard(String messageText, Update update) {
        long chatId = update.hasCallbackQuery() ? update.getCallbackQuery().getMessage().getChatId() : update.getMessage().getChatId();
        SendMessage message = new SendMessage();
        message.setText(messageText);

        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboard = new ArrayList<>();

        dataManager.getServicesByName().forEach(s -> {
            var kr = new KeyboardRow();
            kr.add(s);
            keyboard.add(kr);
        });

        var kr = new KeyboardRow();
        kr.add("Закончить");
        keyboard.add(kr);

        keyboardMarkup.setKeyboard(keyboard);
        keyboardMarkup.setResizeKeyboard(true);
        message.setReplyMarkup(keyboardMarkup);
        message.setChatId(chatId);

        return message;
    }




    public static SendMessage adminServicesAndPricesKeyboard(String messageText, Update update) {
        long chatId = update.hasCallbackQuery()? update.getCallbackQuery().getMessage().getChatId(): update.getMessage().getChatId();
        SendMessage message = new SendMessage();
        message.setText(messageText);

        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboard = new ArrayList<>();
        KeyboardRow row = new KeyboardRow();
        row.add("Показать услуги и цены");
        keyboard.add(row);

        row = new KeyboardRow();
        row.add("Добавить услугу");
        keyboard.add(row);

        row = new KeyboardRow();
        row.add("Удалить услугу");
        keyboard.add(row);

        row = new KeyboardRow();
        row.add("Изменить цену");
        keyboard.add(row);

        row = new KeyboardRow();
        row.add("<< В начало");
        keyboard.add(row);

        keyboardMarkup.setKeyboard(keyboard);
        keyboardMarkup.setResizeKeyboard(true);
        message.setReplyMarkup(keyboardMarkup);
        message.setChatId(chatId);

        return message;
    }


    public static SendMessage adminRegCalendarKeyboard(String messageText, Update update) {
        long chatId = update.hasCallbackQuery()? update.getCallbackQuery().getMessage().getChatId(): update.getMessage().getChatId();
        SendMessage message = new SendMessage();
        message.setText(messageText);

        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboard = new ArrayList<>();
        KeyboardRow row = new KeyboardRow();
        row.add("Все записи");
        keyboard.add(row);

        row = new KeyboardRow();
        row.add("На сегодня");
        keyboard.add(row);

        row = new KeyboardRow();
        row.add("Свободные даты");
        keyboard.add(row);

        row = new KeyboardRow();
        row.add("Редактирование...");
        keyboard.add(row);

        row = new KeyboardRow();
        row.add("<< В начало");
        keyboard.add(row);

        keyboardMarkup.setKeyboard(keyboard);
        keyboardMarkup.setResizeKeyboard(true);
        message.setReplyMarkup(keyboardMarkup);
        message.setChatId(chatId);

        return message;
    }


    public static SendMessage adminRegCalendarEditKeyboard(String messageText, Update update) {
        long chatId = update.hasCallbackQuery()? update.getCallbackQuery().getMessage().getChatId(): update.getMessage().getChatId();
        SendMessage message = new SendMessage();
        message.setText(messageText);

        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboard = new ArrayList<>();
        KeyboardRow row = new KeyboardRow();
        row.add("Отказывать в записи после... ");
        keyboard.add(row);

        row = new KeyboardRow();
        row.add("Разрешить запись после...");
        keyboard.add(row);

        row = new KeyboardRow();
        row.add("Редактировать дату...");
        keyboard.add(row);

        row = new KeyboardRow();
        row.add("<< В начало");
        keyboard.add(row);

        keyboardMarkup.setKeyboard(keyboard);
        keyboardMarkup.setResizeKeyboard(true);
        message.setReplyMarkup(keyboardMarkup);
        message.setChatId(chatId);

        return message;
    }

}
