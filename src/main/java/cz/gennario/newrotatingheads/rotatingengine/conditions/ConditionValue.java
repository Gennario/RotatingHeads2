package cz.gennario.newrotatingheads.rotatingengine.conditions;

import cz.gennario.newrotatingheads.Main;
import cz.gennario.newrotatingheads.utils.replacement.Replacement;
import dev.dejvokep.boostedyaml.block.implementation.Section;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ConditionValue {

    private String type, input, output;
    private ConditionsAPI conditionsAPI;
    private Condition condition;
    private Replacement replacement;
    private boolean negative;

    public ConditionValue(Section section) {
        conditionsAPI = Main.getInstance().getConditionsAPI();

        type = section.getString("type", "null").toLowerCase();;
        if(type.startsWith("!")) {
            type = type.replaceFirst("!", "");
            negative = true;
        }
        input = section.getString("input", "null");
        output = section.getString("output", "null");

        if(!conditionsAPI.getConditions().containsKey(type)) {
            System.out.println("Condition "+type+" doesn't exist!");
            return;
        }

        condition = conditionsAPI.getConditions().get(type);
    }

    public ConditionValue(String type, String input, String output) {
        conditionsAPI = Main.getInstance().getConditionsAPI();

        this.type = type.toLowerCase();;
        if(type.startsWith("!")) {
            type = type.replaceFirst("!", "");
            negative = true;
        }
        this.input = input;
        this.output = output;

        if(!conditionsAPI.getConditions().containsKey(type)) {
            System.out.println("Condition "+type+" doesn't exist!");
            return;
        }

        condition = conditionsAPI.getConditions().get(type);
    }

    public ConditionValue setReplacement(Replacement replacement) {
        this.replacement = replacement;
        return this;
    }

    public String getInput() {
        if(replacement != null) {
            return replacement.replace(null, input);
        }
        return input;
    }
}
