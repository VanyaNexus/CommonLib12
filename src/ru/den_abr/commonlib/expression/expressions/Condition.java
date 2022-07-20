/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  org.bukkit.command.CommandSender
 *  org.bukkit.plugin.Plugin
 */
package ru.den_abr.commonlib.expression.expressions;

import com.google.common.base.Preconditions;
import commonlib.com.minnymin.command.CommandException;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import ru.den_abr.commonlib.expression.AbstractExpression;
import ru.den_abr.commonlib.expression.context.ConditionContext;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.function.Predicate;

public class Condition
extends AbstractExpression {
    private final Predicate<ConditionContext> condition;

    public Condition(Plugin plugin, String id, Predicate<ConditionContext> condition) {
        super(plugin, id);
        this.condition = Preconditions.checkNotNull(condition, "condition");
    }

    public Predicate<ConditionContext> getCondition() {
        return this.condition;
    }

    @Override
    public boolean execute(CommandSender cs, String raw, Map<String, Object> tags) {
        String c;
        ArrayList<String> split = new ArrayList<String>(Arrays.asList(raw.split(":", 2)));
        if (split.size() < 2) {
            split.add("");
        }
        if ((c = split.get(0)).startsWith("!")) {
            c = c.substring(1);
        }
        String[] args = split.get(1).isEmpty() ? new String[0] : split.get(1).split(":");
        ConditionContext context = new ConditionContext(cs, this, raw, args, tags);
        try {
            Predicate<ConditionContext> predicate = this.getCondition();
            if (context.isNegate()) {
                predicate = predicate.negate();
            }
            return predicate.test(context);
        }
        catch (CommandException e) {
            context.sendMessage(e.getCommandMessage());
            return false;
        }
    }
}

