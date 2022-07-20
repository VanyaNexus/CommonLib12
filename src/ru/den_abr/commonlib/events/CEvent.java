/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  org.bukkit.Bukkit
 *  org.bukkit.event.Event
 *  org.bukkit.event.HandlerList
 */
package ru.den_abr.commonlib.events;

import com.google.common.base.Preconditions;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import ru.den_abr.commonlib.CommonLib;
//import sun.reflect.CallerSensitive;
//import sun.reflect.Reflection;

import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

public abstract class CEvent
extends Event {
    private static final Map<String, HandlerList> perClassHandlers = new HashMap<String, HandlerList>();

    public CEvent() {
        Class<?> clazz = this.getClass();
        if (CEvent.class.isAssignableFrom(clazz) && !Modifier.isAbstract(clazz.getModifiers())) {
            try {
                clazz.getDeclaredMethod("getHandlerList");
            }
            catch (NoSuchMethodException e) {
                CommonLib.INSTANCE.getLogger().severe(clazz.getName() + " class does not have static getHandlerList method! Some plugins will not work properly.");
            }
        } else {
            System.out.println("Instantiated " + clazz.getName() + ". Shto...");
        }
    }

    public CEvent(boolean async) {
        super(async);
        Class<?> clazz = ((Object) this).getClass();
        if (CEvent.class.isAssignableFrom(clazz) && !Modifier.isAbstract(clazz.getModifiers())) {
            try {
                clazz.getDeclaredMethod("getHandlerList");
            }
            catch (NoSuchMethodException e) {
                CommonLib.INSTANCE.getLogger().severe(clazz.getName() + " class does not have static getHandlerList method! Some plugins will not work properly.");
            }
        } else {
            System.out.println("Instantiated " + clazz.getName() + ". Shto...");
        }
    }

    public <T extends CEvent> T call() {
        Bukkit.getPluginManager().callEvent(this);
        return (T) this;
    }

    public HandlerList getHandlers() {
        return CEvent.getOrCreateHandlerList(((Object) this).getClass());
    }

    //@CallerSensitive
    protected static HandlerList getOrCreateHandlerList() {
        for (int i = 0; i < 10; ++i) {
            //Class<?> c = Reflection.getCallerClass(i);
            int finalI = i;
            Class<?> c = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE).walk(s ->
                    s.map(StackWalker.StackFrame::getDeclaringClass).skip(finalI).findFirst().orElse(null));
            if (c.equals(CEvent.class) || !CEvent.class.isAssignableFrom(c)) continue;
            return CEvent.getOrCreateHandlerList(c);
        }
        throw new IllegalStateException("Did not found caller class");
    }

    public static HandlerList getHandlerList() {
        return new HandlerList();
    }

    private static HandlerList getOrCreateHandlerList(Class<?> c) {
        Preconditions.checkArgument((!c.equals(CEvent.class) ? 1 : 0) != 0, "Got CEvent class");
        Preconditions.checkArgument(CEvent.class.isAssignableFrom(c), "Got invalid class " + c.getName());
        HandlerList list = perClassHandlers.get(c.getName());
        if (list == null) {
            list = new HandlerList();
            perClassHandlers.put(c.getName(), list);
        }
        return list;
    }
}

