package cz.gennario.newrotatingheads.developer.events;

import cz.gennario.newrotatingheads.system.RotatingHead;
import lombok.Getter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

@Getter
public class HeadReloadEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private final RotatingHead oldRotatingHead, newRotatingHead;

    public HeadReloadEvent(RotatingHead oldRotatingHead, RotatingHead newRotatingHead) {
        this.oldRotatingHead = oldRotatingHead;
        this.newRotatingHead = newRotatingHead;
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
