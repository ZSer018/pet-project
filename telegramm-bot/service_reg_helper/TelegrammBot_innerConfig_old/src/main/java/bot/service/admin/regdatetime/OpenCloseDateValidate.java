package bot.service.admin.regdatetime;

import bot.service.ResponseService;
import bot.simplemessage.SimpleEditMessage;
import bot.simplemessage.SimpleSendMessage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class OpenCloseDateValidate extends ResponseService {

    private static final Logger logger = LogManager.getLogger();


    @Override
    public List<PartialBotApiMethod<? extends Serializable>> responseAction(Update update) {
        var messages = new ArrayList<PartialBotApiMethod<? extends Serializable>>();

        String openString = dataManager.getAdmin().getManicureRegOpenDate();
        String closeString = dataManager.getAdmin().getManicureRegCloseDate();

        Date openDate = null;
        Date closeDate = null;
        try {
            if (openString != null) {
                openDate = new SimpleDateFormat("yyyy.MM.dd").parse(dataManager.getAdmin().getManicureRegOpenDate());
            }
            if (closeString != null) {
                System.out.println(closeString);
                closeDate = new SimpleDateFormat("yyyy.MM.dd").parse(dataManager.getAdmin().getManicureRegCloseDate());
            }
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }



        if (openDate != null && closeDate != null) {
            long diffInMillies = openDate.getTime() - closeDate.getTime();
            long diff = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);

            if (openDate.compareTo(closeDate) == 0) {
                dataManager.getAdmin().setManicureRegCloseDate(null);
                dataManager.getAdmin().setManicureRegOpenDate(null);
                dataManager.saveOpenCloseDateChanges();
                messages.add(new SimpleEditMessage("Дата открытия и закрытия записи на услуги совпадают. " +
                        "\nВсе ограничения по записи сброшены.").getNewEditMessage(update));

            } else if (openDate.before(closeDate)) {
                dataManager.getAdmin().setManicureRegCloseDate(null);
                dataManager.getAdmin().setManicureRegOpenDate(null);
                dataManager.saveOpenCloseDateChanges();
                messages.add(new SimpleEditMessage("Дата открытия записи на услуги не может быть до даты закрытия. " +
                        "\nВсе ограничения по записи сброшены.\nПожалуйста укажите верные даты.").getNewEditMessage(update));

            } else {
                messages.add(new SimpleEditMessage("Дата установлена и в силу вступили новые правила для записи на Ваши услуги." +
                        "\nИнтервал дней свободных от записи :\n" +closeString+"  -  "+openString+"\nДней: "+diff).getNewEditMessage(update));
                dataManager.saveOpenCloseDateChanges();
                logger.info("Адмистратор установил временной интервал, свободный от записи :   "+closeString+"  -  "+openString + "("+" дней: "+diff+")");
            }
        } else {

            if (openDate != null & closeDate == null) {
                dataManager.getAdmin().setManicureRegCloseDate(null);
                dataManager.getAdmin().setManicureRegOpenDate(null);
                messages.add(new SimpleEditMessage("Дата открытия записи на услуги не может быть установлена, если отсутствует дата закрытия. \nОперация отменена.").getNewEditMessage(update));
            } else {
                messages.add(new SimpleEditMessage("Дата установлена и в силу вступили новые правила для записи на Ваши услуги.").getNewEditMessage(update));
                dataManager.saveOpenCloseDateChanges();
            }

            if (closeString != null) {
                logger.info("Адмистратор установил дату, после которой запись на услуги осуществляться не будет:   " + closeString);
            } else {
                logger.info("Адмистратор установил дату, после которой можно записываться на услуги:  " + openString);
            }
        }

        return messages;
    }
}
