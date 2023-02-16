import com.google.common.util.concurrent.AbstractScheduledService;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class TestClass extends AbstractScheduledService {

    @Override
    protected void startUp() {
        System.out.println("Job started at: " + new java.util.Date());
    }

    @Override
    protected void runOneIteration() {
        Date date = new Date();
        String f = new SimpleDateFormat("dd").format(date);
        System.out.println("-------"+f);

        if (f.equals("10") | f.equals("15") | f.equals("20") | f.equals("25")) {

        }
    }

    @Override
    protected Scheduler scheduler() {
        return Scheduler.newFixedRateSchedule(0, 1, TimeUnit.DAYS);
    }

    @Override
    protected void shutDown() {
        System.out.println("Job terminated at: " + new java.util.Date());
    }






}
