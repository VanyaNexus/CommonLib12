/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.comphenix.protocol.PacketType
 *  com.comphenix.protocol.PacketType$Play$Client
 *  com.comphenix.protocol.events.PacketContainer
 *  com.comphenix.protocol.wrappers.EnumWrappers$ChatVisibility
 */
package clib.com.comphenix.packetwrapper;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers;

public class WrapperPlayClientSettings
extends AbstractPacket {
    public static final PacketType TYPE = PacketType.Play.Client.SETTINGS;

    public WrapperPlayClientSettings() {
        super(new PacketContainer(TYPE), TYPE);
        this.handle.getModifier().writeDefaults();
    }

    public WrapperPlayClientSettings(PacketContainer packet) {
        super(packet, TYPE);
    }

    public String getLocale() {
        return this.handle.getStrings().read(0);
    }

    public void setLocale(String value) {
        this.handle.getStrings().write(0, value);
    }

    public int getViewDistance() {
        return this.handle.getIntegers().read(0);
    }

    public void setViewDistance(byte value) {
        this.handle.getIntegers().write(0, Integer.valueOf(value));
    }

    public EnumWrappers.ChatVisibility getChatFlags() {
        return this.handle.getChatVisibilities().read(0);
    }

    public void setChatFlags(EnumWrappers.ChatVisibility value) {
        this.handle.getChatVisibilities().write(0, value);
    }

    public boolean getChatColours() {
        return this.handle.getSpecificModifier(Boolean.TYPE).read(0);
    }

    public void setChatColours(boolean value) {
        this.handle.getSpecificModifier(Boolean.TYPE).write(0, value);
    }

    public int getDisplayedSkinParts() {
        return this.handle.getIntegers().read(1);
    }

    public void setDisplayedSkinParts(int value) {
        this.handle.getIntegers().write(1, value);
    }
}

