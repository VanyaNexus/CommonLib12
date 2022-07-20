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
import ru.den_abr.commonlib.expression.context.ActionContext;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.function.Predicate;

public class Action
extends AbstractExpression {
    private Predicate<ActionContext> action;

    public Action(Plugin plugin, String id, Predicate<ActionContext> action) {
        super(plugin, id);
        this.action = Preconditions.checkNotNull(action);
    }

    @Override
    public boolean execute(CommandSender cs, String raw, Map<String, Object> tags) {
        ArrayList<String> split = new ArrayList<String>(Arrays.asList(raw.split(":", 2)));
        if (split.size() < 2) {
            split.add("");
        }
        String[] args = ((String)split.get(1)).isEmpty() ? new String[0] : ((String)split.get(1)).split(":");
        ActionContext context = new ActionContext(cs, this, raw, args, tags);
        try {
            Predicate<ActionContext> predicate = this.getAction();
            return predicate.test(context);
        }
        catch (CommandException e) {
            context.sendMessage(e.getCommandMessage());
            return false;
        }
    }

    public Predicate<ActionContext> getAction() {
        return this.action;
    }
}

