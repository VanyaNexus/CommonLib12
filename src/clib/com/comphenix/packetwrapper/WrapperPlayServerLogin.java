/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.comphenix.protocol.PacketType
 *  com.comphenix.protocol.PacketType$Play$Server
 *  com.comphenix.protocol.events.PacketContainer
 *  com.comphenix.protocol.events.PacketEvent
 *  com.comphenix.protocol.wrappers.EnumWrappers$Difficulty
 *  com.comphenix.protocol.wrappers.EnumWrappers$NativeGameMode
 *  org.bukkit.World
 *  org.bukkit.WorldType
 *  org.bukkit.entity.Entity
 */
package clib.com.comphenix.packetwrapper;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers;
import org.bukkit.World;
import org.bukkit.WorldType;
import org.bukkit.entity.Entity;

public class WrapperPlayServerLogin
extends AbstractPacket {
    public static final PacketType TYPE = PacketType.Play.Server.LOGIN;

    public WrapperPlayServerLogin() {
        super(new PacketContainer(TYPE), TYPE);
        this.handle.getModifier().writeDefaults();
    }

    public WrapperPlayServerLogin(PacketContainer packet) {
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

    public EnumWrappers.NativeGameMode getGamemode() {
        return this.handle.getGameModes().read(0);
    }

    public void setGamemode(EnumWrappers.NativeGameMode value) {
        this.handle.getGameModes().write(0, value);
    }

    public int getDimension() {
        return this.handle.getIntegers().read(0);
    }

    public void setDimension(int value) {
        this.handle.getIntegers().write(0, value);
    }

    public EnumWrappers.Difficulty getDifficulty() {
        return this.handle.getDifficulties().read(0);
    }

    public void setDifficulty(EnumWrappers.Difficulty value) {
        this.handle.getDifficulties().write(0, value);
    }

    public int getMaxPlayers() {
        return this.handle.getIntegers().read(1);
    }

    public void setMaxPlayers(int value) {
        this.handle.getIntegers().write(0, value);
    }

    public WorldType getLevelType() {
        return this.handle.getWorldTypeModifier().read(0);
    }

    public void setLevelType(WorldType value) {
        this.handle.getWorldTypeModifier().write(0, value);
    }

    public boolean getReducedDebugInfo() {
        return this.handle.getBooleans().read(0);
    }

    public void setReducedDebugInfo(boolean value) {
        this.handle.getBooleans().write(0, value);
    }
}

