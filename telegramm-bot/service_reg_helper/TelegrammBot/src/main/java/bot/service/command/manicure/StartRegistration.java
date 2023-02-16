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

public class StartRegistration extends ResponseService {

    @Override
    public List<PartialBotApiMethod<? extends Serializable>> responseAction(Update update) {
        long chatId = update.hasCallbackQuery()? update.getCallbackQuery().getMessage().getChatId(): update.getMessage().getChatId();

        dataManager.manicureRegStart(chatId);

        return List.of(
                KeyboardsManager.removeKeyboard("Мы рады, что Вы выбрали именно нас!", update),
                selectType(update)
        );
    }

    private SendMessage selectType(Update update) {
        long chatId = update.hasCallbackQuery()? update.getCallbackQuery().getMessage().getChatId(): update.getMessage().getChatId();

        SendMessage message = new SendMessage();
        String answer = "На сегодняшний день цены таковы: \n"+
                dataManager.getServicesAndPricesString()+
                "\nКакая услуга Вас интересует? ";

        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        List<InlineKeyboardButton> btnInline;

        var services = dataManager.getServices();

        for (String service: services) {
            btnInline = new ArrayList<>();
            btnInline.add(KeyboardsManager.getInlineKeyboardButton(service, "BSReg_"+service));
            rows.add(btnInline);
        }


        btnInline = new ArrayList<>();
        btnInline.add(KeyboardsManager.getInlineKeyboardButton("Возможно позже", "BSReg_later"));
        rows.add(btnInline);

        markupInline.setKeyboard(rows);
        message.setReplyMarkup(markupInline);
        message.setChatId(chatId);
        message.setText(answer);

        return message;
    }


}
