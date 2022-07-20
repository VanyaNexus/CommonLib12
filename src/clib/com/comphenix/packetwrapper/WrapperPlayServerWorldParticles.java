/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.comphenix.protocol.PacketType
 *  com.comphenix.protocol.PacketType$Play$Server
 *  com.comphenix.protocol.events.PacketContainer
 *  com.comphenix.protocol.wrappers.EnumWrappers$Particle
 */
package clib.com.comphenix.packetwrapper;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers;

public class WrapperPlayServerWorldParticles
extends AbstractPacket {
    public static final PacketType TYPE = PacketType.Play.Server.WORLD_PARTICLES;

    public WrapperPlayServerWorldParticles() {
        super(new PacketContainer(TYPE), TYPE);
        this.handle.getModifier().writeDefaults();
    }

    public WrapperPlayServerWorldParticles(PacketContainer packet) {
        super(packet, TYPE);
    }

    public EnumWrappers.Particle getParticleType() {
        return this.handle.getParticles().read(0);
    }

    public void setParticleType(EnumWrappers.Particle value) {
        this.handle.getParticles().write(0, value);
    }

    public float getX() {
        return this.handle.getFloat().read(0).floatValue();
    }

    public void setX(float value) {
        this.handle.getFloat().write(0, Float.valueOf(value));
    }

    public float getY() {
        return this.handle.getFloat().read(1).floatValue();
    }

    public void setY(float value) {
        this.handle.getFloat().write(1, Float.valueOf(value));
    }

    public float getZ() {
        return this.handle.getFloat().read(2).floatValue();
    }

    public void setZ(float value) {
        this.handle.getFloat().write(2, Float.valueOf(value));
    }

    public float getOffsetX() {
        return this.handle.getFloat().read(3).floatValue();
    }

    public void setOffsetX(float value) {
        this.handle.getFloat().write(3, Float.valueOf(value));
    }

    public float getOffsetY() {
        return this.handle.getFloat().read(4).floatValue();
    }

    public void setOffsetY(float value) {
        this.handle.getFloat().write(4, Float.valueOf(value));
    }

    public float getOffsetZ() {
        return this.handle.getFloat().read(5).floatValue();
    }

    public void setOffsetZ(float value) {
        this.handle.getFloat().write(5, Float.valueOf(value));
    }

    public float getParticleData() {
        return this.handle.getFloat().read(6).floatValue();
    }

    public void setParticleData(float value) {
        this.handle.getFloat().write(6, Float.valueOf(value));
    }

    public int getNumberOfParticles() {
        return this.handle.getIntegers().read(0);
    }

    public void setNumberOfParticles(int value) {
        this.handle.getIntegers().write(0, value);
    }

    public boolean getLongDistance() {
        return this.handle.getBooleans().read(0);
    }

    public void setLongDistance(boolean value) {
        this.handle.getBooleans().write(0, value);
    }

    public int[] getData() {
        return this.handle.getIntegerArrays().read(0);
    }

    public void setData(int[] value) {
        this.handle.getIntegerArrays().write(0, value);
    }
}

