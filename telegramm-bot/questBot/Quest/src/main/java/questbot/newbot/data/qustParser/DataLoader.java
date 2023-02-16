package questbot.newbot.data.qustParser;

import questbot.newbot.data.QuestFilesManager;

import java.util.*;
import java.util.stream.Collectors;

public class DataLoader {

    private String questFileName = "quest.txt";

    private final QuestFilesManager questFilesManager;

    public DataLoader() {
        this.questFilesManager = new QuestFilesManager();
    }

    public LinkedList<String> getQuestSteps(String command){
        return Arrays.stream(questFilesManager.getProperty(questFileName, command).split("\\+")).collect(Collectors.toCollection(LinkedList::new));
    }

    public HashMap<String, LinkedHashMap<String, Integer>> getQuestNextStep(String command) {
        var tempMap = new HashMap<String, LinkedHashMap<String, Integer>>();
        List<String> messageList;
        List<String> answerList;

        LinkedHashMap<String, Integer> messageAndDelayMap = new LinkedHashMap<>();
        LinkedHashMap<String, Integer> answersMap = null;

        String[] temp = questFilesManager.getProperty(questFileName, command).split("=");
        messageList = Arrays.stream(temp[0].split("\\+")).collect(Collectors.toList());

        for (int i = 0; i < messageList.size(); i += 2) {
            messageAndDelayMap.put(messageList.get(i),
                    i + 1 < messageList.size() ?
                            Integer.parseInt(messageList.get(i + 1)) :
                            0);
        }

        if (temp.length > 1) {
            answerList = Arrays.stream(temp[1].split("\\+")).collect(Collectors.toList());
            answersMap = new LinkedHashMap<>();

            for (String value : answerList) {
                answersMap.put(value, 0);
            }

            answersMap.put("answersRequiredNumber", 1);
            answersMap.put("showReplyMessages", 0);

            if (temp.length > 2) {
                String[] temp2 = temp[2].split("\\+");
                answersMap.put("answersRequiredNumber", Integer.parseInt(temp2[0]));

                if (temp2.length > 1) {
                    answersMap.put("showReplyMessages", 1);
                }
            }

        }

        tempMap.put("messages", messageAndDelayMap);
        tempMap.put("answers", answersMap);
        return tempMap;
    }



    public LinkedHashMap<String, Integer> getOnPointMessages(String command) {
        var messageAndDelayMap = new LinkedHashMap<String, Integer>();
        String temp = questFilesManager.getProperty(questFileName, command);

       if (temp == null || temp.length() == 0){
            return null;
        }

        List<String> messageList = Arrays.stream(temp.split("\\+")).collect(Collectors.toList());
        for (int i = 0; i < messageList.size(); i += 2) {
            messageAndDelayMap.put(messageList.get(i), i + 1 < messageList.size() ? Integer.parseInt(messageList.get(i + 1)) : 0);
        }

        return messageAndDelayMap;
    }



    public String getGeoCoordinates(String command){
        String temp = questFilesManager.getProperty(questFileName, command);

        if (temp == null || temp.length() == 0) {
            return null;
        }

        return questFilesManager.getProperty(questFileName, command);
    }

    public Long getId(String command){
        String temp = questFilesManager.getProperty(questFileName, command);

        if (temp == null || temp.length() == 0) {
            return 1L;
        }

        return Long.parseLong(questFilesManager.getProperty(questFileName, command));
    }

    public int getInt(String command){
        String temp = questFilesManager.getProperty(questFileName, command);

        if (temp == null || temp.length() == 0) {
            return 1;
        }

        return Integer.parseInt(temp);
    }


    public boolean getBoolean(String command){
        String temp = questFilesManager.getProperty(questFileName, command);
        if (temp == null || temp.equals("")){
            return false;
        }
        return Boolean.parseBoolean(questFilesManager.getProperty(questFileName, command));
    }


}
