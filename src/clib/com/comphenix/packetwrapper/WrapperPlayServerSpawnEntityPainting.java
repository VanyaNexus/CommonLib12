/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.comphenix.protocol.PacketType
 *  com.comphenix.protocol.PacketType$Play$Server
 *  com.comphenix.protocol.events.PacketContainer
 *  com.comphenix.protocol.events.PacketEvent
 *  com.comphenix.protocol.reflect.StructureModifier
 *  com.comphenix.protocol.utility.MinecraftReflection
 *  com.comphenix.protocol.wrappers.BlockPosition
 *  com.comphenix.protocol.wrappers.EnumWrappers
 *  org.bukkit.World
 *  org.bukkit.entity.Entity
 */
package clib.com.comphenix.packetwrapper;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.reflect.StructureModifier;
import com.comphenix.protocol.utility.MinecraftReflection;
import com.comphenix.protocol.wrappers.BlockPosition;
import com.comphenix.protocol.wrappers.EnumWrappers;
import org.bukkit.World;
import org.bukkit.entity.Entity;

public class WrapperPlayServerSpawnEntityPainting
extends AbstractPacket {
    public static final PacketType TYPE = PacketType.Play.Server.SPAWN_ENTITY_PAINTING;
    private static final Class<?> DIRECTION_CLASS = MinecraftReflection.getMinecraftClass("EnumDirection");

    public WrapperPlayServerSpawnEntityPainting() {
        super(new PacketContainer(TYPE), TYPE);
        this.handle.getModifier().writeDefaults();
    }

    public WrapperPlayServerSpawnEntityPainting(PacketContainer packet) {
        super(packet, TYPE);
    }

    public int getEntityID() {
        return this.handle.getIntegers().read(0);
    }

    public void setEntityID(int value) {
        this.handle.getIntegers().write(0, value);
    }

    public Entity getEntity(World world) {
        return this.handle.getEntityModifier(world).read(0);
    }

    public Entity getEntity(PacketEvent event) {
        return this.getEntity(event.getPlayer().getWorld());
    }

    public String getTitle() {
        return this.handle.getStrings().read(0);
    }

    public void setTitle(String value) {
        this.handle.getStrings().write(0, value);
    }

    public BlockPosition getLocation() {
        return this.handle.getBlockPositionModifier().read(0);
    }

    public void setLocation(BlockPosition value) {
        this.handle.getBlockPositionModifier().write(0, value);
    }

    private StructureModifier<Direction> getDirections() {
        return this.handle.getModifier().withType(DIRECTION_CLASS, EnumWrappers.getGenericConverter(Direction.class));
    }

    public Direction getDirection() {
        return (Direction) this.getDirections().read(0);
    }

    public void setDirection(Direction value) {
        this.getDirections().write(0, value);
    }

    public enum Direction {
        DOWN,
        UP,
        NORTH,
        SOUTH,
        WEST,
        EAST

    }
}

