package questbot.newbot.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import questbot.newbot.StartTextQuest;
import questbot.newbot.quest.Quest;

import java.io.*;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

public class QuestFilesManager {

    private final String path;

    public QuestFilesManager() {
        String parent = null;
        try {
            parent = String.valueOf(new File(new File(StartTextQuest.class
                    .getProtectionDomain()
                    .getCodeSource()
                    .getLocation()
                    .toURI()).getParent()));
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

        path = parent;
    }

    public Quest readQuestFile() {
        Quest quest;

        try (Reader reader = new InputStreamReader(new FileInputStream(path+"\\quest.quest"), StandardCharsets.UTF_8)) {
            Gson gson = new GsonBuilder().create();
            quest = gson.fromJson(reader, Quest.class);

            quest.getQuest().forEach(questStage -> {
                System.out.println(questStage.getStageName());
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return quest;
    }




    public void buildQuestFile(Quest quest){
        try (Writer writer = new OutputStreamWriter(
                new FileOutputStream(path+"\\quest.quest"), StandardCharsets.UTF_8)) {
            Gson gson = new GsonBuilder().create();
            gson.toJson(quest, writer);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public HashSet<Long> getIdSet(String idDescription){
        String filePath = path+"\\bot.properties";

        Properties props = new Properties();
        String answer = null;
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(filePath), StandardCharsets.UTF_8));
            props.load(reader);
            answer = props.getProperty(idDescription, null);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        HashSet<Long> tempId = new HashSet<>();
        if (answer != null && answer.length()>1){
           tempId = Arrays.stream(answer.split(",")).mapToLong(Long::parseLong).boxed().collect(Collectors.toCollection(HashSet::new));
        }

        return tempId;
    }



    public String getProperty(String file, String requestText){
        String filePath = path+"\\"+file;

        Properties props = new Properties();
        String answer = null;
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(filePath), StandardCharsets.UTF_8));
            props.load(reader);
            answer = props.getProperty(requestText.toLowerCase(), "");
        } catch (FileNotFoundException ignored) {
            System.out.println("no property file");
        } catch (IOException ignored) {
        }

        return answer;
    }

}
