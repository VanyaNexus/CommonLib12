package ru.den_abr.commonlib.utility;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableBiMap;
import com.google.gson.*;
import com.google.gson.internal.Streams;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import ru.den_abr.commonlib.CommonLib;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public final class RuntimeTypeAdapterFactory<T> implements TypeAdapterFactory
{
    private final Class<?> baseType;
    private final String typeFieldName;
    private final BiMap<String, Class<?>> labelToType;
    
    private RuntimeTypeAdapterFactory(final Class<?> baseType, final String typeFieldName) {
        this.labelToType = HashBiMap.create();
        if (typeFieldName == null || baseType == null) {
            throw new NullPointerException();
        }
        this.baseType = baseType;
        this.typeFieldName = typeFieldName;
    }
    
    public static <T> RuntimeTypeAdapterFactory<T> of(final Class<T> baseType, final String typeFieldName) {
        return new RuntimeTypeAdapterFactory<T>(baseType, typeFieldName);
    }
    
    public static <T> RuntimeTypeAdapterFactory<T> of(final Class<T> baseType) {
        return new RuntimeTypeAdapterFactory<T>(baseType, "type");
    }
    
    public RuntimeTypeAdapterFactory<T> registerSubtype(final Class<? extends T> type, final String label) {
        if (type == null || label == null) {
            throw new NullPointerException();
        }
        if (this.labelToType.remove(label) != null) {
            final Gson gson = GsonUtil.GSON;
            try {
                final Field calls = gson.getClass().getDeclaredField("calls");
                calls.setAccessible(true);
                final ThreadLocal<Map<TypeToken<?>, ?>> tl = (ThreadLocal<Map<TypeToken<?>, ?>>)calls.get(gson);
                final Map<TypeToken<?>, ?> threadCalls = tl.get();
                if (threadCalls != null) {
                    tl.set(null);
                }
                final Field typeTokenCache = gson.getClass().getDeclaredField("typeTokenCache");
                typeTokenCache.setAccessible(true);
                final Map<TypeToken<?>, TypeAdapter<?>> cache = (Map<TypeToken<?>, TypeAdapter<?>>)typeTokenCache.get(gson);
                cache.clear();
            }
            catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        this.labelToType.put(label, type);
        CommonLib.INSTANCE.getLogger().info("[" + this.baseType.getSimpleName() + "] Registered mapping " + label + " for class " + type.getName() + ". Now available: " + this.labelToType.keySet());
        return this;
    }
    
    public RuntimeTypeAdapterFactory<T> registerSubtype(final Class<? extends T> type) {
        return this.registerSubtype(type, type.getSimpleName());
    }
    
    public BiMap<String, Class<?>> getRegisteredTypes() {
        return ImmutableBiMap.copyOf((Map)this.labelToType);
    }
    
    public <R> TypeAdapter<R> create(final Gson gson, final TypeToken<R> type) {
        if (!this.baseType.isAssignableFrom(type.getRawType())) {
            return null;
        }
        return new TypeAdapter<R>() {
            private final Map<String, TypeAdapter<R>> labelToAdapter = new HashMap<String, TypeAdapter<R>>();
            private final Map<Class<?>, TypeAdapter<R>> typeToAdapter = new HashMap<Class<?>, TypeAdapter<R>>();
            
            public R read(final JsonReader in) throws IOException {
                final JsonElement jsonElement = Streams.parse(in);
                final JsonElement labelJsonElement = jsonElement.getAsJsonObject().remove(RuntimeTypeAdapterFactory.this.typeFieldName);
                if (labelJsonElement == null) {
                    throw new JsonParseException("[" + RuntimeTypeAdapterFactory.this.typeFieldName + "] cannot deserialize " + RuntimeTypeAdapterFactory.this.baseType + " because it does not define a field named " + RuntimeTypeAdapterFactory.this.typeFieldName);
                }
                final String label = labelJsonElement.getAsString();
                TypeAdapter<R> delegate = this.labelToAdapter.get(label);
                if (delegate == null && RuntimeTypeAdapterFactory.this.labelToType.containsKey(label)) {
                    delegate = gson.getDelegateAdapter(RuntimeTypeAdapterFactory.this, TypeToken.get((Class)RuntimeTypeAdapterFactory.this.labelToType.get(label)));
                    this.labelToAdapter.put(label, delegate);
                }
                if (delegate == null) {
                    throw new JsonParseException("[" + RuntimeTypeAdapterFactory.this.baseType.getSimpleName() + "] cannot deserialize " + RuntimeTypeAdapterFactory.this.baseType + " subtype named " + label + "; did you forget to register a subtype? Available: " + RuntimeTypeAdapterFactory.this.labelToType.keySet());
                }
                return delegate.fromJsonTree(jsonElement);
            }
            
            public void write(final JsonWriter out, final R value) throws IOException {
                final Class<?> srcType = value.getClass();
                final String label = RuntimeTypeAdapterFactory.this.labelToType.inverse().get(srcType);
                TypeAdapter<R> delegate = this.labelToAdapter.get(label);
                if (delegate == null && RuntimeTypeAdapterFactory.this.labelToType.containsKey(label)) {
                    delegate = (TypeAdapter<R>)gson.getDelegateAdapter(RuntimeTypeAdapterFactory.this, TypeToken.get((Class)RuntimeTypeAdapterFactory.this.labelToType.get(label)));
                    this.labelToAdapter.put(label, delegate);
                }
                if (delegate == null) {
                    throw new JsonParseException("cannot serialize " + srcType.getName() + "; did you forget to register a subtype?");
                }
                final JsonObject jsonObject = delegate.toJsonTree(value).getAsJsonObject();
                if (jsonObject.has(RuntimeTypeAdapterFactory.this.typeFieldName)) {
                    throw new JsonParseException("cannot serialize " + srcType.getName() + " because it already defines a field named " + RuntimeTypeAdapterFactory.this.typeFieldName);
                }
                final JsonObject clone = new JsonObject();
                clone.add(RuntimeTypeAdapterFactory.this.typeFieldName, new JsonPrimitive(label));
                for (final Map.Entry<String, JsonElement> e : jsonObject.entrySet()) {
                    clone.add(e.getKey(), e.getValue());
                }
                Streams.write(clone, out);
            }
        }.nullSafe();
    }
}
