/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.comphenix.protocol.PacketType
 *  com.comphenix.protocol.PacketType$Login$Client
 *  com.comphenix.protocol.events.PacketContainer
 */
package clib.com.comphenix.packetwrapper;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;

public class WrapperLoginClientEncryptionBegin
extends AbstractPacket {
    public static final PacketType TYPE = PacketType.Login.Client.ENCRYPTION_BEGIN;

    public WrapperLoginClientEncryptionBegin() {
        super(new PacketContainer(TYPE), TYPE);
        this.handle.getModifier().writeDefaults();
    }

    public WrapperLoginClientEncryptionBegin(PacketContainer packet) {
        super(packet, TYPE);
    }

    public byte[] getSharedSecret() {
        return this.handle.getByteArrays().read(0);
    }

    public void setSharedSecret(byte[] value) {
        this.handle.getByteArrays().write(0, value);
    }

    public byte[] getVerifyToken() {
        return this.handle.getByteArrays().read(1);
    }

    public void setVerifyToken(byte[] value) {
        this.handle.getByteArrays().write(1, value);
    }
}

