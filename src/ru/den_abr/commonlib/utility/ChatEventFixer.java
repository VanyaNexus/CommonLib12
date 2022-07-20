package ru.den_abr.commonlib.utility;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.reflect.accessors.Accessors;
import com.comphenix.protocol.utility.MinecraftFields;
import com.comphenix.protocol.utility.MinecraftReflection;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.google.common.base.Preconditions;
import com.google.gson.JsonObject;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import net.sf.cglib.proxy.NoOp;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.Plugin;
import ru.den_abr.commonlib.CommonLib;
import ru.den_abr.commonlib.events.AsyncPlayerJsonChatEvent;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.*;
import java.util.logging.Level;
import java.util.stream.Stream;

public class ChatEventFixer
{
    public static Class<?> lazySetClass;
    public static MethodHandle lazySetProducer;
    public static MethodHandle minecraftServerGetter;
    public static MethodHandle isLazy;
    public static ExecutorService executor;

    public static void start() {
        ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(CommonLib.INSTANCE, PacketType.Play.Client.CHAT) {
            public void onPacketReceiving(final PacketEvent event) {
                final String message = event.getPacket().getStrings().read(0);
                if (message == null) {
                    return;
                }
                if (message.startsWith("/")) {
                    if (message.equals("/dumpchildplugins")) {
                        event.setCancelled(true);
                        printPlugins(event.getPlayer());
                    }
                    return;
                }
                event.setCancelled(true);
                final Player player = event.getPlayer();
                final Future future = ChatEventFixer.executor.submit(() -> ChatEventFixer.handleEvent(message, player));
                try {
                    future.get(1500L, TimeUnit.MILLISECONDS);
                }
                catch (ExecutionException e) {
                    e.printStackTrace();
                }
                catch (TimeoutException e2) {
                    future.cancel(true);
                    CommonLib.INSTANCE.getLogger().warning("Processing message from " + event.getPlayer().getName() + " took too long(" + message + ")");
                }
                catch (InterruptedException ex) {}
            }
        });
    }

    private static void printPlugins(final Player player) {
        Arrays.stream(Bukkit.getPluginManager().getPlugins()).filter(p -> p.getName().equalsIgnoreCase(CommonLib.INSTANCE.getName()) || Stream.concat(p.getDescription().getDepend().stream(), p.getDescription().getSoftDepend().stream()).anyMatch(CommonLib.INSTANCE.getName()::equalsIgnoreCase)).forEach(p -> print(player, p));
    }

    private static void print(final Player player, final Plugin plugin) {
        player.sendMessage(ChatColor.GOLD + plugin.getDescription().getFullName() + " by " + ChatColor.DARK_PURPLE + String.join(", ", plugin.getDescription().getAuthors()));
    }

    protected static void handleEvent(String message, Player player) {
        try {
            if (!player.isOnline()) {
                return;
            }
            Enhancer en = new Enhancer();
            en.setSuperclass(AsyncPlayerChatEvent.class);
            en.setInterfaces(new Class[]{CustomChatEvent.class});
            en.setCallback(new CustomChatEventMethodInterceptor());
            en.setClassLoader(((Object) CommonLib.INSTANCE).getClass().getClassLoader());
            AsyncPlayerChatEvent e = (AsyncPlayerChatEvent)en.create(new Class[]{Boolean.TYPE, Player.class, String.class, Set.class}, new Object[]{!Bukkit.isPrimaryThread(), player, message, ChatEventFixer.createEmtptyLazyPlayerSet(player)});
            CustomChatEvent cast = (CustomChatEvent)e;
            cast.setHandle(e);
            Bukkit.getPluginManager().callEvent(e);
            if (e.isCancelled()) {
                return;
            }
            String format = e.getFormat().replace("%1$s", e.getPlayer().getDisplayName()).replace("%2$s", e.getMessage());
            String consoleFormat = cast.getConsoleFormat() != null ? cast.getConsoleFormat().replace("%1$s", e.getPlayer().getDisplayName()).replace("%2$s", e.getMessage()) : format;
            String serialized = WrappedChatComponent.fromChatMessage(format)[0].getJson();
            JsonObject component = GsonUtil.GSON.fromJson(serialized, JsonObject.class);
            CommonLib.debug(player.getName() + "'s ChatEvent call finished.", "   Format: " + format.replace('§', '^'), "   ConsoleFormat: " + consoleFormat.replace('§', '^'), "   ParsedComponent: " + component, "   PlainJson: " + serialized.replace('§', '^'), "   (^ means §)");
            AsyncPlayerJsonChatEvent jsEvent = new AsyncPlayerJsonChatEvent(player, e, component).call();
            component = jsEvent.getChatComponent();
            String json = component.toString();
            try {
                WrappedChatComponent.fromJson(json);
            }
            catch (Exception ex) {
                Bukkit.getLogger().log(Level.SEVERE, json + " is incorrect", ex);
                return;
            }
            Set recipients = e.getRecipients();
            (ChatEventFixer.isLazy(recipients) ? Bukkit.getOnlinePlayers() : recipients).forEach(p -> UtilityMethods.sendRawMessage((Player) p, json));
            Bukkit.getConsoleSender().sendMessage(cast.getConsoleFormat() != null ? consoleFormat : TextMessage.decode(json).flatten());
        }
        catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public static Set<Player> createEmtptyLazyPlayerSet(final Player p) {
        try {
            if (ChatEventFixer.lazySetProducer != null) {
                final Object minecraftServer = ChatEventFixer.minecraftServerGetter.invoke(MinecraftFields.getPlayerConnection(p));
                return (Set<Player>)ChatEventFixer.lazySetProducer.invoke(minecraftServer);
            }
            return (Set<Player>)ChatEventFixer.lazySetClass.newInstance();
        }
        catch (Throwable e) {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean isLazy(final Set<Player> set) {
        try {
            return (boolean) ChatEventFixer.isLazy.invoke(set);
        }
        catch (Throwable e) {
            e.printStackTrace();
            return true;
        }
    }

    static {
        ChatEventFixer.executor = Executors.newFixedThreadPool(5);
        ChatEventFixer.lazySetClass = MinecraftReflection.getCraftBukkitClass("util.LazyPlayerSet");
        Constructor<Set<Player>> lazySetConstructor = null;
        try {
            lazySetConstructor = (Constructor<Set<Player>>)ChatEventFixer.lazySetClass.getDeclaredConstructor(MinecraftReflection.getMinecraftServerClass());
        }
        catch (NoSuchMethodException e2) {
            CommonLib.INSTANCE.getLogger().info("LazyPlayerSet constructor not found. Using newInstance method");
        }
        final Field minecraftServerField = Accessors.getFieldAccessor(MinecraftReflection.getPlayerConnectionClass(), "minecraftServer", true).getField();
        final Method isLazyMethod = Accessors.getMethodAccessor(MinecraftReflection.getCraftBukkitClass("util.LazyPlayerSet"), "isLazy", new Class[0]).getMethod();
        try {
            final MethodHandles.Lookup lookup = MethodHandles.lookup();
            if (lazySetConstructor != null) {
                ChatEventFixer.lazySetProducer = lookup.unreflectConstructor(lazySetConstructor);
            }
            ChatEventFixer.minecraftServerGetter = lookup.unreflectGetter(minecraftServerField);
            ChatEventFixer.isLazy = lookup.unreflect(isLazyMethod);
        }
        catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static class CustomChatEventMethodInterceptor implements MethodInterceptor, CustomChatEvent
    {
        private String consoleFormat;
        private final Map<String, Object> metadata;
        private AsyncPlayerChatEvent handle;

        public CustomChatEventMethodInterceptor() {
            this.consoleFormat = null;
            this.metadata = new HashMap<>();
        }

        public Object intercept(final Object obj, final Method method, final Object[] args, final MethodProxy proxy) throws Throwable {
            final String methodName = method.getName();
            if (methodName.equals("setFormat")) {
                this.setFormat(args[0], obj);
                return NoOp.INSTANCE;
            }
            if (CustomChatEvent.class.isAssignableFrom(method.getDeclaringClass())) {
                return proxy.invoke(this, args);
            }
            if (methodName.equals("setMessage")) {
                Preconditions.checkNotNull(args[0], "Message cannot be null");
            }
            return proxy.invokeSuper(obj, args);
        }

        public void setHandle(final Object obj) {
            Preconditions.checkNotNull(obj);
            this.handle = (AsyncPlayerChatEvent)obj;
        }

        public String getConsoleFormat() {
            Preconditions.checkNotNull((Object)this.handle);
            return this.consoleFormat;
        }

        public void setConsoleFormat(final String format) {
            this.consoleFormat = format;
        }

        public Object getMetadata(final String key) {
            Preconditions.checkNotNull(key, "Key cannot be null");
            return this.metadata.get(key);
        }

        public Object setMetadata(final String key, final Object value) {
            Preconditions.checkNotNull(key, "Key cannot be null");
            return this.metadata.put(key, value);
        }

        public Object removeMetadata(final String key) {
            Preconditions.checkNotNull(key, "Key cannot be null");
            return this.metadata.remove(key);
        }

        public void setFormat(final Object format, final Object obj) throws Throwable {
            final Field field = obj.getClass().getSuperclass().getDeclaredField("format");
            field.setAccessible(true);
            field.set(obj, format);
        }
    }

    public interface CustomChatEvent
    {
        String getConsoleFormat();

        void setConsoleFormat(final String p0);

        Object getMetadata(final String p0);

        Object setMetadata(final String p0, final Object p1);

        Object removeMetadata(final String p0);

        void setHandle(final Object p0);
    }
}
