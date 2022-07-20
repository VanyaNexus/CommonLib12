//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package ru.den_abr.commonlib.utility;

import com.google.common.base.Preconditions;
import com.google.common.collect.BiMap;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import org.hjson.JsonValue;
import org.hjson.Stringify;
import ru.den_abr.commonlib.menu.*;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.StandardOpenOption;

public class GsonUtil {
    public static Gson GSON;
    private static RuntimeTypeAdapterFactory<Icon> iconAdapters;
    private static RuntimeTypeAdapterFactory<Menu> menuAdapters;
    public static final FilenameFilter jsonFilter;

    public GsonUtil() {
    }

    public static void registerCustomIconClass(Class<? extends Icon> clazz) {
        registerCustomIconClass(clazz, clazz.getSimpleName());
    }

    public static void registerCustomIconClass(Class<? extends Icon> clazz, String label) {
        iconAdapters.registerSubtype(clazz, label);
    }

    public static void registerCustomMenuClass(Class<? extends Menu> clazz) {
        registerCustomMenuClass(clazz, clazz.getSimpleName());
    }

    public static void registerCustomMenuClass(Class<? extends Menu> clazz, String label) {
        menuAdapters.registerSubtype(clazz, label);
    }

    public static BiMap<String, Class<?>> getRegisteredMenus() {
        return menuAdapters.getRegisteredTypes();
    }

    public static BiMap<String, Class<?>> getRegisteredIcons() {
        return iconAdapters.getRegisteredTypes();
    }

    public static void writeToFile(File file, String str) {
        try {
            if (!file.getAbsoluteFile().getParentFile().isDirectory()) {
                file.getAbsoluteFile().getParentFile().mkdirs();
            }

            Files.write(file.toPath(), str.getBytes(StandardCharsets.UTF_8), new OpenOption[]{StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING});
        } catch (IOException var3) {
            var3.printStackTrace();
        }

    }

    public static void writeAsJsonToFile(File file, Object obj) {
        writeAsJsonToFile(file, obj, GsonUtil.Format.JSON);
    }

    public static void writeAsJsonToFile(File file, Object obj, GsonUtil.Format format) {
        String json = GSON.toJson(obj);
        writeToFile(file, format == GsonUtil.Format.JSON ? json : JsonValue.readJSON(json).toString(Stringify.HJSON));
    }

    public static JsonType getJsonType(JsonElement element) {
        Preconditions.checkNotNull(element);
        if (element.isJsonNull()) {
            return JsonType.NULL;
        } else if (element.isJsonArray()) {
            return JsonType.ARRAY;
        } else if (element.isJsonObject()) {
            return JsonType.OBJECT;
        } else if (element.isJsonPrimitive()) {
            return JsonType.PRIMITIVE;
        } else {
            throw new IllegalStateException("Unknown type of " + element.getClass().getName() + element);
        }
    }

    public static String readFile(File file) {
        try {
            return new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8);
        } catch (IOException var2) {
            var2.printStackTrace();
            return null;
        }
    }

    static {
        GsonBuilder builder = new GsonBuilder();
        builder.setPrettyPrinting().disableHtmlEscaping();

        /*try {
            builder.setLenient();
        } catch (NoSuchMethodError var2) {
            CommonLib.INSTANCE.getLogger().info("GsonBuilder.setLenient does not exist");
        }*/

        builder.enableComplexMapKeySerialization().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES);
        iconAdapters = RuntimeTypeAdapterFactory.of(Icon.class, "internalName");
        iconAdapters.registerSubtype(DefaultIcon.class);
        menuAdapters = RuntimeTypeAdapterFactory.of(Menu.class, "internalName");
        menuAdapters.registerSubtype(AbstractMenu.class);
        menuAdapters.registerSubtype(DefaultMenu.class);
        builder.registerTypeAdapterFactory(menuAdapters);
        builder.registerTypeAdapterFactory(iconAdapters);
        GSON = builder.create();
        jsonFilter = (dir, name) -> {
            return name.endsWith(".json");
        };
    }

    public static enum Format {
        JSON,
        HJSON;

        private Format() {
        }
    }
}
