package bot.service.command.filter;

import bot.managers.KeyboardsManager;
import bot.service.ResponseService;
import bot.service.command.call_master.CallMary;
import bot.service.command.portfolio.ShowPortfolio;
import bot.simplemessage.SimpleSendMessage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.io.Serializable;
import java.util.List;

public class NoSignUpTextFilter extends ResponseService {

    private static final Logger logger = LogManager.getLogger();

    @Override
    public List<PartialBotApiMethod<? extends Serializable>> responseAction(Update update) {
        long userId = update.getMessage().getChatId();
        String text = update.getMessage().getText();
        String user_first_name = update.getMessage().getChat().getFirstName();
        String user_last_name = update.getMessage().getChat().getLastName();
        String user_username = update.getMessage().getChat().getUserName();

        switch (text) {
            case "/start": {
                if (!dataManager.userIsView(userId)) {
                    dataManager.userSetDefault(userId);
                    logger.info("Незарегистрированный пользователь:  ( имя: "+
                            user_first_name +", фам.: "+user_last_name+", Id: "+ userId+", ник:" + user_username+ " )   :    /start ");
                    return List.of(KeyboardsManager.getUserKeyboard("Здравствуйте! Что бы Вы хотели?", update));
                } break;
            }

            case "Зарегистрироваться": {
                if (!dataManager.userIsView(userId)) {
                    dataManager.setUserName(userId,"regStart");
                    logger.info("Незарегистрированный пользователь:  ( имя: "+ user_first_name +", фам.: "
                            +user_last_name+", Id: "+ userId+", ник:" + user_username+ " )   :    ++++ Начал процесс регистрации! ++++ ");
                    return List.of(KeyboardsManager.getSignCancelKeyboard(null, update));
                } break;
            }

            case "Написать мастеру": {
                if (!dataManager.userIsView(userId)) {
                    return new CallMary().responseAction(update);
                } break;
            }

            case "Услуги и цены": {
                logger.info("Незарегистрированный пользователь:  ( имя: "+ user_first_name +", фам.: "
                        +user_last_name+", Id: "+ userId+", ник:" + user_username+ ")  :   Смотрит раздел 'Услуги и цены'");
                return List.of( new SimpleSendMessage(dataManager.getServicesAndPricesString()+dataManager.getAdminNotifyMessage(),0).getNewMessage(update));
            }





            case "Посмотреть портфолио": {
                if (dataManager.getServices().size() != 0) {
                    dataManager.userGoView(userId);
                    return List.of(KeyboardsManager.choosePhotoTypeKeyboard("Выберите пожалуйста раздел портфолио мастера\nИногда, в зависимости от загруженности сервера на отправку фотографий требуется некоторое время, пожалуйста будьте терпеливы :)", update));
                }else {
                    return List.of(new SimpleSendMessage("К сожалению данный раздел пока еще в разработке :(",0).getNewMessage(update));
                }
            }

            case "Закончить": {
                if (dataManager.userActivityStatus(userId)) {
                    dataManager.userSetDefault(userId);
                    logger.info("Незарегистрированный пользователь:  ( имя: "+ user_first_name +", фам.: "
                            +user_last_name+", Id: "+ userId+", ник:" + user_username+ " )   :    завершил просмотр ");
                    return List.of(KeyboardsManager.getUserKeyboard("Здравствуйте! Что бы Вы хотели?", update));
                } break;
            }






            default: {
                if (dataManager.userIsView(userId) && dataManager.getServicesByName().contains(text)) {
                    dataManager.setUserViewType(userId, text);
                    dataManager.setUserViewingList(userId, null);
                    logger.info("Незарегистрированный пользователь:  ( имя: "+ user_first_name +", фам.: "
                            +user_last_name+", Id: "+ userId+", ник:" + user_username+ " )   :    Просматривает галерею портфолио:   '"+text+"'");
                    return new ShowPortfolio(null).responseAction(update);
                }

                dataManager.userSetDefault(userId);
                return List.of(
                        new SimpleSendMessage("Я не понимаю эту команду : " + update.getMessage().getText() ,0).getNewMessage(update),
                        KeyboardsManager.getUserKeyboard("Давайте начнем заново :) ", update)
                );
            }

        }

        return List.of(new SimpleSendMessage("NoSignUpFilter: " + update.getMessage().getText() + "?",0).getNewMessage(update));
    }




}
