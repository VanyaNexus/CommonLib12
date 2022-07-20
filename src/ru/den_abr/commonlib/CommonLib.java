package ru.den_abr.commonlib;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.injector.server.TemporaryPlayerFactory;
import com.comphenix.protocol.utility.MinecraftVersion;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.google.common.base.Preconditions;
import commonlib.com.minnymin.command.CommandFramework;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import ru.den_abr.commonlib.boards.MainScoreboard;
import ru.den_abr.commonlib.boards.packet.TeamModificationController;
import ru.den_abr.commonlib.boards.packet.VirtualScoreboard;
import ru.den_abr.commonlib.commands.PlaceholderCommands;
import ru.den_abr.commonlib.commands.UtilityCommands;
import ru.den_abr.commonlib.configuration.Config;
import ru.den_abr.commonlib.expression.ExpressionManager;
import ru.den_abr.commonlib.messages.Message;
import ru.den_abr.commonlib.messages.MessageKey;
import ru.den_abr.commonlib.messages.MessageRegistry;
import ru.den_abr.commonlib.messages.Messages;
import ru.den_abr.commonlib.placeholders.ClibPAPIHook;
import ru.den_abr.commonlib.placeholders.PlaceholderManager;
import ru.den_abr.commonlib.requests.RequestManager;
import ru.den_abr.commonlib.utility.ChatEventFixer;
import ru.den_abr.commonlib.utility.EmptyEnchantment;
import ru.den_abr.commonlib.utility.UniversalEconomyService;
import ru.den_abr.commonlib.utility.UtilityMethods;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;

public class CommonLib extends JavaPlugin implements Listener
{
    public static CommonLib INSTANCE;
    public static GeneralConfig CONF;
    private static Map<String, CommandFramework> commandFrameworks;
    private static MainScoreboard scoreboard;
    static RequestManager reqMan;
    private static ClibPAPIHook papiHook;
    static UniversalEconomyService economyService;
    
    public void onLoad() {
        /*if (Double.parseDouble(System.getProperty("java.specification.version")) != 1.8) {
            this.getLogger().severe("ONLY JAVA 8 IS SUPPORTED");
            return;
        }*/

        org.burningwave.core.assembler.StaticComponentContainer.Modules.exportAllToAll();
        this.getDataFolder().mkdirs();
        (CommonLib.INSTANCE = this).downloadLibs();
        CommonLib.scoreboard = new MainScoreboard();
        EmptyEnchantment.EMPTY.getName();
    }
    
    public void onEnable() {
        //System.setProperty("illegal-access", "permit");
        if (CommonLib.INSTANCE == null) {
            this.getServer().getPluginManager().disablePlugin(this);
            return;
        }
        CommonLib.CONF = Config.load(this, "config.json", GeneralConfig.class);
        this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
        if (System.getProperties().containsKey("CLibStarted")) {
            (System.getProperties()).put("CLibReloaded", 0);
        }
        else {
            (System.getProperties()).put("CLibStarted", 0);
        }
        MessageRegistry.registerDefaults();
        CommonLib.reqMan = new RequestManager();
        this.getServer().getPluginManager().registerEvents(new InternalListener(this), this);
        final CommandFramework cf = commands(this);
        cf.registerCommands(new UtilityCommands());
        cf.registerCommands(new PlaceholderCommands());
        PlaceholderManager.registerPlaceholder(this, "player-name", OfflinePlayer::getName);
        PlaceholderManager.registerPlaceholder(this, "ticks-lived", p -> p.getTicksLived() + "");
        this.registerPlaceholdersAPI();
        if (UtilityMethods.has("Vault") && !isEconomyReady()) {
            UniversalEconomyService.start();
        }
        if (CommonLib.CONF.isCustomIncomingChatHandler() && UtilityMethods.has("ProtocolLib")) {
            ChatEventFixer.start();
        }
        PlaceholderManager.loadFromClasses();
        if (UtilityMethods.has("ProtocolLib")) {
            ProtocolLibrary.getProtocolManager().addPacketListener(TeamModificationController.get(this));
        }
        UtilityMethods.forciblyLoadAllClasses(this);
        ExpressionManager.registerDefaults();
        this.listenJoin();
        Bukkit.getOnlinePlayers().forEach(getMainScoreboard().getHandle()::set);
    }
    
