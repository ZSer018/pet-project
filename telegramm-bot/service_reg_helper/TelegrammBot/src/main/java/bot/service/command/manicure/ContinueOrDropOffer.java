package bot.service.command.manicure;

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

public class ContinueOrDropOffer extends ResponseService {

    @Override
    public List<PartialBotApiMethod<? extends Serializable>> responseAction(Update update) {
        long chatId = update.hasCallbackQuery() ? update.getCallbackQuery().getMessage().getChatId() : update.getMessage().getChatId();

        SendMessage message = new SendMessage();
        String answer = "Вы не закончили процесс регистрации на услугу, "+dataManager.getUserName(chatId)+". Хотите отказаться от него и вернуться в главное меню?";
        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();

        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        List<InlineKeyboardButton> btnInline;

        btnInline = new ArrayList<>();
        btnInline.add(KeyboardsManager.getInlineKeyboardButton("Да, хочу", "BSReg_abort"));
        rows.add(btnInline);

        btnInline = new ArrayList<>();
        btnInline.add(KeyboardsManager.getInlineKeyboardButton("Нет", "BSReg_continue"));
        rows.add(btnInline);

        markupInline.setKeyboard(rows);
        message.setReplyMarkup(markupInline);
        message.setChatId(chatId);
        message.setText(answer);

        return List.of(message);
    }



}
