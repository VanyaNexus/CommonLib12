/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  com.google.common.reflect.TypeToken
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  org.bukkit.ChatColor
 *  org.bukkit.plugin.Plugin
 */
package ru.den_abr.commonlib.messages;

import com.google.common.base.Preconditions;
import com.google.common.reflect.TypeToken;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.bukkit.ChatColor;
import org.bukkit.plugin.Plugin;
import ru.den_abr.commonlib.utility.GsonUtil;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.regex.Pattern;

public class Messages {
    public static final Pattern keyPattern = Pattern.compile("\\$(\\([а-яА-Яa-zA-Z .-_0-9]*\\))");
    public static final Messages EMPTYMAP = new Messages(new JsonObject());
    private final JsonObject map;

    public Messages(JsonObject map) {
        this.map = (JsonObject)Preconditions.checkNotNull((Object)map);
    }

    public Message get(String key) {
        if (this.has(key)) {
            return new Message(key, this.map.get(key), this);
        }
        return null;
    }

    public boolean has(String key) {
        return this.map.has(key);
    }

    public static String color(String mes) {
        return ChatColor.translateAlternateColorCodes('&', String.valueOf(mes));
    }

    public JsonObject getMessagesObject() {
        return this.map;
    }

    @Deprecated
    public Map<String, ?> getMessages() {
        return GsonUtil.GSON.fromJson(this.map, new TypeToken<Map<String, ?>>(){}.getType());
    }

    @Deprecated
    public static Messages fromJson(File json) {
        return MessageRegistry.load(json);
    }

    @Deprecated
    public static Messages fromPlugin(Plugin plugin, String fileName) {
        return MessageRegistry.load(plugin, fileName);
    }

    @Deprecated
    public static Messages loadByKeys(File file, MessageKey[] keys) {
        return Messages.loadByKeys(file, Arrays.asList(keys));
    }

    @Deprecated
    public static Messages loadByKeys(File file, Collection<MessageKey> keys) {
        return MessageRegistry.load(file, keys);
    }
}

