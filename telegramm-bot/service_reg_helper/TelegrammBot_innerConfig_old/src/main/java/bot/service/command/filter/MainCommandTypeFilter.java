package bot.service.command.filter;

import bot.service.ResponseService;
import bot.service.admin.AddDeletePhoto;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.io.Serializable;
import java.util.List;

public class MainCommandTypeFilter extends ResponseService {

    @Override
    public List<PartialBotApiMethod<? extends Serializable>> responseAction(Update update) {
        long chatId = update.hasCallbackQuery() ? update.getCallbackQuery().getMessage().getChatId() : update.getMessage().getChatId();

        dataManager.usersContains(chatId);

        if (update.hasMessage() && update.getMessage().hasPhoto()) {
            return new AddDeletePhoto().responseAction(update);
        }

        if (update.hasCallbackQuery()){
            return new UserCallbackFilter().responseAction(update);
        }

        if (update.hasMessage() && update.getMessage().hasText()){
            return new MainTextCommandFilter().responseAction(update);
        }

        return null;
    }

}
