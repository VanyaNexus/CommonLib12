/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.comphenix.protocol.PacketType
 *  com.comphenix.protocol.PacketType$Play$Client
 *  com.comphenix.protocol.events.PacketContainer
 *  com.comphenix.protocol.wrappers.EnumWrappers$ClientCommand
 */
package clib.com.comphenix.packetwrapper;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers;

public class WrapperPlayClientClientCommand
extends AbstractPacket {
    public static final PacketType TYPE = PacketType.Play.Client.CLIENT_COMMAND;

    public WrapperPlayClientClientCommand() {
        super(new PacketContainer(TYPE), TYPE);
        this.handle.getModifier().writeDefaults();
    }

    public WrapperPlayClientClientCommand(PacketContainer packet) {
        super(packet, TYPE);
    }

    public EnumWrappers.ClientCommand getAction() {
        return this.handle.getClientCommands().read(0);
    }

    public void setAction(EnumWrappers.ClientCommand value) {
        this.handle.getClientCommands().write(0, value);
    }
}

