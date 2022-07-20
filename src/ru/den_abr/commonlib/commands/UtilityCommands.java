/*
 * Decompiled with CFR 0.150.
 *
 * Could not load the following classes:
 *  com.comphenix.protocol.reflect.accessors.Accessors
 *  com.comphenix.protocol.utility.MinecraftReflection
 *  com.comphenix.protocol.wrappers.EnumWrappers$Difficulty
 *  com.comphenix.protocol.wrappers.EnumWrappers$NativeGameMode
 *  com.comphenix.protocol.wrappers.WrappedDataWatcher
 *  com.comphenix.protocol.wrappers.WrappedWatchableObject
 *  com.comphenix.protocol.wrappers.nbt.NbtBase
 *  com.comphenix.protocol.wrappers.nbt.NbtCompound
 *  com.comphenix.protocol.wrappers.nbt.NbtFactory
 *  com.google.common.base.Joiner
 *  com.google.common.collect.Iterators
 *  com.google.gson.JsonElement
 *  org.apache.commons.lang3.StringUtils
 *  org.bukkit.Bukkit
 *  org.bukkit.ChatColor
 *  org.bukkit.GameMode
 *  org.bukkit.Material
 *  org.bukkit.Sound
 *  org.bukkit.World
 *  org.bukkit.block.Block
 *  org.bukkit.command.CommandSender
 *  org.bukkit.command.PluginCommand
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.EntityType
 *  org.bukkit.entity.Player
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.inventory.PlayerInventory
 *  org.bukkit.inventory.meta.ItemMeta
 *  org.bukkit.inventory.meta.PotionMeta
 *  org.bukkit.inventory.meta.SkullMeta
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.potion.PotionEffect
 *  org.bukkit.potion.PotionEffectType
 *  org.hjson.JsonValue
 *  org.hjson.Stringify
 */
package ru.den_abr.commonlib.commands;

