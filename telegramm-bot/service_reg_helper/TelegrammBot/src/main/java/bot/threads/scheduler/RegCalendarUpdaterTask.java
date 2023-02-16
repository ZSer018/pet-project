package bot.threads.scheduler;

import bot.managers.DataManager;

import java.text.SimpleDateFormat;
import java.util.Date;


public class RegCalendarUpdaterTask{

    private static boolean updated = false;

    public static void runIteration() {
        Date date = new Date();

        String today = new SimpleDateFormat("dd").format(date);

        if (Integer.parseInt(today) % 2 == 0 && !updated) {
            DataManager dataManager = DataManager.getInstance();
            dataManager.updateAndReloadFreeDateCalendar();
            updated = true;
        }
         else
             updated = false;
    }





}