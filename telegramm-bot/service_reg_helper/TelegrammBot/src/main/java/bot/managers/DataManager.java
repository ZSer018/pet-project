package bot.managers;
import bot.Bot;
import bot.objects.CustomerObject;
import bot.objects.ManicureRegObject;
import bot.service.database.DBService;
import bot.simplemessage.SimpleEditMessage;
import bot.simplemessage.SimpleSendMessage;
import bot.threads.scheduler.ManicureRegistrationMapUpdateTask;
import bot.threads.scheduler.NotifyTask;
import bot.threads.scheduler.RegCalendarUpdaterTask;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

public class DataManager {

    private static DataManager dataManager;
    private final DBService dbService;
    private static ScheduledExecutorService executorService;
    private final Bot bot;






    public final Long ADMIN_ID;
    public boolean admin_addingPhotos = false;
    public boolean admin_deletePhotos = false;
    public boolean admin_editServicesAndPrices = false;
    public boolean admin_sendingMessagesToAllCustomers = false;
    public String admin_stringMessageToAll = null;
    public boolean admin_regNotify = false;
    public boolean admin_canRegOnService = true;
    public boolean admin_choosingRegDate = false;
    public boolean admin_setUpNotifyString = false;
    public String admin_manicureRegCloseDate = null;
    public String admin_manicureRegOpenDate = null;
    public String admin_ServicesAndPricesMessage = null;



    private final Map<Long, CustomerObject> customers;
    private final Map<String, HashMap<String, String>> portfolio;
    private Map<String, LinkedHashMap<String, String>> manicureFreeDateCalendar;
    private final Map<String, Integer> servicesAndPrices;
    private Map<Long, ManicureRegObject> customersManicureRegistration;



    public enum userSignUpStatus { NO_SIGNUP, SIGNUP_COMPLETED, SIGNUP_IN_PROGRESS}
    public enum manicureRegStatus {NO_REG_ERROR, SELECT_TYPE, SELECT_WEEK, SELECT_DAY, SELECT_HOUR, REG_COMPLETE}



    private DataManager(DBService dbService, Long adminID, String adminName, String adminTGUsername, Bot bot) {
        this.dbService = dbService;
        this.bot = bot;
        executorService = Executors.newScheduledThreadPool(3);

        //dbService.dropDB();

        customers = dbService.getCustomers();
        portfolio = dbService.getPortfolio();
        manicureFreeDateCalendar = dbService.getServiceCalendar();
        servicesAndPrices = dbService.getServicesAndPrices();
        customersManicureRegistration = dbService.getCustomersManicureRegistration();
        ADMIN_ID = adminID;

        CustomerObject admin;
        if (customers.containsKey(adminID)) {
            admin = customers.get(adminID);
        } else {
            admin = new CustomerObject();
            admin.setTelegramId(ADMIN_ID);
        }
            admin.setName(adminName);
            admin.setTgUsername(adminTGUsername);
            customers.put(ADMIN_ID, admin);

        var days = dbService.getBotOptions();
        if (days.size() != 0){
            admin_manicureRegOpenDate = days.get("openDate");
            admin_manicureRegCloseDate = days.get("closeDate");
            admin_canRegOnService = Boolean.parseBoolean(days.get("regOnService"));
            admin_regNotify = Boolean.parseBoolean(days.get("regNotify"));
            admin_ServicesAndPricesMessage = days.get("serviceMessage");
        }
        days.forEach((key, value) -> System.out.println(key + " , " + value));
    }



    public static void init(DBService dbService, long adminId, String adminName, String adminTGUsername, Bot bot){
        if (dataManager == null) {
            dataManager = new DataManager(dbService, adminId, adminName, adminTGUsername, bot);

            executorService.scheduleAtFixedRate(NotifyTask::runIteration, 0, 10, TimeUnit.MINUTES);
            executorService.scheduleAtFixedRate(ManicureRegistrationMapUpdateTask::runIteration, 0, 1, TimeUnit.HOURS);
            executorService.scheduleAtFixedRate(RegCalendarUpdaterTask::runIteration, 0, 1, TimeUnit.DAYS);
        } else throw new IllegalStateException("Instance already was initialized");
    }

