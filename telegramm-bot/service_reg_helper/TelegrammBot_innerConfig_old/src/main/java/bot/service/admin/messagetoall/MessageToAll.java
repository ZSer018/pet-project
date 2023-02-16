package bot.service.admin.messagetoall;

import bot.service.ResponseService;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.io.Serializable;
import java.util.List;

public class MessageToAll extends ResponseService {
    @Override
    public List<PartialBotApiMethod<? extends Serializable>> responseAction(Update update) {
        return dataManager.getGroupMessageList("Хотим сообщить что:\n" + update.getMessage().getText(), update);
    }
}
