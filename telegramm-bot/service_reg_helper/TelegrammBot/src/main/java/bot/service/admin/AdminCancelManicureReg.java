package bot.service.admin;

import bot.objects.ManicureRegObject;
import bot.service.ResponseService;
import bot.simplemessage.SimpleEditMessage;
import bot.simplemessage.SimpleSendMessage;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.io.Serializable;
import java.util.List;

public class AdminCancelManicureReg extends ResponseService {

    @Override
    public List<PartialBotApiMethod<? extends Serializable>> responseAction(Update update) {
        long userId = Long.parseLong(update.getCallbackQuery().getData().split("_")[2]);

        var temp =  dataManager.getManicureRegObject(userId);
        dataManager.manicureCancelReg(userId);

        return List.of(
                new SimpleEditMessage("Пользователь удален").getNewEditMessage(update),
                new SimpleSendMessage("Запись на указанное время отменена. Время теперь не занято.", dataManager.ADMIN_ID).getNewMessage(update),
                new SimpleSendMessage("Ваша запись на услуги маникюра отменена: \n"+temp.getDate() + " - "+temp.getTime() + " \n"+temp.getManicureType() +": "+ temp.getCost() + "\n\nПо всем вопросам Вы можете обратиться к мастеру, используя соответствующее меню.", userId).getNewMessage(update)
        );
    }
}
