package questbot.newbot;


import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import questbot.newbot.bot.Bot;
import questbot.newbot.data.QuestFilesManager;
import questbot.newbot.quest.Quest;

public class StartJsonQuest {


    public static void main(String[] args) {
        QuestFilesManager questFilesManager = new QuestFilesManager();
        System.out.println("------------LOADING JSON QUEST FILE------------");
        Quest quest = questFilesManager.readQuestFile();

        Bot bot = new Bot(
                questFilesManager.getProperty("bot.properties", "bot_token"),
                questFilesManager.getProperty("bot.properties","bot_name"),
                questFilesManager.getIdSet("admin_id"),
                questFilesManager.getIdSet("player_id"),
                quest

        );
        System.out.println("------------LOADING DONE------------");

        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(bot);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

}
