package bot.service.command.filter;

import bot.managers.KeyboardsManager;
import bot.service.ResponseService;
import bot.service.command.signup.CallbackPleaseConfirmData;
import bot.simplemessage.SimpleSendMessage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.io.Serializable;
import java.util.List;

public class SignUpInProgressTextFilter extends ResponseService {

    private static final Logger logger = LogManager.getLogger();

    @Override
    public List<PartialBotApiMethod<? extends Serializable>> responseAction(Update update) {
        String text = update.getMessage().getText();
        long userId = update.getMessage().getChatId();
        String user_first_name = update.getMessage().getChat().getFirstName();
        String user_last_name = update.getMessage().getChat().getLastName();
        String user_username = update.getMessage().getChat().getUserName();




        var unusualCommands = List.of(
                "Написать мастеру",
                "Зарегистрироваться",
                "/start",
                "Посмотреть портфолио",
                "Закончить",
                "Отменить запись",
                "Записаться на маникюр",
                "Напомнить время записи");


        if (text.equals("Отказаться от регистрации")) {
            dataManager.cancelSignUp(update.getMessage().getChatId());
            logger.info("Незарегистрированный пользователь:  ( имя: "+ user_first_name +", фам.: "+user_last_name+", Id: "+ userId+", ник:" + user_username+ " )   :    ----  Отказ от регистрации ---- "+ text);
            return List.of(KeyboardsManager.getStartKeyboard("Регистрация отменена", update));
        }

        if (!unusualCommands.contains(text)) {
            logger.info("Незарегистрированный пользователь:  ( имя: "+ user_first_name +", фам.: "+user_last_name+", Id: "+ userId+", ник:" + user_username+ " )   :    * Попытка ввода неактивной команды или ввод имени пользователя для регистрации *  "+ text);
            return new CallbackPleaseConfirmData().responseAction(update);
        }

        return List.of(new SimpleSendMessage("В процессе регистрации данная команда недоступна. Закончите пожалуйста резистрацию, или откажитесь от нее.",0).getNewMessage(update));

    }


}
