/*
 * Decompiled with CFR 0.150.
 */
package commonlib.com.minnymin.command;

import ru.den_abr.commonlib.messages.Message;
import ru.den_abr.commonlib.messages.MessageKey;

public class CommandException
extends RuntimeException {
    private Message message;

    public CommandException(MessageKey mes) {
        this.message = mes.message();
    }

    public CommandException(Message mes) {
        this.message = mes;
    }

    public CommandException(String mes) {
        this.message = new Message("command.exception", mes);
    }

    public Message getCommandMessage() {
        return this.message;
    }
}

