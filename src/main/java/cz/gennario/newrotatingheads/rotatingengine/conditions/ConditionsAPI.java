package cz.gennario.newrotatingheads.rotatingengine.conditions;

import cz.gennario.newrotatingheads.utils.Utils;
import cz.gennario.newrotatingheads.utils.replacement.Replacement;
import dev.dejvokep.boostedyaml.block.implementation.Section;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class ConditionsAPI {

    private final Map<String, Condition> conditions;

    public ConditionsAPI() {
        this.conditions = new HashMap<>();

        loadDefaults();
    }

    public void loadDefaults() {
        addCondition("==", (input, output, player, replacement) -> replacement.replace(player, input).equals(output));
        addCondition("==Aa", (input, output, player, replacement) -> replacement.replace(player, input).equalsIgnoreCase(output));
        addCondition(">", (input, output, player, replacement) -> {
            double i = Double.parseDouble(replacement.replace(player, input));
            double o = Double.parseDouble(output);
            return i > o;
        });
        addCondition("<", (input, output, player, replacement) -> {
            double i = Double.parseDouble(replacement.replace(player, input));
            double o = Double.parseDouble(output);
            return i < o;
        });
        addCondition(">=", (input, output, player, replacement) -> {
            double i = Double.parseDouble(replacement.replace(player, input));
            double o = Double.parseDouble(output);
            return i >= o;
        });
        addCondition("<=", (input, output, player, replacement) -> {
            double i = Double.parseDouble(replacement.replace(player, input));
            double o = Double.parseDouble(output);
            return i <= o;
        });
        addCondition("has-permission", (input, output, player, replacement) -> {
            String i = replacement.replace(player, input);
            return player.hasPermission(i);
        });
        addCondition("is-sneaking", (input, output, player, replacement) -> player.isSneaking());
        addCondition("is-op", (input, output, player, replacement) -> player.isOp());
        addCondition("is-in-water", (input, output, player, replacement) -> player.isInWater());
        addCondition("is-flying", (input, output, player, replacement) -> player.isFlying());
        addCondition("is-gliding", (input, output, player, replacement) -> player.isGliding());
        addCondition("is-swimming", (input, output, player, replacement) -> player.isSwimming());
        addCondition("is-frozen", (input, output, player, replacement) -> player.isFrozen());
        addCondition("is-sleeping", (input, output, player, replacement) -> player.isSleeping());
        addCondition("has-fly-allowed", (input, output, player, replacement) -> player.getAllowFlight());
        addCondition("has-gamemode", (input, output, player, replacement) -> {
            String i = replacement.replace(player, input);
            return player.getGameMode().equals(GameMode.valueOf(i.toUpperCase()));
        });
    }

    public void addCondition(String type, ConditionResponse response) {
        conditions.put(type, new Condition(type, response));
    }

    public boolean check(Player player, Section section, @Nullable Replacement replacement) {
        if(replacement == null) replacement = new Replacement((player1, string) -> string);

        boolean negative = false;
        String type = section.getString("type").toLowerCase();
        if(type.startsWith("!")) {
            type = type.replaceFirst("!", "");
            negative = true;
        }
        String input = Utils.colorize(player, replacement.replace(player, section.getString("input", "null")));
        String output = section.getString("output", "null");

        if(!conditions.containsKey(type)) {
            System.out.println("Condition "+type+" doesn't exist!");
            return false;
        }

        Condition condition = conditions.get(type);

        if(negative) {
            return !condition.getConditionResponse().check(input, output, player, replacement);
        }

        return condition.getConditionResponse().check(input, output, player, replacement);
    }

    public boolean check(Player player, ConditionValue conditionValue, @Nullable Replacement replacement) {
        if(replacement == null) replacement = new Replacement((player1, string) -> string);

        String input = Utils.colorize(player, Utils.colorize(player, conditionValue.getInput()));
        String output = conditionValue.getOutput();

        Condition condition = conditionValue.getCondition();

        if(conditionValue.isNegative()) {
            return !condition.getConditionResponse().check(input, output, player, replacement);
        }

        return condition.getConditionResponse().check(input, output, player, replacement);
    }

}
