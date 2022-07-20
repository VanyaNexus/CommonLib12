/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.HashBasedTable
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Table
 *  com.google.gson.Gson
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParseException
 *  com.google.gson.stream.JsonReader
 *  org.bukkit.OfflinePlayer
 *  org.bukkit.entity.Player
 *  org.bukkit.plugin.Plugin
 */
package ru.den_abr.commonlib.messages;

import com.google.common.base.Preconditions;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Table;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.stream.JsonReader;
import de.janmm14.jsonmessagemaker.api.JsonMessageConverter;
import net.md_5.bungee.chat.ComponentSerializer;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.json.simple.parser.JSONParser;
import ru.den_abr.commonlib.CommonLib;
import ru.den_abr.commonlib.utility.GsonUtil;
import ru.den_abr.commonlib.utility.ItemLore;

import java.io.File;
import java.io.StringReader;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.logging.Level;

public class MessageRegistry {
    private static final Map<String, Messages> messages = Collections.synchronizedMap(new LinkedHashMap());
    private static final Table<Plugin, Predicate<Object>, TagProcessor<?>> customTags = HashBasedTable.create();
    private static final List<Plugin> usingNewMessagesSystem = new ArrayList<Plugin>();

    public static void registerDefaults() {
        MessageRegistry.unregisterAll();
        MessageRegistry.registerTagAdapter(CommonLib.INSTANCE, JsonElement.class, e -> e);
        MessageRegistry.registerTagAdapter(CommonLib.INSTANCE, Number.class, ((Gson)GsonUtil.GSON)::toJsonTree);
        MessageRegistry.registerTagAdapter(CommonLib.INSTANCE, String.class, ((Gson)GsonUtil.GSON)::toJsonTree);
        MessageRegistry.registerTagAdapter(CommonLib.INSTANCE, Message.class, Message::getCompleteValue);
        MessageRegistry.registerTagAdapter(CommonLib.INSTANCE, MessageKey.class, e -> e.message().getCompleteValue());
        MessageRegistry.registerPrimitiveTagAdapter(CommonLib.INSTANCE, ItemLore.class, ItemLore::getDisplayName);
        MessageRegistry.registerPrimitiveTagAdapter(CommonLib.INSTANCE, Player.class, OfflinePlayer::getName);
    }

    public static void addMessages(String name, Messages map) {
        Preconditions.checkNotNull((Object)name, "Name cannot be null");
        Preconditions.checkNotNull((Object)map, "Messages cannot be null");
        messages.put(name, map);
    }

    public static Messages removeMessages(String name) {
        return messages.remove(name);
    }

    public static Message message(MessageKey key) {
        Preconditions.checkNotNull((Object)key, "Key cannot be null");
        Message entry = MessageRegistry.message(key.getKey());
        if (!entry.exists()) {
            entry = new Message(key.getKey(), key.getDefault() instanceof JsonElement ? (JsonElement)key.getDefault() : GsonUtil.GSON.toJsonTree(key.getDefault()));
        }
        return entry;
    }

    public static Message message(String key) {
        Preconditions.checkNotNull((Object)key, "Key cannot be null");
        Messages map = null;
        for (Map.Entry<String, Messages> entry : messages.entrySet()) {
            if (!entry.getValue().has(key)) continue;
            map = entry.getValue();
            break;
        }
        if (map == null) {
            if (CommonLib.CONF.isPrintUnknownMessages()) {
                new MessageNotFoundException("[NOTERROR] Can't find message key " + key).printStackTrace();
            }
            return new Message(key, GsonUtil.GSON.toJsonTree("UNKNOWN KEY " + key), Messages.EMPTYMAP);
        }
        return map.get(key);
    }

    public static <T> void registerPrimitiveTagAdapter(Plugin plugin, Class<T> type, Function<T, Object> adapter) {
        MessageRegistry.registerTagProcessor(plugin, type::isInstance, e -> GsonUtil.GSON.toJsonTree(adapter.apply((T) e)));
    }

    public static <T> void registerTagAdapter(Plugin plugin, Class<T> type, TagProcessor<T> adapter) {
        MessageRegistry.registerTagProcessor(plugin, type::isInstance, adapter);
    }