    private void downloadLibs() {
        if (MinecraftVersion.getCurrentVersion().getMinor() >= 14) {
            UtilityMethods.installLib(this, false, "libs", "https://repo1.maven.org/maven2/commons-io/commons-io/2.11.0/commons-io-2.11.0.jar", "https://repo1.maven.org/maven2/org/apache/commons/commons-lang3/3.9/commons-lang3-3.9.jar");
        }
        UtilityMethods.installLib(this, false, "libs", "http://central.maven.org/maven2/cglib/cglib-nodep/3.2.10/cglib-nodep-3.2.10.jar", "http://central.maven.org/maven2/commons-logging/commons-logging/1.2/commons-logging-1.2.jar", "http://central.maven.org/maven2/org/apache/httpcomponents/httpcore/4.4.10/httpcore-4.4.10.jar", "http://central.maven.org/maven2/org/apache/httpcomponents/httpclient/4.5.6/httpclient-4.5.6.jar");
        if (!UtilityMethods.isAfterReload()) {
            UtilityMethods.installLib(this, false, true, "libs", "http://central.maven.org/maven2/org/hjson/hjson/3.0.0/hjson-3.0.0.jar");
        }
    }
    
    public void onDisable() {
        if (CommonLib.INSTANCE == null) {
            return;
        }
        if (UtilityMethods.has("ProtocolLib")) {
            ProtocolLibrary.getProtocolManager().removePacketListeners(this);
        }
        getMainScoreboard().reset();
        getMainScoreboard().getHandle().unsetAll();
        CommonLib.reqMan.cancelAll();
        if (CommonLib.papiHook != null) {
            PlaceholderAPI.unregisterPlaceholderHook(this.getName());
        }
        MessageRegistry.unregisterAll();
        PlaceholderManager.unregisterAll();
        ExpressionManager.unregisterAll();
        CommonLib.commandFrameworks.values().forEach(CommandFramework::unregisterAll);
        CommonLib.commandFrameworks.clear();
    }
    
    public void unregisterHooks(final Plugin p) {
        PlaceholderManager.unregisterAll(p);
        MessageRegistry.unregisterTagProcessors(p);
        ExpressionManager.unregisterAll(p);
        if (CommonLib.commandFrameworks.containsKey(p.getName())) {
            this.getLogger().info("Unregistering all " + p.getName() + " commands");
            CommonLib.commandFrameworks.remove(p.getName()).unregisterAll();
        }
    }
    
    void registerPlaceholdersAPI() {
        if (UtilityMethods.has("PlaceholderAPI")) {
            CommonLib.papiHook = new ClibPAPIHook();
            if (CommonLib.papiHook.register()) {
                this.getLogger().info("Hooked with PlaceholderAPI");
            }
        }
    }
    
    void listenJoin() {
        ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(this, new PacketType[] { PacketType.Play.Server.PLAYER_INFO }) {
            public void onPacketSending(final PacketEvent e) {
                final Player player = e.getPlayer();
                if (TemporaryPlayerFactory.getInjectorFromPlayer(player) != null) {
                    return;
                }
                final VirtualScoreboard board = CommonLib.getMainScoreboard().getHandle();
                if (e.getPacket().getPlayerInfoAction().read(0) == EnumWrappers.PlayerInfoAction.ADD_PLAYER && board.notWatching(player.getUniqueId())) {
                    board.set(player);
                }
            }
        });
    }
    
    public static UniversalEconomyService getEconomyService() {
        return (UniversalEconomyService)Preconditions.checkNotNull((Object)CommonLib.economyService, "Economy is not ready");
    }
    
    public static void setEconomyService(final UniversalEconomyService economyService) {
        CommonLib.economyService = economyService;
    }
    
    public static boolean isEconomyReady() {
        return CommonLib.economyService != null;
    }
    
    @Deprecated
    public static void addLocale(final String name, final Messages locale) {
        MessageRegistry.addMessages(name, locale);
    }
    
    @Deprecated
    public static Map<String, Messages> getMessageMaps() {
        return MessageRegistry.getMessagesMap();
    }
    
    @Deprecated
    public static Message message(final MessageKey key) {
        Preconditions.checkNotNull((Object)key, "Key cannot be null");
        return message(key.getKey());
    }
    
    @Deprecated
    public static Message message(final String key) {
        return MessageRegistry.message(key);
    }
    
    @Deprecated
    public static void removeLocale(final String name) {
        MessageRegistry.removeMessages(name);
    }
    
    public static MainScoreboard getMainScoreboard() {
        return CommonLib.scoreboard;
    }
    
    public static RequestManager getRequestsManager() {
        return CommonLib.reqMan;
    }
    
    public static CommandFramework commands(final Plugin p) {
        if (!CommonLib.commandFrameworks.containsKey(p.getName())) {
            CommonLib.commandFrameworks.put(p.getName(), CommandFramework.create(p));
        }
        return CommonLib.commandFrameworks.get(p.getName());
    }
    
    public static void debug(final Object... o) {
        if (CommonLib.CONF.isDebug()) {
            Arrays.stream(o).map(obj -> "[DEBUG] " + obj).forEach((Consumer<? super Object>)System.out::println);
        }
    }
    
    static {
        CommonLib.commandFrameworks = Collections.synchronizedMap(new LinkedHashMap<String, CommandFramework>());
    }
}