    public static DataManager getInstance() {
        if (dataManager != null) {
            return dataManager;
        }

        throw new IllegalStateException("Instance is not initialize");
    }







    public void adminSetDefault() {
        admin_addingPhotos = false;
       admin_deletePhotos = false;
       admin_setUpNotifyString = false;
       admin_sendingMessagesToAllCustomers = false;
       admin_editServicesAndPrices = false;
       admin_choosingRegDate = false;

        userSetDefault(ADMIN_ID);
    }

    public void userSetDefault(long userId) {
        CustomerObject customer = customers.get(userId);
        customer.setViewingPhotos(false);
        customer.setViewingType("-none-");
        customer.setViewingImageList(null);
    }











    public void isUsersContains(long userId){
        if (!customers.containsKey(userId)){
            customers.put(userId, new CustomerObject());
        }
    }
    public String getCustomerSignUpDate(long userId){
        return customers.get(userId).getSignInDate();
    }
    public void setCustomerSignUpDate(long userId, String date){
        customers.get(userId).setSignInDate(date);
    }
    public String getCustomerTgUsername(long userId){
        return customers.get(userId).getTgUsername();
    }
    public void setCustomerTgUsername(long userId, String TGUserName){
        customers.get(userId).setTgUsername(TGUserName);
    }
    public boolean userActivityStatus(long userId) {
        CustomerObject customer = customers.get(userId);
        return customer.isViewingPhotos();
        // return customer.isViewingPhotos() || customer.isAddingPhotos() || customer.isDeletePhotos() || customer.isEditServicesAndPrices();
    }
    public void userGoView(long userId) {
        customers.get(userId).setViewingPhotos(true);
    }
    public boolean userIsView(long userId) {
        if (customers.containsKey(userId)) {
            return customers.get(userId).isViewingPhotos();
        }
        return false;
    }
    public String getUserViewType(long userId) {
        return customers.get(userId).getViewingType();
    }
    public void setUserViewType(long userId, String text) {
        customers.get(userId).setViewingType(text);
    }
    public HashMap<String, String> getUserViewingList(long userId) {
        return customers.get(userId).getViewingImageList();
    }
    public void setUserViewingList(long userId, HashMap<String, String> map) {
        customers.get(userId).setViewingImageList(map);
    }
    public String getUserName(long userId) {
        return customers.get(userId).getName();
    }
    public void setUserName(long userId, String name) {
        customers.get(userId).setName(name);
    }
    public String getUserPhone(long userId) {
        return customers.get(userId).getPhone();
    }
    public void setUserPhone(long userId, String phone) {
        customers.get(userId).setPhone(phone);
    }
    public void setUserTgId(long userId, long setId) {
        customers.get(userId).setTelegramId(setId);
    }
    public userSignUpStatus getUserSignUpStatus(long userId) {
        if (!customers.get(userId).equals(new CustomerObject())) {
            if (customers.get(userId).getTelegramId() != 0) {
                return userSignUpStatus.SIGNUP_COMPLETED;
            }
            return userSignUpStatus.SIGNUP_IN_PROGRESS;
        }
        return userSignUpStatus.NO_SIGNUP;
    }
    public void cancelSignUp(long id){
        customers.put(id, new CustomerObject());
    }
    public boolean addNewCustomer(long userId) {
        try {
            dbService.signUpCustomer(customers.get(userId));
        } catch (Exception e) {
            System.out.println("Error DB insert: " + e.getMessage());
            return false;
        }
        return true;
    }








    public void transferMessages(List<PartialBotApiMethod<? extends Serializable>> messageList){
        bot.executeMessage(messageList);
    }

