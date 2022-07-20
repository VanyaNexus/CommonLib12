/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.comphenix.protocol.PacketType
 *  com.comphenix.protocol.PacketType$Play$Server
 *  com.comphenix.protocol.events.PacketContainer
 *  com.comphenix.protocol.events.PacketEvent
 *  org.bukkit.World
 *  org.bukkit.entity.Entity
 */
package clib.com.comphenix.packetwrapper;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import org.bukkit.World;
import org.bukkit.entity.Entity;

public class WrapperPlayServerRelEntityMove
extends AbstractPacket {
    public static final PacketType TYPE = PacketType.Play.Server.REL_ENTITY_MOVE;

    public WrapperPlayServerRelEntityMove() {
        super(new PacketContainer(TYPE), TYPE);
        this.handle.getModifier().writeDefaults();
    }

    public WrapperPlayServerRelEntityMove(PacketContainer packet) {
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

    public double getDx() {
        return (double) this.handle.getBytes().read(0).byteValue() / 32.0;
    }

    public void setDx(double value) {
        this.handle.getBytes().write(0, (byte)(value * 32.0));
    }

    public double getDy() {
        return (double) this.handle.getBytes().read(1).byteValue() / 32.0;
    }

    public void setDy(double value) {
        this.handle.getBytes().write(1, (byte)(value * 32.0));
    }

    public double getDz() {
        return (double) this.handle.getBytes().read(2).byteValue() / 32.0;
    }

    public void setDz(double value) {
        this.handle.getBytes().write(2, (byte)(value * 32.0));
    }

    public boolean getOnGround() {
        return this.handle.getSpecificModifier(Boolean.TYPE).read(0);
    }

    public void setOnGround(boolean value) {
        this.handle.getSpecificModifier(Boolean.TYPE).write(0, value);
    }
}

