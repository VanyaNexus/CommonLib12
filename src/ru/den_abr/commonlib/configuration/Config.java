/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.comphenix.protocol.reflect.accessors.Accessors
 *  com.comphenix.protocol.reflect.accessors.FieldAccessor
 *  com.google.common.base.Preconditions
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  org.bukkit.plugin.Plugin
 *  org.hjson.JsonValue
 *  org.hjson.Stringify
 */
package ru.den_abr.commonlib.configuration;

import com.comphenix.protocol.reflect.accessors.Accessors;
import com.comphenix.protocol.reflect.accessors.FieldAccessor;
import com.google.common.base.Preconditions;
import com.google.gson.JsonObject;
import org.bukkit.plugin.Plugin;
import org.hjson.JsonValue;
import org.hjson.Stringify;
import ru.den_abr.commonlib.utility.GsonUtil;
import ru.den_abr.commonlib.utility.UtilityMethods;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.stream.Collectors;

public class Config {
    private int internalVersionDontTouch = -1;
    private transient File file;

    public final void setFile(File file) {
        this.file = file;
    }

    public final File getFile() {
        return this.file;
    }

    public int getVersion() {
        return this.internalVersionDontTouch;
    }

    public int setVersion(int version) {
        int prev = this.internalVersionDontTouch;
        this.internalVersionDontTouch = version;
        return prev;
    }

    public static <T, V> Map<T, V> fastMap(List<T> keys, List<V> values) {
        Preconditions.checkNotNull(keys);
        Preconditions.checkNotNull(values);
        Preconditions.checkArgument((keys.size() == values.size() ? 1 : 0) != 0, "Different keys and values sizes " + keys.size() + " != " + values.size());
        HashMap<T, V> map = new HashMap<T, V>();
        for (int i = 0; i < keys.size(); ++i) {
            map.put(keys.get(i), values.get(i));
        }
        return map;
    }

    public static <T, V> Map<T, V> fastSingleMap(T key, V value) {
        HashMap<T, V> map = new HashMap<T, V>();
        map.put(key, value);
        return map;
    }

    public static <T> List<T> fastList(T ... values) {
        return Arrays.stream(values).collect(Collectors.toList());
    }

    public static <T extends Config> T load(File file, Class<T> clazz) {
        return Config.load(file, clazz, -1);
    }

    public static <T extends Config> T load(File file, Class<T> clazz, int version) {
        return Config.load(file, clazz, version, true);
    }

    private static <T extends Config> T load(File file, Class<T> clazz, int version, boolean init) {
        if (!file.exists() || file.length() == 0L) {
            file.getParentFile().mkdirs();
            try {
                Config t = clazz.newInstance();
                t.setFile(file);
                t.save();
                if (init) {
                    t.init();
                }
                return (T)t;
            }
            catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
        String json = GsonUtil.readFile(file);
        json = JsonValue.readHjson(json).toString(Stringify.PLAIN);
        JsonObject jobj = GsonUtil.GSON.fromJson(json, JsonObject.class);
        Config t = GsonUtil.GSON.fromJson(jobj, clazz);
        t.setFile(file);
        if (init) {
            t.init();
        }
        int currentVersion = t.setVersion(version);
        try {
            if (version == -1) {
                JsonObject def = GsonUtil.GSON.toJsonTree(clazz.newInstance()).getAsJsonObject();
                if (!def.entrySet().stream().map(Map.Entry::getKey).filter(k -> !k.equals("internal_version_dont_touch")).allMatch(jobj::has)) {
                    t.save();
                }
            } else if (currentVersion != version) {
                t.save();
            }
        }
        catch (IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
        }
        return (T)t;
    }

    public static <T extends Config> T load(Plugin p, String fileName, Class<T> clazz) {
        return Config.load(p, fileName, clazz, -1);
    }

    public static <T extends Config> T load(Plugin p, String fileName, Class<T> clazz, int version) {
        File file = new File(p.getDataFolder(), fileName);
        return Config.load(file, clazz, version);
    }

    public void init() {
    }

    public <T> void reload() {
        Object temp = Config.load(this.file, this.getClass(), -1, false);
        for (Field field : UtilityMethods.getAllFields(new ArrayList<>(), this.getClass())) {
            int mod = field.getModifiers();
            if (Modifier.isFinal(mod) || Modifier.isStatic(mod)) continue;
            FieldAccessor accessor = Accessors.getFieldAccessor(field, true);
            accessor.set(this, accessor.get(temp));
            if (field.getType().isPrimitive()) continue;
            accessor.set(temp, null);
        }
        this.init();
    }

    public void save() {
        Preconditions.checkNotNull((Object)this.file, "File is not set");
        JsonObject obj = GsonUtil.GSON.toJsonTree(this).getAsJsonObject();
        if (this.internalVersionDontTouch == -1) {
            obj.remove("internal_version_dont_touch");
        }
        GsonUtil.writeAsJsonToFile(this.file, obj, GsonUtil.Format.HJSON);
    }
}