    public String getAdminNotifyMessage(){
        return admin_ServicesAndPricesMessage == null? " ": "\n--------\n"+admin_ServicesAndPricesMessage;
    }







    public boolean addPortfolioImg(String uniqueId, String imgId){
        var innerMap = portfolio.containsKey(dataManager.getUserViewType(ADMIN_ID)) ? portfolio.get(dataManager.getUserViewType(ADMIN_ID)) : new HashMap<String, String>();

            if (innerMap.containsKey(uniqueId)){
                return false;
            }

            innerMap.put(uniqueId, imgId);

            portfolio.put(dataManager.getUserViewType(ADMIN_ID), innerMap);
            dbService.addImageToPortfolio(uniqueId, dataManager.getUserViewType(ADMIN_ID), imgId);
            return true;
    }
    public boolean removeImgFromPortfolio(String fileUniqueId){

        AtomicBoolean found = new AtomicBoolean(false);

         portfolio.forEach((s, innerMap) -> {
                if (innerMap.containsKey(fileUniqueId)) {
                    found.set(true);
                    innerMap.remove(fileUniqueId);
                    dbService.removeImageFromPortfolio(fileUniqueId);
                }
         });

        return found.get();
    }
    public HashMap<String, String> getPortfolioImgByType(String type){
        System.out.println("Portfolio is in: "+portfolio.containsKey(type));
       return portfolio.get(type);
    }












