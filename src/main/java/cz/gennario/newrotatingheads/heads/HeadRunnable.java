package cz.gennario.newrotatingheads.heads;

import cz.gennario.newrotatingheads.Main;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class HeadRunnable extends BukkitRunnable {
    @Override
    public void run() {
        for (RotatingHead value : Main.getInstance().getHeads().values()) {
            for (Player player : value.getLastlocation().getWorld().getPlayers()) {
                if(!value.getPlayers().contains(player)) {
                    if (player.getLocation().distance(value.getLastlocation()) <= value.getViewDistance()) {
                        value.spawn(player);
                    }
                }else {
                    if (player.getLocation().distance(value.getLastlocation()) > value.getViewDistance()) {
                        value.despawn(player);
                    }
                }
            }
            value.pingAnimations();
        }
    }
}
