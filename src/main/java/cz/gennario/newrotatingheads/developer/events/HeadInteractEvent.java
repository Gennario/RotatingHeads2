package cz.gennario.newrotatingheads.developer.events;

import com.comphenix.protocol.PacketType;
import cz.gennario.newrotatingheads.system.HeadInteraction;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

@Getter
@Setter
public class HeadInteractEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private final int entityId;
    private final Player player;
    private final HeadInteraction.HeadClickType clickType;
    private boolean isCancelled, shift;

    public HeadInteractEvent(int entityId, Player player, HeadInteraction.HeadClickType clickType, boolean shift) {
        this.entityId = entityId;
        this.player = player;
        this.clickType = clickType;
        this.shift = shift;
        this.isCancelled = false;
    }

    @Override
    public boolean isCancelled() {
        return isCancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.isCancelled = cancel;
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
