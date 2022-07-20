/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  org.bukkit.command.CommandSender
 *  org.bukkit.plugin.Plugin
 */
package ru.den_abr.commonlib.expression;

import com.google.common.base.Preconditions;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import java.util.Map;

public abstract class AbstractExpression {
    private final Plugin plugin;
    private final String id;

    public AbstractExpression(Plugin plugin, String id) {
        this.plugin = Preconditions.checkNotNull(plugin, "plugin");
        this.id = Preconditions.checkNotNull(id, "id");
    }

    public Plugin getPlugin() {
        return this.plugin;
    }

    public String getId() {
        return this.id;
    }

    public abstract boolean execute(CommandSender var1, String var2, Map<String, Object> var3);
}

