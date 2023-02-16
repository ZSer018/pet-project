package bot.service.command.call_master;

import bot.service.ResponseService;
import bot.simplemessage.SimpleSendMessage;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.io.Serializable;
import java.util.List;

public class CallMary extends ResponseService {

    @Override
    public List<PartialBotApiMethod<? extends Serializable>> responseAction(Update update) {
        return List.of(new SimpleSendMessage("Вы можете связаться с мастером, кликнув по ее никнэйму \n>> "
                +dataManager.getCustomerTgUsername(dataManager.ADMIN_ID)+" <<" +
                "\nМастера зовут "+dataManager.getUserName(dataManager.ADMIN_ID),0).getNewMessage(update));
    }
}
