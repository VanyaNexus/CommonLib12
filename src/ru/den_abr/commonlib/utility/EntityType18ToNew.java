package ru.den_abr.commonlib.utility;

public enum EntityType18ToNew
{
    DROPPED_ITEM("Item", "minecraft:item"), 
    EXPERIENCE_ORB("XPOrb", "minecraft:xp_orb"), 
    LEASH_HITCH("LeashKnot", "minecraft:leash_knot"), 
    PAINTING("Painting", "minecraft:painting"), 
    ARROW("Arrow", "minecraft:arrow"), 
    SNOWBALL("Snowball", "minecraft:snowball"), 
    FIREBALL("Fireball", "minecraft:fireball"), 
    SMALL_FIREBALL("SmallFireball", "minecraft:small_fireball"), 
    ENDER_PEARL("ThrownEnderpearl", "minecraft:ender_pearl"), 
    ENDER_SIGNAL("EyeOfEnderSignal", "minecraft:eye_of_ender_signal"), 
    THROWN_EXP_BOTTLE("ThrownExpBottle", "minecraft:xp_bottle"), 
    ITEM_FRAME("ItemFrame", "minecraft:item_frame"), 
    WITHER_SKULL("WitherSkull", "minecraft:wither_skull"), 
    PRIMED_TNT("PrimedTnt", "minecraft:tnt"), 
    FALLING_BLOCK("FallingSand", "minecraft:falling_block"), 
    FIREWORK("FireworksRocketEntity", "minecraft:fireworks_rocket"), 
    ARMOR_STAND("ArmorStand", "minecraft:armor_stand"), 
    MINECART_COMMAND("MinecartCommandBlock", "minecraft:commandblock_minecart"), 
    BOAT("Boat", "minecraft:boat"), 
    MINECART("MinecartRideable", "minecraft:minecart"), 
    MINECART_CHEST("MinecartChest", "minecraft:chest_minecart"), 
    MINECART_FURNACE("MinecartFurnace", "minecraft:chest_minecart"), 
    MINECART_TNT("MinecartTNT", "minecraft:tnt_minecart"), 
    MINECART_HOPPER("MinecartHopper", "minecraft:hopper_minecart"), 
    MINECART_MOB_SPAWNER("MinecartMobSpawner", "minecraft:spawner_minecart"), 
    CREEPER("Creeper", "minecraft:creeper"), 
    SKELETON("Skeleton", "minecraft:skeleton"), 
    SPIDER("Spider", "minecraft:spider"), 
    GIANT("Giant", "minecraft:giant"), 
    ZOMBIE("Zombie", "minecraft:zombie"), 
    SLIME("Slime", "minecraft:slime"), 
    GHAST("Ghast", "minecraft:ghast"), 
    PIG_ZOMBIE("PigZombie", "minecraft:zombie_pigman"), 
    ENDERMAN("Enderman", "minecraft:enderman"), 
    CAVE_SPIDER("CaveSpider", "minecraft:cave_spider"), 
    SILVERFISH("Silverfish", "minecraft:silverfish"), 
    BLAZE("Blaze", "minecraft:blaze"), 
    MAGMA_CUBE("LavaSlime", "minecraft:magma_cube"), 
    ENDER_DRAGON("EnderDragon", "minecraft:ender_dragon"), 
    WITHER("WitherBoss", "minecraft:wither"), 
    BAT("Bat", "minecraft:bat"), 
    WITCH("Witch", "minecraft:witch"), 
    ENDERMITE("Endermite", "minecraft:endermite"), 
    GUARDIAN("Guardian", "minecraft:guardian"), 
    PIG("Pig", "minecraft:pig"), 
    SHEEP("Sheep", "minecraft:sheep"), 
    COW("Cow", "minecraft:cow"), 
    CHICKEN("Chicken", "minecraft:chicken"), 
    SQUID("Squid", "minecraft:squid"), 
    WOLF("Wolf", "minecraft:wolf"), 
    MUSHROOM_COW("MushroomCow", "minecraft:mooshroom"), 
    SNOWMAN("SnowMan", "minecraft:snowman"), 
    OCELOT("Ozelot", "minecraft:ocelot"), 
    IRON_GOLEM("VillagerGolem", "minecraft:villager_golem"), 
    HORSE("EntityHorse", "minecraft:horse"), 
    RABBIT("Rabbit", "minecraft:rabbit"), 
    VILLAGER("Villager", "minecraft:villager"), 
    ENDER_CRYSTAL("EnderCrystal", "minecraft:ender_crystal");
    
    private String oldName;
    private String newName;
    
    private EntityType18ToNew(final String old, final String newName) {
        this.oldName = old;
        this.newName = newName;
    }
    
    public String getOldName() {
        return this.oldName;
    }
    
    public String getNewName() {
        return this.newName;
    }
    
    public static EntityType18ToNew getById(final String id) {
        for (final EntityType18ToNew t : values()) {
            if (t.getNewName().equals(id) || t.getOldName().equals(id)) {
                return t;
            }
        }
        return null;
    }
}
