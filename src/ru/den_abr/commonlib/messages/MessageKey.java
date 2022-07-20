/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonElement
 *  net.md_5.bungee.api.chat.BaseComponent
 *  net.md_5.bungee.chat.ComponentSerializer
 *  org.bukkit.command.CommandSender
 */
package ru.den_abr.commonlib.messages;

import com.google.gson.JsonElement;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.chat.ComponentSerializer;
import org.bukkit.command.CommandSender;
import ru.den_abr.commonlib.utility.GsonUtil;

import java.util.Arrays;
import java.util.List;

public interface MessageKey {
    public String getKey();

    public Object getDefault();

    default public Message message() {
        return MessageRegistry.message(this);
    }

    default public String get() {
        return this.message().getAsString();
    }

    default public List<String> getList() {
        return this.message().getAsStringList();
    }

    default public void send(CommandSender cs) {
        this.send(cs, MessagePosition.CHAT);
    }

    default public void send(CommandSender cs, MessagePosition pos) {
        this.message().send(cs, pos);
    }

    default public Message tag(MessageTagger tagger) {
        return this.message().tag(tagger);
    }

    default public Message tag(String key, Object value) {
        return this.message().tag(key, value);
    }

    default public Object convertArray(String ... string) {
        return MessageKey.convertDefault(string);
    }

    default public JsonElement toJson(BaseComponent ... components) {
        return (JsonElement)GsonUtil.GSON.fromJson(ComponentSerializer.toString((BaseComponent[])components), JsonElement.class);
    }

    default public void broadcast() {
        this.broadcast(MessagePosition.CHAT);
    }

    default public void broadcast(MessagePosition pos) {
        this.message().broadcast(pos);
    }

    public static Object convertDefault(String ... strings) {
        if (strings.length > 1) {
            return Arrays.asList(strings);
        }
        return strings.length == 1 ? strings[0] : null;
    }
}

