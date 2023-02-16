package bot.service.admin.regdatetime;

import bot.objects.ManicureRegObject;
import bot.service.ResponseService;
import bot.simplemessage.SimpleSendMessage;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.io.Serializable;
import java.util.List;

public class RegHoursResponse extends ResponseService {

    private final String hour;

    public RegHoursResponse(String hour) {
        this.hour = hour;
    }

    @Override
    public List<PartialBotApiMethod<? extends Serializable>> responseAction(Update update) {
        ManicureRegObject manicureRegObject = new ManicureRegObject();
        manicureRegObject.setDate(dataManager.getAdmin().getSignInDate());
        manicureRegObject.setTime(hour);

        dataManager.saveRegCalendarChanges(manicureRegObject);

        return List.of(dataManager.editRegTimeOnSelectedDate(dataManager.getAdmin().getSignInDate(), hour, update.getMessage().getChatId()));

/*        return List.of(new SimpleSendMessage(
                "Время '"+hour+"' теперь "+dataManager.editRegTimeOnSelectedDate(dataManager.getAdmin().getSignInDate(), hour), 0
        ).getNewMessage(update));*/
    }
}
