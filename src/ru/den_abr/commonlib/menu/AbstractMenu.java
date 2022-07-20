/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.comphenix.protocol.reflect.accessors.Accessors
 *  com.google.common.collect.ImmutableMap
 *  org.bukkit.entity.Player
 */
package ru.den_abr.commonlib.menu;

import com.comphenix.protocol.reflect.accessors.Accessors;
import com.google.common.collect.ImmutableMap;
import org.bukkit.entity.Player;
import ru.den_abr.commonlib.CommonLib;
import ru.den_abr.commonlib.menu.event.ClickEvent;
import ru.den_abr.commonlib.menu.event.ClickHandler;
import ru.den_abr.commonlib.placeholders.PlaceholderManager;
import ru.den_abr.commonlib.utility.ItemLore;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

public abstract class AbstractMenu
implements Menu {
    public static final ItemLore AIR = new ItemLore("AIR", 1, Collections.emptyMap());
    private transient Map<Integer, String> handledSlots = new HashMap<Integer, String>();
    private transient Set<ClickHandler> handlers = new HashSet<ClickHandler>();
    private transient Map<String, MethodHandle> methodCache = new HashMap<String, MethodHandle>();
    private transient Map<String, ClickHandler> handlerCache = new HashMap<String, ClickHandler>();
    private transient boolean allowOutsideClicks = false;
    protected Map<Integer, Icon> contents = new ConcurrentHashMap<Integer, Icon>();
    private String name = "name";
    private String title = "title";
    private int rows = 0;

    @Override
    public String getTitle(Player player) {
        return PlaceholderManager.proccessString(player, this.title, true);
    }

    @Override
    public String getName() {
        return this.name;
    }

    public Map<Integer, Icon> getIcons() {
        try {
            return ImmutableMap.copyOf(this.getRawContents());
        }
        catch (ConcurrentModificationException e) {
            CommonLib.INSTANCE.getLogger().log(Level.SEVERE, "Got CME copying icons from " + this.getClass().getSimpleName() + " menu", e);
            return Collections.emptyMap();
        }
    }

    public void setIcon(int i, Icon icon) {
        this.setIcon(i, icon, null);
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setIcon(int i, Icon icon, String handler) {
        if (icon == null) {
            this.getRawContents().remove(i);
        } else {
            this.getRawContents().put(i, icon);
            if (handler != null) {
                this.handledSlots.put(i, handler);
                this.methodCache.remove(handler);
                this.handlerCache.remove(handler);
            }
        }
    }

    @Override
    public int getRows() {
        return this.rows;
    }

    public void setRows(int rows) {
        this.rows = rows;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Map<Integer, Icon> getRawContents() {
        return this.contents;
    }

    public boolean hasIcon(int slot) {
        return this.getRawContents().containsKey(slot);
    }

    public void setIcons(Map<Integer, Icon> contents) {
        this.getRawContents().clear();
        this.getRawContents().putAll(contents);
    }

    @Override
    public boolean isOutsideClicksAllowed() {
        return this.allowOutsideClicks;
    }

    @Override
    public void setAllowOutsideClicks(boolean allow) {
        this.allowOutsideClicks = allow;
    }

    public Icon getIcon(int i) {
        return this.getRawContents().get(i);
    }

    @Override
    public void onPress(ClickEvent event) {
        if (!this.hasIcon(event.getSlot())) {
            return;
        }
        int slot = event.getSlot();
        Icon i = this.getIcon(slot);
        event.setIcon(i);
        if (this.handledSlots.containsKey(slot)) {
            this.handle(event);
        } else {
            i.onPress(event);
        }
    }

    @Override
    public void registerClickHandler(ClickHandler handler) {
        this.handlers.add(handler);
    }

    @Override
    public void onOpen(Player player) {
    }

    @Override
    public void onClose(Player player) {
    }

    public MenuHolder createHolder(Player player) {
        return new MenuHolder(this, player);
    }

    private boolean hasMethod(String name, Object obj) {
        try {
            Class<?> clazz = obj.getClass();
            Method method = Accessors.getMethodAccessor(clazz, (String)name, (Class[])new Class[]{ClickEvent.class}).getMethod();
            return method.getReturnType() == Boolean.TYPE;
        }
        catch (IllegalArgumentException e) {
            return false;
        }
    }

    private MethodHandle extractInvoker(String name, Object obj) {
        Class<?> clazz = obj.getClass();
        Method method = Accessors.getMethodAccessor(clazz, (String)name, (Class[])new Class[]{ClickEvent.class}).getMethod();
        try {
            return MethodHandles.lookup().unreflect(method);
        }
        catch (IllegalAccessException e) {
            throw new RuntimeException("Clouldn't unreflect method " + method.getName() + " from class " + obj.getClass().getName(), e);
        }
    }

    private boolean handle(ClickEvent event) {
        MethodHandle found;
        String methodName = this.handledSlots.get(event.getSlot());
        ClickHandler handlerInstance = this.handlerCache.get(methodName);
        if (handlerInstance == null) {
            handlerInstance = this.handlers.stream().filter(inst -> this.hasMethod(methodName, inst)).findFirst().get();
            this.handlerCache.put(methodName, handlerInstance);
        }
        if ((found = this.methodCache.get(methodName)) == null) {
            found = this.extractInvoker(methodName, handlerInstance);
            this.methodCache.put(methodName, found);
        }
        try {
            return (boolean) found.invoke(handlerInstance, event);
        }
        catch (Throwable e) {
            e.printStackTrace();
            return false;
        }
    }
}

