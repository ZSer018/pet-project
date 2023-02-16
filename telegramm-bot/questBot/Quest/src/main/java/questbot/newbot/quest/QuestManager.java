package questbot.newbot.quest;

import lombok.Getter;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendAnimation;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Location;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.Update;
import questbot.newbot.data.QuestFilesManager;


import java.io.File;
import java.io.Serializable;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

public class QuestManager {


    private final Quest quest;
    private QuestStage currentPart;
    @Getter
    private final HashSet<Long> PLAYER_ID;
    private final HashSet<Long> ADMIN_ID;

    private boolean pause = false;
    private boolean test = false;

    public QuestManager(HashSet<Long> ADMIN_ID, HashSet<Long> PLAYER_ID, Quest quest) {
        this.quest = quest;
        this.PLAYER_ID = PLAYER_ID;
        this.ADMIN_ID = ADMIN_ID;
    }

    public LinkedHashMap<PartialBotApiMethod<? extends Serializable>, Integer> responseAction(Update update) {
        if (update.hasEditedMessage() && !update.getEditedMessage().hasLocation()){
            return null;
        }

        List<PhotoSize> photoList = null;
        String caption = null;
        long chatId = update.hasMessage()? update.getMessage().getChatId() : update.getEditedMessage().getChatId();
        String messageText = update.hasMessage()? update.getMessage().getText(): update.getEditedMessage().getText();
        Location location = update.hasMessage()? update.getMessage().getLocation(): update.getEditedMessage().getLocation();

        if (update.hasMessage()) {
            photoList = update.getMessage().getPhoto();
            caption = update.getMessage().getCaption();
        }


        if (ADMIN_ID.contains(chatId)){
            return adminAction(messageText, photoList, caption);
        }

        if (PLAYER_ID.contains(chatId)){
            return playerAction(messageText, chatId, location, photoList);
        }

        String user = update.getMessage().getChat().getUserName();
        String userName = update.getMessage().getChat().getFirstName();
        String userLastName = update.getMessage().getChat().getLastName();

        String userinfo = user != null?  "Ник: "+user : "Ник: - " +
                userName != null? "Имя: "+ userName: "Имя: -"+
                userLastName != null? "Фамилия: "+userLastName: "Фамилия: -";

        return messageFilter(userinfo, messageText, chatId);
    }

    private LinkedHashMap<PartialBotApiMethod<? extends Serializable>, Integer> messageFilter(String userInfo, String message, Long chatId){
        var result = new LinkedHashMap<PartialBotApiMethod<? extends Serializable>, Integer>();

        if (message.contains("/start")){
            result.put(sendMessage("Регистрация в квесте: \n\n/player  -  регистрация в качестве участника\n/admin  -  регистрация в качестве администратора\n\n" +
                    "Администратор следит за прохождением квеста и управляет им.\n"+userInfo+"\nВаш ID:"+chatId, chatId), 0);
            return result;
        }

        if (message.contains("/player")){
            PLAYER_ID.add(chatId);
            result.put(sendMessage("Зарегистрирован новый игрок: \n"+userInfo+"\nВаш ID:", chatId), 0);
            return result;
        }

        if (message.contains("/admin")){
            ADMIN_ID.add(chatId);
            result.put(sendMessage("Зарегистрирован администратор: \n"+userInfo+"\nВаш ID:"+chatId, chatId), 0);
            result.put(sendMessage("Команды и возможности администратора:", chatId), 0);
            result.put(sendMessage("\uD83D\uDC49 'старт'    - старт квеста\n\n" +
                    "\uD83D\uDC49 'старт *число*'    - старт квеста с выбранного этапа. Например: 'старт 5' запустит квест начиная с пятого этапа.\n\n" +
                    "\uD83D\uDC49 '- *текст сообщения* времяв мс'       - отправить игрокам сообщение от имени бота, спустя указанное время. " +
                    "Например:  '- Укажите информацию более детально 10000'.\n Если время не указывать, сообщение будет передано немедленно \n" +
                    "Послезно для того, что бы игрок явным образом не видел, что сообщение отправляет администратор, поскольку они приходят с большой задержкой.\n\n" +
                    "\uD83D\uDC49 'далее'   - немедленно перейти к следующему этапу квеста, не дожидаясь ответов игроков или достижения ими геоточки.\n\n" +
                    "\uD83D\uDC49 'тест'    - после включения этой опции, сообщения в этапах квестов будут отправляться, игнорируя время задержки между ними. То есть с задержкой 0 мс. " +
                    "Используется для тестирования квеста до боевого запуска.\n\n" +
                    "\uD83D\uDC49 'пауза'   - после включения этой опции, любой траффик от игрока будет игнорироваться. Полезно, если нужно потянуть время до перехода к следующему этапу.\n\n" +
                    "Любой другой текст отправленный администратором в чат, будет пересылаться другим администраторам.\n\n\n\n" +
                    "Администратор может посылать в чат изображения с коментарием или без. Все изображения посланные в чат, будут отправляться игрокам. " +
                    "Если у изображения есть комментарий, в концен которого после пробела стоит число, оно будет интерпритировано как задержка перед отправкой. \n\n" +
                    "Например: *прикрепленное изображение* Мне кажется, стоит обратить внимание на этот символ.... 50000\n\n" +
                    "Изображение будет отослано с этой подписью спустя 5 секунд после отправки администратором в чат.\n\n\n" +
                    "----------------------------------------------------------------------------------------------------\n\n" +
                    "Любое изображение посланное игроком отправляется администраторам. В ответ на изображение бот отвечает игроку: Хм...\n" +
                    "Используется для того, что бы можно было вести диалог от имени бота, помогая игрокам в орентировании или решении каких-либо задач.", chatId), 0);
            return result;
        }

        return null;
    }


