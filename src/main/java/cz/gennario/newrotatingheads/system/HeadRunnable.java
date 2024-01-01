package cz.gennario.newrotatingheads.system;

import cz.gennario.newrotatingheads.Main;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class HeadRunnable extends BukkitRunnable {
    @Override
    public void run() {
        for (RotatingHead value : new ArrayList<>(Main.getInstance().getHeads().values())) {
            if(value.getHeadStatus().equals(RotatingHead.HeadStatus.ENABLED)) {
                Location lastlocation = value.getLocation();
                if(lastlocation != null) {
                    lastlocation = lastlocation.clone();
                    for (Player player : lastlocation.getWorld().getPlayers()) {
                        if (!value.getPlayers().contains(player)) {
                            boolean canSpawn = true;
                            if (value.getViewPermission() != null && !value.getViewPermission().equals("none")) {
                                canSpawn = player.hasPermission(value.getViewPermission());
                            }
                            if (canSpawn) {
                                if (player.getLocation().distance(value.getLastlocation()) <= value.getViewDistance()) {
                                    value.spawn(player);
                                }
                            }
                        } else {
                            boolean canSpawn = true;
                            if (value.getViewPermission() != null && !value.getViewPermission().equals("none")) {
                                canSpawn = player.hasPermission(value.getViewPermission());
                            }
                            if (player.getLocation().distance(value.getLastlocation()) > value.getViewDistance() || !canSpawn) {
                                value.despawn(player);
                            }
                        }
                    }
                }
                List<Player> players = new ArrayList<>(value.getPlayers());
                if(players != null && !players.isEmpty()) {
                    for (Player player : new ArrayList<>(players)) {
                        if(player == null) continue;
                        if (!player.isOnline()) value.despawn(player);
                        if (!Objects.requireNonNull(value.getLastlocation().getWorld()).getPlayers().contains(player)) {
                            value.despawn(player);
                        }
                    }
                }
                value.pingAnimations();
                value.pingConditions();
            }
        }
    }
}
