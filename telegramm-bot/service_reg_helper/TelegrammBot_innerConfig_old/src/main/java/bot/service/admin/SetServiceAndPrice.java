package bot.service.admin;

import bot.managers.KeyboardsManager;
import bot.service.ResponseService;
import bot.simplemessage.SimpleSendMessage;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.io.Serializable;
import java.util.List;

public class SetServiceAndPrice extends ResponseService {

    @Override
    public List<PartialBotApiMethod<? extends Serializable>> responseAction(Update update) {
        String text = update.getMessage().getText();

        String
                param1 = "-null-",
                param2 = "-null-";

        if (text.charAt(0) == '-' && text.charAt(1) == ' ') {
            int index = update.getMessage().getText().indexOf(" ");
            param1 = "-";
            param2 = text.substring(index + 1);
        } else {
            int index = update.getMessage().getText().lastIndexOf(" ");
            if (index != -1) {
                param1 = text.substring(0, index);
                param2 = text.substring(index + 1);
            }
        }


        if (!param2.equals("-null-")) {
            if (param1.equals("-")) {
                if (dataManager.removeService(param2)) {
                    dataManager.getAdmin().setEditServicesAndPrices(false);
                    return List.of(
                            new SimpleSendMessage("Услуга " + param1 + " удалена", 0).getNewMessage(update),
                            KeyboardsManager.adminServicesAndPricesKeyboard("\uD83E\uDEE1", update),
                            new SimpleSendMessage(dataManager.getServicesAndPricesString(),0).getNewMessage(update)
                    );
                } else {
                    return List.of(
                            new SimpleSendMessage("Указанная услуга в списке не найдена. Удаление невозможно",0).getNewMessage(update),
                            new SimpleSendMessage("\uD83E\uDEE4",0).getNewMessage(update),
                            new SimpleSendMessage("Вот актуальные названия услуг: \n" + dataManager.getServicesAndPricesString(),0).getNewMessage(update)
                    );
                }
            }


            if (param2.matches("\\d+")) {
                dataManager.addOrUpdateServiceAndPrice(param1, Integer.parseInt(param2));
                //dataManager.getAdmin().setEditServicesAndPrices(false);
                return List.of(
                        new SimpleSendMessage("Новая цена на услугу установлена",0).getNewMessage(update),
                        //KeyboardsManager.adminServicesAndPricesKeyboard("\uD83E\uDEE1", update),
                        new SimpleSendMessage(dataManager.getServicesAndPricesString(),0).getNewMessage(update)
                );
            }
        }

        return List.of(
                new SimpleSendMessage("Не опознан ни один сценарий управления услугами. Пожалуйста повторите ввод снова",0).getNewMessage(update),
                new SimpleSendMessage("\uD83E\uDEE4",0).getNewMessage(update)
        );
    }


}
