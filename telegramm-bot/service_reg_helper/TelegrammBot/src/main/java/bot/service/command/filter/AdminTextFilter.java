package bot.service.command.filter;

import bot.managers.KeyboardsManager;
import bot.service.ResponseService;
import bot.service.admin.SetServiceAndPrice;
import bot.service.admin.regdatetime.RegHoursResponse;
import bot.service.admin.regdatetime.SelectDate;
import bot.service.admin.messagetoall.MessageToAll;
import bot.service.command.portfolio.ShowPortfolio;
import bot.simplemessage.SimpleSendMessage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;
import java.io.Serializable;
import java.util.List;


public class AdminTextFilter extends ResponseService {

    private static final Logger logger = LogManager.getLogger();

    @Override
    public List<PartialBotApiMethod<? extends Serializable>> responseAction(Update update) {
        String text = update.getMessage().getText();

        logger.info("Админские делишки...");

        switch (text) {

            case "Услуги и цены": {
                return List.of(KeyboardsManager.adminServicesAndPricesKeyboard("Здесь Вы можете добавить или удалить услуги, а так же установить им актуальные цены", update));
            }


            case "Дополнительное сообщение": {
                dataManager.admin_setUpNotifyString = true;
                return List.of(
                        dataManager.getAdminNotifyMessage().equals(" ")?
                                new SimpleSendMessage("На данный момент сообщение не установлено!",0).getNewMessage(update):
                                new SimpleSendMessage("На данный момент сообщение такое: "+dataManager.getAdminNotifyMessage(),0).getNewMessage(update),

                        new SimpleSendMessage("Владыка! Пришли мне сообщение, которое будет демонтрироваться всем в разделе 'Услуги и цены'. " +
                                "\nвыглядеть это будет примерно так:\n\n\n" +
                                "Маникюр: 1000\n" +
                                "Педикюр: 2000\n" +
                                "Наращивание: 2400\n" +
                                "-----------\n"+
                                "Дизайн 1 ногтя от 50р\n" +
                                "Кроме того, в салоне действует акция 'Приведи друга и получи скидку 30% на следующий маникюр'  \n\n\n" +
                                "для того что бы удалить сообщение, отправь знак минуса: '-' ", 0).getNewMessage(update),
                        KeyboardsManager.getSignCancelKeyboard("Ожидаю ввода строки....", update)

                        );
            }


            case "Показать услуги и цены": {
                return List.of(new SimpleSendMessage(dataManager.getServicesAndPricesString(),0).getNewMessage(update));
            }

            case "Удалить услугу": {
                dataManager.admin_editServicesAndPrices = true;
                return List.of(
                        KeyboardsManager.getSignCancelKeyboard("Для того что бы удалить услугу из списка, введите:\nзнак минуса '-' и через пробел название услуги.\n\n" +
                                "Например: \n" +
                                "- наращивание", update)
                );
            }
            case "Добавить услугу":
            case "Изменить цену": {
                dataManager.admin_editServicesAndPrices = true;
                return List.of(KeyboardsManager.getSignCancelKeyboard("Введите название услуги и через пробел ее цену.\n\n" +
                        "Например: \n" +
                        "Наращивание 3000", update)
                );
            }


            case "<< В начало":
            case "Закончить": {
                dataManager.adminSetDefault();
                return List.of(KeyboardsManager.adminMainKeyboard("Что бы Вы хотели еще, Владыка?", update));
            }


            case "Фотографии": {
                return List.of(KeyboardsManager.adminPhotoManagementKeyboard("Здесь Вы можете посмотреть все имеющиеся в Вашем портфолио фотографии, а так же добавить новые или удалить устаревшие", update));
            }
            case "Посмотреть фотографии": {
                dataManager.userGoView(dataManager.ADMIN_ID);
                return List.of(KeyboardsManager.choosePhotoTypeKeyboard("Владыка! Пожалуйста выберите, фотографии из какого раздела Вам показать", update));
            }
            case "Добавить фотографии": {
                dataManager.admin_addingPhotos = true;
                return List.of(
                        KeyboardsManager.choosePhotoTypeKeyboard("Владыка! Выберите раздел, в который будут добавляться фотографии", update)
                );
            }
            case "Удалить фотографии": {
                dataManager.admin_deletePhotos = true;
                return List.of(
                        KeyboardsManager.getSignCancelKeyboard("Владыка! Пришлите мне фотографии, которые Вы хотите удалить из базы", update)
                );
            }




            case "Календарь записи": {
                return List.of(KeyboardsManager.adminRegCalendarKeyboard("Здесь Вы можете посмотреть и редактировать календарь записей на услуги маникюра\nЧто Вам показать, Владыка?", update));
            }
            case "Все записи": {
                return List.of( new SimpleSendMessage(dataManager.getAllManicureRecords(),0).getNewMessage(update));
            }
            case "На сегодня": {
                return List.of( new SimpleSendMessage(dataManager.getTodayManicureRecords(),0).getNewMessage(update));
            }
            case "Свободные даты": {
                return List.of( new SimpleSendMessage(dataManager.getFreeManicureDateTime(),0).getNewMessage(update));
            }
            case "Редактирование...": {
                return List.of(KeyboardsManager.adminRegCalendarEditKeyboard("Здесь Вы можете отредактировать календарь записи. \n\n\n" +
                        "'Отказывать в записи после...' - выбрать дату, после которой запись на маникюр будет невозможна\n\n" +
                        "'Разрешить запись после...' - выбрать дату, после которой запись на маникюр будет доступна\n\n" +
                        "'Редактировать дату...' - выбрать дату, для редактирования у нее времени записи\n\n"
                        , update));
            }
            case "Отказывать в записи после...": {
                dataManager.admin_choosingRegDate = true;
                return new SelectDate(SelectDate.Type.DATE, SelectDate.Action.CLOSE_DATE).responseAction(update);
            }
            case "Разрешить запись после...": {
                dataManager.admin_choosingRegDate = true;
                return new SelectDate(SelectDate.Type.DATE, SelectDate.Action.OPEN_DATE).responseAction(update);
            }

            case "Редактировать дату...": {
                dataManager.admin_choosingRegDate = true;
                return new SelectDate(SelectDate.Type.HOURS, null).responseAction(update);
            }




            case "Сообщение всем": {
                dataManager.admin_sendingMessagesToAllCustomers = true;
                return List.of(KeyboardsManager.getSignCancelKeyboard("Введите сообщения, которые будут отправлены всем зарегистрированным пользователям." +
                        " Учти Владыка, каждое сообщение будет начинаться так:\n" +
                        "\nЗдравствйте, *имя пользователя*!\n Хотим сообщить что:\n*текст сообщения*\n\n\n" +
                        "Пример: \n" +
                        "Здравствуйте, Мария!\nХотим сообщить что: цена на некоторые услуги изменились. Для ознакомления загляните в меню 'Услуги и цены'", update));

            }



            case "Настройки": {
                return List.of(KeyboardsManager.adminSettingsKeyboard("Владыка, тут вы можете что то настроить у меня :)", update));
            }

            case "Вкл/выкл уведомления о записи": {
                dataManager.admin_regNotify = !dataManager.admin_regNotify;
                dataManager.saveBotOptions();
                if (dataManager.admin_regNotify) {
                    return List.of(KeyboardsManager.adminSettingsKeyboard("Теперь вам БУДУТ приходить уведомления о новых записях и их отмене", update));
                } else {
                    return List.of(KeyboardsManager.adminSettingsKeyboard("Теперь вам НЕ будут приходить уведомления о новых записях и их отмене", update));
                }
            }

            case "Вкл/выкл запись на маникюр": {
                dataManager.admin_canRegOnService =  !dataManager.admin_canRegOnService;
                dataManager.saveBotOptions();
                if (dataManager.admin_canRegOnService) {
                    return List.of(KeyboardsManager.adminSettingsKeyboard("Теперь любой желающий сможет записаться к Вам на маникюр, Владыка!", update));
                } else {
                    return List.of(KeyboardsManager.adminSettingsKeyboard("Теперь никто больше не сможет записаться на маникюр, Владыка!\nОтдыхайте, Вы заслужили это!", update));
                }
            }





            default: {

                if (dataManager.admin_setUpNotifyString){
                    if (text.equals("-")) {
                        dataManager.admin_ServicesAndPricesMessage = null;
                    } else {
                        dataManager.admin_ServicesAndPricesMessage = text;
                    }
                    dataManager.admin_setUpNotifyString = false;
                    dataManager.saveBotOptions();
                    return List.of(
                            KeyboardsManager.adminMainKeyboard("Фраза установлена",update),
                            new SimpleSendMessage("На данный момент сообщение такое: "+dataManager.getAdminNotifyMessage(),0).getNewMessage(update)
                    );
                }

                if (dataManager.admin_choosingRegDate){
                    return new RegHoursResponse(text).responseAction(update);
                }

                if (dataManager.admin_editServicesAndPrices) {
                    return new SetServiceAndPrice().responseAction(update);
                }

                if (dataManager.admin_sendingMessagesToAllCustomers) {

                    return new MessageToAll().responseAction(update);
                }

                if (dataManager.userIsView(dataManager.ADMIN_ID) && dataManager.getServicesByName().contains(text)) {
                    dataManager.setUserViewType(dataManager.ADMIN_ID, text);
                    dataManager.setUserViewingList(dataManager.ADMIN_ID, null);
                    return new ShowPortfolio(null).responseAction(update);
                }

                if (dataManager.admin_addingPhotos) {
                    if (dataManager.getServicesByName().contains(text)) {
                        dataManager.setUserViewType(dataManager.ADMIN_ID, text);
                        return List.of(
                                KeyboardsManager.getSignCancelKeyboard("Владыка! Можете приступить к добавлению фотографий", update)
                        );
                    }
                }

                return List.of(KeyboardsManager.adminMainKeyboard("Что бы Вы хотели еще, Владыка?", update));

            }
        }
    }
}
