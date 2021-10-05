package com.github.sirblobman.compressed.hearts.event;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

import com.github.sirblobman.api.utility.Validate;
import com.github.sirblobman.compressed.hearts.object.DisplayType;

public final class PlayerChangeHeartsDisplayTypeEvent extends PlayerEvent {
    private static final HandlerList HANDLER_LIST;
    
    static {
        HANDLER_LIST = new HandlerList();
    }
    
    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }
    
    private final DisplayType oldType;
    private final DisplayType newType;
    
    public PlayerChangeHeartsDisplayTypeEvent(Player player, DisplayType oldType, DisplayType newType) {
        super(player);
        this.oldType = Validate.notNull(oldType, "oldType must not be null!");
        this.newType = Validate.notNull(newType, "newType must not be null!");
    }
    
    @Override
    public HandlerList getHandlers() {
        return getHandlerList();
    }
    
    public DisplayType getOldType() {
        return this.oldType;
    }
    
    public DisplayType getNewType() {
        return this.newType;
    }
}
