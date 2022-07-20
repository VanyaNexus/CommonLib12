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

public class WrapperPlayServerExperience
extends AbstractPacket {
    public static final PacketType TYPE = PacketType.Play.Server.EXPERIENCE;

    public WrapperPlayServerExperience() {
        super(new PacketContainer(TYPE), TYPE);
        this.handle.getModifier().writeDefaults();
    }

    public WrapperPlayServerExperience(PacketContainer packet) {
        super(packet, TYPE);
    }

    public float getExperienceBar() {
        return this.handle.getFloat().read(0).floatValue();
    }

    public void setExperienceBar(float value) {
        this.handle.getFloat().write(0, Float.valueOf(value));
    }

    public int getLevel() {
        return this.handle.getIntegers().read(1);
    }

    public void setLevel(int value) {
        this.handle.getIntegers().write(1, value);
    }

    public int getTotalExperience() {
        return this.handle.getIntegers().read(0);
    }

    public void setTotalExperience(int value) {
        this.handle.getIntegers().write(0, value);
    }
}

