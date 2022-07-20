/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.comphenix.protocol.PacketType
 *  com.comphenix.protocol.PacketType$Play$Server
 *  com.comphenix.protocol.events.PacketContainer
 *  com.comphenix.protocol.events.PacketEvent
 *  com.comphenix.protocol.wrappers.WrappedChatComponent
 *  org.bukkit.World
 *  org.bukkit.entity.Entity
 */
package clib.com.comphenix.packetwrapper;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import org.bukkit.World;
import org.bukkit.entity.Entity;

public class WrapperPlayServerOpenWindow
extends AbstractPacket {
    public static final PacketType TYPE = PacketType.Play.Server.OPEN_WINDOW;

    public WrapperPlayServerOpenWindow() {
        super(new PacketContainer(TYPE), TYPE);
        this.handle.getModifier().writeDefaults();
    }

    public WrapperPlayServerOpenWindow(PacketContainer packet) {
        super(packet, TYPE);
    }

    public int getWindowID() {
        return this.handle.getIntegers().read(0);
    }

    public void setWindowID(int value) {
        this.handle.getIntegers().write(0, value);
    }

    public String getInventoryType() {
        return this.handle.getStrings().read(0);
    }

    public void setInventoryType(String value) {
        this.handle.getStrings().write(0, value);
    }

    public WrappedChatComponent getWindowTitle() {
        return this.handle.getChatComponents().read(0);
    }

    public void setWindowTitle(WrappedChatComponent value) {
        this.handle.getChatComponents().write(0, value);
    }

    public int getNumberOfSlots() {
        return this.handle.getIntegers().read(1);
    }

    public void setNumberOfSlots(int value) {
        this.handle.getIntegers().write(1, value);
    }

    public int getEntityID() {
        return this.handle.getIntegers().read(0);
    }

    public void setEntityID(int value) {
        this.handle.getIntegers().write(0, value);
    }

    public Entity getEntity(World world) {
        return this.handle.getEntityModifier(world).read(0);
    }

    public Entity getEntity(PacketEvent event) {
        return this.getEntity(event.getPlayer().getWorld());
    }
}

