package com.github.sirblobman.compressed.hearts.event;

import org.jetbrains.annotations.NotNull;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

import com.github.sirblobman.compressed.hearts.display.DisplayType;

public final class PlayerChangeDisplayTypeEvent extends PlayerEvent {
    private static final HandlerList HANDLER_LIST;

    static {
        HANDLER_LIST = new HandlerList();
    }

    private final DisplayType oldType;
    private final DisplayType newType;

    public PlayerChangeDisplayTypeEvent(@NotNull Player player, @NotNull DisplayType oldType,
                                        @NotNull DisplayType newType) {
        super(player);
        this.oldType = oldType;
        this.newType = newType;
    }

    public static @NotNull HandlerList getHandlerList() {
        return HANDLER_LIST;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return getHandlerList();
    }

    public @NotNull DisplayType getOldType() {
        return this.oldType;
    }

    public @NotNull DisplayType getNewType() {
        return this.newType;
    }
}
