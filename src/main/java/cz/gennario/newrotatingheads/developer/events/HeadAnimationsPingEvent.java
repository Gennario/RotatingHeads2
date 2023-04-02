package cz.gennario.newrotatingheads.developer.events;

import cz.gennario.newrotatingheads.system.RotatingHead;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class HeadAnimationsPingEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private final RotatingHead rotatingHead;
    private boolean isCancelled;

    public HeadAnimationsPingEvent(RotatingHead rotatingHead, boolean isCancelled) {
        this.rotatingHead = rotatingHead;
        this.isCancelled = isCancelled;
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
}