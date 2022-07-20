package ru.den_abr.commonlib.utility;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.gson.*;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.PropertyMap;
import com.mojang.authlib.yggdrasil.response.ProfileSearchResultsResponse;
import com.mojang.util.UUIDTypeAdapter;
import org.apache.http.HttpEntity;
import org.apache.http.client.entity.EntityBuilder;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import ru.den_abr.commonlib.CommonLib;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class MojangAPI
{
    public static Cache<String, GameProfile> GLOBAL_PROFILE_CACHE;
    private final CloseableHttpClient client;
    private final ExecutorService executor;
    private static final String API_URL = "https://api.mojang.com/";
    private static final String SESSION_URL = "https://sessionserver.mojang.com/session/minecraft/profile/";
    private final Gson gson;
    
    public MojangAPI(final int threads) {
        this.client = HttpClients.createMinimal();
        this.executor = Executors.newFixedThreadPool(threads);
        final GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(PropertyMap.class, new PropertyMap.Serializer());
        gsonBuilder.registerTypeAdapter(UUID.class, new UUIDTypeAdapter());
        gsonBuilder.registerTypeAdapter(ProfileSearchResultsResponse.class, new ProfileSearchResultsResponse.Serializer());
        gsonBuilder.registerTypeAdapter(GameProfile.class, new GameProfileSerializer());
        this.gson = gsonBuilder.create();
    }
    
    public void destroy() {
        this.executor.shutdownNow();
        try {
            this.client.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public Gson getGson() {
        return this.gson;
    }

    public Future<Map<String, String>> playersToUUIDs(List<String> names, Consumer<Map<String, String>> resultHandler) {
        Paginator<String> paged = new Paginator(names, CommonLib.CONF.getUuidLookupBatchSize());
        MojangAPI.RethrowSupplier<Map<String, String>> callable = () -> {
            Map<String, String> map = new LinkedHashMap();
            Iterator var3 = paged.getPagesList().iterator();

            while(var3.hasNext()) {
                List<String> part = (List)var3.next();
                HttpPost post = new HttpPost("https://api.mojang.com/profiles/minecraft");
                HttpEntity entity = EntityBuilder.create().setText(this.gson.toJson(part)).setContentType(ContentType.APPLICATION_JSON).build();
                post.setEntity(entity);
                CloseableHttpResponse resp = this.client.execute(post);
                Throwable var8 = null;

                try {
                    String text = EntityUtils.toString(resp.getEntity());
                    JsonElement element = this.gson.fromJson(text, JsonElement.class);
                    JsonArray list;
                    if (!element.isJsonArray()) {
                        CommonLib.INSTANCE.getLogger().severe("Wrong response from Mojang API: " + element);
                        this.adjustMaxProfileSize(element);
                        list = new JsonArray();
                    } else {
                        list = element.getAsJsonArray();
                    }

                    Map<String, String> lowerMap = new HashMap();
                    part.forEach((namex) -> {
                        String var10000 = lowerMap.put(namex.toLowerCase(), namex);
                    });
                    Iterator var13 = list.iterator();

                    while(var13.hasNext()) {
                        JsonElement obj = (JsonElement)var13.next();
                        String uuid = obj.getAsJsonObject().get("id").getAsString();
                        String name = obj.getAsJsonObject().get("name").getAsString();
                        map.put(lowerMap.getOrDefault(name.toLowerCase(), name), uuid);
                        lowerMap.remove(name.toLowerCase());
                    }

                    lowerMap.forEach((ln, namex) -> {
                        String var10000 = map.put(namex, null);
                    });
                    lowerMap.clear();
                } catch (Throwable var24) {
                    var8 = var24;
                    throw var24;
                } finally {
                    if (resp != null) {
                        if (var8 != null) {
                            try {
                                resp.close();
                            } catch (Throwable var23) {
                                var8.addSuppressed(var23);
                            }
                        } else {
                            resp.close();
                        }
                    }

                }
            }

            return map;
        };
        return CompletableFuture.supplyAsync(callable, this.executor).whenComplete((map, throwable) -> {
            resultHandler.accept(map);
        });
    }
    
    private void adjustMaxProfileSize(final JsonElement element) {
        if (element.isJsonObject()) {
            final JsonElement error = element.getAsJsonObject().get("errorMessage");
            if (error.isJsonPrimitive()) {
                final String message = error.getAsString();
                final String pattern = "Not more that ([0-9]+) profile name per call is allowed.";
                if (message.matches(pattern)) {
                    final int max = Integer.parseInt(message.replaceAll(pattern, "$1"));
                    if (CommonLib.CONF.getUuidLookupBatchSize() != max) {
                        CommonLib.CONF.setUuidLookupBatchSize(max);
                        CommonLib.INSTANCE.getLogger().warning("Set lookup max size to " + max + " due to Mojang complaints");
                    }
                }
            }
        }
    }
    
    public Future<GameProfile> getProfile(final String uuid, final Consumer<GameProfile> resultHandler) {
        return this.executor.submit(() -> {
            GameProfile cached = MojangAPI.GLOBAL_PROFILE_CACHE.get(uuid.toLowerCase(), () -> this.fetchProfile(uuid));
            resultHandler.accept(cached);
            return cached;
        });
    }
    
    public ExecutorService getExecutor() {
        return this.executor;
    }


    private GameProfile fetchProfile(String uuid) {
        HttpGet get = new HttpGet("https://sessionserver.mojang.com/session/minecraft/profile/" + uuid.replace("-", "") + "?unsigned=false");
        try (CloseableHttpResponse resp = this.client.execute(get)) {
            if (resp.getStatusLine().getStatusCode() != 200) {
                CommonLib.INSTANCE.getLogger()
                        .warning("Can't load " + uuid + " profile. Got " + resp.getStatusLine().getStatusCode() + " response code");
                return null;
            }
            JsonObject obj = this.gson.fromJson(EntityUtils.toString(resp.getEntity()), JsonObject.class);
            if (obj.has("error")) {
                CommonLib.INSTANCE.getLogger()
                        .warning("Can't load " + uuid + " profile. Got error - " + buildError(obj));
                return null;
            }
            return parseGameProfile(obj);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public GameProfile parseGameProfile(final JsonObject obj) {
        return (GameProfile)this.gson.fromJson(obj, (Class)GameProfile.class);
    }
    
    public JsonObject toJson(final GameProfile profile) {
        return this.gson.toJsonTree(profile).getAsJsonObject();
    }
    
    private String buildError(final JsonObject object) {
        final StringBuilder builder = new StringBuilder();
        if (object.has("error")) {
            builder.append(object.getAsJsonPrimitive("error").getAsString());
        }
        if (object.has("errorMessage")) {
            builder.append(':').append(object.getAsJsonPrimitive("errorMessage").getAsString());
        }
        if (object.has("cause")) {
            builder.append('(').append(object.getAsJsonPrimitive("cause").getAsString()).append(')');
        }
        return builder.toString();
    }
    
    static {
        MojangAPI.GLOBAL_PROFILE_CACHE = CacheBuilder.newBuilder().expireAfterWrite(1L, TimeUnit.DAYS).build();
    }
    
    private static class GameProfileSerializer implements JsonSerializer<GameProfile>, JsonDeserializer<GameProfile>
    {
        public GameProfile deserialize(final JsonElement jsonElement, final Type type, final JsonDeserializationContext jsonDeserializationContext) {
            final JsonObject jsonObject = (JsonObject)jsonElement;
            final UUID uuid = jsonObject.has("id") ? ((UUID)jsonDeserializationContext.deserialize(jsonObject.get("id"), UUID.class)) : null;
            final String name = jsonObject.has("name") ? jsonObject.getAsJsonPrimitive("name").getAsString() : null;
            final GameProfile profile = new GameProfile(uuid, name);
            if (jsonObject.has("properties")) {
                final JsonArray props = jsonObject.get("properties").getAsJsonArray();
                profile.getProperties().putAll(jsonDeserializationContext.deserialize(props, PropertyMap.class));
            }
            return profile;
        }
        
        public JsonElement serialize(final GameProfile gameProfile, final Type type, final JsonSerializationContext jsonSerializationContext) {
            final JsonObject jsonObject = new JsonObject();
            if (gameProfile.getId() != null) {
                jsonObject.add("id", jsonSerializationContext.serialize(gameProfile.getId()));
            }
            if (gameProfile.getName() != null) {
                jsonObject.addProperty("name", gameProfile.getName());
            }
            if (!gameProfile.getProperties().isEmpty()) {
                final JsonElement props = jsonSerializationContext.serialize(gameProfile.getProperties());
                jsonObject.add("properties", props);
            }
            return jsonObject;
        }
    }
    
    private interface RethrowSupplier<V> extends Supplier<V>
    {
        V supply() throws Exception;
        
        default V get() {
            try {
                return this.supply();
            }
            catch (Exception e) {
                UtilityMethods.rethrow(e);
                return null;
            }
        }
    }
}
