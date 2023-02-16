package bot.service.command.manicure;

import bot.service.ResponseService;
import bot.managers.KeyboardsManager;
import bot.simplemessage.SimpleEditMessage;
import bot.simplemessage.SimpleSendMessage;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class CallbackCancelReg extends ResponseService {

    private final String text;

    public CallbackCancelReg(String text) {
        this.text = text;
    }

    @Override
    public List<PartialBotApiMethod<? extends Serializable>> responseAction(Update update) {
        long chatId = update.hasCallbackQuery()? update.getCallbackQuery().getMessage().getChatId(): update.getMessage().getChatId();
        var reg = dataManager.getManicureRegObject(update.getCallbackQuery().getMessage().getChatId());

        dataManager.manicureCancelReg(chatId);
        dataManager.userSetDefault(chatId);

        var temp = new ArrayList<PartialBotApiMethod<? extends Serializable>>();
        if (dataManager.admin_regNotify) {
           temp.add( new SimpleSendMessage("Владыка, сжалься но у тебя отмена записи: \n"
                    +dataManager.getUserName(reg.getTelegramId())+"\n"
                    +dataManager.strDateToDateAndMonthName(reg.getDate())+",   "+reg.getTime()+"\n"
                    +reg.getManicureType()+"\n"
                    +"цена: "+reg.getCost()
                    ,dataManager.ADMIN_ID).getNewMessage(update));
            temp.add( new SimpleSendMessage("\uD83E\uDD72", dataManager.ADMIN_ID).getNewMessage(update));

        }

        temp.add(new SimpleEditMessage(text).getNewEditMessage(update));
        temp.add(KeyboardsManager.getSignInKeyboard("\uD83E\uDD72", update));

        return temp;
    }
}
