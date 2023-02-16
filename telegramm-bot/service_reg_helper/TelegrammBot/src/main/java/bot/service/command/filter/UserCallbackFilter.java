package bot.service.command.filter;

import bot.managers.DataManager;
import bot.service.ResponseService;
import bot.managers.KeyboardsManager;
import bot.service.admin.AdminCancelManicureReg;
import bot.service.admin.messagetoall.CallbackMessageToAll;
import bot.service.admin.regdatetime.OpenCloseDateValidate;
import bot.service.admin.regdatetime.SelectHours;
import bot.service.command.manicure.*;
import bot.service.command.portfolio.ShowPortfolio;
import bot.service.command.signup.CallbackSignUpConfirmed;
import bot.simplemessage.SimpleEditMessage;
import bot.simplemessage.SimpleSendMessage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.io.Serializable;
import java.util.List;

public class UserCallbackFilter extends ResponseService {

    private static final Logger logger = LogManager.getLogger();

    @Override
    public List<PartialBotApiMethod<? extends Serializable>> responseAction(Update update) {
        String callback = update.getCallbackQuery().getData();
        long userId = update.getCallbackQuery().getMessage().getChatId();
        String user_first_name = update.getCallbackQuery().getMessage().getChat().getFirstName();
        String user_last_name = update.getCallbackQuery().getMessage().getChat().getLastName();
        String user_username = update.getCallbackQuery().getMessage().getChat().getUserName();
        String customerName = dataManager.getUserName(userId);
        String customerTGUserName = dataManager.getUserName(userId);


        switch (callback) {
            case "signUp_confirmOk": {
                if (dataManager.getUserSignUpStatus(userId) == DataManager.userSignUpStatus.SIGNUP_IN_PROGRESS) {
                    logger.info("!!!! ------------------------------------------------------------------------------- !!!!" );
                    logger.info("!!!! --------------------  Новый клиент: "+ dataManager.getUserName(userId) + " -------------------- !!!!" );
                    logger.info("!!!! ------------------------------------------------------------------------------- !!!!" );
                    return new CallbackSignUpConfirmed().responseAction(update);
                }
                break;
            }


            case "signUp_editData": {
                if (dataManager.getUserSignUpStatus(userId) == DataManager.userSignUpStatus.SIGNUP_IN_PROGRESS) {
                    logger.info("Незарегистрированный пользователь:  ( имя: "+ user_first_name +", фам.: "+user_last_name+", Id: "+ userId+", ник: " + user_username+ " )   :    ++++  изменение регистрационных данных  ++++ ");
                    return List.of(
                            new SimpleEditMessage("Повтор ввода данных").getNewEditMessage(update),
                            KeyboardsManager.getSignCancelKeyboard(null, update)
                    );
                }
                break;
            }


            case "regConfirm_YES": {
                logger.info("Клиент:  ( тг_имя: "+ user_first_name +", тг_ф.: "+user_last_name+", тг_Id: "+ userId+"  тг_ник: " +customerTGUserName + " )      Имя клиента: "+ customerName +"  :  Подтвердил свое посещение завтра");
                return List.of(new SimpleEditMessage("Спасибо! Будем вас ждать!").getNewEditMessage(update),
                        new SimpleSendMessage("Запись на зватра подтверждена от: " + dataManager.getUserName(userId),
                                dataManager.ADMIN_ID).getNewMessage(update)
                );
            }


            case "sendMessageToAll_yes": {
                return new CallbackMessageToAll().responseAction(update);
            }

            case "sendMessageToAll_later": {
                return List.of(new SimpleEditMessage("Отправка отменена").getNewEditMessage(update));
            }


            case "viewing_later": {
                if (dataManager.userIsView(userId)) {
                    dataManager.setUserViewingList(userId,null);
                    return List.of(new SimpleEditMessage("Надеемся Вам понравились работы мастера!").getNewEditMessage(update));
                }
                break;
            }


            case "BSReg_continue": {
                if (dataManager.customerManicureRegStatus(userId) != DataManager.manicureRegStatus.NO_REG_ERROR
                        && dataManager.customerManicureRegStatus(userId) != DataManager.manicureRegStatus.REG_COMPLETE) {
                    return List.of(new SimpleEditMessage("Для завершения регистрации воспользуйтесь все еще активным меню в сообщении чуть выше.").getNewEditMessage(update));
                }
                break;
            }


            case "regConfirm_NO":
            case "manicure_reg_cancel_yes": {
                if (dataManager.customerManicureRegStatus(userId) != DataManager.manicureRegStatus.NO_REG_ERROR) {
                    logger.info("Клиент:  ( тг_имя: "+ user_first_name +", тг_ф.: "+user_last_name+", тг_Id: "+ userId+"  тг_ник: " +customerTGUserName + " )      Имя клиента: "+ customerName +"  :  отмена текущего действия / подтверждение отмены записи");
                    return new CallbackCancelReg("Запись отменена. Надеемся что вы вернетесь к нам снова "
                            + dataManager.getUserName(userId) + "!").responseAction(update);
                }
                break;
            }

            case "BSReg_later":
            case "BSReg_abort": {
                if (dataManager.customerManicureRegStatus(userId) != DataManager.manicureRegStatus.NO_REG_ERROR
                        && dataManager.customerManicureRegStatus(userId) != DataManager.manicureRegStatus.REG_COMPLETE) {
                    dataManager.manicureRegAbort(userId);
                    dataManager.userSetDefault(userId);
                    return List.of(new SimpleEditMessage("С нетепрением будем Вас ждать!").getNewEditMessage(update),
                            KeyboardsManager.getUserKeyboard("Что бы Вы хотели, "+ dataManager.getUserName(userId)+"?", update));
                }
                break;
            }


            case "manicure_reg_cancel_no": {
                if (dataManager.customerManicureRegStatus(userId) == DataManager.manicureRegStatus.NO_REG_ERROR) {
                    return List.of(new SimpleEditMessage("Вы не записаны на прием.").getNewEditMessage(update));
                } else {
                    return List.of(new SimpleEditMessage("С нетепрением будем Вас ждать!").getNewEditMessage(update));
                }
            }




            default: {
                //Admin
                if (callback.startsWith("adminSelectCloseDay_")){
                    dataManager.admin_choosingRegDate = false;
                    dataManager.admin_manicureRegCloseDate = update.getCallbackQuery().getData().split("_")[1];
                    return new OpenCloseDateValidate().responseAction(update);
                }
                if (callback.startsWith("adminSelectOpenDay_")){
                    dataManager.admin_choosingRegDate = false;
                    dataManager.admin_manicureRegOpenDate = update.getCallbackQuery().getData().split("_")[1];
                    return new OpenCloseDateValidate().responseAction(update);
                }
                if (callback.startsWith("adminSelectHours_")){
                    return new SelectHours(update.getCallbackQuery().getData().split("_")[1]).responseAction(update);
                }
                if (callback.startsWith("adminEditDate_yes")){
                    return new AdminCancelManicureReg().responseAction(update);
                }
                if (callback.startsWith("adminEditDate_no")){
                    return List.of(new SimpleEditMessage("удаление отменено").getNewEditMessage(update));
                }





                //User
                if (callback.startsWith("viewing_ShowMore")) {
                    if (dataManager.userIsView(userId)) {
                        return new ShowPortfolio(dataManager.getUserViewingList(userId)).responseAction(update);
                    }
                    break;
                }


                String mtype = update.getCallbackQuery().getData().split("_")[1];
                if (dataManager.getServices().contains(mtype) & dataManager.customerManicureRegStatus(userId) == DataManager.manicureRegStatus.SELECT_TYPE) {
                    return new CallbackSelectWeek().responseAction(update);
                }


                if (callback.startsWith("BSReg_week_")) {
                    if (dataManager.customerManicureRegStatus(userId) == DataManager.manicureRegStatus.SELECT_WEEK) {
                        return new CallbackSelectDay().responseAction(update);
                    }
                }

                if (callback.startsWith("BSReg_day_")) {
                    if (dataManager.customerManicureRegStatus(userId) == DataManager.manicureRegStatus.SELECT_DAY) {
                        return new CallbackSelectHour().responseAction(update);
                    }
                }

                if (callback.startsWith("BSReg_hour_")) {
                    if (dataManager.customerManicureRegStatus(userId) == DataManager.manicureRegStatus.SELECT_HOUR) {
                        logger.info("Клиент:  ( тг_имя: "+ user_first_name +", тг_ф.: "+user_last_name+", тг_Id: "+ userId+"  тг_ник: " +customerTGUserName + " )      Имя клиента: "+ customerName +"  :  + записался на маникюр +  ");
                        return new CallbackRegComplete().responseAction(update);
                    }
                }


                //any
                return List.of(new SimpleEditMessage("Вы ответили на неактуальное сообщение в чате :(").getNewEditMessage(update));
            }
        }
        return List.of(new SimpleEditMessage("Вы ответили на неактуальное сообщение в чате :(").getNewEditMessage(update));
    }


}