    public static void registerTagProcessor(Plugin plugin, Predicate<Object> condition, TagProcessor processor) {
        Preconditions.checkNotNull(plugin, "Plugin cannot be null");
        Preconditions.checkNotNull(condition, "Condition cannot be null");
        Preconditions.checkNotNull(processor, "TagProcessor cannot be null");
        customTags.put(plugin, condition, processor);
    }

    public static void unregisterTagProcessors(Plugin plugin) {
        ImmutableList.copyOf(customTags.row(plugin).keySet()).forEach(cond -> {
            TagProcessor cfr_ignored_0 = customTags.remove(plugin, cond);
        });
    }

    public static TagProcessor findTagProcessor(Object object) {
        return customTags.columnKeySet().stream().filter(cond -> cond.test(object)).map(customTags::column).flatMap(map -> map.values().stream()).findFirst().orElse(((Gson)GsonUtil.GSON)::toJsonTree);
    }

    public static void unregisterAll() {
        customTags.clear();
    }

    public static Messages load(Plugin plugin, String jsonFileName) {
        return MessageRegistry.load(new File(plugin.getDataFolder(), jsonFileName));
    }

    public static Messages load(Plugin plugin, String jsonFileName, MessageKey[] keys) {
        return MessageRegistry.load(plugin, jsonFileName, Arrays.asList(keys));
    }

    public static Messages load(Plugin plugin, String jsonFileName, Collection<MessageKey> keys) {
        return MessageRegistry.load(new File(plugin.getDataFolder(), jsonFileName), keys);
    }

    public static Messages load(File file, Collection<MessageKey> keys) {
        JsonObject jobject;
        boolean exists = file.exists();
        if (!exists) {
            file.getParentFile().mkdirs();
            GsonUtil.writeToFile(file, "{}");
        }
        try {
            jobject = MessageRegistry.loadFromFile(file);
        }
        catch (JsonParseException e) {
            CommonLib.INSTANCE.getLogger().log(Level.SEVERE, "Cannot parse messages file " + file.getAbsolutePath(), e);
            return new Messages(new JsonObject());
        }
        boolean hasMissedKeys = keys.stream().filter(Objects::nonNull).anyMatch(k -> !jobject.has(k.getKey()));
        keys.stream().filter(Objects::nonNull).filter(k -> !jobject.has(k.getKey())).forEach(k -> jobject.add(k.getKey(), GsonUtil.GSON.toJsonTree(k.getDefault())));
        if (hasMissedKeys) {
            GsonUtil.writeAsJsonToFile(file, new TreeMap(GsonUtil.GSON.fromJson(jobject, Map.class)));
        }
     /*   jobject.entrySet().forEach(key -> {
            System.out.println("key: " + key.getKey() + " | value before: " + key.getValue());
            key.setValue(new Gson().toJsonTree(ComponentSerializer.toString(JsonMessageConverter.DEFAULT.convert(key.getValue().getAsString()))));
            System.out.println("value after: " + key.getValue());
            //key.setValue(new JSONParser().parse(ComponentSerializer.toString(JsonMessageConverter.DEFAULT.convert(key.getValue().getAsString()))));
        });*/
        return new Messages(jobject);
    }

    public static Messages load(File file) {
        try {
            return new Messages(MessageRegistry.loadFromFile(file));
        }
        catch (JsonParseException e) {
            CommonLib.INSTANCE.getLogger().log(Level.SEVERE, "Cannot parse messages file " + file.getAbsolutePath(), e);
            return new Messages(new JsonObject());
        }
    }

    public static void usingNewMessages(Plugin plugin) {
        if (!usingNewMessagesSystem.contains(plugin)) {
            usingNewMessagesSystem.add(plugin);
        }
    }

    public static boolean isUsingNewMessages(Plugin plugin) {
        return usingNewMessagesSystem.contains(plugin);
    }

    private static JsonObject loadFromFile(File json) {
        if (!json.exists()) {
            GsonUtil.writeToFile(json, "{}");
            return new JsonObject();
        }
        String content = GsonUtil.readFile(json);
        if (content.isEmpty()) {
            return new JsonObject();
        }
        JsonReader reader = new JsonReader(new StringReader(GsonUtil.readFile(json)));
        reader.setLenient(true);
        return GsonUtil.GSON.fromJson(reader, JsonObject.class);
    }

    public static Map<String, Messages> getMessagesMap() {
        return messages;
    }
}

