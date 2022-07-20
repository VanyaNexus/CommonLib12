/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.comphenix.protocol.PacketType
 *  com.comphenix.protocol.PacketType$Play$Server
 *  com.comphenix.protocol.events.PacketContainer
 *  com.comphenix.protocol.wrappers.BlockPosition
 */
package clib.com.comphenix.packetwrapper;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.BlockPosition;

import java.util.List;

public class WrapperPlayServerExplosion
extends AbstractPacket {
    public static final PacketType TYPE = PacketType.Play.Server.EXPLOSION;

    public WrapperPlayServerExplosion() {
        super(new PacketContainer(TYPE), TYPE);
        this.handle.getModifier().writeDefaults();
    }

    public WrapperPlayServerExplosion(PacketContainer packet) {
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

    public float getRadius() {
        return this.handle.getFloat().read(0).floatValue();
    }

    public void setRadius(float value) {
        this.handle.getFloat().write(0, Float.valueOf(value));
    }

    public List<BlockPosition> getRecors() {
        return this.handle.getBlockPositionCollectionModifier().read(0);
    }

    public void setRecords(List<BlockPosition> value) {
        this.handle.getBlockPositionCollectionModifier().write(0, value);
    }

    public float getPlayerVelocityX() {
        return this.handle.getFloat().read(0).floatValue();
    }

    public void setPlayerVelocityX(float value) {
        this.handle.getFloat().write(0, Float.valueOf(value));
    }

    public float getPlayerVelocityY() {
        return this.handle.getFloat().read(1).floatValue();
    }

    public void setPlayerVelocityY(float value) {
        this.handle.getFloat().write(1, Float.valueOf(value));
    }

    public float getPlayerVelocityZ() {
        return this.handle.getFloat().read(2).floatValue();
    }

    public void setPlayerVelocityZ(float value) {
        this.handle.getFloat().write(2, Float.valueOf(value));
    }
}

