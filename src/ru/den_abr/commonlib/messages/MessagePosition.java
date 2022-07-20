/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.comphenix.protocol.wrappers.EnumWrappers$ChatType
 */
package ru.den_abr.commonlib.messages;

import com.comphenix.protocol.wrappers.EnumWrappers;

public enum MessagePosition {
    CHAT,
    ACTIONBAR;


    public static EnumWrappers.ChatType toChatType(MessagePosition pos) {
        switch (pos) {
            case CHAT: {
                return EnumWrappers.ChatType.CHAT;
            }
            case ACTIONBAR: {
                return EnumWrappers.ChatType.GAME_INFO;
            }
        }
        throw new IllegalArgumentException("Illegal position " + pos);
    }
}

