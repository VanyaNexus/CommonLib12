/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonObject
 *  org.bukkit.entity.Player
 *  org.bukkit.event.HandlerList
 *  org.bukkit.event.player.AsyncPlayerChatEvent
 */
package ru.den_abr.commonlib.events;

import com.google.gson.JsonObject;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import ru.den_abr.commonlib.utility.ChatEventFixer;

public class AsyncPlayerJsonChatEvent
extends CancellableCEvent {
    private Player player;
    private AsyncPlayerChatEvent cause;
    private JsonObject chatComponent;

    public AsyncPlayerJsonChatEvent(Player player, AsyncPlayerChatEvent cause, JsonObject chatComponent) {
        super(true);
        this.player = player;
        this.cause = cause;
        this.chatComponent = chatComponent;
    }

    public Player getPlayer() {
        return this.player;
    }

    public AsyncPlayerChatEvent getChatEvent() {
        return this.cause;
    }

    public ChatEventFixer.CustomChatEvent getCustomEvent() {
        return (ChatEventFixer.CustomChatEvent)this.cause;
    }

    public void setChatComponent(JsonObject chatComponent) {
        this.chatComponent = chatComponent;
    }

    public JsonObject getChatComponent() {
        return this.chatComponent;
    }

    public static HandlerList getHandlerList() {
        return AsyncPlayerJsonChatEvent.getOrCreateHandlerList();
    }
}

