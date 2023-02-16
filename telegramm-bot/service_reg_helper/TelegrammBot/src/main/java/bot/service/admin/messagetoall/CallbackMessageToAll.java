package bot.service.admin.messagetoall;

import bot.service.ResponseService;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.io.Serializable;
import java.util.List;

public class CallbackMessageToAll extends ResponseService {

    @Override
    public List<PartialBotApiMethod<? extends Serializable>> responseAction(Update update) {
        return dataManager.admin_getGroupMessageList(update);
    }



}
