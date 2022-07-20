package ru.den_abr.commonlib;

import commonlib.com.minnymin.command.CommandBuilder;
import commonlib.com.minnymin.command.CommandFramework;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import ru.den_abr.commonlib.configuration.Config;
import ru.den_abr.commonlib.messages.MessageKey;
import ru.den_abr.commonlib.messages.MessageRegistry;
import ru.den_abr.commonlib.messages.Messages;
import ru.den_abr.commonlib.placeholders.Placeholder;
import ru.den_abr.commonlib.placeholders.PlaceholderManager;
import ru.den_abr.commonlib.utility.UtilityMethods;

import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;

public abstract class CommonPlugin extends JavaPlugin implements Listener
{
    public void registerCommands(final Object... commandsContainer) {
        Arrays.stream(commandsContainer).forEach(this.commands()::registerCommands);
    }
    
    public <T extends Listener> T registerListener(final T listener) {
        this.getServer().getPluginManager().registerEvents(listener, this);
        return listener;
    }
    
    public void registerListeners(final Listener... listeners) {
        Arrays.stream(listeners).forEach(this::registerListener);
    }
    
    public CommandBuilder buildCommand(final String name) {
        return new CommandBuilder(name).plugin(this);
    }
    
    public CommandFramework commands() {
        return CommonLib.commands(this);
    }
    
    public <T extends Config> T loadSettings(final String file, final Class<T> clazz) {
        return Config.load(this, file, clazz);
    }
    
    public Messages loadMessages(final String file, final MessageKey... keys) {
        return this.loadMessages(file, Arrays.asList(keys));
    }
    
    public Messages loadMessages(final String file, final Collection<MessageKey> keys) {
        return this.loadMessages(this.getName().toLowerCase(), file, keys);
    }
    
    public Messages loadMessages(final String tag, final String file, final MessageKey... keys) {
        return this.loadMessages(tag, file, Arrays.asList(keys));
    }
    
    public Messages loadMessages(final String tag, final String file, final Collection<MessageKey> keys) {
        final Messages messages = MessageRegistry.load(this, file, keys);
        MessageRegistry.addMessages(tag, messages);
        return messages;
    }
    
    public void installLibs(final String... directUrls) {
        this.installLibs("libs", directUrls);
    }
    
    public void installLibs(final String folder, final String... directUrls) {
        this.installLibs(false, folder, directUrls);
    }
    
    public void installLibs(final boolean force, final String folder, final String... directUrls) {
        this.installLibs(force, false, folder, directUrls);
    }
    
    public void installLibs(final boolean force, final boolean toSystem, final String folder, final String... directUrls) {
        UtilityMethods.installLib(this, force, toSystem, folder, directUrls);
    }
    
    public void preloadClasses() {
        UtilityMethods.forciblyLoadAllClasses(this);
    }
    
    public boolean checkLib(final Callable<Object> check) {
        try {
            check.call();
            return true;
        }
        catch (Exception | NoClassDefFoundError ex) {
            return false;
        }
    }
    
    public boolean checkLib(final Runnable check) {
        return this.checkLib(Executors.callable(check));
    }
    
    public void registerPlaceholder(final String id, final Placeholder replacer) {
        this.registerPlaceholder(id, false, replacer);
    }
    
    public void registerPlaceholder(final String id, final boolean forceSync, final Placeholder replacer) {
        this.registerPlaceholder(id, forceSync, true, replacer);
    }
    
    public void registerPlaceholder(final String id, final boolean forceSync, final boolean requiresPlayer, final Placeholder replacer) {
        PlaceholderManager.registerPlaceholder(this, id, replacer, forceSync, requiresPlayer);
    }
}
