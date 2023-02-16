package bot.objects;

import lombok.Data;

@Data
public class ManicureRegObject {

    private boolean notified = false;

    private long telegramId = 0;
    private String manicureType = null;
    private int cost;

    private String chosenWeek = null;
    private String date = null;
    private String time = null;

    private CustomerObject master;



    @Override
    public String toString() {
        return  "Услуга: " + manicureType + '\n' +
                "Цена: " + cost + '\n' +
                "Дата: " + date + '\n' +
                "Время: " + time + '\n' ;
                //+"Мастер: " + master.getName() + '\n' ;
    }
}
