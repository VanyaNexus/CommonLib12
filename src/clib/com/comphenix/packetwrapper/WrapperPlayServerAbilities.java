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

public class WrapperPlayServerAbilities
extends AbstractPacket {
    public static final PacketType TYPE = PacketType.Play.Server.ABILITIES;

    public WrapperPlayServerAbilities() {
        super(new PacketContainer(TYPE), TYPE);
        this.handle.getModifier().writeDefaults();
    }

    public WrapperPlayServerAbilities(PacketContainer packet) {
        super(packet, TYPE);
    }

    public boolean isInvulnurable() {
        return this.handle.getBooleans().read(0);
    }

    public void setInvulnurable(boolean value) {
        this.handle.getBooleans().write(0, value);
    }

    public boolean isFlying() {
        return this.handle.getBooleans().read(1);
    }

    public void setFlying(boolean value) {
        this.handle.getBooleans().write(1, value);
    }

    public boolean canFly() {
        return this.handle.getBooleans().read(2);
    }

    public void setCanFly(boolean value) {
        this.handle.getBooleans().write(2, value);
    }

    public boolean canInstantlyBuild() {
        return this.handle.getBooleans().read(3);
    }

    public void setCanInstantlyBuild(boolean value) {
        this.handle.getBooleans().write(3, value);
    }

    public float getFlyingSpeed() {
        return this.handle.getFloat().read(0).floatValue();
    }

    public void setFlyingSpeed(float value) {
        this.handle.getFloat().write(0, Float.valueOf(value));
    }

    public float getWalkingSpeed() {
        return this.handle.getFloat().read(1).floatValue();
    }

    public void setWalkingSpeed(float value) {
        this.handle.getFloat().write(1, Float.valueOf(value));
    }
}

