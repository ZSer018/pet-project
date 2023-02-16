package questbot.newbot.bot;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendAnimation;
import org.telegram.telegrambots.meta.api.methods.send.SendMediaGroup;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import questbot.newbot.quest.Quest;
import questbot.newbot.quest.QuestManager;

import java.io.Serializable;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Bot extends TelegramLongPollingBot {

    private final String BOT_TOKEN;
    private final String BOT_NAME;
    private final QuestManager questManager;
    private boolean mutePlayer = false;

    private final ScheduledExecutorService executorService;

    public Bot(String BOT_TOKEN, String BOT_NAME, HashSet<Long> admins, HashSet<Long> players, Quest quest) {
        this.BOT_TOKEN = BOT_TOKEN;
        this.BOT_NAME = BOT_NAME;

        questManager = new QuestManager(admins, players, quest);
        executorService = Executors.newSingleThreadScheduledExecutor();
    }



    @Override
    public String getBotUsername() {
        return BOT_NAME;
    }

    @Override
    public String getBotToken() {
        return BOT_TOKEN;
    }


    @Override
    public void onUpdateReceived(Update update) {
        long chatId = update.hasMessage()? update.getMessage().getChatId() : update.getEditedMessage().getChatId();

        if (questManager.getPLAYER_ID().contains(chatId)){
            if (mutePlayer){
                return;
            }
        }

        executeMessage(questManager.responseAction(update));
    }

    public void executeMessage(LinkedHashMap<PartialBotApiMethod<? extends Serializable>, Integer> messages) {
        if (messages == null) {
            return;
        }
        mutePlayer = true;

        messages.forEach((message, integer) -> {
                    String x = message.getClass().getName().substring(message.getClass().getName().lastIndexOf(".") + 1);

                    try {
                        switch (x) {
                            case "SendAnimation" -> execute((SendAnimation) message);
                            case "SendPhoto" -> execute((SendPhoto) message);
                            case "SendMediaGroup" -> execute((SendMediaGroup) message);
                            case "SendMessage" -> execute((SendMessage) message);
                        }

                        Thread.sleep(integer);

                    } catch (TelegramApiException | InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
        );

        executorService.scheduleAtFixedRate(this::appendMsg, 0, 3, TimeUnit.SECONDS);
     }

    private void  appendMsg(){
        mutePlayer = false;
    }

}
