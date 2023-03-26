package cz.gennario.newrotatingheads.rotatingengine.conditions;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Condition {

    private String type;
    private ConditionResponse conditionResponse;

    public Condition(String type, ConditionResponse conditionResponse) {
        this.type = type;
        this.conditionResponse = conditionResponse;
    }

}
