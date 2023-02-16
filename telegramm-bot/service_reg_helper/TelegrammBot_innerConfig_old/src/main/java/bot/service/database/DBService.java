package bot.service.database;

import bot.objects.CustomerObject;
import bot.objects.ManicureRegObject;

import java.text.ParseException;
import java.util.*;

public interface DBService {

    void signUpCustomer(CustomerObject customerObject);
    Map<Long, CustomerObject> getCustomers();

    CustomerObject getAdmin();

    void addImageToPortfolio(String uniqueId, String type, String Id);
    void removeImageFromPortfolio(String id);
    Map<String, HashMap<String, String>> getPortfolio();

    void removeImages(List<String> imagesId);




    Map<String, LinkedHashMap<String, String>> getServiceCalendar();
    void serviceCalendarExtension(Map<String, LinkedHashMap<String, String>> regCalendarMap, int count) throws ParseException;

    void updateServiceCalendar(ManicureRegObject manicureRegObject);




    void regCustomerForManicure(ManicureRegObject manicureRegObject);
    Map<Long, ManicureRegObject> getCustomersManicureRegistration();
    void removeCustomerManicureRegistration(ManicureRegObject manicureRegObject);
    void setOpenCloseDate(String openDate, String closeDate);
    public Map<String, String> getOpenCloseDate();




    void adminRemoveService(String serviceName, int price);
    void addOrUpdateServiceAndPrice(String serviceName, int price);
    Map<String, Integer> getServicesAndPrices();






    void createDB();
    void dropDB();

}
