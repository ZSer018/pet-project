package questbot.newbot;

import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import questbot.newbot.bot.Bot;
import questbot.newbot.data.QuestFilesManager;
import questbot.newbot.data.qustParser.QuestParser;
import questbot.newbot.quest.Quest;


public class StartTextQuest {


    public static void main(String[] args) {
        QuestParser questParser = new QuestParser();
        System.out.println("------------LOADING TEXT QUEST FILE------------");
        Quest quest = questParser.loadAndParseQuest();

        QuestFilesManager questFilesManager = new QuestFilesManager();
        questFilesManager.buildQuestFile(quest);


        Bot bot = new Bot(
                questFilesManager.getProperty("bot.properties","bot_token"),
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
