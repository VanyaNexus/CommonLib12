/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.comphenix.protocol.PacketType
 *  com.comphenix.protocol.PacketType$Play$Server
 *  com.comphenix.protocol.events.PacketContainer
 *  com.comphenix.protocol.reflect.IntEnum
 *  com.comphenix.protocol.reflect.StructureModifier
 */
package clib.com.comphenix.packetwrapper;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.reflect.IntEnum;
import com.comphenix.protocol.reflect.StructureModifier;

public class WrapperPlayServerScoreboardObjective
extends AbstractPacket {
    public static final PacketType TYPE = PacketType.Play.Server.SCOREBOARD_OBJECTIVE;

    public WrapperPlayServerScoreboardObjective() {
        super(new PacketContainer(TYPE), TYPE);
        this.handle.getModifier().writeDefaults();
    }

    public WrapperPlayServerScoreboardObjective(PacketContainer packet) {
        super(packet, TYPE);
    }

    public String getName() {
        return this.handle.getStrings().read(0);
    }

    public void setName(String value) {
        this.handle.getStrings().write(0, value);
    }

    public String getDisplayName() {
        return this.handle.getStrings().read(1);
    }

    public void setDisplayName(String value) {
        this.handle.getStrings().write(1, value);
    }

    public String getHealthDisplay() {
        return this.handle.getSpecificModifier(Enum.class).read(0).name();
    }

    public void setHealthDisplay(String value) {
        StructureModifier mod = this.handle.getSpecificModifier(Enum.class);
        Object constant = Enum.valueOf(((Enum)mod.read(0)).getClass(), value.toUpperCase());
        mod.write(0, constant);
    }

    public int getMode() {
        return this.handle.getIntegers().read(0);
    }

    public void setMode(int value) {
        this.handle.getIntegers().write(0, value);
    }

    public static class Mode
    extends IntEnum {
        public static final int ADD_OBJECTIVE = 0;
        public static final int REMOVE_OBJECTIVE = 1;
        public static final int UPDATE_VALUE = 2;
        private static final Mode INSTANCE = new Mode();

        public static Mode getInstance() {
            return INSTANCE;
        }
    }
}

