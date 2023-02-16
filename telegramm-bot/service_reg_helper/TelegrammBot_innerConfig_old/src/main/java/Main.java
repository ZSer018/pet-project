import bot.Bot;
import bot.managers.DataManager;
import bot.service.database.DBService;
import bot.service.database.mongo.MongodbService;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.io.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class Main{

    private static TelegramBotsApi botsApi = null;
    private static Bot bot = null;

    private static final Logger logger = LogManager.getLogger();

    public static void main(String[] args)  {
        logger.info("!!!!!!!!!!!!!!!!------------- Запуск приложения -------------!!!!!!!!!!!!!!!");

        DBService dbService = new MongodbService();
        DataManager.init(dbService, bot);
        Bot bot = new Bot("5691626921:AAHnkCqZolUvgx6zJifELpuuYDQHA5RRuKM", "@MemoryBank_bot");
        //Bot bot = new Bot("5736402771:AAG7z_FRTpOPG3G9Qef8zwymQVZoNjKJPEo", "@NailServiceManager_bot");
        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(bot);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }


    }


}