/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.ImmutableSet
 *  org.bukkit.Bukkit
 *  org.bukkit.Sound
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Player
 *  org.bukkit.plugin.Plugin
 */
package ru.den_abr.commonlib.expression;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import ru.den_abr.commonlib.CommonLib;
import ru.den_abr.commonlib.expression.context.ActionContext;
import ru.den_abr.commonlib.expression.context.ConditionContext;
import ru.den_abr.commonlib.expression.context.ExpressionContext;
import ru.den_abr.commonlib.expression.expressions.Action;
import ru.den_abr.commonlib.expression.expressions.Condition;
import ru.den_abr.commonlib.menu.Menu;
import ru.den_abr.commonlib.menu.MenuManager;
import ru.den_abr.commonlib.messages.Message;
import ru.den_abr.commonlib.messages.MessageRegistry;
import ru.den_abr.commonlib.messages.MessageTagger;
import ru.den_abr.commonlib.messages.Messages;
import ru.den_abr.commonlib.utility.UtilityMethods;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

public class ExpressionManager {
    private static Map<String, AbstractExpression> expressions = new HashMap<String, AbstractExpression>();

    public static void registerCondition(String tag, Plugin plugin, Predicate<ConditionContext> cond) {
        ExpressionManager.registerExpression(new Condition(plugin, tag, cond));
    }

    public static void registerAction(String tag, Plugin plugin, Predicate<ActionContext> action) {
        ExpressionManager.registerExpression(new Action(plugin, tag, action));
    }

    public static void registerExpression(AbstractExpression expr) {
        expressions.put(expr.getId().toLowerCase(), expr);
    }

    public static void unregisterExpression(String tag) {
        expressions.remove(tag.toLowerCase());
    }

    public static void unregisterAll(Plugin p) {
        ImmutableSet.copyOf(expressions.values()).stream().filter(c -> c.getPlugin().getName().equals(p.getName())).map(AbstractExpression::getId).forEach(ExpressionManager::unregisterExpression);
    }

    public static boolean parseAndExecute(CommandSender cs, String raw) {
        return ExpressionManager.parseAndExecute(cs, raw, Collections.emptyMap());
    }

    public static boolean parseAndExecute(CommandSender cs, String raw, Map<String, Object> passedTags) {
        String expr = raw.split(":", 2)[0].toLowerCase();
        if (expr.startsWith("!")) {
            expr = expr.substring(1);
        }
        if (!ExpressionManager.isRegisteredExpression(raw)) {
            throw new UnknownExpressionException("Unknown expression '" + expr + "'! Input: '" + raw + "");
        }
        return expressions.get(expr).execute(cs, raw, passedTags);
    }

    public static boolean isRegisteredExpression(String raw) {
        String[] spl = ((String)Preconditions.checkNotNull((Object)raw)).split(":");
        String c = spl[0];
        if (c.startsWith("!")) {
            c = c.substring(1);
        }
        return expressions.containsKey(c.toLowerCase());
    }

    public static void unregisterAll() {
        expressions.clear();
    }

    public static void registerDefaults() {
        ExpressionManager.registerAction("chat", (Plugin)CommonLib.INSTANCE, ctx -> {
            ctx.getPlayer().chat(ExpressionManager.argWithPlayer(ctx));
            return true;
        });
        ExpressionManager.registerAction("cmd", (Plugin)CommonLib.INSTANCE, ctx -> Bukkit.dispatchCommand((CommandSender)Bukkit.getConsoleSender(), (String)ExpressionManager.argWithPlayer(ctx)));
        ExpressionManager.registerAction("connect", (Plugin)CommonLib.INSTANCE, ctx -> {
            UtilityMethods.transferPlayer(ctx.getPlayer(), ctx.getArgs(0));
            return true;
        });
        ExpressionManager.registerAction("msg", (Plugin)CommonLib.INSTANCE, ctx -> {
            for (String m : ctx.getArgs()) {
                Message mes;
                if (m.matches(Messages.keyPattern.pattern())) {
                    String key = m.substring(2, m.length() - 1);
                    mes = MessageRegistry.message(key);
                } else {
                    mes = Message.create("commonlib.expression.msg", m);
                }
                mes.tag("player", ctx.getPlayer().getName());
                mes.tag((MessageTagger)ctx);
                ctx.sendMessage(mes);
            }
            return true;
        });
        ExpressionManager.registerAction("backmenu", (Plugin)CommonLib.INSTANCE, ctx -> {
            Player p = ctx.getPlayer();
            Menu prev = MenuManager.getLastMenu(p);
            if (prev != null) {
                prev.open(p);
                return true;
            }
            p.closeInventory();
            return false;
        });
        ExpressionManager.registerAction("playsound", (Plugin)CommonLib.INSTANCE, ctx -> {
            Sound sound = null;
            float volume = 1.0f;
            float pitch = 1.0f;
            try {
                sound = Sound.valueOf((String)ctx.getArgs(0));
                if (ctx.length() > 1) {
                    pitch = Float.parseFloat(ctx.getArgs(1));
                }
                if (ctx.length() > 2) {
                    volume = Float.parseFloat(ctx.getArgs(2));
                }
            }
            catch (Exception e) {
                ctx.halt(e.getMessage());
            }
            ctx.getPlayer().playSound(ctx.getPlayer().getLocation(), sound, volume, pitch);
            return true;
        });
        ExpressionManager.registerAction("close", (Plugin)CommonLib.INSTANCE, ctx -> {
            ctx.getPlayer().closeInventory();
            return true;
        });
    }

    private static String argWithPlayer(ExpressionContext ctx) {
        return ctx.getArgs(0).replace("(player)", ctx.getPlayer().getName());
    }
}

