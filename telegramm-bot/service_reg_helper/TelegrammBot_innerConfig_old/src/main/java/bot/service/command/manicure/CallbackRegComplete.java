package bot.service.command.manicure;

import bot.managers.DataManager;
import bot.service.ResponseService;
import bot.managers.KeyboardsManager;
import bot.simplemessage.SimpleEditMessage;
import bot.simplemessage.SimpleSendMessage;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class CallbackRegComplete extends ResponseService {

    private static final Object lock = new Object();

    @Override
    public List<PartialBotApiMethod<? extends Serializable>> responseAction(Update update) {
        var temp = new ArrayList<PartialBotApiMethod<? extends Serializable>>();

        if (regFinalStep(update)){
            temp.add(new SimpleEditMessage("Вы успешно зарегистрированы!").getNewEditMessage(update));
            temp.add(KeyboardsManager.getSignInKeyboard("С нетерпением будем Вас ждать!", update));
            temp.add(new SimpleSendMessage("\uD83D\uDE0A",0).getNewMessage(update));

            if (dataManager.getAdmin().isRegNotify()){
                var reg = dataManager.getManicureRegObject(update.getCallbackQuery().getMessage().getChatId());
                temp.add(new SimpleSendMessage("Владыка, хорошая новость: \n к тебе новая запись: \n"
                                +dataManager.getUserName(reg.getTelegramId())+"\n"
                                +dataManager.strDateToDateAndMonthName(reg.getDate())+",   "+reg.getTime()+"\n"
                                +reg.getManicureType()+"\n"
                                +"цена: "+reg.getCost()
                        ,dataManager.getAdmin().getTelegramId()).getNewMessage(update));
                temp.add(new SimpleSendMessage("\uD83E\uDD11", dataManager.getAdmin().getTelegramId()).getNewMessage(update));
            }
        } else {
            dataManager.manicureRegAbort(update.getMessage().getChatId());
            dataManager.userSetDefault(update.getMessage().getChatId());

            temp.add(new SimpleEditMessage("К сожалению что-то пошло не так. Пожалуйста начните процедуру ругистрации заново или обратитесь напрямую к мастеру маникюра: "+
                    dataManager.getAdmin().getTgUsername()).getNewEditMessage(update));
            temp.add(KeyboardsManager.getSignInKeyboard("Приносим свои извинения", update));
            temp.add(new SimpleSendMessage("\uD83D\uDE14",0).getNewMessage(update));
        }

        return temp;
    }

    private boolean regFinalStep(Update update) {
        long chat_id = update.getCallbackQuery().getMessage().getChatId();

        if (dataManager.customerManicureRegStatus(chat_id) != DataManager.manicureRegStatus.SELECT_HOUR){
            return false;
        }

        var manicureObject = dataManager.getManicureRegObject(chat_id);
        manicureObject.setTime(update.getCallbackQuery().getData().replaceAll("BSReg_hour_", ""));
        manicureObject.setTelegramId(chat_id);

        synchronized (lock) {
            if (dataManager.checkManucureRegDataIsFree(manicureObject.getDate(), manicureObject.getTime())) {
                if (dataManager.manicureDBRegistration(chat_id)) {
                    dataManager.updateRegCalendar(manicureObject, false);

                } else {
                    dataManager.manicureCancelReg(chat_id);
                    return false;
                }
                return true;
            } else return false;
        }
    }
}
