/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.comphenix.protocol.PacketType
 *  com.comphenix.protocol.PacketType$Play$Server
 *  com.comphenix.protocol.events.PacketContainer
 */
package clib.com.comphenix.packetwrapper;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;

public class WrapperPlayServerMap
extends AbstractPacket {
    public static final PacketType TYPE = PacketType.Play.Server.MAP;

    public WrapperPlayServerMap() {
        super(new PacketContainer(TYPE), TYPE);
        this.handle.getModifier().writeDefaults();
    }

    public WrapperPlayServerMap(PacketContainer packet) {
        super(packet, TYPE);
    }

    public int getItemDamage() {
        return this.handle.getIntegers().read(0);
    }

    public void setItemDamage(int value) {
        this.handle.getIntegers().write(0, value);
    }

    public byte getScale() {
        return this.handle.getBytes().read(0);
    }

    public void setScale(byte value) {
        this.handle.getBytes().write(0, value);
    }

    public Object[] getMapIcons() {
        return (Object[])this.handle.getModifier().read(2);
    }

    public void setMapIcons(Object[] value) {
        this.handle.getModifier().write(2, value);
    }

    public int getColumns() {
        return this.handle.getIntegers().read(1);
    }

    public void setColumns(int value) {
        this.handle.getIntegers().write(1, value);
    }

    public int getRows() {
        return this.handle.getIntegers().read(2);
    }

    public void setRows(int value) {
        this.handle.getIntegers().write(2, value);
    }

    public int getX() {
        return this.handle.getIntegers().read(3);
    }

    public void setX(int value) {
        this.handle.getIntegers().write(3, value);
    }

    public int getZ() {
        return this.handle.getIntegers().read(4);
    }

    public void setZ(int value) {
        this.handle.getIntegers().write(4, value);
    }

    public byte[] getData() {
        return this.handle.getByteArrays().read(0);
    }

    public void setData(byte[] value) {
        this.handle.getByteArrays().write(0, value);
    }
}

