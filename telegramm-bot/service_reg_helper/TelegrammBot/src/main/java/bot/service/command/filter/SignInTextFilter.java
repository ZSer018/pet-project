package bot.service.command.filter;

import bot.managers.DataManager;
import bot.managers.KeyboardsManager;
import bot.service.ResponseService;
import bot.service.command.call_master.CallMary;
import bot.service.command.manicure.CancelRegStart;
import bot.service.command.manicure.ContinueOrDropOffer;
import bot.service.command.manicure.StartRegistration;
import bot.service.command.portfolio.ShowPortfolio;
import bot.simplemessage.SimpleSendMessage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.io.Serializable;
import java.util.List;


public class SignInTextFilter extends ResponseService {

    private static final Logger logger = LogManager.getLogger();


    @Override
    public List<PartialBotApiMethod<? extends Serializable>> responseAction(Update update){
        long userId = update.getMessage().getChatId();
        String text = update.getMessage().getText();
        String user_first_name = update.getMessage().getChat().getFirstName();
        String user_last_name = update.getMessage().getChat().getLastName();
        String customerName = dataManager.getUserName(userId);
        String customerTGUserName = dataManager.getUserName(userId);




        if (dataManager.customerManicureRegStatus(userId) != DataManager.manicureRegStatus.NO_REG_ERROR &&
                dataManager.customerManicureRegStatus(userId) != DataManager.manicureRegStatus.REG_COMPLETE){
            return new ContinueOrDropOffer().responseAction(update);
        }


        switch (text){
            case "/start": {
                if (!dataManager.userIsView(userId)) {
                    logger.info("Клиент:  ( тг_имя: "+ user_first_name +", тг_ф.: "+user_last_name+", тг_Id: "+ userId+"  тг_ник: " + customerTGUserName + " )      Имя клиента: "+ customerName +"  :  /start");
                    dataManager.userSetDefault(userId);
                    return List.of(KeyboardsManager.getUserKeyboard("Здравствуйте, " + dataManager.getUserName(userId) + "! Что бы Вы хотели?", update));
                } break;
            }


            case "Услуги и цены": {
                logger.info("Клиент:  ( тг_имя: "+ user_first_name +", тг_ф.: "+user_last_name+", тг_Id: "+ userId+"  тг_ник: " +customerTGUserName + " )      Имя клиента: "+ customerName +"  :   Смотрит раздел 'Услуги и цены'");
                return List.of( new SimpleSendMessage(dataManager.getServicesAndPricesString()+dataManager.getAdminNotifyMessage(),0).getNewMessage(update));
            }



            case "Напомнить время записи": {
                logger.info("Клиент:  ( тг_имя: "+ user_first_name +", тг_ф.: "+user_last_name+", тг_Id: "+ userId+"  тг_ник: " +customerTGUserName + " )      Имя клиента: "+ customerName +"  :  Напоминание времени записи");
                    return List.of( new SimpleSendMessage(dataManager.getUserManicureRegData(userId),0).getNewMessage(update));
            }

            case "Записаться на маникюр": {
                if (dataManager.admin_canRegOnService) {
                    if (dataManager.customerManicureRegStatus(userId) == DataManager.manicureRegStatus.NO_REG_ERROR) {
                        logger.info("Клиент:  ( тг_имя: "+ user_first_name +", тг_ф.: "+user_last_name+", тг_Id: "+ userId+"  тг_ник: " +customerTGUserName + " )      Имя клиента: "+ customerName +"  :  Хочет записаться на маникюр");
                        return new StartRegistration().responseAction(update);
                    }
                } else return List.of(new SimpleSendMessage("К сожалению запись на маникюр пока приостановлена. \nПриносим свои извинения.\nДля связи с мастером используйте соотвествующее меню", 0).getNewMessage(update));
            }

            case "Посмотреть портфолио": {
                if (dataManager.getServices().size() != 0) {
                    logger.info("Клиент:  ( тг_имя: "+ user_first_name +", тг_ф.: "+user_last_name+", тг_Id: "+ userId+"  тг_ник: " +customerTGUserName + " )      Имя клиента: "+ customerName +"  :  Просмотр портфолио");
                    dataManager.userGoView(userId);
                    return List.of(KeyboardsManager.choosePhotoTypeKeyboard(dataManager.getUserName(userId)+ ", выберите пожалуйста раздел портфолио мастера.\n\nИногда, в зависимости от загруженности сервера на отправку фотографий требуется некоторое время, пожалуйста будьте терпеливы :)", update));
                }else {
                    return List.of(new SimpleSendMessage("К сожалению данный раздел пока еще в разработке :(",0).getNewMessage(update));
                }
            }

            case "Написать мастеру": {
                return new CallMary().responseAction(update);
            }

            case "Отменить запись": {
                logger.info("Клиент:  ( тг_имя: "+ user_first_name +", тг_ф.: "+user_last_name+", тг_Id: "+ userId+"  тг_ник: " +customerTGUserName + " )      Имя клиента: "+ customerName +"  :  Хочет отменить запись на маникюр");
                    return new CancelRegStart().responseAction(update);
            }

            case "Закончить" : {
                if (dataManager.userActivityStatus(userId)){
                    dataManager.userSetDefault(userId);
                    logger.info("Клиент:  ( тг_имя: "+ user_first_name +", тг_ф.: "+user_last_name+", тг_Id: "+ userId+"  тг_ник: " +customerTGUserName + " )      Имя клиента: "+ customerName +"  :  Отмена действия");
                    return List.of(KeyboardsManager.getUserKeyboard("Здравствуйте, " + dataManager.getUserName(userId) + "! Что бы Вы хотели?", update));
                }
            }


            default: {
                if (dataManager.userIsView(userId) && dataManager.getServicesByName().contains(text)) {
                    logger.info("Клиент:  ( тг_имя: "+ user_first_name +", тг_ф.: "+user_last_name+", тг_Id: "+ userId+"  тг_ник: " +customerTGUserName + " )      Имя клиента: "+ customerName +"  :  Просматривает галерею портфолио '"+text+"'");
                    dataManager.setUserViewType(userId, text);
                    dataManager.setUserViewingList(userId, null);
                    return new ShowPortfolio(null).responseAction(update);
                }

                dataManager.userSetDefault(userId);
                return List.of(
                        KeyboardsManager.getUserKeyboard("Здравствуйте, " + dataManager.getUserName(userId) + "! Что бы Вы хотели?", update)
                );
            }
        }

        return List.of(KeyboardsManager.getUserKeyboard("Здравствуйте, " + dataManager.getUserName(userId) + "! Что бы Вы хотели?", update));
    }
}