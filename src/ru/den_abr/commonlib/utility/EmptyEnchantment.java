package ru.den_abr.commonlib.utility;

import com.comphenix.protocol.reflect.accessors.Accessors;
import com.comphenix.protocol.utility.MinecraftVersion;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import ru.den_abr.commonlib.CommonLib;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.logging.Level;

public class EmptyEnchantment {
    public static Enchantment EMPTY;
    public static final String NAME = "glowing";
    public static NamespacedKey KEY;

    private static Class[] getConstructorTypes() {
        if (MinecraftVersion.getCurrentVersion().getMinor() <= 12) {
            return new Class[]{Integer.TYPE};
        }
        return new Class[]{NamespacedKey.class};
    }

    private static Object[] getConstructorArgs() {
        if (MinecraftVersion.getCurrentVersion().getMinor() <= 12) {
            return new Object[]{1488};
        }
        return new Object[]{KEY};
    }

    private static Object handleIntercept(Object o, Method method, Object[] objects, MethodProxy proxy) throws Throwable {
        switch (method.getName()) {
            case "toString": {
                return "EmptyEnchantment";
            }
            case "canEnchantItem": {
                return true;
            }
            case "getName": {
                return NAME;
            }
            case "getId": {
                return 1488;
            }
            case "getStartLevel":
            case "getMaxLevel": {
                return 1;
            }
        }
        return proxy.invokeSuper(o, objects);
    }

    static {
        KEY = new NamespacedKey(CommonLib.INSTANCE, NAME);
        Enhancer enhancer = new Enhancer();
        enhancer.setCallback((MethodInterceptor)(arg_0, arg_1, arg_2, arg_3) -> EmptyEnchantment.handleIntercept(arg_0, arg_1, arg_2, arg_3));
        enhancer.setClassLoader(((Object) CommonLib.INSTANCE).getClass().getClassLoader());
        enhancer.setSuperclass(Enchantment.class);
        try {
            EMPTY = (Enchantment)enhancer.create(EmptyEnchantment.getConstructorTypes(), EmptyEnchantment.getConstructorArgs());
        }
        catch (Exception e) {
            CommonLib.INSTANCE.getLogger().log(Level.SEVERE, "Unsupported version " + MinecraftVersion.getCurrentVersion(), e);
        }
        if (MinecraftVersion.getCurrentVersion().getMinor() <= 12) {
            Map byId = (Map)Accessors.getFieldAccessor(Enchantment.class, "byId", true).get(null);
            byId.put(1488, EMPTY);
        } else {
            Map byKey = (Map)Accessors.getFieldAccessor(Enchantment.class, "byKey", true).get(null);
            byKey.put(KEY, EMPTY);
        }
        Map byName = (Map)Accessors.getFieldAccessor(Enchantment.class, "byName", true).get(null);
        byName.put(NAME, EMPTY);
    }
}