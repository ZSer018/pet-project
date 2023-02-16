package bot.service.command.filter;

import bot.managers.DataManager;
import bot.service.ResponseService;
import bot.simplemessage.SimpleSendMessage;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

public class MainTextCommandFilter extends ResponseService {

    @Override
    public List<PartialBotApiMethod<? extends Serializable>> responseAction(Update update) {
        DataManager.userSignUpStatus userSignUpStatus = dataManager.getUserSignUpStatus(update.getMessage().getChatId());

        if (Objects.equals(dataManager.ADMIN_ID, update.getMessage().getChatId())){
            return new AdminTextFilter().responseAction(update);
        } //else return List.of(new SimpleSendMessage("Я на ремонте",0).getNewMessage(update));

        switch (userSignUpStatus) {

            case NO_SIGNUP: {
                return new NoSignUpTextFilter().responseAction(update);
            }

            case SIGNUP_COMPLETED: {
                return new SignInTextFilter().responseAction(update);
            }

            case SIGNUP_IN_PROGRESS: {
                return new SignUpInProgressTextFilter().responseAction(update);
            }

            default: {
                return List.of(new SimpleSendMessage(update.getMessage().getText() + "?", 0).getNewMessage(update));
            }
        }
    }


}

