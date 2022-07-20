package ru.den_abr.commonlib.utility;

import com.comphenix.protocol.reflect.accessors.Accessors;
import com.comphenix.protocol.utility.MinecraftReflection;
import com.comphenix.protocol.utility.MinecraftVersion;
import com.comphenix.protocol.wrappers.nbt.NbtCompound;
import com.comphenix.protocol.wrappers.nbt.NbtFactory;
import com.comphenix.protocol.wrappers.nbt.NbtWrapper;
import com.google.common.base.Preconditions;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.stream.Collectors;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Material;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;
import org.bukkit.util.NumberConversions;
import ru.den_abr.commonlib.CommonLib;
import ru.den_abr.commonlib.messages.Message;
import ru.den_abr.commonlib.messages.MessageKey;

public class ItemLore implements Cloneable {
    private String id;

    private int amount;

    private String durabilityRange;

    private Boolean glowing;

    private Boolean hideAttributes;

    private Map<String, Object> meta;

    public ItemLore(Material material) {
        this(material, 1);
    }

    public ItemLore(Material material, int amount) {
        this(material, amount, (Map<String, Object>)null);
    }

    public ItemLore(Material material, int amount, Map<String, Object> meta) {
        this(material.name(), amount, meta);
    }

    public ItemLore(String id) {
        this(id, 1);
    }

    public ItemLore(String id, int amount) {
        this(id, 1, (Map<String, Object>)null);
    }

    public ItemLore(String id, int amount, Map<String, Object> meta) {
        this(id, amount, null, null, meta);
    }

    public ItemLore(String id, int amount, String durabilityRange, Boolean glowing, Map<String, Object> meta) {
        this.id = id;
        this.amount = amount;
        this.durabilityRange = durabilityRange;
        setGlowing(glowing);
        this.meta = (meta == Collections.EMPTY_MAP) ? null : meta;
    }

    public ItemLore(ItemStack is) {
        this.id = getId(is);
        if (is.hasItemMeta()) {
            ItemMeta im = is.getItemMeta();
            this.meta = (Map<String, Object>)GsonUtil.GSON.fromJson(GsonUtil.GSON.toJson(deepSerialize(im.serialize())), Map.class);
            if (this.meta.containsKey("color") &&
                    getColor((Map<String, Number>)this.meta.get("color")) == null)
                this.meta.remove("color");
            if (im.getItemFlags().size() == (ItemFlag.values()).length) {
                this.hideAttributes = Boolean.valueOf(true);
                this.meta.remove("ItemFlags");
            }
            this.meta.remove("meta-type");
            if (this.meta.isEmpty())
                this.meta = null;
        }
        if (is.getType() != Material.POTION && is.getDurability() != is.getData().getData())
            this.durabilityRange = String.valueOf(is.getDurability());
        setGlowing(Boolean.valueOf(is.containsEnchantment(EmptyEnchantment.EMPTY)));
        this.amount = is.getAmount();
    }

    private Map<String, Object> deepSerialize(Map<String, Object> serialize) {
        Map<String, Object> map = new HashMap<>();
        serialize.forEach((k, v) -> {
            if (v instanceof Map) {
                map.put(k, deepSerialize((Map<String, Object>)v));
            } else if (v instanceof Collection) {
                List<?> list = new ArrayList(deepSerialize((Collection<Object>)v));
                map.put(k, list);
            } else if (v instanceof ConfigurationSerializable) {
                map.put(k, deepSerialize(((ConfigurationSerializable)v).serialize()));
            } else {
                map.put(k, v);
            }
        });
        return map;
    }

    public void setHideAttributes(Boolean hideAttributes) {
        this.hideAttributes = hideAttributes;
    }

    public boolean isHideAttributes() {
        return (this.hideAttributes != null && this.hideAttributes.booleanValue());
    }