    public String strDateToDateAndMonthName(String date){
        String temp[] = date.split("\\.");
        Calendar calendar = Calendar.getInstance();
        calendar.set(1, Integer.parseInt(temp[1])-1, Integer.parseInt(temp[2]));
        return temp[2] + " " + calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, new Locale("ru"));
    }
    public HashMap<String, String> getRegWeeks() {
        int x = -1;
        StringBuilder stringBuilder = new StringBuilder();
        Calendar calendar = Calendar.getInstance();
        String tempdate = null;
        boolean pass = false;

        LinkedHashMap<String, String> manicureWeeksData = new LinkedHashMap<>();
        for (var entry : manicureFreeDateCalendar.keySet()) {

            if (entry.equals(admin_manicureRegCloseDate)){
                pass = true;
            }

            if (entry.equals(admin_manicureRegOpenDate)){
                pass = false;
            }

            if (!pass) {
                x++;
            }

            if (x > 7) {
                x = 0;
            }

            if (!pass && x == 0) {
                tempdate = entry;
                stringBuilder.append(strDateToDateAndMonthName(entry));
            }
            if (!pass && x == 7) {
                stringBuilder.append("  -  ").append(strDateToDateAndMonthName(entry));
                manicureWeeksData.put(stringBuilder.toString(), tempdate);
                stringBuilder.setLength(0);
            }
        }

        if (stringBuilder.length() > 5 & stringBuilder.length() < 15) {
            manicureWeeksData.remove(stringBuilder.toString());
            stringBuilder.append(" и далее...");
            manicureWeeksData.put(stringBuilder.toString(), tempdate);
        }

        return manicureWeeksData;
    }
    public LinkedHashMap<String, String> getRegDays(long customerId) {
        var temp = new LinkedHashMap<String, String>();
        String date = customersManicureRegistration.get(customerId).getChosenWeek();

        boolean pass = false;
        int x = 0;
        boolean getSeven = false;

        for (var entry : manicureFreeDateCalendar.entrySet()) {
            if (entry.getKey().equals(admin_manicureRegCloseDate)) {
                pass = true;
            }

            if (entry.getKey().equals(admin_manicureRegOpenDate)) {
                pass = false;
            }

            if (entry.getKey().equals(date)) {
                getSeven = true;
            }

            if (getSeven) {
                long count = entry.getValue().values().stream().filter(s -> s.equals("+")).count();
                if (count > 0) {
                    if (!pass) {
                        temp.put(entry.getKey(), strDateToDateAndMonthName(entry.getKey()));
                        x++;
                    }
                }
            }
            if (x == 8) {
                break;
            }
        }

        return temp;
    }
    public ArrayList<String> getRegHours(long customerId) {
        var temp = new ArrayList<String>();
        String date = customersManicureRegistration.get(customerId).getDate();

        var hours = manicureFreeDateCalendar.get(date);
        if (hours.entrySet().stream().anyMatch(e -> e.getValue().equals("+"))) {
            hours.entrySet().stream().filter(e -> e.getValue().equals("+")).forEach(e -> temp.add(e.getKey()));

        }
        return temp;
    }
    public boolean checkManucureRegDataIsFree(String date, String time) {
        return manicureFreeDateCalendar.get(date).get(time).equals("+");
    }







    public List<PartialBotApiMethod<? extends Serializable>> admin_getGroupMessageList(Update update){
        var messages = new LinkedList<PartialBotApiMethod<? extends Serializable>>();
        messages.add(new SimpleEditMessage("Сообщение отправлено...").getNewEditMessage(update));

        customers.forEach((k, customer) -> {
            if (customer.getTelegramId() != dataManager.ADMIN_ID) {
                messages.add(new SimpleSendMessage("Здравствуйте, "+customer.getName() + "! "+ "Хотим сообщить что:\n"+admin_stringMessageToAll, customer.getTelegramId()).getNewMessage(update));
            }
        });

        admin_stringMessageToAll = "";
        return messages;
    }





    public String getServicesAndPricesString(){
        if (servicesAndPrices.size() == 0){
            return "-Список услуг пуст-";
        }
        StringBuilder stringBuilder = new StringBuilder();
        servicesAndPrices.forEach((k, v) -> stringBuilder.append(k).append(": ").append(v).append("\n"));
        return stringBuilder.toString();
    }
    public Set<String> getServices(){
        if (servicesAndPrices.size() != 0){
            return servicesAndPrices.keySet();
        }
        return new HashSet<>();
    }
    public void addOrUpdateServiceAndPrice(String serviceName, int price){
        String name = "-none-";
        for (var entry: servicesAndPrices.entrySet()) {
            if (entry.getKey().equalsIgnoreCase(serviceName)){
                name = entry.getKey();
            }
        }

        if (!name.equals("-none-")) {
            dbService.addOrUpdateServiceAndPrice(name, price);
            servicesAndPrices.put(name, price);
        }else {
            dbService.addOrUpdateServiceAndPrice(serviceName, price);
            servicesAndPrices.put(serviceName, price);
        }
    }
    public boolean removeService(String serviceName){
        String name = null;
        for (var entry: servicesAndPrices.entrySet()) {
            if (entry.getKey().equalsIgnoreCase(serviceName)){
                name = entry.getKey();
            }
        }

        if (name != null) {
            dbService.adminRemoveService(serviceName, servicesAndPrices.get(name));
            servicesAndPrices.remove(name);
            return true;
        }

        return false;
    }
    public int getServicePrice(String serviceName){
        return servicesAndPrices.get(serviceName);
    }
    public Set<String> getServicesByName(){
        return servicesAndPrices.keySet();
    }








    public ManicureRegObject getManicureRegObject(long userId) {
        return customersManicureRegistration.get(userId);
    }

    public void manicureRegStart(long userId){
        customersManicureRegistration.put(userId, new ManicureRegObject());
    }

    public void manicureCancelReg(long userId){
        updateRegCalendar(getManicureRegObject(userId), true);
        dbService.removeCustomerManicureRegistration(customersManicureRegistration.get(userId));
        customersManicureRegistration.remove(userId);
    }

    public void manicureRegAbort(long userId){
        customersManicureRegistration.remove(userId);
    }

    public manicureRegStatus customerManicureRegStatus(long userId){

        if (customersManicureRegistration.containsKey(userId)){
            ManicureRegObject reg = customersManicureRegistration.get(userId);

            if (reg.getTelegramId() != 0){
                return manicureRegStatus.REG_COMPLETE;
            } else if (reg.getDate() != null){
                return manicureRegStatus.SELECT_HOUR;
            }else if (reg.getChosenWeek() != null){
                return manicureRegStatus.SELECT_DAY;
            } else if (reg.getManicureType() != null){
                return manicureRegStatus.SELECT_WEEK;
            } else return manicureRegStatus.SELECT_TYPE;
        }
        return manicureRegStatus.NO_REG_ERROR;
    }

    public boolean manicureDBRegistration(long userId) {
        try {
            dbService.regCustomerForManicure(customersManicureRegistration.get(userId));
        } catch (Exception e) {
            System.out.println("Error DB insert: " + e.getMessage());
            return false;
        }
        return true;
    }

    public Collection<ManicureRegObject> getManicureCustomers(){
        return customersManicureRegistration.values();
    }


    public String getUserManicureRegData(long userId){
        return "Дата : "+strDateToDateAndMonthName(customersManicureRegistration.get(userId).getDate()) +"\n"+
               "Время : "+customersManicureRegistration.get(userId).getTime() +"\n"+
               "Услуга : "+customersManicureRegistration.get(userId).getManicureType() +"\n"+
               "Стоимость : "+customersManicureRegistration.get(userId).getCost();
    }

    public String getAllManicureRecords(){
        StringBuilder regList = new StringBuilder();
        customers.forEach((aLong, customerObject) -> System.out.println(aLong + ", "+ customerObject));
        customersManicureRegistration.forEach((K, V) -> {
            System.out.println(customers.get(K));
            regList.append(strDateToDateAndMonthName(V.getDate()))
                    .append(", ")
                    .append(V.getTime())
                    .append(": \n")
                    .append(customers.get(K).getName()).append(", \n")
                    .append(V.getManicureType())
                    .append(", цена: ")
                    .append(V.getCost())
                    .append("\n\n");

        });

        if (regList.toString().length() == 0){
            return "Записей нет";
        }
        return regList.toString();
    }

    public String getTodayManicureRecords() {
        StringBuilder regList = new StringBuilder();

        Calendar calendar = Calendar.getInstance();
        Date d = calendar.getTime();
        String today = new SimpleDateFormat("yyyy.MM.dd").format(d);

        var list = customersManicureRegistration
                .values()
                .stream()
                .filter(v -> v.getDate().equals(today))
                .collect(Collectors.toList());


        if (list.size() != 0) {
            list.forEach(customer -> {
                regList .append(customer.getTime())
                        .append(": \n")
                        .append(customers.get(customer.getTelegramId()).getName()).append(", \n")
                        .append(customer.getManicureType())
                        .append(", цена: ")
                        .append(customer.getCost())
                        .append("\n\n");

            });
        }
        if (regList.toString().length() == 0){
            return "Записей нет";
        }
        return regList.toString();
    }

    public String getFreeManicureDateTime(){
        StringBuilder regList = new StringBuilder();
        var freeReg = new LinkedHashMap<String, ArrayList<String>>();

        for (var entry : manicureFreeDateCalendar.entrySet() ) {

            var map =  entry.getValue();
            var list = new ArrayList<String>();
            for (var entry2: map.entrySet()) {
                if (entry2.getValue().equals("+")){
                    list.add(entry2.getKey());
                }
            }
            if (list.size() != 0) {
                freeReg.put(entry.getKey(), list);
            }
        }


        freeReg.forEach((date, hours) -> {
            regList.append(strDateToDateAndMonthName(date))
                    .append(": ")
                    .append(hours.size()==4? "  - весь день -  " : "  "+ Arrays.toString(hours.toArray()))
                    .append("\n");
        });

        return regList.toString();
    }

    public Map<String, String> getServiceCalendar(){
        var freeReg = new LinkedHashMap<String, String>();
        Calendar calendar = Calendar.getInstance();

        for (int i = 0; i < manicureFreeDateCalendar.size(); i++) {
            calendar.add(Calendar.DATE, 1);
            Date date = calendar.getTime();
            String dateStr = new SimpleDateFormat("yyyy.MM.dd").format(date);
            freeReg.put(strDateToDateAndMonthName(dateStr), dateStr);
        }

        return freeReg;
    }

    public List<String> getDateTime(String date) {
        var list = new ArrayList<String>();
        var map = manicureFreeDateCalendar.get(date);

        for (var entry : map.entrySet()) {
            //if (entry.getValue().equals("+")) {
                list.add(entry.getKey());
            //}
        }
        return list;
    }


    public SendMessage editRegTimeOnSelectedDate(String date, String time, long chatId) {
        var tempDate = manicureFreeDateCalendar.get(date);
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);

        if (manicureFreeDateCalendar.containsKey(date)) {

            var customerRegList = customersManicureRegistration.values().stream().filter(manicureRegObject -> manicureRegObject.getDate().equals(date)).collect(Collectors.toList());
            var customer = customerRegList.stream().filter(manicureRegObject -> manicureRegObject.getTime().equals(time)).collect(Collectors.toList());

            if (customer.size() > 0) {
                InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
                List<List<InlineKeyboardButton>> rows = new ArrayList<>();
                List<InlineKeyboardButton> btnInline = new ArrayList<>();
                btnInline.add(KeyboardsManager.getInlineKeyboardButton("Да, отменить", "adminEditDate_yes_" + customer.get(0).getTelegramId()));
                rows.add(btnInline);

                btnInline = new ArrayList<>();
                btnInline.add(KeyboardsManager.getInlineKeyboardButton("Не отменять", "adminEditDate_no"));
                rows.add(btnInline);

                markupInline.setKeyboard(rows);
                sendMessage.setReplyMarkup(markupInline);
                sendMessage.setText("На это время у Вас есть запись: \n" + customers.get(customer.get(0).getTelegramId()).getName() + " : " + customer.get(0).getManicureType() + "\nОтменить запись?");
                return sendMessage;
            } else {


                sendMessage.setText("Время '" + time + "' теперь ");
                if (tempDate.get(time) != null) {
                    if (tempDate.get(time).equals("+")) {
                        tempDate.put(time, "-");
                        manicureFreeDateCalendar.put(date, tempDate);
                        sendMessage.setText(sendMessage.getText() + " недоступно для записи");
                    } else {
                        tempDate.put(time, "+");
                        manicureFreeDateCalendar.put(date, tempDate);
                        sendMessage.setText(sendMessage.getText() + " доступно для записи");
                    }
                }
                return sendMessage;
            }

        }
        sendMessage.setText(" - Время не найдено - ");
        return sendMessage;
    }



    public void saveBotOptions() {
        dbService.setBotOptions(admin_regNotify,
                admin_canRegOnService,
                admin_manicureRegCloseDate,
                admin_manicureRegOpenDate,
                admin_ServicesAndPricesMessage);
    }


    public void saveRegCalendarChanges(ManicureRegObject object){
        dbService.updateServiceCalendar(object);
    }

    public void updateRegCalendar(ManicureRegObject manicureRegObject, boolean canselReg){
        var temp = manicureFreeDateCalendar.get(manicureRegObject.getDate());
        if (temp != null) {
            if (canselReg) {
                temp.put(manicureRegObject.getTime(), "+");
            } else {
                temp.put(manicureRegObject.getTime(), "-");
            }
            manicureFreeDateCalendar.put(manicureRegObject.getDate(), temp);
        }
    }

    public void updateAndReloadFreeDateCalendar(){
        manicureFreeDateCalendar = dbService.getServiceCalendar();
    }

    public void updateManicureRegMap(){
        customersManicureRegistration = dbService.getCustomersManicureRegistration();
    }

}
