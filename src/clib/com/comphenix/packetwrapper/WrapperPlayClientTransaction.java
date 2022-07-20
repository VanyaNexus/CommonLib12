/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.comphenix.protocol.PacketType
 *  com.comphenix.protocol.PacketType$Play$Client
 *  com.comphenix.protocol.events.PacketContainer
 */
package clib.com.comphenix.packetwrapper;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;

public class WrapperPlayClientTransaction
extends AbstractPacket {
    public static final PacketType TYPE = PacketType.Play.Client.TRANSACTION;

    public WrapperPlayClientTransaction() {
        super(new PacketContainer(TYPE), TYPE);
        this.handle.getModifier().writeDefaults();
    }

    public WrapperPlayClientTransaction(PacketContainer packet) {
        super(packet, TYPE);
    }

    public int getWindowId() {
        return this.handle.getIntegers().read(0);
    }

    public void setWindowId(byte value) {
        this.handle.getIntegers().write(0, Integer.valueOf(value));
    }

    public short getActionNumber() {
        return this.handle.getShorts().read(0);
    }

    public void setActionNumber(short value) {
        this.handle.getShorts().write(0, value);
    }

    public boolean getAccepted() {
        return this.handle.getSpecificModifier(Boolean.TYPE).read(0);
    }

    public void setAccepted(boolean value) {
        this.handle.getSpecificModifier(Boolean.TYPE).write(0, value);
    }
}

