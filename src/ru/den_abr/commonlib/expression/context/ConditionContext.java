/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  org.bukkit.command.CommandSender
 */
package ru.den_abr.commonlib.expression.context;

import org.bukkit.command.CommandSender;
import ru.den_abr.commonlib.expression.AbstractExpression;

import java.util.Map;

public class ConditionContext
extends ExpressionContext {
    public ConditionContext(CommandSender sender, AbstractExpression expression, String label, String[] args, Map<String, Object> passedTags) {
        super(sender, expression, label, args, passedTags);
    }

    public boolean isNegate() {
        return this.label.startsWith("!");
    }

    @Override
    public String getLabel() {
        return this.isNegate() ? this.label.substring(1) : this.label;
    }
}

