package ru.den_abr.commonlib.utility;

import com.google.common.base.Preconditions;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import org.bukkit.ChatColor;

import java.util.EnumSet;
import java.util.Set;

public final class TextMessage
{
    private static final ChatColor[] FORMATTING;
    private final JsonObject object;
    
    public TextMessage(final String text) {
        this(convert(String.valueOf(text)));
    }
    
    public TextMessage(final JsonObject object) {
        Preconditions.checkNotNull((Object)object, (Object)"object must not be null");
        this.object = object;
    }
    
    private static void flatten(final StringBuilder dest, final JsonObject obj) {
        if (obj.has("color")) {
            try {
                dest.append(ChatColor.valueOf(obj.get("color").getAsString().toUpperCase()));
            }
            catch (IllegalArgumentException ex) {}
        }
        for (final ChatColor format : TextMessage.FORMATTING) {
            final String name = (format == ChatColor.MAGIC) ? "obfuscated" : format.name().toLowerCase();
            if (obj.has(name) && obj.get(name).getAsBoolean()) {
                dest.append(format);
            }
        }
        if (obj.has("text")) {
            dest.append(obj.get("text").getAsString());
        }
        if (obj.has("extra")) {
            final JsonArray array = obj.get("extra").getAsJsonArray();
            for (final JsonElement o : array) {
                if (o.isJsonObject()) {
                    flatten(dest, (JsonObject)o);
                }
                else {
                    dest.append(o.getAsString());
                }
            }
        }
    }
    
    public String flatten() {
        final StringBuilder builder = new StringBuilder();
        flatten(builder, this.object);
        final String result = builder.toString();
        return result;
    }
    
    public static TextMessage decode(final String json) {
        try {
            final JsonElement o = (JsonElement)GsonUtil.GSON.fromJson(json, (Class)JsonElement.class);
            if (o instanceof JsonObject) {
                return new TextMessage((JsonObject)o);
            }
            return new TextMessage(o.toString());
        }
        catch (JsonSyntaxException e) {
            return new TextMessage(json);
        }
    }
    
    private static JsonObject convert(final String text) {
        final JsonArray items = new JsonArray();
        final Set<ChatColor> formatting = EnumSet.noneOf(ChatColor.class);
        final StringBuilder current = new StringBuilder();
        ChatColor color = null;
        for (int i = 0; i < text.length(); ++i) {
            final char ch = text.charAt(i);
            if (ch != '§') {
                current.append(ch);
            }
            else if (i != text.length() - 1) {
                append(items, current, color, formatting);
                final ChatColor code = ChatColor.getByChar(text.charAt(++i));
                if (code.isFormat() || code == ChatColor.RESET) {
                    formatting.add(code);
                }
                else {
                    color = code;
                    formatting.clear();
                }
            }
        }
        append(items, current, color, formatting);
        if (items.size() == 0) {
            final JsonObject object = new JsonObject();
            object.addProperty("text", "");
            return object;
        }
        if (items.size() == 1) {
            return items.get(0).getAsJsonObject();
        }
        JsonObject object = items.get(0).getAsJsonObject();
        if (object.entrySet().size() == 1) {
            final JsonArray jarr = new JsonArray();
            for (int j = 1; j < items.size(); ++j) {
                jarr.add(items.get(j));
            }
            object.add("extra", (JsonElement)jarr);
        }
        else {
            object = new JsonObject();
            object.addProperty("text", "");
            object.add("extra", (JsonElement)items);
        }
        return object;
    }
    
    private static void append(final JsonArray items, final StringBuilder current, final ChatColor color, final Set<ChatColor> formatting) {
        if (current.length() == 0) {
            return;
        }
        final JsonObject object = new JsonObject();
        object.addProperty("text", current.toString());
        if (color != null) {
            object.addProperty("color", color.name().toLowerCase());
        }
        for (final ChatColor format : formatting) {
            if (format == ChatColor.MAGIC) {
                object.addProperty("obfuscated", true);
            }
            else {
                object.addProperty(format.name().toLowerCase(), true);
            }
        }
        current.setLength(0);
        items.add((JsonElement)object);
    }
    
    public String encode() {
        return this.object.toString();
    }
    
    public String asPlaintext() {
        if (this.object.has("text")) {
            final JsonElement obj = this.object.get("text");
            if (obj.isJsonPrimitive()) {
                return obj.getAsString();
            }
        }
        return "";
    }
    
    @Override
    public String toString() {
        return "Message" + this.encode();
    }
    
    static {
        FORMATTING = new ChatColor[] { ChatColor.MAGIC, ChatColor.BOLD, ChatColor.STRIKETHROUGH, ChatColor.UNDERLINE, ChatColor.ITALIC, ChatColor.RESET };
    }
}
