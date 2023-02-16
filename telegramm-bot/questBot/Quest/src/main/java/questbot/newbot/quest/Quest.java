package questbot.newbot.quest;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Queue;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Quest {
    @SerializedName("QuestStage")
    private Queue<QuestStage> quest;

    public QuestStage getNewPart(){
        return quest.poll();
    }
}
