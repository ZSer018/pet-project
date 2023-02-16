package questbot.newbot.data.qustParser;


import questbot.newbot.quest.Quest;
import questbot.newbot.quest.QuestStage;

import java.util.*;

public class QuestParser {

    private final DataLoader dataLoader;
    private final Queue<String> questMainMessageQueue;


    public QuestParser() {
        dataLoader = new DataLoader();
        questMainMessageQueue = dataLoader.getQuestSteps("quest_step_list");
    }


    public Quest loadAndParseQuest() {
        Quest quest = new Quest();
        Queue<QuestStage> questStagesQueue = new LinkedList<>();

        while (!questMainMessageQueue.isEmpty()) {
            String currentQuestPart = questMainMessageQueue.poll();

            System.out.println(currentQuestPart);

            var questPartMap = dataLoader.getQuestNextStep(currentQuestPart);
            var answers = questPartMap.get("answers");

            QuestStage questStage = new QuestStage();

            questStage.setStageName(currentQuestPart);

            questStage.setBotMessages(questPartMap.get("messages"));
            questStage.setShowIncorrectAnswerMessages(questPartMap.get("answers").get("showReplyMessages") == 1);
            questStage.setRequiredNumberOfAnswers(questPartMap.get("answers").get("answersRequiredNumber"));

            answers.entrySet().removeIf(stringIntegerEntry -> stringIntegerEntry.getKey().equals("showReplyMessages"));
            answers.entrySet().removeIf(stringIntegerEntry -> stringIntegerEntry.getKey().equals("answersRequiredNumber"));


            Iterator<Map.Entry<String, Integer>> iterator = answers.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, Integer> entry = iterator.next();
                //Если наден тег геолокации
                if (entry.getKey().contains("geoloc")) {

                    String[] temp = dataLoader.getGeoCoordinates(entry.getKey()).split("\\+");

                    var tempMap = new HashMap<Integer, String>();
                    for (int j = 0; j < temp.length; j++) {
                        tempMap.put(j, temp[j]);
                    }
                    questStage.setGeolocationPoints(tempMap);

                    var geolocMessages = new HashMap<String, LinkedHashMap<String, Integer>>();
                    geolocMessages.put("on_point", dataLoader.getOnPointMessages(entry.getKey() + "_on_point_text"));
                    geolocMessages.put("inner_radius", dataLoader.getOnPointMessages(entry.getKey() + "_inner_radius_text"));
                    geolocMessages.put("outer_radius", dataLoader.getOnPointMessages(entry.getKey() + "_outer_radius_text"));
                    geolocMessages.put("out_of_range", dataLoader.getOnPointMessages(entry.getKey() + "_out_of_range_text"));
                    geolocMessages.put("hasty_answer", dataLoader.getOnPointMessages(entry.getKey() + "_hasty_answer"));
                    questStage.setGeoLocationMessages(geolocMessages);

                    iterator.remove();

                    questStage.setOnReachedSwitchToNextStage(dataLoader.getBoolean(entry.getKey() +"_on_reached_switch_to_next_quest_stage"));
                    questStage.setOnReachedSwitchToNextStageTime(dataLoader.getInt(entry.getKey() +"_switching_time"));

                    questStage.setGeoOnPointRadius(
                            dataLoader.getInt(entry.getKey() + "_on_point_radius_range") != 1 ?
                                    dataLoader.getInt(entry.getKey() + "_on_point_radius_range") :
                                    20);
                    questStage.setGeoPointInnerRadius(
                            dataLoader.getInt(entry.getKey() + "_inner_radius_range") != 1 ?
                                    dataLoader.getInt(entry.getKey() + "_inner_radius_range") :
                                    80);
                    questStage.setGeoPointOuterRadius(
                            dataLoader.getInt(entry.getKey() + "_outer_radius_range") != 1 ?
                                    dataLoader.getInt(entry.getKey() + "_outer_radius_range") :
                                    200);
                }
            }


            questStage.setAnswers(new HashSet<>(answers.keySet()));
            questStagesQueue.add(questStage);
        }
        quest.setQuest(questStagesQueue);

        return quest;
    }






}
