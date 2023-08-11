package io.github.sawors.simplesit;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

public class PlayerSitEvent extends PlayerEvent implements Cancellable {
    
    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled = false;
    private final Block seat;
    
    public PlayerSitEvent(@NotNull Player who, Block seat) {
        super(who);
        this.seat = seat;
    }
    
    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }
    
    @Override
    public boolean isCancelled() {
        return cancelled;
    }
    
    @Override
    public void setCancelled(boolean cancel) {
        cancelled = cancel;
    }
    
    public Block getSeat() {
        return seat;
    }
}
