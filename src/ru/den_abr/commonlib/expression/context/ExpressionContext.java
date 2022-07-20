/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Player
 */
package ru.den_abr.commonlib.expression.context;

import commonlib.com.minnymin.command.CommandException;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.den_abr.commonlib.expression.AbstractExpression;
import ru.den_abr.commonlib.messages.Message;
import ru.den_abr.commonlib.messages.MessageKey;
import ru.den_abr.commonlib.messages.MessageTagger;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class ExpressionContext
implements MessageTagger {
    protected CommandSender sender;
    protected AbstractExpression expression;
    protected String label;
    protected String[] args;
    protected Map<String, Object> passedTags;

    protected ExpressionContext(CommandSender sender, AbstractExpression expression, String label, String[] args) {
        this(sender, expression, label, args, Collections.emptyMap());
    }

    protected ExpressionContext(CommandSender sender, AbstractExpression expression, String label, String[] args, Map<String, Object> passedTags) {
        this.sender = sender;
        this.expression = expression;
        this.label = label;
        this.args = args;
        this.passedTags = passedTags;
    }

    public CommandSender getSender() {
        return this.sender;
    }

    public <T> T getExpression() {
        return (T)this.expression;
    }

    public String getLabel() {
        return this.label;
    }

    public String[] getArgs() {
        return this.args;
    }

    public int getInt(int index) {
        return Integer.parseInt(this.getArgs(index));
    }

    public List<String> getStringList(int index) {
        return Arrays.asList(this.getArgs(index).split(";"));
    }

    public Player getArgsPlayer(int index) {
        return Bukkit.getPlayerExact((String)this.getArgs(index));
    }

    public double getDouble(int index) {
        return Double.parseDouble(this.getArgs(index));
    }

    public String getArgs(int index) {
        return this.args[index];
    }

    public int length() {
        return this.args.length;
    }

    public boolean isPlayer() {
        return this.sender instanceof Player;
    }

    public void sendMessage(String message) {
        this.sender.sendMessage(message);
    }

    public void sendMessage(Message message) {
        message.send(this.sender);
    }

    public void sendMessage(MessageKey messageKey) {
        this.sendMessage(messageKey.message());
    }

    public void halt(String message) throws CommandException {
        throw new CommandException(message);
    }

    public void halt(Message message) throws CommandException {
        throw new CommandException(message);
    }

    public void halt(MessageKey messageKey) throws CommandException {
        throw new CommandException(messageKey);
    }

    public Player getPlayer() {
        if (this.sender instanceof Player) {
            return (Player)this.sender;
        }
        return null;
    }

    @Override
    public void applyMessageTags(Message mes) {
        this.passedTags.forEach((arg_0, arg_1) -> mes.tag(arg_0, arg_1));
    }
}