    private LinkedHashMap<PartialBotApiMethod<? extends Serializable>, Integer> playerAction(String message, Long chatId, Location location, List<PhotoSize> photoList){
        if (pause){
            return null;
        }

        if (location != null){
            return checkLocation(location, chatId);
        }

        if (photoList != null){
            return proceedPlayerPhoto(photoList);
        }

        if (message != null){
            return checkAnswer(message.toLowerCase());
        }

        return null;
    }





















    private LinkedHashMap<PartialBotApiMethod<? extends Serializable>, Integer> adminAction(String messageText, List<PhotoSize> photoList, String caption){
        if (messageText != null){
            return proceedAdminCommand(messageText);
        }

        if (photoList != null){
            return proceedAdminPhoto(photoList, caption);
        }

        return null;
    }


    private LinkedHashMap<PartialBotApiMethod<? extends Serializable>, Integer> proceedAdminPhoto(List<PhotoSize> photoList, String caption) {
        var result = new LinkedHashMap<PartialBotApiMethod<? extends Serializable>, Integer>();
        int delay = 0;

        if (caption != null) {
            try {
                delay = Integer.parseInt(caption.substring(caption.lastIndexOf(" ") + 1));
            } catch (NumberFormatException ignored) {}

            if (delay != 0) {
                caption = caption.substring(0, caption.lastIndexOf(" "));
            }
        }

        try {
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

         String fileId = photoList.stream()
                    .sorted(Comparator.comparing(PhotoSize::getFileSize).reversed())
                .findFirst()
                .orElse(null).getFileId();

        String finalCaption = caption;
        PLAYER_ID.forEach(aLong ->  result.put(forwardImg(fileId, finalCaption, aLong), 0));
        return result;
    }


    private LinkedHashMap<PartialBotApiMethod<? extends Serializable>, Integer> proceedAdminCommand(String messageText){
        var result = new LinkedHashMap<PartialBotApiMethod<? extends Serializable>, Integer>();


        if (messageText.toLowerCase().contains("test") || messageText.toLowerCase().contains("тест")) {
            test = !test;
            ADMIN_ID.forEach(aLong -> result.put(sendMessage("тестовый режим = "+test, aLong), 0));
            return result;
        }

        if (messageText.toLowerCase().contains("старт")) {
            if (PLAYER_ID.size() == 0){
                ADMIN_ID.forEach(aLong -> result.put(sendMessage("Невозможно запустить квест, не имея ни одного игрока.", aLong), 0));
                return result;
            }

            if (messageText.contains(" ")) {
                int x = Integer.parseInt(messageText.substring(messageText.lastIndexOf(" ") + 1));
                if (x > 0) {
                    for (int i = 0; i < x; i++) {
                        quest.getQuest().poll();
                    }
                    return loadNextPart(false, null);
                }
            } else {
                return loadNextPart(false, null);
            }
        }

        if (messageText.contains("-")) {
            int x = 0;
            try {
                x = Integer.parseInt(messageText.substring(messageText.lastIndexOf(" ") +1));
            } catch (NumberFormatException ignored) { }

            if (x > 2) {
                 messageText = messageText.substring(0, (messageText.lastIndexOf(" ") +1)).replaceAll("-", "");
            } else {
                messageText = messageText.replaceAll("-", "");
            }

            try {
                Thread.sleep(x);
            } catch (InterruptedException e) {}

            String finalMessageText = messageText;
            PLAYER_ID.forEach(aLong -> result.put(sendMessage(finalMessageText, aLong), 0));
            return result;
        }

        if (messageText.toLowerCase().contains("пауза")){
            pause = !pause;
            ADMIN_ID.forEach(aLong -> result.put(sendMessage("Пауза = "+pause, aLong), 0));
            return result;
        }

        if (messageText.toLowerCase().contains("далее")){
            return loadNextPart(false, null);
        }


        System.out.println(messageText);
        String finalMessageText1 = messageText;
        ADMIN_ID.forEach(System.out::println);
        ADMIN_ID.forEach(aLong -> result.put(sendMessage(finalMessageText1, aLong), 0));
        return result;
    }



    private LinkedHashMap<PartialBotApiMethod<? extends Serializable>, Integer> proceedPlayerPhoto(List<PhotoSize> photoList) {
        var result = new LinkedHashMap<PartialBotApiMethod<? extends Serializable>, Integer>();
            PLAYER_ID.forEach(aLong -> result.put(sendMessage("Хм...", aLong), 500));

        String fileId = photoList.stream()
                .sorted(Comparator.comparing(PhotoSize::getFileSize).reversed())
                .findFirst()
                .orElse(null).getFileId();

        ADMIN_ID.forEach(aLong ->  result.put(forwardImg(fileId, null, aLong), 0));
        return result;
    }

    private LinkedHashMap<PartialBotApiMethod<? extends Serializable>, Integer> loadNextPart(Boolean pointReached, String message){
        var result = new LinkedHashMap<PartialBotApiMethod<? extends Serializable>, Integer>();

        if (pointReached){
            currentPart.getGeoLocationMessages().get("on_point").put("...", currentPart.getOnReachedSwitchToNextStageTime());
            currentPart.getGeoLocationMessages().get("on_point").forEach((s, integer) -> {
                PLAYER_ID.forEach(aLong -> result.put(sendMessage(s, aLong), integer));
            });
        }

        currentPart = quest.getNewPart();
        ADMIN_ID.forEach(aLong ->  result.put(sendMessage("Сообщение игрока: "+message+"\n\nЗагружен новый этап квеста: "+currentPart.getStageName(), aLong), 0));


        var messages = currentPart.getBotMessages();

        messages.forEach((s, integer) -> {
            PartialBotApiMethod<? extends Serializable> msg = null;

            if (s.contains(":\\")|s.contains(":/")){
                if(s.contains(".gif")) {
                    PLAYER_ID.forEach(l -> result.put(sendAnimation(s, null, l), test ? 0 : integer));
                } else {
                    PLAYER_ID.forEach(l -> result.put(sendImg(s, null, l), test ? 0 : integer));
                }
            } else {
                PLAYER_ID.forEach(l -> result.put(sendMessage(s, l), test? 0: integer));
            }
         });

        return result;
    }


    private double getDistance(double lat_a, double lng_a, double lat_b, double lng_b) {
        float pk = (float) (180/3.14169);

        double a1 = lat_a / pk;
        double a2 = lng_a / pk;
        double b1 = lat_b / pk;
        double b2 = lng_b / pk;

        double t1 = Math.cos(a1) * Math.cos(a2) * Math.cos(b1) * Math.cos(b2);
        double t2 = Math.cos(a1)*Math.sin(a2)*Math.cos(b1)*Math.sin(b2);
        double t3 = Math.sin(a1)*Math.sin(b1);
        double tt = Math.acos(t1 + t2 + t3);

        return 6366000*tt;
    }

    private LinkedHashMap<PartialBotApiMethod<? extends Serializable>, Integer> checkLocation(Location location, Long chatId) {
        if (currentPart.isPointReached()){
            return null;
        }

        var result = new LinkedHashMap<PartialBotApiMethod<? extends Serializable>, Integer>();

        double la = location.getLatitude();
        double lo = location.getLongitude();

        var coordinates = currentPart.getGeolocationPoints();

        AtomicReference<Double> distance = new AtomicReference<>(99999999999999.999999999999999);

        coordinates.forEach((integer, s) -> {
            double[] coord = Arrays.stream(s.split(", ")).mapToDouble(Double::parseDouble).toArray();
            double temoDist = getDistance(coord[0], coord[1], la, lo);
            if (temoDist < distance.get()){
                distance.set(temoDist);
            }
        });


        String strDist = "\nРасстояние до цели, примерно: "+String.valueOf(distance.get()).substring(0,5)+" метров";

        if (distance.get() <= currentPart.getGeoOnPointRadius()){
            currentPart.setPointReached(true);

            if (currentPart.isOnReachedSwitchToNextStage()) {
                return loadNextPart(true, null);
            }

            currentPart.getGeoLocationMessages().get("on_point").forEach((s, integer) -> {
                PLAYER_ID.forEach(aLong -> result.put(sendMessage(s, aLong), integer));
            });
            PLAYER_ID.forEach(aLong -> result.put(sendMessage("\n"+strDist, aLong), 0));

            return result;
        }

        if (distance.get() <= currentPart.getGeoPointInnerRadius()){

            if (!currentPart.isInnerRadiusReached()) {
                currentPart.getGeoLocationMessages().get("inner_radius").forEach((s, integer) -> {
                    PLAYER_ID.forEach(aLong -> result.put(sendMessage(s, aLong), integer));
                });
                currentPart.setInnerRadiusReached(true);
            }

            PLAYER_ID.forEach(aLong -> result.put(sendMessage("\n"+strDist, aLong), 0));
            return result;
        }

        if (distance.get() <= currentPart.getGeoPointOuterRadius()){

            if (!currentPart.isOuterRadiusReached()) {
                currentPart.getGeoLocationMessages().get("outer_radius").forEach((s, integer) -> {
                    PLAYER_ID.forEach(aLong -> result.put(sendMessage(s, aLong), integer));
                });
                currentPart.setOuterRadiusReached(true);
            }
            PLAYER_ID.forEach(aLong -> result.put(sendMessage("\n"+strDist, aLong), 0));
            return result;
        }

        if (distance.get() > currentPart.getGeoPointOuterRadius()){
            if (!currentPart.isOutOfRangeReached()) {
                currentPart.getGeoLocationMessages().get("out_of_range").forEach((s, integer) -> {
                    PLAYER_ID.forEach(aLong -> result.put(sendMessage(s, aLong), integer));
                });
                currentPart.setOutOfRangeReached(true);
            }
            PLAYER_ID.forEach(aLong -> result.put(sendMessage("\n"+strDist, aLong), 0));
            return result;
        }

        return result;
    }

    private LinkedHashMap<PartialBotApiMethod<? extends Serializable>, Integer> checkAnswer(String message) {
        var result = new LinkedHashMap<PartialBotApiMethod<? extends Serializable>, Integer>();
        ADMIN_ID.forEach(aLong ->  result.put(sendMessage("Сообщение игрока: "+message, aLong), 0));

        if (currentPart.getGeolocationPoints() != null && !currentPart.isPointReached()) {
            PLAYER_ID.forEach(aLong ->
                    currentPart.getGeoLocationMessages().get("hasty_answer").forEach((s, integer) ->
                            result.put(sendMessage(s, aLong), 0)
                    )
            );
            return result;
        }

        String[] answerMess = message.split(" ");


        var answers = currentPart.getAnswers();

        for (String userMessage : answerMess) {
            Iterator<String> iterator = answers.iterator();
            while (iterator.hasNext()) {
                String answer = iterator.next();
                if (userMessage.contains(answer)) {
                    currentPart.reduceRequiredNumberOfAnswers();
                    iterator.remove();
                }
            }
        }

        if (currentPart.isShowCorrectAnswerMessages()) {
            PLAYER_ID.forEach(aLong -> result.put(sendMessage("Верно", aLong), 0));
        }

        if (currentPart.getRequiredNumberOfAnswers() < 1) {
            return loadNextPart(false, message);
        }

        return result;
    }



    public SendMessage sendMessage(String messageText, long chatId) {
        SendMessage message = new SendMessage();
        message.setText(messageText);
        message.setChatId(chatId);
        return message;
    }


    public SendPhoto sendImg(String file, String caption, long chatId) {
        SendPhoto picMsg = new SendPhoto();
        picMsg.setChatId(chatId);
        if (file.contains(":\\") || file.contains(":/")){
            picMsg.setPhoto(new InputFile(new File(file)));
        } else {
            picMsg.setPhoto(new InputFile(file));
        }

        if (caption != null) {
            picMsg.setCaption(caption);
        }

        return picMsg;
    }


    public SendAnimation sendAnimation(String file, String caption, long chatId) {
        SendAnimation picMsg = new SendAnimation();
        picMsg.setChatId(chatId);
        if (file.contains(":\\") || file.contains(":/")){
            picMsg.setAnimation(new InputFile(new File(file)));
        } else {
            picMsg.setAnimation(new InputFile(file));
        }

        if (caption != null) {
            picMsg.setCaption(caption);
        }
        return picMsg;
    }


    public SendPhoto forwardImg(String fileID, String picText, long chatId) {
        SendPhoto picMsg = new SendPhoto();
        picMsg.setChatId(chatId);
        picMsg.setPhoto(new InputFile(fileID));
        picMsg.setCaption(picText);
        return picMsg;
    }


    @Override
    public String toString() {
        return "QuestManager{" +
                "quest=" + quest +
                ", currentPart=" + currentPart +
                ", PLAYER_ID=" + PLAYER_ID +
                ", ADMIN_ID=" + ADMIN_ID +
                ", pause=" + pause +
                ", test=" + test +
                '}';
    }
}
