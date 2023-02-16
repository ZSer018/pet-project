package bot.threads.scheduler;

import bot.managers.DataManager;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ManicureRegistrationMapUpdateTask {

    public static void runIteration() {
        Date date = new Date();
        String hour = new SimpleDateFormat("kk").format(date);

       if (hour.equals("19")) {
            DataManager dataManager = DataManager.getInstance();
            dataManager.updateManicureRegMap();
        }
    }
}
