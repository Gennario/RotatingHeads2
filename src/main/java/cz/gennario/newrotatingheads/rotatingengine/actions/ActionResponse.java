package cz.gennario.newrotatingheads.rotatingengine.actions;

import cz.gennario.newrotatingheads.utils.replacement.Replacement;
import org.bukkit.entity.Player;

public interface ActionResponse {

    void action(Player player, String identifier, ActionData data, Replacement replacement);

}
