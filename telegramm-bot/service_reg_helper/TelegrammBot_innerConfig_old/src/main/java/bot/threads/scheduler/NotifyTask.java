package bot.threads.scheduler;

import bot.managers.DataManager;
import bot.managers.KeyboardsManager;
import bot.simplemessage.SimpleSendMessage;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class NotifyTask{

    private static DataManager dataManager;
    private static boolean notified = false;

    public static void runIteration() {
        if (dataManager == null){
            dataManager = DataManager.getInstance();
        }

        theDayBeforeTheService();
        anHourBeforeTheService();
    }


    private static void theDayBeforeTheService(){
        Date date = new Date();
        String hour = new SimpleDateFormat("kk").format(date);

        if (hour.equals("13") && !notified) {
            final var notifyList = new ArrayList<PartialBotApiMethod<? extends Serializable>>();

            Calendar tomorrow = Calendar.getInstance();
            tomorrow.add(tomorrow.DATE, 1);

            Date tomorrowDate = tomorrow.getTime();
            String tomorrowStr = new SimpleDateFormat("yyyy.MM.dd").format(tomorrowDate);

            dataManager.getManicureCustomers().forEach(customerRegData -> {
                if (customerRegData.getDate().equals(tomorrowStr)) {
                    notifyList.add(getNotifyMessage("Здравствуйте, " + dataManager.getUserName(customerRegData.getTelegramId())
                                    + "!\nНапоминаем, что завтра Вы записаны на прием к мастеру маникюра: \nВремя: "
                                    + customerRegData.getTime() + "\nУслуга: " + customerRegData.getManicureType() + "\nЦена: " + customerRegData.getCost() + "\nВы придете?"
                            , customerRegData.getTelegramId()));
                }
            });
            dataManager.transferMessages(notifyList);
            notified = true;
        } else if (!hour.equals("13")) {
            notified = false;
        }
    }

    private static void anHourBeforeTheService(){
        Date date = new Date();
        String today = new SimpleDateFormat("yyyy.MM.dd").format(date);

        String hourNow = new SimpleDateFormat("kk").format(date);

        dataManager.getManicureCustomers().forEach(customerRegData -> {

            if (customerRegData.getDate().equals(today) && !customerRegData.isNotified()) {
                String regHour = customerRegData.getTime().split(":")[0];

                int x = Integer.parseInt(regHour) - Integer.parseInt(hourNow);
                if (x == 1){
                    customerRegData.setNotified(true);
                    dataManager.transferMessages(
                            List.of(
                                    new SimpleSendMessage("Здравствуйте, "+ dataManager.getUserName(customerRegData.getTelegramId())+"!", customerRegData.getTelegramId()).getNewMessage(null),
                                    new SimpleSendMessage("\uD83D\uDE42", customerRegData.getTelegramId()).getNewMessage(null),
                                    new SimpleSendMessage("Просто хотим напомнить, что через час у Вас прием: ", customerRegData.getTelegramId()).getNewMessage(null),
                                    new SimpleSendMessage(dataManager.getUserManicureRegData(customerRegData.getTelegramId()), customerRegData.getTelegramId()).getNewMessage(null)
                            )
                    );
                }
            }
        });


    }


    private static SendMessage getNotifyMessage(String messageText, long userId) {
        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        List<InlineKeyboardButton> btnInline;

        btnInline = new ArrayList<>();
        btnInline.add(KeyboardsManager.getInlineKeyboardButton("Да, я приду", "regConfirm_YES"));
        rows.add(btnInline);

        btnInline = new ArrayList<>();
        btnInline.add(KeyboardsManager.getInlineKeyboardButton("К сожалению, нет", "regConfirm_NO"));
        rows.add(btnInline);

        markupInline.setKeyboard(rows);

        SendMessage message = new SendMessage();
        message.setChatId(userId);
        message.setReplyMarkup(markupInline);
        message.setText(messageText);
        return message;
    }

}
