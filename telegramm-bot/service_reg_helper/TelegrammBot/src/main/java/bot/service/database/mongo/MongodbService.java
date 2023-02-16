package bot.service.database.mongo;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import bot.objects.CustomerObject;
import bot.objects.ManicureRegObject;
import org.bson.BsonDocument;
import org.bson.Document;
import bot.service.database.DBService;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


public class MongodbService implements DBService {

    private final MongoClientURI connectionString;

    public MongodbService() {
        connectionString = new MongoClientURI("mongodb://localhost:27017");
    }


    @Override
    public CustomerObject getAdmin() {
        return null;
    }



    @Override
    public Map<Long, CustomerObject> getCustomers() {
        var usermap = new ConcurrentHashMap<Long, CustomerObject>();

        MongoClient mongoClient = null;
        try {
            mongoClient = new MongoClient(connectionString);
            MongoDatabase database = mongoClient.getDatabase("BeautyService");
            MongoCollection<Document> collection = database.getCollection("users");

            try (MongoCursor<Document> cur = collection.find().iterator()) {
                while (cur.hasNext()) {
                    var doc = cur.next();
                    var customer = new CustomerObject();
                    customer.setName(String.valueOf(doc.get("name")));
                    customer.setPhone(String.valueOf(doc.get("phone")));
                    customer.setTgUsername(String.valueOf(doc.get("contact")));
                    customer.setTelegramId((long) doc.get("telegramId"));
                    //customer.setAdminNotifyMessage(  doc.containsKey("notifyString")? (String) doc.get("notifyString") : null);
                    customer.setSignInDate((String) doc.get("setSignInDate"));

                    usermap.put((long) doc.get("telegramId"), customer);
                }
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            assert mongoClient != null;
            mongoClient.close();
        }
        return usermap;
    }

    @Override
    public void signUpCustomer(CustomerObject customerObject) {
        MongoClient mongoClient = null;
        try {
            mongoClient = new MongoClient(connectionString);
            MongoDatabase database = mongoClient.getDatabase("BeautyService");
            MongoCollection<Document> collection = database.getCollection("users");

            Document doc = new Document("telegramId", customerObject.getTelegramId())
                    .append("name", customerObject.getName())
                    .append("phone", customerObject.getPhone())
                    .append("contact", customerObject.getTgUsername())
                    //.append("notifyString", customerObject.getAdminNotifyMessage())
                    .append("setSignInDate", customerObject.getSignInDate());

            collection.insertOne(doc);

        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        } finally {
            assert mongoClient != null;
            mongoClient.close();
        }
    }









    @Override
    public Map<String, HashMap<String, String>> getPortfolio() {
        var portfolio = new ConcurrentHashMap<String, HashMap<String,String>>();

        MongoClient mongoClient = null;
        try {
            mongoClient = new MongoClient(connectionString);
            MongoDatabase database = mongoClient.getDatabase("BeautyService");
            MongoCollection<Document> collection = database.getCollection("portfolio");

            try (MongoCursor<Document> cur = collection.find().iterator()) {
                while (cur.hasNext()) {
                    var doc = cur.next();
                    var innerMap= portfolio.get((String) doc.get("type")) != null?
                            portfolio.get((String) doc.get("type"))
                            : new HashMap<String, String>();

                    innerMap.put((String) doc.get("uniqueID"), (String) doc.get("ID"));
                    portfolio.put((String) doc.get("type"), innerMap);
                }
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            assert mongoClient != null;
            mongoClient.close();
        }
        return portfolio;
    }


    @Override
    public void addImageToPortfolio(String uniqueId, String type, String Id) {
        MongoClient mongoClient = null;
        try {
            mongoClient = new MongoClient(connectionString);
            MongoDatabase database = mongoClient.getDatabase("BeautyService");
            MongoCollection<Document> collection = database.getCollection("portfolio");

            Document doc = new Document("uniqueID", uniqueId)
                    .append("type", type)
                    .append("ID", Id);
            collection.insertOne(doc);

        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }finally {
            assert mongoClient != null;
            mongoClient.close();
        }
    }


    public void removeImageFromPortfolio(String uniqueId) {
        MongoClient mongoClient = null;
        try {
            mongoClient = new MongoClient(connectionString);
            MongoDatabase database = mongoClient.getDatabase("BeautyService");
            MongoCollection<Document> collection = database.getCollection("portfolio");

            try (MongoCursor<Document> cur = collection.find().iterator()) {
                while (cur.hasNext()) {
                    var doc = cur.next();
                    if (doc.get("uniqueID").equals(uniqueId)){
                        System.out.println("REMOVE");
                        collection.deleteOne(doc);
                        break;
                    }
                }
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            assert mongoClient != null;
            mongoClient.close();
        }
    }


    @Override
    public void removeImages(List<String> imagesId) {}

    @Override
    public Map<String,  LinkedHashMap<String, String>> getServiceCalendar() {

        var regCalendarMap = Collections.synchronizedMap(new LinkedHashMap<String, LinkedHashMap<String, String>>());

        MongoClient mongoClient = null;
        try {
            mongoClient = new MongoClient(connectionString);
            MongoDatabase database = mongoClient.getDatabase("BeautyService");
            MongoCollection<Document> collection = database.getCollection("regCalendar");

            Date today = new Date();

            try (MongoCursor<Document> cur = collection.find().iterator()) {
                while (cur.hasNext()) {
                    var doc = cur.next();

                    String date = (String) doc.get("date");
                    Date regDate = new SimpleDateFormat("yyyy.MM.dd").parse(date);

                    if (today.compareTo(regDate) <= 0) {
                        var temp = regCalendarMap.get((String) doc.get("date"));

                        if (temp == null) {
                            temp = new LinkedHashMap<String, String>();
                            temp.put(String.valueOf(doc.get("time")), String.valueOf(doc.get("active")));
                        }
                        if (regCalendarMap.get(date) != null) {
                            temp.put(String.valueOf(doc.get("time")), String.valueOf(doc.get("active")));
                        }

                        regCalendarMap.put(date, temp);
                    }
                }
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            assert mongoClient != null;
            mongoClient.close();
        }

        if (regCalendarMap.size() < 36) {
            serviceCalendarExtension(regCalendarMap, 36 - regCalendarMap.size());
        }

        return regCalendarMap;
    }

    @Override
    public void serviceCalendarExtension(Map<String, LinkedHashMap<String, String>> regCalendarMap, int count) {
        Calendar calendar = Calendar.getInstance();

        if (regCalendarMap.size() > 0) {
            Date lastDate = null;

           for (var temp : regCalendarMap.keySet()) {
                try {
                    lastDate = new SimpleDateFormat("yyyy.MM.dd").parse(temp);
                    calendar.setTime(lastDate);
                } catch (ParseException ignored) {
                }
            }
        }

        MongoClient mongoClient = null;

        try {
            mongoClient = new MongoClient(connectionString);
            MongoDatabase database = mongoClient.getDatabase("BeautyService");
            MongoCollection<Document> collection = database.getCollection("regCalendar");
            ArrayList<Document> regDateDocuments = new ArrayList<>(30);
            String[] regTime = new String[]{"9:00", "12:00", "15:00", "18:00"};

            for (int i = 0; i < count; i++) {
                calendar.add(calendar.DATE, 1);

                Date d = calendar.getTime();
                var tempMap = new LinkedHashMap<String, String>();
                for (String s : regTime) {
                    Document doc = new Document("date", new SimpleDateFormat("yyyy.MM.dd").format(d))
                            .append("time", s)
                            .append("active", "+");
                    regDateDocuments.add(doc);
                    tempMap.put(s, "+");
                }

                String outputDate = new SimpleDateFormat("yyyy.MM.dd").format(d);
                regCalendarMap.put(outputDate, tempMap);
            }
            collection.insertMany(regDateDocuments);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        } finally {
            assert mongoClient != null;
            mongoClient.close();
        }
    }

    @Override
    public void updateServiceCalendar(ManicureRegObject manicureRegObject) {
        MongoClient mongoClient = null;
        try {
            mongoClient = new MongoClient(connectionString);
            MongoDatabase database = mongoClient.getDatabase("BeautyService");
            MongoCollection<Document> collection = database.getCollection("regCalendar");

            Document newDoc = new Document("date", manicureRegObject.getDate())
                    .append("time", manicureRegObject.getTime());

            try (MongoCursor<Document> cur = collection.find().iterator()) {
                while (cur.hasNext()) {
                    var doc = cur.next();
                    if ( doc.get("date").equals(manicureRegObject.getDate())  &&  doc.get("time").equals(manicureRegObject.getTime())  ){
                        if (doc.get("active").equals("+")){
                            newDoc.append("active", "-");
                            collection.replaceOne(doc, newDoc);
                            break;
                        } else {
                            newDoc.append("active", "+");
                            collection.replaceOne(doc, newDoc);
                            break;
                        }
                    }
                }
            }

        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }finally {
            assert mongoClient != null;
            mongoClient.close();
        }
    }











    public void setBotOptions(boolean regNotify, boolean regOnService, String closeDate, String openDate, String serviceMessage) {
        MongoClient mongoClient = null;
        try {
            mongoClient = new MongoClient(connectionString);
            MongoDatabase database = mongoClient.getDatabase("BeautyService");
            MongoCollection<Document> collection = database.getCollection("botOptions");

            Document regNotifyBlank = new Document("regNotify", "regNotify");
            Document regNotifyNew = new Document("regNotify", "regNotify").append("notify", regNotify);

            Document regOnServiceBlank = new Document("regOnService", "regOnService");
            Document regOnServiceNew = new Document("regOnService", "regOnService").append("regService", regOnService);

            Document openDocBlank = new Document("openDate", "openDate");
            Document openDocNew = new Document("openDate", "openDate").append("date", openDate);

            Document closeDocBlank = new Document("closeDate", "closeDate");
            Document closeDocNew = new Document("closeDate", "closeDate").append("date", closeDate);

            Document serviceMessageBlank = new Document("serviceMessage", "serviceMessage");
            Document serviceMessageNew = new Document("serviceMessage", "serviceMessage").append("message", serviceMessage);

            if (collection.findOneAndReplace(regNotifyBlank, regNotifyNew) == null) {
                collection.insertOne(regNotifyNew);
            }

            if (collection.findOneAndReplace(regOnServiceBlank, regOnServiceNew) == null) {
                collection.insertOne(regOnServiceNew);
            }

            if (collection.findOneAndReplace(openDocBlank, openDocNew) == null) {
                collection.insertOne(openDocNew);
            }

            if (collection.findOneAndReplace(closeDocBlank, closeDocNew) == null) {
                collection.insertOne(closeDocNew);
            }
            if (collection.findOneAndReplace(serviceMessageBlank, serviceMessageNew) == null) {
                collection.insertOne(serviceMessageNew);
            }

        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }finally {
            assert mongoClient != null;
            mongoClient.close();
        }
    }

    public Map<String, String> getBotOptions() {
        var dates = new HashMap<String, String>();

        MongoClient mongoClient = null;
        try {
            mongoClient = new MongoClient(connectionString);
            MongoDatabase database = mongoClient.getDatabase("BeautyService");
            MongoCollection<Document> collection = database.getCollection("botOptions");

            String open = null, close = null;
            try (MongoCursor<Document> cur = collection.find().iterator()) {
                while (cur.hasNext()) {
                    var doc = cur.next();
                    if (doc.containsKey("openDate")) {
                        dates.put((String) doc.get("openDate"), (String) doc.get("date"));
                    }

                    if (doc.containsKey("closeDate")) {
                        dates.put((String) doc.get("closeDate"), (String) doc.get("date"));
                    }

                    if (doc.containsKey("regNotify")) {
                        dates.put((String) doc.get("regNotify"), String.valueOf( doc.get("notify")));
                    }

                    if (doc.containsKey("regOnService")) {
                        dates.put((String) doc.get("regOnService"), String.valueOf(doc.get("regService")));
                    }

                    if (doc.containsKey("serviceMessage")) {
                        dates.put((String) doc.get("serviceMessage"), (String) doc.get("message"));
                    }
                }
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            assert mongoClient != null;
            mongoClient.close();
        }

        return dates;
    }










    @Override
    public Map<Long, ManicureRegObject> getCustomersManicureRegistration(){
        var regObjectHashMap = new ConcurrentHashMap<Long, ManicureRegObject>();
        Date today = new Date();
        MongoClient mongoClient = null;
        try {
            mongoClient = new MongoClient(connectionString);
            MongoDatabase database = mongoClient.getDatabase("BeautyService");
            MongoCollection<Document> collection = database.getCollection("customersManicureRegistration");
            try (MongoCursor<Document> cur = collection.find().iterator()) {
                while (cur.hasNext()) {
                    var doc = cur.next();
                    String dateStr = (String) doc.get("date");
                    Date regDate = new SimpleDateFormat("yyyy.MM.dd").parse(dateStr);

                    if (regDate.compareTo(today) >= 0) {
                        var manicureReg = new ManicureRegObject();
                        manicureReg.setManicureType(String.valueOf(doc.get("type")));
                        manicureReg.setCost((int) doc.get("cost"));
                        manicureReg.setDate(String.valueOf(doc.get("date")));
                        manicureReg.setTime(String.valueOf(doc.get("time")));
                        manicureReg.setTelegramId((long) doc.get("telegramId"));
                        regObjectHashMap.put((long) doc.get("telegramId"), manicureReg);
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            assert mongoClient != null;
            mongoClient.close();
        }
        return regObjectHashMap;
    }


    @Override
    public void regCustomerForManicure(ManicureRegObject manicureRegObject) {
        MongoClient mongoClient = null;
        try {
            mongoClient = new MongoClient(connectionString);
            MongoDatabase database = mongoClient.getDatabase("BeautyService");
            MongoCollection<Document> collection = database.getCollection("customersManicureRegistration");

            Document doc = new Document("telegramId", manicureRegObject.getTelegramId())
                    .append("type", manicureRegObject.getManicureType())
                    .append("cost", manicureRegObject.getCost())
                    .append("date", manicureRegObject.getDate())
                    .append("time", manicureRegObject.getTime());
            collection.insertOne(doc);


            collection = database.getCollection("regCalendar");
            Document doc1 = new Document("date", manicureRegObject.getDate())
                    .append("time", manicureRegObject.getTime())
                    .append("active", "+");

            Document doc2 = new Document("date", manicureRegObject.getDate())
                    .append("time", manicureRegObject.getTime())
                    .append("active", "-");

           collection.findOneAndReplace(doc1, doc2);

         } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }finally {
            assert mongoClient != null;
            mongoClient.close();
        }
    }


    @Override
    public void removeCustomerManicureRegistration(ManicureRegObject manicureRegObject) {
        MongoClient mongoClient = null;
        try {
            mongoClient = new MongoClient(connectionString);
            MongoDatabase database = mongoClient.getDatabase("BeautyService");
            MongoCollection<Document> collection = database.getCollection("customersManicureRegistration");

            try (MongoCursor<Document> cur = collection.find().iterator()) {
                while (cur.hasNext()) {
                    var doc = cur.next();
                    if ( (long)doc.get("telegramId") == manicureRegObject.getTelegramId() ){
                        collection.deleteOne(doc);
                        break;
                    }
                }
            }


            collection = database.getCollection("regCalendar");
            Document doc1 = new Document("date", manicureRegObject.getDate())
                    .append("time", manicureRegObject.getTime())
                    .append("active", "-");

            Document doc2 = new Document("date", manicureRegObject.getDate())
                    .append("time", manicureRegObject.getTime())
                    .append("active", "+");
            collection.findOneAndReplace(doc1, doc2);


        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            assert mongoClient != null;
            mongoClient.close();
        }
    }



    @Override
    public Map<String, Integer> getServicesAndPrices() {
        var servAndPrice = new ConcurrentHashMap<String, Integer>();

        MongoClient mongoClient = null;
        try {
            mongoClient = new MongoClient(connectionString);
            MongoDatabase database = mongoClient.getDatabase("BeautyService");
            MongoCollection<Document> collection = database.getCollection("servicesAndPrices");

            try (MongoCursor<Document> cur = collection.find().iterator()) {
                while (cur.hasNext()) {
                    var doc = cur.next();
                    servAndPrice.put((String) doc.get("ServiceName"), (int) doc.get("price"));
                }
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            assert mongoClient != null;
            mongoClient.close();
        }

        return servAndPrice;
    }


    @Override
    public void adminRemoveService(String serviceName, int price) {
        MongoClient mongoClient = null;
        try {
            mongoClient = new MongoClient(connectionString);
            MongoDatabase database = mongoClient.getDatabase("BeautyService");
            MongoCollection<Document> collection = database.getCollection("servicesAndPrices");

            Document document = new Document("ServiceName", serviceName).append("price", price);
            collection.deleteOne(document);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            assert mongoClient != null;
            mongoClient.close();
        }
    }




    @Override
    public void addOrUpdateServiceAndPrice(String serviceName, int price) {
        MongoClient mongoClient = null;
        try {
            mongoClient = new MongoClient(connectionString);
            MongoDatabase database = mongoClient.getDatabase("BeautyService");
            MongoCollection<Document> collection = database.getCollection("servicesAndPrices");

            Document doc1 = new Document("ServiceName", serviceName);
            Document doc2 = new Document("ServiceName", serviceName).append("price", price);

            if (collection.findOneAndReplace(doc1, doc2) == null) {
                collection.insertOne(doc2);
            }

        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }finally {
            assert mongoClient != null;
            mongoClient.close();
        }
    }





    @Override
    public void dropDB() {
        MongoClient mongoClient = null;
        MongoCollection<Document> collection = null;
        try {
            mongoClient = new MongoClient(connectionString);
           MongoDatabase database = mongoClient.getDatabase("BeautyService");

            /*collection = database.getCollection("users");
            if (collection.countDocuments() != 0) {
                collection.drop();
            }
            collection = database.getCollection("customersManicureRegistration");
            if (collection.countDocuments() != 0) {
                collection.drop();
            }
            collection = database.getCollection("regCalendar");
            if (collection.countDocuments() != 0) {
                collection.drop();
            }
            collection = database.getCollection("servicesAndPrices");
            if (collection.countDocuments() != 0) {
                collection.drop();
            }*/
            collection = database.getCollection("portfolio");
            if (collection.countDocuments() != 0) {
                collection.drop();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }finally {
            assert mongoClient != null;
            mongoClient.close();
        }
    }

    @Override
    public void createDB() {

    }

}