    private List<Object> deepSerialize(Collection<Object> v) {
        return (List<Object>)v.stream().map(val -> (val instanceof ConfigurationSerializable) ? deepSerialize(((ConfigurationSerializable)val).serialize()) : ((val instanceof Collection) ? deepSerialize((Collection<Object>)val) : val))

                .collect(Collectors.toList());
    }

    public ItemStack toItem(int amount) {
        ItemStack is = parseItemWithData(this.id, amount);
        if (is == null || is.getType() == Material.AIR)
            return is;
        if (this.durabilityRange != null && !this.durabilityRange.isEmpty())
            if (this.durabilityRange.contains(":")) {
                short maxDur = is.getType().getMaxDurability();
                String[] rangeString = this.durabilityRange.split(":");
                int minBound = Integer.parseInt(rangeString[0]);
                int maxBound = (rangeString.length == 1) ? maxDur : Integer.parseInt(rangeString[1]);
                int damage = 100 - ThreadLocalRandom.current().nextInt(minBound, maxBound + 1);
                is.setDurability((short)(maxDur * damage / 100));
            } else {
                is.setDurability(Short.parseShort(this.durabilityRange));
            }
        Optional<Map<String, Object>> meta = getMeta();
        if (meta.isPresent()) {
            ItemMeta im = is.getItemMeta();
            deserializeMeta(meta.get(), im, this.glowing);
            is.setItemMeta(im);
        } else if (this.glowing == Boolean.TRUE) {
            is.addUnsafeEnchantment(EmptyEnchantment.EMPTY, 1);
        }
        if (isHideAttributes())
            UtilityMethods.hideAttributes(is);
        return is;
    }

    public String getDisplayName() {
        return getDisplayName(false);
    }

    public String getDisplayName(boolean color) {
        return getMeta().map(m -> (String)this.meta.getOrDefault("display-name", this.id)).map(l -> color ? UtilityMethods.color(l) : l)

                .orElse(this.id);
    }

    public List<String> getLore() {
        return getLore(false);
    }

    public List<String> getLore(boolean color) {
        return (List)this.getMeta().map((m) -> {
            return (List)this.meta.get("lore");
        }).map((l) -> {
            return color ? UtilityMethods.color(l) : l;
        }).orElse(Collections.emptyList());
    }

    public void setDisplayName(String name) {
        getOrCreateMeta().put("display-name", name);
    }

    public void setDisplayName(Message name) {
        setDisplayName(name.getAsString());
    }

    public void setDisplayName(MessageKey name) {
        setDisplayName(name.message());
    }

    public void setLore(List<String> lore) {
        getOrCreateMeta().put("lore", lore);
    }

    public void setLore(Message lore) {
        setLore(lore.getAsStringList());
    }

    public void setLore(MessageKey lore) {
        setLore(lore.message());
    }

    public Map<String, Object> getOrCreateMeta() {
        if (this.meta == null)
            this.meta = new HashMap<>();
        return this.meta;
    }

    private Optional<Map<String, Object>> getMeta() {
        return Optional.ofNullable(this.meta);
    }

    public void setDurabilityRange(String durabilityRange) {
        this.durabilityRange = durabilityRange;
    }

    public void setDurability(int dur) {
        setDurabilityRange(String.valueOf(dur));
    }

    public static String getId(ItemStack is) {
        if (MinecraftVersion.getCurrentVersion().getMinor() < 13) {
            if (is.getType() == Material.POTION)
                return is.getType().name() + ((is.getDurability() != 0) ? (":" + is.getDurability()) : "");
            return is.getType() + ((is.getData().getData() != 0) ? (":" + is.getData().getData()) : "");
        }
        return is.getType().name();
    }

