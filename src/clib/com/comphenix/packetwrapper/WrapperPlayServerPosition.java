/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.comphenix.protocol.PacketType
 *  com.comphenix.protocol.PacketType$Play$Server
 *  com.comphenix.protocol.events.PacketContainer
 *  com.comphenix.protocol.reflect.EquivalentConverter
 *  com.comphenix.protocol.reflect.StructureModifier
 *  com.comphenix.protocol.utility.MinecraftReflection
 *  com.comphenix.protocol.wrappers.BukkitConverters
 *  com.comphenix.protocol.wrappers.EnumWrappers
 */
package clib.com.comphenix.packetwrapper;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.reflect.EquivalentConverter;
import com.comphenix.protocol.reflect.StructureModifier;
import com.comphenix.protocol.utility.MinecraftReflection;
import com.comphenix.protocol.wrappers.BukkitConverters;
import com.comphenix.protocol.wrappers.EnumWrappers;

import java.util.Set;

public class WrapperPlayServerPosition
extends AbstractPacket {
    public static final PacketType TYPE = PacketType.Play.Server.POSITION;
    private static final Class<?> FLAGS_CLASS = MinecraftReflection.getMinecraftClass("EnumPlayerTeleportFlags", new String[]{"PacketPlayOutPosition$EnumPlayerTeleportFlags"});

    public WrapperPlayServerPosition() {
        super(new PacketContainer(TYPE), TYPE);
        this.handle.getModifier().writeDefaults();
    }

    public WrapperPlayServerPosition(PacketContainer packet) {
        super(packet, TYPE);
    }

    public double getX() {
        return this.handle.getDoubles().read(0);
    }

    public void setX(double value) {
        this.handle.getDoubles().write(0, value);
    }

    public double getY() {
        return this.handle.getDoubles().read(1);
    }

    public void setY(double value) {
        this.handle.getDoubles().write(1, value);
    }

    public double getZ() {
        return this.handle.getDoubles().read(2);
    }

    public void setZ(double value) {
        this.handle.getDoubles().write(2, value);
    }

    public float getYaw() {
        return this.handle.getFloat().read(0).floatValue();
    }

    public void setYaw(float value) {
        this.handle.getFloat().write(0, Float.valueOf(value));
    }

    public float getPitch() {
        return this.handle.getFloat().read(1).floatValue();
    }

    public void setPitch(float value) {
        this.handle.getFloat().write(1, Float.valueOf(value));
    }

    private StructureModifier<Set<PlayerTeleportFlag>> getFlagsModifier() {
        return this.handle.getModifier().withType(Set.class, BukkitConverters.getSetConverter(FLAGS_CLASS, (EquivalentConverter)EnumWrappers.getGenericConverter(PlayerTeleportFlag.class)));
    }

    public Set<PlayerTeleportFlag> getFlags() {
        return this.getFlagsModifier().read(0);
    }

    public void setFlags(Set<PlayerTeleportFlag> value) {
        this.getFlagsModifier().write(0, value);
    }

    public enum PlayerTeleportFlag {
        X,
        Y,
        Z,
        Y_ROT,
        X_ROT

    }
}

