package ru.den_abr.commonlib.utility;

import clib.com.comphenix.packetwrapper.WrapperPlayServerPlayerListHeaderFooter;
import clib.com.comphenix.packetwrapper.WrapperPlayServerTitle;
import com.comphenix.protocol.PacketType.Play.Server;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.injector.netty.WirePacket;
import com.comphenix.protocol.reflect.accessors.Accessors;
import com.comphenix.protocol.utility.MinecraftReflection;
import com.comphenix.protocol.utility.MinecraftVersion;
import com.comphenix.protocol.utility.StreamSerializer;
import com.comphenix.protocol.wrappers.EnumWrappers.ChatType;
import com.comphenix.protocol.wrappers.EnumWrappers.TitleAction;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.nbt.NbtCompound;
import com.comphenix.protocol.wrappers.nbt.NbtFactory;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList.Builder;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.google.common.io.Closer;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import commonlib.com.minnymin.command.CommandFramework;
import commonlib.com.minnymin.command.Executor;
import de.janmm14.jsonmessagemaker.api.JsonMessageConverter;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.chat.ComponentSerializer;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.chat.plugins.Chat_PermissionsEx;
import net.milkbowl.vault.permission.Permission;
import org.apache.commons.io.FilenameUtils;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.Command;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.UnknownDependencyException;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.util.Vector;
import protocolsupport.api.ProtocolSupportAPI;
import ru.den_abr.commonlib.CommonLib;
import ru.den_abr.commonlib.boards.FastOfflinePlayer;
import ru.den_abr.commonlib.messages.Message;
import ru.den_abr.commonlib.messages.MessagePosition;
import ru.den_abr.commonlib.messages.MessageRegistry;
import ru.den_abr.commonlib.nametags.NametagsManager;
import ru.den_abr.commonlib.placeholders.PlaceholderManager;
import ru.den_abr.commonlib.utility.ChatEventFixer.CustomChatEvent;
import us.myles.ViaVersion.api.Via;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class UtilityMethods {
    public static final BigDecimal ONE_HUNDRED = new BigDecimal("100");
    public static final BlockFace[] RADIAL;

    public UtilityMethods() {
    }

    public static void transferPlayer(Player p, String server) {
        ByteArrayDataOutput data = ByteStreams.newDataOutput();
        data.writeUTF("Connect");
        data.writeUTF(server);
        p.sendPluginMessage(CommonLib.INSTANCE, "BungeeCord", data.toByteArray());
    }

    public static void clearInventory(Player p) {
        p.getInventory().clear();
        p.getInventory().setArmorContents(null);
    }

    public static void clearEffects(Player p) {
        Iterator<PotionEffect> var1 = p.getActivePotionEffects().iterator();

        while(var1.hasNext()) {
            PotionEffect eff = var1.next();
            p.removePotionEffect(eff.getType());
        }

    }

    public static void setSuffix(String p, String suffix) {
        NametagsManager.setSuffix(Bukkit.getPlayerExact(p), suffix);
    }

    public static void setPrefixAndSuffix(String p, String pref, String suf) {
        NametagsManager.setPlayerNametag(Bukkit.getPlayerExact(p), pref, suf);
    }

    public static <T> List<T> getPage(List<T> sourceList, int page, int pageSize) {
        Preconditions.checkArgument(pageSize > 0, "invalid page size: " + pageSize);
        Preconditions.checkArgument(page >= 0, "invalid page: " + page);
        int fromIndex = page * pageSize;
        return sourceList != null && sourceList.size() >= fromIndex ? sourceList.subList(fromIndex, Math.min(fromIndex + pageSize, sourceList.size())) : Collections.emptyList();
    }

    public static void setChatFormatDirectly(AsyncPlayerChatEvent e, String format) {
        try {
            Field forField = e.getClass().getDeclaredField("format");
            forField.setAccessible(true);
            forField.set(e, format);
        } catch (SecurityException | IllegalArgumentException | IllegalAccessException | NoSuchFieldException var3) {
            e.setFormat(format);
        }

    }

    public static boolean isItemAir(ItemStack is) {
        return is == null || is.getType() == Material.AIR;
    }

    public static void sendActionBar(Player p, String legacy) {
        sendRawMessage(p, ComponentSerializer.toString(TextComponent.fromLegacyText(legacy)), MessagePosition.ACTIONBAR);
    }

    public static void sendRawMessage(Player p, String json) {
        sendRawMessage(p, json, MessagePosition.CHAT);
    }

    public static void sendRawMessage(Player p, String json, MessagePosition pos) {
        sendRawMessage(p, json, pos, pos == MessagePosition.ACTIONBAR ? 3 : 0);
    }

    public static void sendRawMessage(Player p, String json, MessagePosition pos, int repeats) {
        if (!json.equals("{}") && p != null && p.isOnline()) {
            if (pos == MessagePosition.ACTIONBAR) {
                json = "{\"text\": \"" + jsonToString(json) + "\"}";
            }

            sendRawFixedMessage(p, json, pos, repeats);
        }
    }

    private static void sendRawFixedMessage(Player p, String fixedJson, MessagePosition pos, int repeats) {
        if (MinecraftVersion.getCurrentVersion().isAtLeast(MinecraftVersion.COLOR_UPDATE)) {
            sendRawDirectly(p, fixedJson, pos);
        } else {
            sendRawCompatible(p, fixedJson, pos);
        }

        if (repeats > 0) {
            Bukkit.getScheduler().runTaskLaterAsynchronously(CommonLib.INSTANCE, () -> {
                sendRawFixedMessage(p, fixedJson, pos, repeats - 1);
            }, 20L);
        }

    }

    private static void sendRawCompatible(Player player, String json, MessagePosition pos) {
        PacketContainer packet = new PacketContainer(Server.CHAT);
        ChatType type = MessagePosition.toChatType(pos);
        packet.getBytes().writeSafely(0, type.getId());
        packet.getChatTypes().writeSafely(0, type);
/*        if(json.contains("[jmm|")) {
            System.out.println("has jmm send converted: " + json);
            packet.getSpecificModifier(BaseComponent[].class).write(0, JsonMessageConverter.DEFAULT.convert(json));
        } else {
            System.out.println("dont has jmm send UNCONVERTED: " + json + " | ");
            packet.getSpecificModifier(BaseComponent[].class).write(0, ComponentSerializer.parse(json));
        }*/
        packet.getSpecificModifier(BaseComponent[].class).write(0, ComponentSerializer.parse(json));
        sendPacket(player, packet);
    }

    private static void sendRawDirectly(Player p, String json, MessagePosition pos) {
        int protocol = getProtocol(p);
        if (protocol >= 47 && !CommonLib.CONF.isCompatJson()) {
            boolean ps = has("ProtocolSupport");
            StreamSerializer serializer = StreamSerializer.getDefault();
            ByteArrayOutputStream arrayOut = new ByteArrayOutputStream();
            DataOutputStream out = new DataOutputStream(arrayOut);

            try {
                int packetId = getChatPacketId(protocol);
                if (ps) {
                    serializer.serializeVarInt(out, packetId);
                }

                serializer.serializeString(out, json);
                out.writeByte(MessagePosition.toChatType(pos).getId());
                byte[] raw = arrayOut.toByteArray();
                if (ps) {
                    ProtocolSupportAPI.getConnection(p).sendRawPacket(raw);
                } else {
                    ProtocolLibrary.getProtocolManager().sendWirePacket(p, new WirePacket(packetId, raw));
                }
            } catch (InvocationTargetException | IOException var10) {
                CommonLib.INSTANCE.getLogger().log(Level.SEVERE, "Can't send " + pos + " message " + json + " to " + p.getName(), var10);
            }

        } else {
            sendRawCompatible(p, json, pos);
        }
    }

    private static int getChatPacketId(int protocol) {
        if (protocol == 47) {
            return 2;
        }
        if(protocol >= 721) {
            return 14;
        }
        if (protocol > 47 && protocol <= 340 || protocol > 498) {
            return 15;
        }
        return 14;
    }

    public static String jsonToString(JsonElement el) {
        return el.isJsonObject() && el.getAsJsonObject().entrySet().isEmpty() ? "" : jsonToString(el.toString());
    }

    public static String jsonToString(String json) {
        if (json != null && !json.isEmpty() && !json.equals("{}")) {
            try {
                return BaseComponent.toLegacyText(ComponentSerializer.parse(json));
            } catch (Exception var2) {
                System.out.println("Error ocurred while serializing " + json);
                var2.printStackTrace();
                return null;
            }
        } else {
            return "";
        }
    }

    public static int getProtocol(Player player) {
        int fallbackProtocol = ProtocolLibrary.getProtocolManager().getProtocolVersion(player);
        if (!has("ViaVersion") && !has("ProtocolSupport")) {
            return fallbackProtocol;
        } else {
            Predicate<Integer> not = (integer) -> {
                return integer != fallbackProtocol;
            };
            return Stream.of(getPSProtocol(player).filter(not), getViaProtocol(player).filter(not)).filter(Optional::isPresent).mapToInt(Optional::get).findFirst().orElse(fallbackProtocol);
        }
    }

    private static Optional<Integer> getPSProtocol(Player player) {
        return !has("ProtocolSupport") ? Optional.empty() : Optional.of(ProtocolSupportAPI.getProtocolVersion(player).getId());
    }

    private static Optional<Integer> getViaProtocol(Player player) {
        return !has("ViaVersion") ? Optional.empty() : Optional.of(Via.getAPI().getPlayerVersion(player));
    }

    public static String getOriginalCommand(String fullCmd) {
        fullCmd = fullCmd.trim();
        if (fullCmd.isEmpty()) {
            return fullCmd;
        } else {
            String first = fullCmd.trim().toLowerCase().split(" ")[0];
            if (first.contains(":")) {
                first = first.split(":")[1];
            }

            if (first.startsWith("/")) {
                if (first.length() == 1) {
                    return "";
                }

                first = first.substring(1);
            }

            Command command = null;

            try {
                SimpleCommandMap scm = (SimpleCommandMap)getFromField(Bukkit.getServer(), "commandMap");
                command = scm.getCommand(first);
            } catch (ReflectiveOperationException var5) {
                var5.printStackTrace();
            }

            if (command != null) {
                if (command instanceof PluginCommand && ((PluginCommand)command).getExecutor() instanceof CommandFramework) {
                    CommandFramework cf = (CommandFramework)((PluginCommand)command).getExecutor();
                    Executor exec = cf.getExecutor(command.getName());
                    if (exec != null) {
                        return exec.getName();
                    }
                }

                return command.getName();
            } else {
                return first;
            }
        }
    }

    public static JsonElement replaceValues(JsonElement source, Function<JsonPrimitive, JsonElement> replacer) {
        if (source.isJsonPrimitive()) {
            return replacer.apply(source.getAsJsonPrimitive());
        } else if (source.isJsonArray()) {
            JsonArray jarr = new JsonArray();
            StreamSupport.stream(source.getAsJsonArray().spliterator(), false).map((el) -> {
                return replaceValues(el, replacer);
            }).forEachOrdered(jarr::add);
            return jarr;
        } else if (source.isJsonNull()) {
            return source;
        } else {
            JsonObject obj = new JsonObject();
            source.getAsJsonObject().entrySet().forEach((e) -> {
                obj.add(e.getKey(), replaceValues(e.getValue(), replacer));
            });
            return obj;
        }
    }

    public static Object getFromField(Object instance, String field) throws ReflectiveOperationException {
        Field f = instance.getClass().getDeclaredField(field);
        f.setAccessible(true);
        return f.get(instance);
    }

    public static boolean isChatMessage(PacketContainer pc) {
        Byte position = pc.getBytes().readSafely(0);
        if (position != null) {
            return position == 1;
        } else {
            return pc.getChatTypes().read(0) == ChatType.CHAT;
        }
    }

    public static void setPrefix(String p, String pref) {
        NametagsManager.setPrefix(Bukkit.getPlayerExact(p), pref);
    }

    public static NbtCompound getBlockNbt(Block block) {
        checkDependency("ProtocolLib");
        return NbtFactory.readBlockState(block);
    }

    public static World loadWorld(String name, boolean freeze) {
        World world = (new WorldCreator(name)).type(WorldType.CUSTOMIZED).generator(EmptyChunkGenerator.INSTANCE).generateStructures(false).createWorld();
        if (freeze) {
            freezeWorld(world);
        }

        return world;
    }

    public static void setBlockNbt(Block block, NbtCompound nbt) {
        checkDependency("ProtocolLib");
        NbtFactory.writeBlockState(block, nbt);
    }

    public static void getAllEntitiesInWorld(World world, boolean unloadAfter, boolean save, Predicate<Entity> filter, Consumer<Collection<Entity>> onLoad) {
        File worldFolder = world.getWorldFolder();
        File region = new File(worldFolder, "region");
        if (!region.exists()) {
            region = new File(Arrays.stream(worldFolder.listFiles()).filter(File::isDirectory).filter((f) -> {
                return f.getName().startsWith("DIM");
            }).findAny().get(), "region");
        }

        List<Entity> entities = new ArrayList<>();
        List<Chunk> forceLoaded = new ArrayList<>();
        Arrays.stream(region.listFiles((file, name) -> name.endsWith(".mca"))).forEach((file) -> {
            String[] spl = file.getName().split("\\.");
            int x = Integer.parseInt(spl[1]);
            int z = Integer.parseInt(spl[2]);

            for (int fx = mcaToChunkMin(x); fx < mcaToChunkMax(x) + 1; ++fx) {
                for (int fz = mcaToChunkMin(z); fz < mcaToChunkMax(z) + 1; ++fz) {
                    if (world.isChunkLoaded(fx, fz)) {
                        Arrays.stream(world.getChunkAt(fx, fz).getEntities()).filter(filter::test).forEach(entities::add);
                    }
                    else if (world.loadChunk(fx, fz, false)) {
                        Chunk chank = world.getChunkAt(fx, fz);
                        if (unloadAfter) {
                            forceLoaded.add(chank);
                        }
                        Arrays.stream(chank.getEntities()).filter(filter::test).forEach(entities::add);
                    }
                }
            }

        });
        onLoad.accept(entities);
        if (unloadAfter) {
            forceLoaded.forEach((ch) -> {
                world.unloadChunkRequest(ch.getX(), ch.getZ(), save);
            });
        }

    }


    public static List<MarkerArmorStand> getMarkersInTheWorldNBT(World world) {
        Builder<MarkerArmorStand> builder = new Builder<>();
        return builder.build();
    }

    public static int mcaToChunkMax(int cord) {
        return (cord + 1 << 5) - 1;
    }

    public static boolean has(String string) {
        return Bukkit.getPluginManager().isPluginEnabled(string);
    }

    public static int mcaToChunkMin(int cord) {
        return cord << 5;
    }

    public static void hideTitle(Player p) {
        checkDependency("ProtocolLib");
        WrapperPlayServerTitle clear = new WrapperPlayServerTitle();
        clear.setAction(TitleAction.CLEAR);
        sendPacket(p, clear.getHandle());
    }

    public static void resetTitle(Player p) {
        p.resetTitle();
    }

    public static void sendTitle(Player p, Message combined, int fadeIn, int stay, int fadeOut) {
        sendTitleTimes(p, fadeIn, stay, fadeOut);
        List<String> asList = (List<String>)PlaceholderManager.proccessCollection(p, combined.getAsList());
        String title = asList.size() > 0 ? asList.get(0) : "";
        String subTitle = asList.size() > 1 ? asList.get(1) : "";
        sendTitle(p, title, subTitle);
    }

    public static void sendTitle(Player p, String title, String subtitle, int fadeIn, int stay, int fadeOut) {
        sendTitleTimes(p, fadeIn, stay, fadeOut);
        sendTitle(p, title, subtitle);
    }

    public static void sendTitle(Player p, String title, String subtitle) {
        sendTitlePacket(p, title, TitleAction.TITLE);
        sendTitlePacket(p, subtitle, TitleAction.SUBTITLE);
    }

    private static void sendTitlePacket(Player player, String text, TitleAction action) {
        if (text == null) {
            text = "";
        }

        try {
            PacketContainer packet = new PacketContainer(Server.TITLE);
            packet.getTitleActions().write(0, action);
            packet.getChatComponents().write(0, WrappedChatComponent.fromText(text));
            sendPacket(player, packet);
        } catch (NullPointerException var4) {
            var4.printStackTrace();
        }

    }

    public static void setHeaderFooter(Player p, String header, String footer) {
        checkDependency("ProtocolLib");
        WrapperPlayServerPlayerListHeaderFooter plist = new WrapperPlayServerPlayerListHeaderFooter();
        plist.setFooter(WrappedChatComponent.fromText(color(header)));
        plist.setHeader(WrappedChatComponent.fromText(color(footer)));
        sendPacket(p, plist.getHandle());
    }

    public static String getGroup(Player player) {
        checkDependency("Vault");
        String group = Bukkit.getServicesManager().getRegistration(Permission.class).getProvider().getPrimaryGroup(player);
        return group != null ? group : "default";
    }

    public static String getGroup(String player) {
        checkDependency("Vault");
        Player online = Bukkit.getPlayerExact(player);
        if (online != null) {
            return getGroup(online);
        } else {
            String group = Bukkit.getServicesManager().getRegistration(Permission.class).getProvider().getPrimaryGroup((String)null, player);
            return group != null ? group : "default";
        }
    }

    public static double getBalance(Player player) {
        return getBalance(player.getName());
    }

    public static double getBalance(String player) {
        return CommonLib.isEconomyReady() ? CommonLib.getEconomyService().getEconomy().getBalance(player) : 0.0D;
    }

    public static boolean hasBalance(Player player, double amount) {
        return hasBalance(player.getName(), amount);
    }

    public static boolean hasBalance(String player, double amount) {
        return getBalance(player) >= amount;
    }

    public static void addBalance(Player player, double amount) {
        addBalance(player.getName(), amount);
    }

    public static void addBalance(String player, double amount) {
        if (CommonLib.isEconomyReady()) {
            CommonLib.getEconomyService().getEconomy().addMoney(player, amount);
        }

    }

    public static void takeBalance(Player player, double amount) {
        takeBalance(player.getName(), amount);
    }

    public static void takeBalance(String player, double amount) {
        if (CommonLib.isEconomyReady()) {
            CommonLib.getEconomyService().getEconomy().takeMoney(player, amount);
        }

    }

    public static void setBalance(Player player, double balance) {
        setBalance(player.getName(), balance);
    }

    public static void setBalance(String player, double balance) {
        if (CommonLib.isEconomyReady()) {
            CommonLib.getEconomyService().getEconomy().setMoney(player, balance);
        }

    }

    public static void sendTitleTimes(Player p, int fadeIn, int stay, int fadeOut) {
        checkDependency("ProtocolLib");
        WrapperPlayServerTitle times = new WrapperPlayServerTitle();
        times.setAction(TitleAction.TIMES);
        times.setFadeIn(fadeIn);
        times.setStay(stay);
        times.setFadeOut(fadeOut);
        sendPacket(p, times.getHandle());
    }

    public static int getInventoryID(Player p) {
        Inventory inv = p.getOpenInventory().getTopInventory();
        if (inv == null) {
            return -1;
        } else {
            int id = -1;

            try {
                Object playerHandle = MinecraftReflection.getCraftPlayerClass().getMethod("getHandle").invoke(p);
                Field actContF = playerHandle.getClass().getField("activeContainer");
                actContF.setAccessible(true);
                Object activeContainer = actContF.get(playerHandle);
                Field winIdF = activeContainer.getClass().getField("windowId");
                winIdF.setAccessible(true);
                id = winIdF.getInt(activeContainer);
            } catch (IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException | NoSuchFieldException | IllegalAccessException var7) {
                var7.printStackTrace();
            }

            return id;
        }
    }

    public static void sendPacket(Player p, PacketContainer pc) {
        try {
            if (p.isOnline()) {
                ProtocolLibrary.getProtocolManager().sendServerPacket(p, pc);
            }
        } catch (IllegalArgumentException var3) {
        } catch (InvocationTargetException var4) {
            var4.printStackTrace();
        }

    }

    public static Integer getPercent(float whole, float part) {
        return (int)((double)(part / whole) * 100.0D);
    }

    public static Integer getPercent(int whole, int part) {
        return (int)(Double.valueOf(part) / Double.valueOf(whole) * 100.0D);
    }

    public static Integer getPercent(double whole, double part) {
        return (int)(part / whole * 100.0D);
    }

    public static BigInteger getPercent(BigInteger whole, BigInteger part) {
        return (new BigDecimal(part)).divide(new BigDecimal(whole), 2, RoundingMode.HALF_DOWN).multiply(ONE_HUNDRED).toBigInteger();
    }

    public static String formatSeconds(int seconds) {
        long longVal = (new BigDecimal(seconds)).longValue();
        int hours = (int)longVal / 3600;
        int remainder = (int)longVal - hours * 3600;
        int mins = remainder / 60;
        remainder -= mins * 60;
        return String.format("%02d:%02d:%02d", hours, mins, remainder);
    }

    public static String formatCoins(double balance) {
        return CommonLib.isEconomyReady() ? CommonLib.getEconomyService().getEconomy().format(balance) : String.valueOf(balance);
    }

    public static String formatMinutes(int time) {
        long longVal = (new BigDecimal(time)).longValue();
        int hours = (int)longVal / 3600;
        int remainder = (int)longVal - hours * 3600;
        int mins = remainder / 60;
        remainder -= mins * 60;
        return String.format("%02d:%02d", mins, remainder);
    }

    public static String translateTime(String time) {
        String unit;
        String number;
        if (time.length() > 1) {
            unit = time.substring(time.length() - 1).toLowerCase();
            number = time.substring(0, time.length() - 1);
        } else {
            unit = "s";
            number = time;
        }

        int ch = Integer.parseInt(number);
        return translateByLastDigit(ch, unit);
    }

    public static String getBiasFromLocale(double data, String keyPrefix) {
        return MessageRegistry.message(keyPrefix + "." + getBias(data, "singular", "plural", "plural2")).getAsString();
    }

    public static String getBias(double data, String однаМинута, String двеМинуты, String пятьМинут) {
        if (data % 10.0D == 1.0D && data % 100.0D != 11.0D) {
            return однаМинута;
        } else {
            return data % 10.0D <= 1.0D || data % 10.0D >= 5.0D || data % 100.0D >= 12.0D && data % 100.0D <= 14.0D ? пятьМинут : двеМинуты;
        }
    }

    public static String getTimeInMaxUnit(long time) {
        int ch;
        if (TimeUnit.MILLISECONDS.toDays(time) > 0L) {
            ch = (int)TimeUnit.MILLISECONDS.toDays(time);
            return translateByLastDigit(ch, "d");
        } else if (TimeUnit.MILLISECONDS.toHours(time) > 0L) {
            ch = (int)TimeUnit.MILLISECONDS.toHours(time);
            return translateByLastDigit(ch, "h");
        } else if (TimeUnit.MILLISECONDS.toMinutes(time) > 0L) {
            ch = (int)TimeUnit.MILLISECONDS.toMinutes(time);
            return translateByLastDigit(ch, "m");
        } else if (TimeUnit.MILLISECONDS.toSeconds(time) > 0L) {
            ch = (int)TimeUnit.MILLISECONDS.toSeconds(time);
            return translateByLastDigit(ch, "s");
        } else {
            ch = (int)time;
            return ch + "мcек.";
        }
    }

    public static boolean isAfterReload() {
        return System.getProperties().containsKey("CLibReloaded");
    }

    public static int getPing(Player p) {
        checkDependency("ProtocolLib");
        return (Integer)Accessors.getFieldAccessor(MinecraftReflection.getEntityPlayerClass(), "ping", true).get(Accessors.getMethodAccessor(MinecraftReflection.getCraftPlayerClass(), "getHandle", new Class[0]).invoke(p));
    }

    public static List<Field> getAllFields(List<Field> fields, Class<?> type) {
        fields.addAll(Arrays.asList(type.getDeclaredFields()));
        if (type.getSuperclass() != null) {
            getAllFields(fields, type.getSuperclass());
        }

        return fields;
    }

    public static long parseTime(String time, TimeUnit defUnit) {
        String unit;
        String number;
        if (time.matches("([0-9]+)([smhdSMHD]$)")) {
            unit = time.replaceAll("([0-9]+)([smhdSMHD]$)", "$2");
            number = time.replaceAll("([0-9]+)([smhdSMHD]$)", "$1");
        } else {
            unit = defUnit == TimeUnit.DAYS ? "d" : (defUnit == TimeUnit.MINUTES ? "m" : (defUnit == TimeUnit.HOURS ? "h" : (defUnit == TimeUnit.SECONDS ? "s" : "none")));
            number = time;
        }

        if (!isInt(number)) {
            return -9223372036854775808L;
        } else {
            int ch = Integer.parseInt(number);
            String var7 = unit.toLowerCase();
            byte var8 = -1;
            switch(var7.hashCode()) {
                case 100:
                    if (var7.equals("d")) {
                        var8 = 3;
                    }
                    break;
                case 104:
                    if (var7.equals("h")) {
                        var8 = 2;
                    }
                    break;
                case 109:
                    if (var7.equals("m")) {
                        var8 = 1;
                    }
                    break;
                case 115:
                    if (var7.equals("s")) {
                        var8 = 0;
                    }
            }

            long result;
            switch(var8) {
                case 0:
                    result = TimeUnit.SECONDS.toMillis(ch);
                    break;
                case 1:
                    result = TimeUnit.MINUTES.toMillis(ch);
                    break;
                case 2:
                    result = TimeUnit.HOURS.toMillis(ch);
                    break;
                case 3:
                    result = TimeUnit.DAYS.toMillis(ch);
                    break;
                default:
                    result = defUnit.toMillis(isInt(time) ? (long)Integer.parseInt(time) : (long)ch);
            }

            return result;
        }
    }

    public static boolean isInt(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException var2) {
            return false;
        }
    }

    public static String translateByLastDigit(int ch, String unit2) {
        byte var4 = -1;
        switch(unit2.hashCode()) {
            case 100:
                if (unit2.equals("d")) {
                    var4 = 3;
                }
                break;
            case 104:
                if (unit2.equals("h")) {
                    var4 = 2;
                }
                break;
            case 109:
                if (unit2.equals("m")) {
                    var4 = 1;
                }
                break;
            case 115:
                if (unit2.equals("s")) {
                    var4 = 0;
                }
        }

        String unit;
        switch(var4) {
            case 0:
                unit = getBias(ch, "секунда", "секунды", "секунд");
                break;
            case 1:
                unit = getBias(ch, "минута", "минуты", "минут");
                break;
            case 2:
                unit = getBias(ch, "час", "часа", "часов");
                break;
            case 3:
                unit = getBias(ch, "день", "дня", "дней");
                break;
            default:
                unit = "мсек.";
        }

        return ch + " " + unit;
    }

    public static void tagTimeBiasInMaxUnit(long time, Message message, String timeTag, String biasTag, String biaslocale) {
        int ch = 0;
        String bias = "";
        if (TimeUnit.MILLISECONDS.toDays(time) > 0L) {
            ch = (int)TimeUnit.MILLISECONDS.toDays(time);
            bias = getBiasFromLocale(ch, biaslocale + ".days");
        } else if (TimeUnit.MILLISECONDS.toHours(time) > 0L) {
            ch = (int)TimeUnit.MILLISECONDS.toHours(time);
            bias = getBiasFromLocale(ch, biaslocale + ".hours");
        } else if (TimeUnit.MILLISECONDS.toMinutes(time) > 0L) {
            ch = (int)TimeUnit.MILLISECONDS.toMinutes(time);
            bias = getBiasFromLocale(ch, biaslocale + ".minutes");
        } else if (TimeUnit.MILLISECONDS.toSeconds(time) > 0L) {
            ch = (int)TimeUnit.MILLISECONDS.toSeconds(time);
            bias = getBiasFromLocale(ch, biaslocale + ".seconds");
        }

        message.tag(timeTag, ch).tag(biasTag, bias);
    }

    public static Message getTimeBiasInMaxUnit(long time, Message message, String timeTag, String biasTag, String biaslocale) {
        int ch = 0;
        String bias = "";
        if (TimeUnit.MILLISECONDS.toDays(time) > 0L) {
            ch = (int)TimeUnit.MILLISECONDS.toDays(time);
            bias = getBiasFromLocale(ch, biaslocale + ".days");
        } else if (TimeUnit.MILLISECONDS.toHours(time) > 0L) {
            ch = (int)TimeUnit.MILLISECONDS.toHours(time);
            bias = getBiasFromLocale(ch, biaslocale + ".hours");
        } else if (TimeUnit.MILLISECONDS.toMinutes(time) > 0L) {
            ch = (int)TimeUnit.MILLISECONDS.toMinutes(time);
            bias = getBiasFromLocale(ch, biaslocale + ".minutes");
        } else if (TimeUnit.MILLISECONDS.toSeconds(time) > 0L) {
            ch = (int)TimeUnit.MILLISECONDS.toSeconds(time);
            bias = getBiasFromLocale(ch, biaslocale + ".seconds");
        }

        message.tag(timeTag, ch).tag(biasTag, bias);
        return message;
    }

    public static String getTimeBiasInMaxUnit(long time, String biaslocale) {
        String bias = "";
        int ch;
        if (TimeUnit.MILLISECONDS.toDays(time) > 0L) {
            ch = (int)TimeUnit.MILLISECONDS.toDays(time);
            bias = getBiasFromLocale(ch, biaslocale + ".days");
        } else if (TimeUnit.MILLISECONDS.toHours(time) > 0L) {
            ch = (int)TimeUnit.MILLISECONDS.toHours(time);
            bias = getBiasFromLocale(ch, biaslocale + ".hours");
        } else if (TimeUnit.MILLISECONDS.toMinutes(time) > 0L) {
            ch = (int)TimeUnit.MILLISECONDS.toMinutes(time);
            bias = getBiasFromLocale(ch, biaslocale + ".minutes");
        } else if (TimeUnit.MILLISECONDS.toSeconds(time) > 0L) {
            ch = (int)TimeUnit.MILLISECONDS.toSeconds(time);
            bias = getBiasFromLocale(ch, biaslocale + ".seconds");
        }

        return bias;
    }

    public static int getMinecraftVersion(Player p) {
        checkDependency("ProtocolLib");
        return ProtocolLibrary.getProtocolManager().getProtocolVersion(p);
    }

    public static List<String> color(Collection<String> s) {
        return s.stream().map(UtilityMethods::color).collect(Collectors.toList());
    }

    public static String color(String s) {
        return ChatColor.translateAlternateColorCodes('&', s);
    }

    public static String stripColor(String s) {
        if (s.contains("&")) {
            s = color(s);
        }

        int length = (s = ChatColor.stripColor(s)).length();

        while(length != (length = (s = ChatColor.stripColor(s)).length())) {
        }

        return s;
    }

    public static ItemStack color(ItemStack is) {
        if (!is.hasItemMeta()) {
            return is;
        } else {
            ItemMeta im = is.getItemMeta();
            if (im.hasDisplayName()) {
                im.setDisplayName(color(im.getDisplayName()));
            }

            if (im.hasLore()) {
                im.setLore(color(im.getLore()));
            }

            is.setItemMeta(im);
            return is;
        }
    }

    public static ItemStack hideAttributes(ItemStack is) {
        if (isItemAir(is)) {
            return is;
        } else {
            ItemMeta im = is.getItemMeta();
            im.addItemFlags(ItemFlag.values());
            is.setItemMeta(im);
            return is;
        }
    }

    public static void freezeWorld(World world) {
        world.setGameRuleValue("doDaylightCycle", "false");
        world.setGameRuleValue("doWeatherCycle", "false");
        world.setGameRuleValue("spectatorsGenerateChunks", "false");
        world.setStorm(false);
        world.setFullTime(0L);
        world.setKeepSpawnInMemory(false);
        world.setPVP(true);
    }

    public static void loadLib(File file, ClassLoader classLoader) throws Throwable {
        URLClassLoader cast = (URLClassLoader)classLoader;
        Method method = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
        method.setAccessible(true);
        List<URL> urls = Arrays.asList(cast.getURLs());
        FileFilter notInClassPath = f -> {
            try {
                return !urls.contains(f.toURI().toURL());
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            }
        };
        if (file.isDirectory()) {
            for (File f : file.listFiles(notInClassPath)) {
                method.invoke(classLoader, new Object[] { f.toURI().toURL() });
            }
        } else if (notInClassPath.accept(file)) {
            method.invoke(classLoader, new Object[] { file.toURI().toURL() });
        }
    }

    public static void loadLib(File file) throws Throwable {
        loadLib(file, ClassLoader.getSystemClassLoader());
    }

    public static void loadLib(File file, Consumer<Throwable> onError) {
        loadLib(file, ClassLoader.getSystemClassLoader(), onError);
    }

    public static void loadLib(File file, ClassLoader loader, Consumer<Throwable> onError) {
        try {
            loadLib(file, loader);
        } catch (Throwable e) {
            onError.accept(e);
        }
    }

    public static void loadLibRethrow(File file) {
        loadLibRethrow(file, ClassLoader.getSystemClassLoader());
    }

    public static void loadLibRethrow(File file, ClassLoader loader) {
        loadLib(file, loader, UtilityMethods::rethrow);
    }

    public static <T extends Throwable> void rethrow(Throwable throwable) throws T {
        throw (T)throwable;
    }

    public static void installLib(Plugin to, boolean force, boolean toSystem, String folder, String... directUrls) {
        for (String directUrl : directUrls) {
            directUrl = directUrl.replace("http://central.maven.org", "https://repo.maven.apache.org");
            String fileName = getFileNameFromURL(directUrl);
            File libFolder = new File(to.getDataFolder(), folder);
            libFolder.mkdirs();
            File file = new File(libFolder, fileName);
            Consumer<Throwable> errorLogger = t -> to.getLogger().log(Level.SEVERE, "Can't inject library " + fileName, t);
            //Consumer<File> onLoad = f -> loadLib(file, toSystem ? ClassLoader.getSystemClassLoader() : to.getClass().getClassLoader(), errorLogger.andThen(UtilityMethods::rethrow));
            Consumer<File> onLoad = f -> loadLib(file, toSystem ? to.getClass().getClassLoader() : to.getClass().getClassLoader(), errorLogger.andThen(UtilityMethods::rethrow));
            if (force || !file.exists()) {
                downloadFile(file, force, directUrl, onLoad);
            } else {
                onLoad.accept(file);
            }
        }
    }

    public static String getFileNameFromURL(String url) {
        String[] split = url.split("/");
        return split[split.length - 1];
    }

    public static void installLib(Plugin to, boolean force, String folder, String... directUrls) {
        installLib(to, force, false, folder, directUrls);
    }


    public static void downloadFile(File target, boolean force, String url, Consumer<File> onload) {
        if (force || !target.exists()) {
            target.getParentFile().mkdirs();
            target.delete();
            CommonLib.INSTANCE.getLogger().info("Downloading " + target.getName() + " from " + url);
            Closer closer = Closer.create();

            try {
                URL u = new URL(url);
                ReadableByteChannel readableBC = closer.register(Channels.newChannel(u.openStream()));
                target.createNewFile();
                FileOutputStream fileOS = closer.register(new FileOutputStream(target));
                fileOS.getChannel().transferFrom(readableBC, 0L, 9223372036854775807L);
                CommonLib.INSTANCE.getLogger().info("Downloaded " + target.getName());
            } catch (IOException var16) {
                var16.printStackTrace();
            } finally {
                try {
                    closer.close();
                } catch (IOException var15) {
                    var15.printStackTrace();
                }

            }
        }

        onload.accept(target);
    }





    /*    public static void loadLib(final File file) throws Throwable {
        final URLClassLoader classLoader = (URLClassLoader)ClassLoader.getSystemClassLoader();
        final Method method = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
        method.setAccessible(true);
        final List<URL> urls = Arrays.asList(classLoader.getURLs());
        final FileFilter notInClassPath = f -> {
            try {
                return !urls.contains(f.toURI().toURL());
            }
            catch (MalformedURLException e) {
                throw new RuntimeException(e);
            }
        };
        if (file.isDirectory()) {
            for (final File f2 : file.listFiles(notInClassPath)) {
                method.invoke(classLoader, f2.toURI().toURL());
            }
        }
        else if (notInClassPath.accept(file)) {
            method.invoke(classLoader, file.toURI().toURL());
        }
    }

    public static void loadLib(final File file, final Consumer<Throwable> onError) {
        try {
            loadLib(file);
        }
        catch (Throwable e) {
            onError.accept(e);
        }
    }

    public static void loadLibRethrow(final File file) {
        loadLib(file, t -> {
            throw new RuntimeException(t);
        });
    }
    public static <T extends Throwable> void rethrow(Throwable throwable) throws Throwable {
        throw throwable;
    }*/



    public static String formatPlayer(String p) {
        return getPrefix(p) + p;
    }

    public static String formatPlayer(Player p) {
        return formatPlayer(p.getName());
    }

    public static String getPrefix(Player p) {
        return getPrefix(p.getName());
    }

    public static String getPrefix(String player) {
        checkDependency("Vault");
        injectPexChat();
        return Optional.ofNullable(Bukkit.getServicesManager().getRegistration(Chat.class)).map(RegisteredServiceProvider::getProvider).map((p) -> {
            return CommonLib.CONF.useNameForVaultQueries() ? p.getPlayerPrefix((String)null, player) : p.getPlayerPrefix(null, FastOfflinePlayer.getWithOfflineUUID(player));
        }).orElse("");
    }

    public static String getSuffix(String player) {
        checkDependency("Vault");
        injectPexChat();
        return Optional.ofNullable(Bukkit.getServicesManager().getRegistration(Chat.class)).map(RegisteredServiceProvider::getProvider).map((p) -> {
            return CommonLib.CONF.useNameForVaultQueries() ? p.getPlayerSuffix((String)null, player) : p.getPlayerSuffix(null, FastOfflinePlayer.getWithOfflineUUID(player));
        }).orElse("");
    }

    public static String getSuffix(Player p) {
        return getSuffix(p.getName());
    }

    public static String getGroupPrefix(String group) {
        checkDependency("Vault");
        injectPexChat();
        return Optional.ofNullable(Bukkit.getServicesManager().getRegistration(Chat.class)).map(RegisteredServiceProvider::getProvider).map((p) -> {
            return p.getGroupPrefix((String)null, group);
        }).orElse("");
    }

    public static String getGroupSuffix(String group) {
        checkDependency("Vault");
        injectPexChat();
        return Optional.ofNullable(Bukkit.getServicesManager().getRegistration(Chat.class)).map(RegisteredServiceProvider::getProvider).map((p) -> {
            return p.getGroupSuffix((String)null, group);
        }).orElse("");
    }

    private static void injectPexChat() {
        RegisteredServiceProvider<Chat> prov = Bukkit.getServicesManager().getRegistration(Chat.class);
        if (has("PermissionsEx") && (prov == null || prov.getProvider() == null)) {
            RegisteredServiceProvider<Permission> perms = Bukkit.getServicesManager().getRegistration(Permission.class);
            Chat CHAT = new Chat_PermissionsEx(perms.getPlugin(), perms.getProvider());
            Bukkit.getServicesManager().register(Chat.class, CHAT, perms.getPlugin(), ServicePriority.Normal);
        }
    }

    public static String centerString(String message) {
        int messagePxSize = 0;
        boolean previousCode = false;
        boolean isBold = false;
        char[] var4 = message.toCharArray();
        int toCompensate = var4.length;

        int spaceLength;
        for(spaceLength = 0; spaceLength < toCompensate; ++spaceLength) {
            char c = var4[spaceLength];
            if (c == 167) {
                previousCode = true;
            } else if (previousCode) {
                previousCode = false;
                isBold = c == 'l' || c == 'L';
            } else {
                DefaultFontInfo dFI = DefaultFontInfo.getDefaultFontInfo(c);
                messagePxSize += isBold ? dFI.getBoldLength() : dFI.getLength();
                ++messagePxSize;
            }
        }

        int halvedMessageSize = messagePxSize / 2;
        toCompensate = 154 - halvedMessageSize;
        spaceLength = DefaultFontInfo.SPACE.getLength() + 1;
        int compensated = 0;

        StringBuilder sb;
        for(sb = new StringBuilder(); compensated < toCompensate; compensated += spaceLength) {
            sb.append(" ");
        }

        sb.append(message);
        return sb.toString();
    }

    public static Object getHandle(Object o) {
        checkDependency("ProtocolLib");
        return Accessors.getMethodAccessor(o.getClass(), "getHandle", new Class[0]).invoke(o);
    }

    public static String getTabPrefix(Player p) {
        checkDependency("Vault");
        injectPexChat();
        RegisteredServiceProvider<Chat> prov = Bukkit.getServicesManager().getRegistration(Chat.class);
        if (prov != null && prov.getProvider() != null) {
            Chat provider = prov.getProvider();
            String option = provider.getPlayerInfoString(p, "tabprefix", null);
            if (option == null) {
                option = provider.getGroupInfoString("", getGroup(p), "tabprefix", null);
            }

            return option == null ? getPrefix(p) : option;
        } else {
            return "";
        }
    }

    public static String getTabSuffix(Player p) {
        checkDependency("Vault");
        injectPexChat();
        RegisteredServiceProvider<Chat> prov = Bukkit.getServicesManager().getRegistration(Chat.class);
        if (prov != null && prov.getProvider() != null) {
            Chat provider = prov.getProvider();
            String option = provider.getPlayerInfoString(p, "tabsuffix", null);
            if (option == null) {
                option = provider.getGroupInfoString("", getGroup(p), "tabsuffix", null);
            }

            return option == null ? getSuffix(p) : option;
        } else {
            return "";
        }
    }

    public static BlockFace locationToBlockFace(Location loc) {
        Vector dir = loc.getDirection();
        float yaw = atan2(dir.getZ(), dir.getX()) - 180.0F;
        return RADIAL[Math.round(yaw / 45.0F) & 7];
    }

    private static float atan2(double y, double x) {
        return 57.29578F * (float)trigmath_atan2(y, x);
    }

    private static double trigmath_atan2(double arg1, double arg2) {
        if (arg1 + arg2 == arg1) {
            return arg1 >= 0.0D ? 1.5707963267948966D : -1.5707963267948966D;
        } else {
            arg1 = atan(arg1 / arg2);
            return arg2 < 0.0D ? (arg1 <= 0.0D ? arg1 + 3.141592653589793D : arg1 - 3.141592653589793D) : arg1;
        }
    }

    private static double atan(double arg) {
        return arg > 0.0D ? msatan(arg) : -msatan(-arg);
    }

    private static double mxatan(double arg) {
        double argsq = arg * arg;
        double value = (((16.15364129822302D * argsq + 268.42548195503974D) * argsq + 1153.029351540485D) * argsq + 1780.406316433197D) * argsq + 896.7859740366387D;
        value /= ((((argsq + 58.95697050844462D) * argsq + 536.2653740312153D) * argsq + 1666.7838148816338D) * argsq + 2079.33497444541D) * argsq + 896.7859740366387D;
        return value * arg;
    }

    private static double msatan(double arg) {
        return arg < 0.41421356237309503D ? mxatan(arg) : (arg > 2.414213562373095D ? 1.5707963267948966D - mxatan(1.0D / arg) : 0.7853981633974483D + mxatan((arg - 1.0D) / (arg + 1.0D)));
    }

    public static void setConsoleFormat(AsyncPlayerChatEvent e, String format) {
        if (e instanceof CustomChatEvent) {
            CustomChatEvent cast = (CustomChatEvent)e;
            cast.setConsoleFormat(format);
        }
    }

    public static String getConsoleFormat(AsyncPlayerChatEvent e) {
        if (!(e instanceof CustomChatEvent)) {
            return e.getFormat();
        } else {
            CustomChatEvent cast = (CustomChatEvent)e;
            return cast.getConsoleFormat();
        }
    }

    public static void checkDependency(String pluginName) {
        if (Bukkit.getPluginManager().getPlugin(pluginName) == null) {
            throw new UnknownDependencyException(pluginName + " is not installed");
        } else {
            Preconditions.checkState(Bukkit.getPluginManager().getPlugin(pluginName).isEnabled(), pluginName + " is not enabled");
        }
    }

    public static void forciblyLoadAllClasses(JavaPlugin plugin) {
        URLClassLoader l = (URLClassLoader)plugin.getClass().getClassLoader();
        URL[] var2 = l.getURLs();
        int var3 = var2.length;

        for(int var4 = 0; var4 < var3; ++var4) {
            URL url = var2[var4];

            try {
                File pluginFile = new File(url.toURI());
                File temp = Files.createTempFile(pluginFile.getName(), ".jar").toFile();
                Files.copy(pluginFile.toPath(), temp.toPath(), StandardCopyOption.REPLACE_EXISTING);
                ArrayList classes = new ArrayList();

                try {
                    ZipFile zip = new ZipFile(temp);
                    Throwable var10 = null;

                    try {
                        Enumeration<ZipEntry> en = (Enumeration<ZipEntry>) zip.entries();

                        while(en.hasMoreElements()) {
                            String name = (en.nextElement()).getName();
                            if (FilenameUtils.getExtension(name).equalsIgnoreCase("class")) {
                                classes.add(FilenameUtils.getBaseName(name.replace('/', '.')));
                            }
                        }
                    } catch (Throwable var29) {
                        var10 = var29;
                        throw var29;
                    } finally {
                        if (zip != null) {
                            if (var10 != null) {
                                try {
                                    zip.close();
                                } catch (Throwable var28) {
                                    var10.addSuppressed(var28);
                                }
                            } else {
                                zip.close();
                            }
                        }

                    }
                } finally {
                    temp.delete();
                }

                classes.forEach((c) -> {
                    try {
                        plugin.getClass().getClassLoader().loadClass(c.toString());
                    } catch (NoClassDefFoundError | ClassNotFoundException ex1) {
                        plugin.getLogger().warning("Could not pre-load " + plugin.getName() + "'s class " + c + ". Cause: " + ex1.getClass().getSimpleName() + ": " + ex1.getMessage());
                    }

                });
            } catch (Exception var32) {
                plugin.getLogger().log(Level.SEVERE, "Exception during force class loading from " + url, var32);
            }
        }

    }

    static {
        RADIAL = new BlockFace[]{BlockFace.WEST, BlockFace.NORTH_WEST, BlockFace.NORTH, BlockFace.NORTH_EAST, BlockFace.EAST, BlockFace.SOUTH_EAST, BlockFace.SOUTH, BlockFace.SOUTH_WEST};
    }
}
