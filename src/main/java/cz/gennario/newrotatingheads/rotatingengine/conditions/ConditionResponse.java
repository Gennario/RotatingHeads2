package cz.gennario.newrotatingheads.rotatingengine.conditions;

import cz.gennario.newrotatingheads.utils.replacement.Replacement;
import org.bukkit.entity.Player;

public interface ConditionResponse {

    boolean check(String input, String output, Player player, Replacement replacement);

}