    public static void deserializeMeta(Map<String, Object> map, ItemMeta im, Boolean glow) {
        if (map.containsKey("internal")) {
            String base64 = (String)map.get("internal");
            ByteArrayInputStream data = new ByteArrayInputStream(Base64.getDecoder().decode(base64));
            try {
                Object handle = MinecraftReflection.getNbtCompressedStreamToolsClass().getDeclaredMethod("a", new Class[] { InputStream.class }).invoke((Object)null, new Object[] { data });
                NbtCompound nbt = NbtFactory.fromNMSCompound(handle);
                Accessors.getFieldAccessor(im.getClass(), "internalTag", true).set(im, handle);
                Map<String, Object> unhandledTags = getUnhandledTags(im);
                nbt.getKeys().forEach(key -> unhandledTags.put(key, ((NbtWrapper)nbt.getValue(key)).getHandle()));
                Accessors.getMethodAccessor(im.getClass(), "deserializeInternal", new Class[] { handle.getClass() }).invoke(im, new Object[] { handle });
            } catch (IllegalAccessException|IllegalArgumentException|java.lang.reflect.InvocationTargetException|NoSuchMethodException|SecurityException e) {
                CommonLib.INSTANCE.getLogger().log(Level.SEVERE, "Cannot read nbt for " + im + " " + base64, e);
            }
        }
        map.forEach((key, v) -> {
            try {
                Map<String, Number> colors;
                Color color;
                String name;
                int power;
                SkullMeta sm;
                ItemFlag[] flags;
                List<Map<String, Object>> patterns;
                BannerMeta bannerMeta;
                switch (key.toLowerCase()) {
                    case "display-name":
                        im.setDisplayName((String)v);
                        break;
                    case "lore":
                        im.setLore((List)v);
                        break;
                    case "enchants":
                        for (Map.Entry<String, Number> enchants : (Iterable<Map.Entry<String, Number>>)((Map)v).entrySet()) {
                            Enchantment ench = Enchantment.getByName(enchants.getKey());
                            if (ench == null)
                                continue;
                            im.addEnchant(ench, ((Number)enchants.getValue()).intValue(), true);
                        }
                        break;
                    case "stored-enchants":
                        if (im instanceof EnchantmentStorageMeta) {
                            EnchantmentStorageMeta esm = (EnchantmentStorageMeta)im;
                            for (Map.Entry<String, Number> enchants : (Iterable<Map.Entry<String, Number>>)((Map)v).entrySet()) {
                                Enchantment ench = Enchantment.getByName(enchants.getKey());
                                if (ench == null)
                                    continue;
                                esm.addStoredEnchant(ench, ((Number)enchants.getValue()).intValue(), true);
                            }
                        }
                        break;
                    case "title":
                        if (im instanceof BookMeta)
                            ((BookMeta)im).setTitle((String)v);
                        break;
                    case "author":
                        if (im instanceof BookMeta)
                            ((BookMeta)im).setAuthor((String)v);
                        break;
                    case "pages":
                        if (im instanceof BookMeta)
                            ((BookMeta)im).setPages((List)v);
                        break;
                    case "color":
                        colors = (Map<String, Number>)v;
                        color = getColor(colors);
                        if (color != null)
                            invokeMethod(im, "setColor", new Class[] { Color.class }, new Object[] { color });
                        break;
                    case "custom-effects":
                        for (Map<String, Object> effect : (Iterable<Map<String, Object>>)v) {
                            String typeName = (String)effect.get("type");
                            Number typeId = (Number)effect.get("effect");
                            PotionEffectType type = (typeName != null) ? PotionEffectType.getByName(typeName) : ((typeId != null) ? PotionEffectType.getById(typeId.intValue()) : null);
                            if (type == null)
                                break;
                            boolean ambient = ((Boolean)effect.get("ambient")).booleanValue();
                            boolean particles = ((Boolean)effect.get("has-particles")).booleanValue();
                            int amplifier = NumberConversions.toInt(effect.get("amplifier"));
                            int duration = NumberConversions.toInt(effect.get("duration"));
                            PotionEffect potEffect = new PotionEffect(type, duration, amplifier, ambient, particles);
                            ((PotionMeta)im).addCustomEffect(potEffect, true);
                        }
                        break;
                    case "potion-type":
                        name = (String)v;
                        if (name.contains(":"))
                            name = name.split(":")[1];
                        try {
                            Object data = Accessors.getMethodAccessor(MinecraftReflection.getCraftBukkitClass("potion.CraftPotionUtil"), "toBukkit", new Class[] { String.class }).invoke(null, new Object[] { name });
                            Accessors.getMethodAccessor(im.getClass(), "setBasePotionData", new Class[] { data.getClass() }).invoke(im, new Object[] { data });
                        } catch (Exception e) {
                            ((PotionMeta)im).setMainEffect(PotionType.valueOf(name.toUpperCase()).getEffectType());
                        }
                        break;
                    case "firework-effects":
                        for (Map<String, Object> effect : (Iterable<Map<String, Object>>)v) {
                            boolean flicker = ((Boolean)effect.get("flicker")).booleanValue();
                            boolean trail = ((Boolean)effect.get("trail")).booleanValue();
                            FireworkEffect.Type type = FireworkEffect.Type.valueOf(effect.get("type").toString().toUpperCase());
                            List<Color> colors1 = new LinkedList<>();
                            for (Map<String, Number> col : (Iterable<Map<String, Number>>)effect.get("colors"))
                                colors1.add(getColor(col));
                            List<Color> fadeColors = new LinkedList<>();
                            Collection<Map<String, Number>> colorsCol = (Collection<Map<String, Number>>)effect.getOrDefault("fade_colors", effect.get("fade-colors"));
                            for (Map<String, Number> col : colorsCol)
                                fadeColors.add(getColor(col));
                            FireworkEffect fe = FireworkEffect.builder().with(type).withColor(colors1).withFade(fadeColors).flicker(flicker).trail(trail).build();
                            ((FireworkMeta)im).addEffect(fe);
                        }
                        break;
                    case "power":
                        power = NumberConversions.toInt(v);
                        invokeMethod(im, "setPower", new Class[] { int.class }, new Object[] { Integer.valueOf(power) });
                        break;
                    case "skull-owner":
                        sm = (SkullMeta)im;
                        sm.setOwner((String)v);
                        break;
                    case "id":
                        if (im.getClass().getSimpleName().equals("CraftMetaSpawnEgg")) {
                            String typeName = String.valueOf(v);
                            EntityType type = EntityType.fromName(typeName);
                            Accessors.getMethodAccessor(im.getClass(), "setSpawnedType", new Class[] { EntityType.class }).invoke(im, new Object[] { type });
                        }
                        break;
                    case "itemflags":
                        flags = (ItemFlag[])((Collection)v).stream().map(o -> ItemFlag.valueOf(String.valueOf(o))).toArray(ItemFlag[]::new);
                        im.addItemFlags(flags);
                        break;
                    case "patterns":
                        patterns = (List<Map<String, Object>>)v;
                        bannerMeta = (BannerMeta)im;
                        patterns.stream().map(org.bukkit.block.banner.Pattern::new).forEach(bannerMeta::addPattern);
                        break;
                }
            } catch (Exception e) {
                CommonLib.INSTANCE.getLogger().log(Level.SEVERE, "Cannot parse meta '" + key + "' = " + v, e);
            }
        });
        if (glow == Boolean.TRUE)
            im.addEnchant(EmptyEnchantment.EMPTY, 1, true);
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public int getAmount() {
        return this.amount;
    }

    public String getRawId() {
        return this.id;
    }

    public Material getType() {
        return Material.matchMaterial(this.id.contains(":") ? this.id.split(":")[0] : this.id);
    }

    public String getId() {
        return getId(toItem());
    }

    public ItemStack toItem() {
        return toItem(this.amount);
    }

    public void setGlowing(Boolean glowing) {
        this.glowing = (glowing == Boolean.TRUE) ? Boolean.TRUE : null;
    }

    public ItemLore clone() {
        ItemLore clone = null;
        try {
            clone = (ItemLore)super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
        if (this.meta != null)
            clone.meta = new HashMap<>(this.meta);
        return clone;
    }

    public static Color getColor(Map<String, Number> map) {
        int red = ((Number)map.get(map.containsKey("red") ? "red" : "RED")).intValue();
        int green = ((Number)map.get(map.containsKey("green") ? "green" : "GREEN")).intValue();
        int blue = ((Number)map.get(map.containsKey("blue") ? "blue" : "BLUE")).intValue();
        if (red < 0 || green < 0 || blue < 0) {
            CommonLib.INSTANCE.getLogger().warning("Wrong color map " + map);
            return null;
        }
        return Color.fromRGB(red, green, blue);
    }

    public static Map<String, Object> getUnhandledTags(ItemMeta meta) {
        return (Map<String, Object>)Accessors.getFieldAccessor(meta.getClass(), "unhandledTags", true).get(meta);
    }

    public static Object invokeMethod(Object o, String m, Class<?>[] params, Object... args) {
        try {
            Method meth = o.getClass().getDeclaredMethod(m, params);
            meth.setAccessible(true);
            return meth.invoke(o, args);
        } catch (NoSuchMethodException|SecurityException|IllegalAccessException|IllegalArgumentException|java.lang.reflect.InvocationTargetException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static ItemStack parseItemWithData(String in, int amount) {
        ItemStack is = null;
        String[] item = in.split(":");
        Material mat = Material.matchMaterial(item[0]);
        if (mat == null) {
            CommonLib.INSTANCE.getLogger().warning("Unknown item id " + in);
            return new ItemStack(Material.AIR);
        }
        if (item.length == 2) {
            if (mat != Material.POTION && MinecraftVersion.getCurrentVersion().getMinor() < 13) {
                is = new ItemStack(mat, amount, mat.getMaxDurability(), Byte.valueOf((byte)Integer.parseInt(item[1])));
            } else {
                is = new ItemStack(mat, amount, (short)Integer.parseInt(item[1]));
            }
        } else {
            is = new ItemStack(mat, amount);
        }
        return is;
    }

    public static ItemLore create(Material material, int amount, MessageKey displayName, MessageKey lore) {
        return create(material, amount, displayName.message(), lore.message());
    }

    public static ItemLore create(Material material, int amount, Message displayName, Message lore) {
        return create(material.name(), amount, displayName, lore);
    }

    public static ItemLore create(Material material, int amount, String displayName, String... lore) {
        return create(material.name(), amount, displayName, Arrays.asList(lore));
    }

    public static ItemLore create(String id, int amount, MessageKey displayName, MessageKey lore) {
        return create(id, amount, displayName.message(), lore.message());
    }

    public static ItemLore create(String id, int amount, Message displayName, Message lore) {
        return create(id, amount, displayName.getAsString(), lore.getAsStringList());
    }

    public static ItemLore create(String id, int amount, String displayName, String... lore) {
        return create(id, amount, displayName, Arrays.asList(lore));
    }

    public static ItemLore create(String id, int amount, String displayName, List<String> lore) {
        ItemStack is = parseItemWithData(id, amount);
        is = setDisplayNameAndLore(is, displayName, lore);
        return new ItemLore(is);
    }

    public static ItemStack setDisplayName(ItemStack is, String name) {
        ItemMeta im = is.getItemMeta();
        if (name != null) {
            im.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
            is.setItemMeta(im);
        }
        return is;
    }

    public static ItemStack setDisplayName(ItemStack is, Message name) {
        return setDisplayName(is, name.getAsString());
    }

    public static ItemStack setDisplayName(ItemStack is, MessageKey name) {
        return setDisplayName(is, name.message());
    }

    public static ItemStack setDisplayNameAndLore(ItemStack is, String name, String... lines) {
        return setLore(setDisplayName(is, name), lines);
    }

    public static ItemStack setDisplayNameAndLore(ItemStack is, String name, List<String> lines) {
        return setLore(setDisplayName(is, name), lines);
    }

    public static ItemStack setLore(ItemStack is, String... lines) {
        return setLore(is, Arrays.asList(lines));
    }

    public static ItemStack setLore(ItemStack is, List<String> lines) {
        List<String> lore = new ArrayList<>();
        ItemMeta im = is.getItemMeta();
        if (lines == null || lines.isEmpty())
            return is;
        for (String l : lines)
            lore.add(ChatColor.translateAlternateColorCodes('&', String.valueOf(l)));
        if (!lore.isEmpty()) {
            im.setLore(lore);
            is.setItemMeta(im);
        }
        return is;
    }

    public static ItemStack setLore(ItemStack is, Message lore) {
        return setLore(is, lore.getAsStringList());
    }

    public static ItemStack setLore(ItemStack is, MessageKey lore) {
        return setLore(is, lore.message());
    }

    public static ItemLoreBuilder builder() {
        return new ItemLoreBuilder();
    }

    public static ItemLoreBuilder builder(String id) {
        return new ItemLoreBuilder(id);
    }

    public static ItemLoreBuilder builder(Material type) {
        return new ItemLoreBuilder(type);
    }

    public static class ItemLoreBuilder {
        private String id;

        private String displayName;

        private List<String> lore;

        private int amount = 1;

        private boolean glowing = false;

        private Map<String, Integer> enchantments;

        public ItemLoreBuilder(String id) {
            this.id = (String)Preconditions.checkNotNull(id);
        }

        public ItemLoreBuilder(Material material) {
            this(((Material)Preconditions.checkNotNull(material)).name());
        }

        public ItemLoreBuilder(Material material, int data) {
            this(((Material)Preconditions.checkNotNull(material)).name() + ":" + data);
        }

        public ItemLoreBuilder() {}

        public ItemLoreBuilder id(String id) {
            this.id = id;
            return this;
        }

        public ItemLoreBuilder type(Material type) {
            return id(type.name());
        }

        public ItemLoreBuilder displayName(String name) {
            this.displayName = name;
            return this;
        }

        public ItemLoreBuilder displayName(Message message) {
            return displayName(message.getAsString());
        }

        public ItemLoreBuilder displayName(MessageKey key) {
            return displayName(key.message());
        }

        public ItemLoreBuilder amount(int amount) {
            this.amount = amount;
            return this;
        }

        public ItemLoreBuilder lore(List<String> lore) {
            this.lore = (lore != null) ? new ArrayList<>(lore) : null;
            return this;
        }

        public ItemLoreBuilder lore(String... lore) {
            return lore(Arrays.asList(lore));
        }

        public ItemLoreBuilder lore(Message message) {
            return lore(message.getAsStringList());
        }

        public ItemLoreBuilder lore(MessageKey key) {
            return lore(key.message());
        }

        public ItemLoreBuilder glowing() {
            this.glowing = true;
            return this;
        }

        public ItemLoreBuilder enchant(Enchantment enchantment, int level) {
            if (this.enchantments == null)
                this.enchantments = new HashMap<>();
            this.enchantments.put(enchantment.getName(), Integer.valueOf(level));
            return this;
        }

        public ItemLore build() {
            Preconditions.checkNotNull(this.id, "Id cannot be null");
            Preconditions.checkNotNull(ItemLore.parseItemWithData(this.id, 1), "Unknown item " + this.id);
            ItemLore item = new ItemLore(this.id, this.amount);
            item.glowing = Boolean.valueOf(this.glowing);
            if (this.displayName != null)
                item.setDisplayName(this.displayName);
            if (this.lore != null)
                item.setLore(this.lore);
            if (this.enchantments != null)
                item.getOrCreateMeta().put("enchants", this.enchantments);
            return item;
        }
    }
}
