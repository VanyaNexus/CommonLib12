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

public class WrapperPlayServerNamedSoundEffect
extends AbstractPacket {
    public static final PacketType TYPE = PacketType.Play.Server.NAMED_SOUND_EFFECT;

    public WrapperPlayServerNamedSoundEffect() {
        super(new PacketContainer(TYPE), TYPE);
        this.handle.getModifier().writeDefaults();
    }

    public WrapperPlayServerNamedSoundEffect(PacketContainer packet) {
        super(packet, TYPE);
    }

    public String getSoundName() {
        return this.handle.getStrings().read(0);
    }

    public void setSoundName(String value) {
        this.handle.getStrings().write(0, value);
    }

    public int getEffectPositionX() {
        return this.handle.getIntegers().read(0);
    }

    public void setEffectPositionX(int value) {
        this.handle.getIntegers().write(0, value);
    }

    public int getEffectPositionY() {
        return this.handle.getIntegers().read(1);
    }

    public void setEffectPositionY(int value) {
        this.handle.getIntegers().write(1, value);
    }

    public int getEffectPositionZ() {
        return this.handle.getIntegers().read(2);
    }

    public void setEffectPositionZ(int value) {
        this.handle.getIntegers().write(2, value);
    }

    public float getVolume() {
        return this.handle.getFloat().read(0).floatValue();
    }

    public void setVolume(float value) {
        this.handle.getFloat().write(0, Float.valueOf(value));
    }

    public int getPitch() {
        return this.handle.getIntegers().read(3);
    }

    public void setPitch(byte value) {
        this.handle.getIntegers().write(3, Integer.valueOf(value));
    }
}

