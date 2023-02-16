package questbot.newbot.quest;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Objects;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class QuestStage {

    private String StageName;

    @SerializedName("BotMessages")
    private LinkedHashMap<String, Integer> botMessages;

    @SerializedName("RequiredAnswers")
    private HashSet<String> answers;
    private int requiredNumberOfAnswers;

    private HashMap<Integer, String> geolocationPoints;
    private HashMap<String, LinkedHashMap<String, Integer>> geoLocationMessages;

    private int geoOnPointRadius; //радиус в метрах, вхождение в который означает, что цель достигнута
    private boolean onReachedSwitchToNextStage; //переключаться на новый шаг при достижении цели
    private int onReachedSwitchToNextStageTime; //время, через которое произойдет переключение
    private boolean pointReached = false; //отметка, что точка была достигнута

    private int geoPointInnerRadius; //радиус в метрах, вхождение в который означает что цель очень близко
    private boolean innerRadiusReached = false;

    private int geoPointOuterRadius; //радиус в метрах, вхождение в который означает что цель приближается
    private boolean outerRadiusReached = false;

    private boolean outOfRangeReached = false;

    private boolean showIncorrectAnswerMessages;
    private boolean showCorrectAnswerMessages;


    public void reduceRequiredNumberOfAnswers(){
        requiredNumberOfAnswers--;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        QuestStage stage = (QuestStage) o;
        return requiredNumberOfAnswers == stage.requiredNumberOfAnswers &&
                geoOnPointRadius == stage.geoOnPointRadius &&
                onReachedSwitchToNextStage == stage.onReachedSwitchToNextStage &&
                onReachedSwitchToNextStageTime == stage.onReachedSwitchToNextStageTime &&
                pointReached == stage.pointReached &&
                geoPointInnerRadius == stage.geoPointInnerRadius &&
                innerRadiusReached == stage.innerRadiusReached &&
                geoPointOuterRadius == stage.geoPointOuterRadius &&
                outerRadiusReached == stage.outerRadiusReached &&
                showIncorrectAnswerMessages == stage.showIncorrectAnswerMessages &&
                showCorrectAnswerMessages == stage.showCorrectAnswerMessages &&
                Objects.equals(botMessages, stage.botMessages) && Objects.equals(answers, stage.answers) &&
                Objects.equals(geolocationPoints, stage.geolocationPoints) &&
                Objects.equals(geoLocationMessages, stage.geoLocationMessages);
    }

    @Override
    public int hashCode() {
        return Objects.hash(botMessages,
                answers,
                requiredNumberOfAnswers,
                geolocationPoints,
                geoLocationMessages,
                geoOnPointRadius,
                onReachedSwitchToNextStage,
                onReachedSwitchToNextStageTime,
                pointReached,
                geoPointInnerRadius,
                innerRadiusReached,
                geoPointOuterRadius,
                outerRadiusReached,
                showIncorrectAnswerMessages,
                showCorrectAnswerMessages);
    }

    @Override
    public String toString() {
        return "QuestStage{" +
                "botMessages=" + botMessages +
                ", answers=" + answers +
                ", requiredNumberOfAnswers=" + requiredNumberOfAnswers +
                ", geolocationPoints=" + geolocationPoints +
                ", geoLocationMessages=" + geoLocationMessages +
                ", geoOnPointRadius=" + geoOnPointRadius +
                ", onReachedSwitchToNextStage=" + onReachedSwitchToNextStage +
                ", onReachedSwitchToNextStageTime=" + onReachedSwitchToNextStageTime +
                ", pointReached=" + pointReached +
                ", geoPointInnerRadius=" + geoPointInnerRadius +
                ", innerRadiusReached=" + innerRadiusReached +
                ", geoPointOuterRadius=" + geoPointOuterRadius +
                ", outerRadiusReached=" + outerRadiusReached +
                ", showIncorrectAnswerMessages=" + showIncorrectAnswerMessages +
                ", showCorrectAnswerMessages=" + showCorrectAnswerMessages +
                '}';
    }
}