import clib.com.comphenix.packetwrapper.WrapperPlayServerRespawn;
import com.comphenix.protocol.reflect.accessors.Accessors;
import com.comphenix.protocol.utility.MinecraftReflection;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import com.comphenix.protocol.wrappers.WrappedWatchableObject;
import com.comphenix.protocol.wrappers.nbt.NbtCompound;
import com.comphenix.protocol.wrappers.nbt.NbtFactory;
import com.google.common.base.Joiner;
import com.google.common.collect.Iterators;
import com.google.gson.JsonElement;
import commonlib.com.darkblade12.particleeffect.Particles;
import commonlib.com.minnymin.command.Command;
import commonlib.com.minnymin.command.CommandArgs;
import commonlib.com.minnymin.command.CommandFramework;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.hjson.JsonValue;
import org.hjson.Stringify;
import ru.den_abr.commonlib.CommonLib;
import ru.den_abr.commonlib.boards.packet.VirtualScoreboard;
import ru.den_abr.commonlib.boards.packet.VirtualTeam;
import ru.den_abr.commonlib.menu.DefaultIcon;
import ru.den_abr.commonlib.menu.PaginatedMenu;
import ru.den_abr.commonlib.menu.event.ClickEvent;
import ru.den_abr.commonlib.messages.Message;
import ru.den_abr.commonlib.messages.MessagePosition;
import ru.den_abr.commonlib.messages.MessageRegistry;
import ru.den_abr.commonlib.utility.GsonUtil;
import ru.den_abr.commonlib.utility.ItemLore;
import ru.den_abr.commonlib.utility.UtilityMethods;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class UtilityCommands {
    @Command(name="clib.soundlist", aliases={"sounds"}, permission="commonlib.sounds")
    public void sounds(CommandArgs args) {
        args.sendMessage(StringUtils.join(Sound.values(), ", "));
    }

    @Command(name="clib.debug", permission="commonlib.debug")
    public void debug(CommandArgs args) {
        CommonLib.CONF.setDebug(!CommonLib.CONF.isDebug());
        args.sendMessage("Debug: " + CommonLib.CONF.isDebug());
    }

    @Command(name="clib.actionbar", permission="commonlib.actionbar")
    public void actionbar(CommandArgs args) {
        UtilityMethods.sendActionBar(args.getPlayer(), Joiner.on(' ').join(args.getArgs()));
    }

    @Command(name="clib.vaultbalance")
    public void clibessl(CommandArgs args) {
        args.sendMessage(UtilityMethods.getBalance(args.getPlayer()) + "");
    }

    @Command(name="clib.vaultgroup", permission="commonlib.vaultgroup")
    public void group(CommandArgs args) {
        args.sendMessage(UtilityMethods.getGroup(args.length() > 0 ? args.getArgs(0) : args.getName()));
    }

    @Command(name="clib.metadata", permission="commonlib.metadata")
    public void metadata(CommandArgs args) {
        WrappedDataWatcher watcher = WrappedDataWatcher.getEntityWatcher(args.getPlayer());
        watcher.getWatchableObjects().stream().sorted(Comparator.comparingInt(WrappedWatchableObject::getIndex)).forEach(o -> args.sendMessage(o.getIndex() + ". " + o.getValue()));
    }

    @Command(name="clib.sendmsg", permission="commonlib.sendmsg")
    public void sendMsg(CommandArgs args) {
        args.checkArgs(1, "/clib sendmsg KEY POS TAGS...");
        String[] keys = args.getArgs(0).split(";");
        MessagePosition pos = args.length() > 1 ? MessagePosition.valueOf(args.getArgs(1).toUpperCase()) : MessagePosition.CHAT;
        Map<String, String> tags = Arrays.stream(args.getArgs()).skip(2L).map(s -> s.split("=")).collect(Collectors.toMap(k -> k[0], v -> v[1]));
        for (String key : keys) {
            Message mes = MessageRegistry.message(key);
            mes.putTags(tags);
            mes.send(args.getPlayer(), pos);
        }
    }

    @Command(name="clib.addeffect", aliases={"addpotioneffect"}, permission="commonlib.addeffect", inGameOnly=true)
    public void addeffect(CommandArgs args) {
        Player p = args.getPlayer();
        if (args.length() < 3) {
            args.sendMessage("/addpotioneffect EFFECT ДЛИТЕЛЬНОСТЬ УРОВЕНЬ");
            args.sendMessage("Эффекты: " + Joiner.on(", ").join(Arrays.stream(PotionEffectType.values()).filter(Objects::nonNull).map(PotionEffectType::getName).map(String::toLowerCase).collect(Collectors.toList())));
            return;
        }
        if (p.getItemInHand() == null || p.getItemInHand().getType() == Material.AIR || !(p.getItemInHand().getItemMeta() instanceof PotionMeta)) {
            p.sendMessage("Возьми зелье в руку");
            return;
        }
        ItemStack item = p.getItemInHand();
        PotionMeta meta = (PotionMeta)item.getItemMeta();
        PotionEffectType effect = PotionEffectType.getByName(args.getArgs(0));
        int duration = Integer.parseInt(args.getArgs(1)) * 20;
        int amplifier = Integer.parseInt(args.getArgs(2)) - 1;
        PotionEffect newEffect = new PotionEffect(effect, duration, amplifier, true, true);
        meta.addCustomEffect(newEffect, true);
        item.setItemMeta(meta);
        p.setItemInHand(item);
    }

    @Command(name="clib.head", permission="commonlib.head")
    public void head(CommandArgs args) {
        ItemStack skull = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
        SkullMeta meta = (SkullMeta)skull.getItemMeta();
        meta.setOwner(args.getArgs(0));
        skull.setItemMeta(meta);
        args.getPlayer().getInventory().addItem(skull);
    }

    @Command(name="clib.particle", permission="commonlib.particle")
    public void particle(CommandArgs args) {
        Particles particles = Particles.valueOf(args.getArgs(0));
        particles.display(0.0f, 0.0f, 0.0f, 0.0f, 1, args.getPlayer().getEyeLocation(), 1000.0);
    }

    @Command(name="clib.stand", aliases={"setstand"}, permission="commonlib.addeffect", inGameOnly=true)
    public void armorStand(CommandArgs args) {
        if (args.length() == 0) {
            args.sendMessage("Укажи имя стенда");
            return;
        }
        if (args.getPlayer().getItemInHand() == null || args.getPlayer().getItemInHand().getType() != Material.ARMOR_STAND) {
            args.sendMessage("Возьми стенд в руку");
            return;
        }
        ItemStack itemInhand = args.getPlayer().getItemInHand();
        NbtCompound nbt = (NbtCompound)NbtFactory.fromItemTag(itemInhand);
        NbtCompound tag = nbt.getCompoundOrDefault("EntityTag");
        tag.put("CustomNameVisible", 1);
        tag.put("NoGravity", 1);
        tag.put("CustomName", Joiner.on(' ').join(args.getArgs()));
        nbt.put("EntityTag", tag);
        NbtFactory.setItemTag(itemInhand, nbt);
        args.getPlayer().setItemInHand(itemInhand);
    }

    @Command(name="clib.seteffect", aliases={"setpotioneffect"}, permission="commonlib.addeffect", inGameOnly=true)
    public void seteffect(CommandArgs args) {
        Player p = args.getPlayer();
        if (args.length() == 0) {
            args.sendMessage("/addpotioneffect EFFECT");
            args.sendMessage("Эффекты: " + Joiner.on(", ").join(Arrays.stream(PotionEffectType.values()).filter(eff -> eff != null).map(PotionEffectType::getName).map(String::toLowerCase).collect(Collectors.toList())));
            return;
        }
        if (p.getItemInHand() == null || p.getItemInHand().getType() == Material.AIR || !(p.getItemInHand().getItemMeta() instanceof PotionMeta)) {
            p.sendMessage("Возьми зелье в руку");
            return;
        }
        ItemStack item = p.getItemInHand();
        PotionMeta meta = (PotionMeta)item.getItemMeta();
        PotionEffectType effect = PotionEffectType.getByName(args.getArgs(0));
        meta.setMainEffect(effect);
        item.setItemMeta(meta);
        p.setItemInHand(item);
    }

    @Command(name="clib.sudo", permission="commonlib.experimental")
    public void sudo(CommandArgs args) {
        Player p = Bukkit.getPlayer(args.getArgs(0));
        String mes = args.getArgs(1);
        if (mes.startsWith("/")) {
            Bukkit.dispatchCommand(p, mes.substring(1));
        } else {
            p.chat(mes);
        }
    }

    @Command(name="clib.removestands", permission="commonlib.experimental")
    public void wih(CommandArgs args) {
        Player p = args.getPlayer();
        World world = p.getWorld();
        int distance = Integer.parseInt(args.getArgs(0));
        for (Entity ent : world.getEntities()) {
            if (ent.getType() != EntityType.ARMOR_STAND || !(ent.getLocation().distance(p.getLocation()) < (double)distance)) continue;
            ent.remove();
        }
    }

    @Command(name="clib.skin", permission="commonlib.experimental")
    public void skin(CommandArgs args) {
        Iterator it = Iterators.cycle((Iterable)Bukkit.getOnlinePlayers());
        AtomicInteger index = new AtomicInteger(Integer.parseInt(args.getArgs(0)));
        Bukkit.getScheduler().runTaskTimer(CommonLib.INSTANCE, () -> {
            Player next = (Player)it.next();
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "skin set " + next.getName() + " " + index.incrementAndGet());
        }, 30L, 30L);
    }

    @Command(name="clib.respawn", permission="commonlib.experimental")
    public void respawn(CommandArgs args) {
        Player p = args.getPlayer();
        WrapperPlayServerRespawn respawn = new WrapperPlayServerRespawn();
        respawn.setDifficulty(EnumWrappers.Difficulty.valueOf(p.getWorld().getDifficulty().name()));
        respawn.setDimension(this.getDimension(p.getWorld()));
        respawn.setLevelType(p.getWorld().getWorldType());
        respawn.setGamemode(EnumWrappers.NativeGameMode.fromBukkit(p.getGameMode()));
        respawn.sendPacket(p);
        p.teleport(p);
        p.updateInventory();
        p.getInventory().setHeldItemSlot(p.getInventory().getHeldItemSlot());
    }

    @Command(name="clib.protocol", permission="commonlib.protocol")
    public void protocol(CommandArgs args) {
        args.sendMessage(UtilityMethods.getProtocol(args.length() > 0 ? args.checkOnline(args.getArgs(0), (Message)null) : args.getPlayer()) + "");
    }

    private int getDimension(World world) {
        Object worldHandle = Accessors.getMethodAccessor(MinecraftReflection.getCraftWorldClass(), "getHandle", new Class[0]).invoke(world);
        return (Integer)Accessors.getFieldAccessor(MinecraftReflection.getWorldServerClass(), "dimension", true).get(worldHandle);
    }

    @Command(name="clib.watcher", permission="commonlib.experimental")
    public void watcher(CommandArgs args) {
        Player p = Bukkit.getPlayer(args.getArgs(0));
        WrappedDataWatcher watcher = WrappedDataWatcher.getEntityWatcher(p);
        System.out.println("===================");
        watcher.asMap().forEach((k, v) -> System.out.println(k + ": " + v.getWatcherObject()));
    }

    @Command(name="clib.clearchat", aliases={"clearchat"}, inGameOnly=true, permission="commonlib.clearchat")
    public void clearChat(CommandArgs args) {
        for (int i = 0; i < 100; ++i) {
            args.sendMessage("");
        }
    }

    @Command(name="clib.serializeinv", aliases={"serializeinv"}, permission="commonlib.serialize.inventory", inGameOnly=true)
    public void serinv(CommandArgs args) throws IOException {
        if (args.length() == 0) {
            args.sendMessage("Укажи имя файла для записи");
            return;
        }
        File file = new File(CommonLib.INSTANCE.getDataFolder(), "serialized" + File.separator + args.getArgs(0) + "_inv.json");
        file.getParentFile().mkdirs();
        Player p = args.getPlayer();
        ArrayList<ItemLore> item = new ArrayList<ItemLore>();
        for (ItemStack inv : p.getInventory().getContents()) {
            if (inv == null || inv.getType() == Material.AIR) continue;
            item.add(new ItemLore(inv));
        }
        for (ItemStack inv : p.getInventory().getArmorContents()) {
            if (inv == null || inv.getType() == Material.AIR) continue;
            item.add(new ItemLore(inv));
        }
        GsonUtil.writeAsJsonToFile(file, item, args.length() > 1 ? GsonUtil.Format.HJSON : GsonUtil.Format.JSON);
        args.sendMessage("Готово");
    }

    @Command(name="clib.deserializeitem", permission="commonlib.deserialize", inGameOnly=true)
    public void deserialize(CommandArgs args) {
        args.checkArgs(1, "Укажи имя файла для записи");
        File file = new File(CommonLib.INSTANCE.getDataFolder(), "serialized" + File.separator + args.getArgs(0) + ".json");
        args.checkState(file.exists(), "Файл не найден");
        JsonElement el = GsonUtil.GSON.fromJson(JsonValue.readHjson(GsonUtil.readFile(file)).toString(Stringify.PLAIN), JsonElement.class);
        (el.isJsonArray() ? StreamSupport.stream(el.getAsJsonArray().spliterator(), false) : Stream.of(el)).map(e -> GsonUtil.GSON.fromJson(e, ItemLore.class)).map(ItemLore::toItem).forEach(arg_0 -> UtilityCommands.lambda$deserialize$8(args.getPlayer().getInventory(), arg_0));
        args.sendMessage("Вещи добавлены в инвентарь");
    }

    @Command(name="clib.serialize", aliases={"serializeitem"}, permission="commonlib.serialize.item", inGameOnly=true)
    public void seritem(CommandArgs args) throws IOException {
        if (args.length() == 0) {
            args.sendMessage("Укажи имя файла для записи");
            return;
        }
        File file = new File(CommonLib.INSTANCE.getDataFolder(), "serialized" + File.separator + args.getArgs(0) + ".json");
        file.getParentFile().mkdirs();
        Player p = args.getPlayer();
        if (p.getItemInHand() == null || p.getItemInHand().getType() == Material.AIR) {
            p.sendMessage("Возьми предмет в руку");
            return;
        }
        ItemLore il = new ItemLore(p.getItemInHand());
        GsonUtil.writeAsJsonToFile(file, il, args.length() > 1 ? GsonUtil.Format.HJSON : GsonUtil.Format.JSON);
        args.sendMessage("Готово");
    }

    @Command(name="clib.iteminfo", permission="commonlib.iteminfo", inGameOnly=true)
    public void iteminfo(CommandArgs args) {
        ItemStack item = args.getPlayer().getItemInHand();
        if (item == null) {
            item = new ItemStack(Material.AIR);
        }
        args.sendMessage(ItemLore.getId(item) + " (" + item.getDurability() + " | " + item.getData().getData() + " | " + item.getType().getMaxDurability() + ")");
        args.sendMessage("Meta: " + (item.getItemMeta() != null ? item.getItemMeta().getClass().getSimpleName() : "null"));
    }

    @Command(name="clib.blockinfo", permission="commonlib.blockinfo", inGameOnly=true)
    public void blockinfo(CommandArgs args) {
        Block item = args.getPlayer().getTargetBlock(null, 6);
        args.checkNotNull(item, ChatColor.RED + "Блок не найден");
        args.sendMessage(item.getType() + ":" + item.getData());
        args.sendMessage("State: " + item.getState());
    }

    @Command(name="clib.skull", permission="commonlib.skull")
    public void skull(CommandArgs args) {
        ItemStack skull = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
        SkullMeta meta = (SkullMeta)skull.getItemMeta();
        meta.setOwner(args.getArgs(0));
        skull.setItemMeta(meta);
        args.getPlayer().getInventory().addItem(skull);
    }

    @Command(name="clib.sbteam", permission="commonlib.sbteam")
    public void sbteam(CommandArgs args) {
        VirtualScoreboard vsb = CommonLib.getMainScoreboard().getHandle();
        VirtualTeam team = vsb.getTeam(args.getArgs(0));
        if (team != null) {
            args.sendMessage(team.getName());
            args.sendMessage(team.isForNameTags() + "");
            args.sendMessage(team.getPrefix());
            args.sendMessage(team.getSuffix());
            args.sendMessage(team.getPlayers().stream().map(op -> op.getUniqueId() + ":" + op.getName()).collect(Collectors.joining(", ")));
            args.sendMessage(String.join(", ", team.getEntries()));
        }
    }

    @Command(name="clib.sbteams", permission="commonlib.sbteams")
    public void sbteams(CommandArgs args) {
        VirtualScoreboard vsb = CommonLib.getMainScoreboard().getHandle();
        args.sendMessage(Joiner.on(", ").join(vsb.getTeams().stream().map(VirtualTeam.class::cast).map(t -> t.getName() + "(" + t.isForNameTags() + ")").collect(Collectors.toList())));
    }

    @Command(name="clib.goto", permission="commonlib.goto")
    public void gotoWorld(CommandArgs args) {
        if (args.length() == 0) {
            args.sendMessage("Укажи имя мира");
            return;
        }
        World world = Bukkit.getWorld(args.getArgs(0));
        if (world == null) {
            world = UtilityMethods.loadWorld(args.getArgs(0), true);
        }
        args.getPlayer().teleport(world.getSpawnLocation());
    }

    @Command(name="clib.move", permission="commonlib.move")
    public void moveToWorld(CommandArgs args) {
        World world;
        Player p;
        if (args.length() < 2) {
            args.halt("Укажи игрока и имя мира");
        }
        if ((p = Bukkit.getPlayerExact(args.getArgs(0))) == null) {
            args.halt(ChatColor.RED + "Игрок не найден");
        }
        if ((world = Bukkit.getWorld(args.getArgs(1))) == null) {
            world = UtilityMethods.loadWorld(args.getArgs(1), true);
        }
        p.teleport(world.getSpawnLocation());
    }

    @Command(name="clib.pagedmenu", permission="commonlib.tests")
    public void pageTest(CommandArgs args) {
        int pageSize = Integer.parseInt(args.getArgs(0));
        int items = Integer.parseInt(args.getArgs(1));
        final PaginatedMenu menu = new PaginatedMenu(pageSize, 21, 23){

            @Override
            public String getTitle(Player player, int page) {
                return "Page " + page;
            }

            @Override
            public ItemLore getPageForward(Player player, int page) {
                return new ItemLore(Material.ARROW);
            }

            @Override
            public ItemLore getPageBack(Player player, int page) {
                return new ItemLore(Material.FEATHER);
            }
        };
        menu.setRows(3);
        menu.setItems(IntStream.range(0, items).mapToObj(i -> new DefaultIcon(ItemLore.create(Material.WOOD, 1, "Item " + i, new String[0])){

            @Override
            public boolean onClick(ClickEvent p) {
                p.getPlayer().sendMessage("Clicked " + i);
                p.close(false);
                menu.getItems().remove(this);
                menu.refreshAll();
                return false;
            }
        }).collect(Collectors.toList()));
        menu.open(args.getPlayer());
    }

    @Command(name="clib.cmdinfo", aliases={"commandinf"}, permission="commonlib.cmdinfo")
    public void cmdinfo(CommandArgs args) {
        if (args.length() == 0) {
            args.sendMessage(ChatColor.RED + "Specify command");
            return;
        }
        PluginCommand cmd = Bukkit.getPluginCommand(args.getArgs(0).toLowerCase());
        if (cmd == null) {
            args.sendMessage(ChatColor.RED + "Command " + args.getArgs(0) + " not found");
            return;
        }
        args.sendMessage(ChatColor.GREEN + "Original command: " + ChatColor.GOLD + cmd.getName());
        if (cmd.getExecutor() instanceof CommandFramework) {
            args.sendMessage(ChatColor.GREEN + "Parent name: " + ChatColor.GOLD + ((CommandFramework)cmd.getExecutor()).getExecutor(cmd.getName()).getName());
        }
        args.sendMessage(ChatColor.GREEN + "Plugin: " + ChatColor.GOLD + cmd.getPlugin().getName());
        args.sendMessage(ChatColor.GREEN + "Aliases: " + ChatColor.GOLD + Joiner.on(", ").join(cmd.getAliases()));
        args.sendMessage(ChatColor.GREEN + "Type: " + ChatColor.GOLD + cmd.getClass().getSimpleName());
        args.sendMessage(ChatColor.GREEN + "Executor class: " + ChatColor.GOLD + cmd.getExecutor().getClass().getName());
    }

    @Command(name="clib.reloadconf", permission="commonlib.reloadconf")
    public void reloadConfig(CommandArgs args) {
        CommonLib.CONF.reload();
        args.sendMessage(ChatColor.GREEN + "Reloaded");
    }

    private static /* synthetic */ void lambda$deserialize$8(PlayerInventory rec$, ItemStack xva$0) {
        rec$.addItem(xva$0);
    }
}

