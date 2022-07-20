/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.comphenix.protocol.PacketType
 *  com.comphenix.protocol.PacketType$Play$Server
 *  com.comphenix.protocol.events.PacketContainer
 *  com.comphenix.protocol.wrappers.EnumWrappers$WorldBorderAction
 */
package clib.com.comphenix.packetwrapper;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers;

public class WrapperPlayServerWorldBorder
extends AbstractPacket {
    public static final PacketType TYPE = PacketType.Play.Server.WORLD_BORDER;

    public WrapperPlayServerWorldBorder() {
        super(new PacketContainer(TYPE), TYPE);
        this.handle.getModifier().writeDefaults();
    }

    public WrapperPlayServerWorldBorder(PacketContainer packet) {
        super(packet, TYPE);
    }

    public EnumWrappers.WorldBorderAction getAction() {
        return this.handle.getWorldBorderActions().read(0);
    }

    public void setAction(EnumWrappers.WorldBorderAction value) {
        this.handle.getWorldBorderActions().write(0, value);
    }

    public int getPortalTeleportBoundary() {
        return this.handle.getIntegers().read(0);
    }

    public void setPortalTeleportBoundary(int value) {
        this.handle.getIntegers().write(0, value);
    }

    public double getCenterX() {
        return this.handle.getDoubles().read(0);
    }

    public void setCenterX(double value) {
        this.handle.getDoubles().write(0, value);
    }

    public double getCenterZ() {
        return this.handle.getDoubles().read(1);
    }

    public void setCenterZ(double value) {
        this.handle.getDoubles().write(1, value);
    }

    public double getOldRadius() {
        return this.handle.getDoubles().read(2);
    }

    public void setOldRadius(double value) {
        this.handle.getDoubles().write(2, value);
    }

    public double getRadius() {
        return this.handle.getDoubles().read(3);
    }

    public void setRadius(double value) {
        this.handle.getDoubles().write(3, value);
    }

    public long getSpeed() {
        return this.handle.getLongs().read(0);
    }

    public void setSpeed(long value) {
        this.handle.getLongs().write(0, value);
    }

    public int getWarningTime() {
        return this.handle.getIntegers().read(1);
    }

    public void setWarningTime(int value) {
        this.handle.getIntegers().write(1, value);
    }

    public int getWarningDistance() {
        return this.handle.getIntegers().read(2);
    }

    public void setWarningDistance(int value) {
        this.handle.getIntegers().write(2, value);
    }
}

