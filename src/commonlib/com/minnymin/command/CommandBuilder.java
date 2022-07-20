/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  org.bukkit.plugin.Plugin
 */
package commonlib.com.minnymin.command;

import com.google.common.base.Preconditions;
import org.bukkit.plugin.Plugin;
import ru.den_abr.commonlib.CommonLib;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

public class CommandBuilder {
    private static final Method CONSUMER_ACCEPT = Arrays.stream(Consumer.class.getDeclaredMethods()).filter(m -> m.getName().equals("accept")).findFirst().orElseThrow(UnsupportedOperationException::new);
    protected String name;
    protected Plugin plugin;
    protected List<String> aliases = Collections.EMPTY_LIST;
    protected String permission = "";
    protected String nopermissionMessage = "";
    protected boolean inGameOnly = false;

    public CommandBuilder(String name) {
        this.name = Preconditions.checkNotNull(name, "Name cannot be null");
    }

    public CommandBuilder inGameOnly() {
        this.inGameOnly = true;
        return this;
    }

    public CommandBuilder plugin(Plugin plugin) {
        this.plugin = Preconditions.checkNotNull(plugin, "Plugin cannot be null");
        return this;
    }

    public CommandBuilder aliases(String ... aliases) {
        this.aliases = Arrays.asList(Preconditions.checkNotNull(aliases, "Aliases cannot be null"));
        return this;
    }

    public CommandBuilder permission(String perm) {
        this.permission = Preconditions.checkNotNull(perm, "Permission cannot be null");
        return this;
    }

    public CommandBuilder noPermission(String message) {
        this.nopermissionMessage = Preconditions.checkNotNull(message, "Message cannot be null");
        return this;
    }

    public void register(Consumer<CommandArgs> handler) {
        Preconditions.checkNotNull(this.plugin, "Plugin is not specified");
        Executor exec = new Executor(this.plugin, handler, CONSUMER_ACCEPT, this.name, this.aliases, this.permission, this.nopermissionMessage, this.inGameOnly);
        CommandFramework fw = CommonLib.commands(this.plugin);
        fw.registerCommand(exec, this.name, CONSUMER_ACCEPT, handler);
        this.aliases.forEach(a -> fw.registerCommand(exec, a, CONSUMER_ACCEPT, handler));
    }

    public String name() {
        return this.name;
    }

    public List<String> aliases() {
        return this.aliases;
    }

    public String noPermissionMessage() {
        return this.nopermissionMessage;
    }

    public String permission() {
        return this.permission;
    }

    public Plugin plugin() {
        return this.plugin;
    }
}

