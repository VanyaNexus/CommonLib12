/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.comphenix.protocol.PacketType
 *  com.comphenix.protocol.PacketType$Play$Server
 *  com.comphenix.protocol.events.PacketContainer
 *  com.comphenix.protocol.wrappers.EnumWrappers$CombatEventType
 */
package clib.com.comphenix.packetwrapper;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers;

public class WrapperPlayServerCombatEvent
extends AbstractPacket {
    public static final PacketType TYPE = PacketType.Play.Server.COMBAT_EVENT;

    public WrapperPlayServerCombatEvent() {
        super(new PacketContainer(TYPE), TYPE);
        this.handle.getModifier().writeDefaults();
    }

    public WrapperPlayServerCombatEvent(PacketContainer packet) {
        super(packet, TYPE);
    }

    public EnumWrappers.CombatEventType getEvent() {
        return this.handle.getCombatEvents().read(0);
    }

    public void setEvent(EnumWrappers.CombatEventType value) {
        this.handle.getCombatEvents().write(0, value);
    }

    public int getDuration() {
        if (this.getEvent() != EnumWrappers.CombatEventType.END_COMBAT) {
            throw new IllegalStateException("Duration only exists for END_COMBAT");
        }
        return this.handle.getIntegers().read(0);
    }

    public void setDuration(int value) {
        if (this.getEvent() != EnumWrappers.CombatEventType.END_COMBAT) {
            throw new IllegalStateException("Duration only exists for END_COMBAT");
        }
        this.handle.getIntegers().write(0, value);
    }

    public int getPlayerID() {
        if (this.getEvent() != EnumWrappers.CombatEventType.ENTITY_DIED) {
            throw new IllegalStateException("Player ID only exists for ENTITY_DEAD");
        }
        return this.handle.getIntegers().read(0);
    }

    public void setPlayerId(int value) {
        if (this.getEvent() != EnumWrappers.CombatEventType.ENTITY_DIED) {
            throw new IllegalStateException("Player ID only exists for ENTITY_DEAD");
        }
        this.handle.getIntegers().write(0, value);
    }

    public int getEntityID() {
        EnumWrappers.CombatEventType event = this.getEvent();
        switch (event) {
            case END_COMBAT: 
            case ENTITY_DIED: {
                return this.handle.getIntegers().read(1);
            }
        }
        throw new IllegalStateException("Entity ID does not exist for " + event);
    }

    public void setEntityId(int value) {
        EnumWrappers.CombatEventType event = this.getEvent();
        switch (event) {
            case END_COMBAT: 
            case ENTITY_DIED: {
                this.handle.getIntegers().write(1, value);
            }
        }
        throw new IllegalStateException("Entity ID does not exist for " + event);
    }

    public String getMessage() {
        if (this.getEvent() != EnumWrappers.CombatEventType.ENTITY_DIED) {
            throw new IllegalStateException("Message only exists for ENTITY_DEAD");
        }
        return this.handle.getStrings().read(0);
    }

    public void setMessage(String value) {
        if (this.getEvent() != EnumWrappers.CombatEventType.ENTITY_DIED) {
            throw new IllegalStateException("Message only exists for ENTITY_DEAD");
        }
        this.handle.getStrings().write(0, value);
    }
}

