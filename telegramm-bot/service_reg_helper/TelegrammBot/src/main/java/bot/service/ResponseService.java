package bot.service;

import bot.managers.DataManager;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.io.Serializable;
import java.util.List;

public abstract class ResponseService {

    protected final DataManager dataManager;

    public ResponseService() {
        dataManager = DataManager.getInstance();
    }

    public abstract List<PartialBotApiMethod<? extends Serializable>> responseAction(Update update);

}
