package cz.gennario.newrotatingheads.system;

import cz.gennario.newrotatingheads.Main;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;

public class HeadRunnable extends BukkitRunnable {
    @Override
    public void run() {
        for (RotatingHead value : Main.getInstance().getHeads().values()) {
            if(value.getHeadStatus().equals(RotatingHead.HeadStatus.ENABLED)) {
                for (Player player : value.getLastlocation().getWorld().getPlayers()) {
                    if (!value.getPlayers().contains(player)) {
                        boolean canSpawn = true;
                        if(value.getViewPermission() != null && !value.getViewPermission().equals("none")) {
                            canSpawn = player.hasPermission(value.getViewPermission());
                        }
                        if(canSpawn) {
                            if (player.getLocation().distance(value.getLastlocation()) <= value.getViewDistance()) {
                                value.spawn(player);
                            }
                        }
                    } else {
                        boolean canSpawn = true;
                        if(value.getViewPermission() != null && !value.getViewPermission().equals("none")) {
                            canSpawn = player.hasPermission(value.getViewPermission());
                        }
                        if (player.getLocation().distance(value.getLastlocation()) > value.getViewDistance() || !canSpawn) {
                            value.despawn(player);
                        }
                    }
                }
                if(value.getPlayers() != null && !value.getPlayers().isEmpty()) {
                    for (Player player : new ArrayList<>(value.getPlayers())) {
                        if (!player.isOnline()) value.despawn(player);
                        if (!value.getLastlocation().getWorld().getPlayers().contains(player)) {
                            value.despawn(player);
                        }
                    }
                }
                value.pingAnimations();
            }
        }
    }
}
