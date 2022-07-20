package ru.den_abr.commonlib.utility;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class PlayerMap<V> implements Map<Player, V>
{
    private final Map<String, V> delegate;
    
    public PlayerMap(final Map<String, V> delegate) {
        this.delegate = (Map<String, V>)Preconditions.checkNotNull((Object)delegate);
    }
    
    public PlayerMap() {
        this(new HashMap());
    }
    
    public Map<String, V> getDelegate() {
        return this.delegate;
    }
    
    @Override
    public int size() {
        return this.delegate.size();
    }
    
    @Override
    public boolean isEmpty() {
        return this.delegate.isEmpty();
    }
    
    @Override
    public boolean containsKey(final Object key) {
        return this.delegate.containsKey(this.getNameFromPlayer(key));
    }
    
    @Override
    public boolean containsValue(final Object value) {
        return this.delegate.containsValue(value);
    }
    
    @Override
    public V get(final Object key) {
        return this.delegate.get(this.getNameFromPlayer(key));
    }
    
    @Override
    public V put(final Player key, final V value) {
        return this.delegate.put(key.getName(), value);
    }
    
    @Override
    public V remove(final Object key) {
        return this.delegate.remove(this.getNameFromPlayer(key));
    }
    
    @Override
    public V getOrDefault(final Object key, final V defaultValue) {
        return this.delegate.getOrDefault(this.getNameFromPlayer(key), defaultValue);
    }
    
    @Override
    public void putAll(final Map<? extends Player, ? extends V> m) {
        final ImmutableMap.Builder<String, V> b = (ImmutableMap.Builder<String, V>)new ImmutableMap.Builder();
        m.forEach((p, v) -> b.put(this.getNameFromPlayer(p), v));
        this.delegate.putAll(b.build());
    }
    
    @Override
    public void clear() {
        this.delegate.clear();
    }
    
    @Override
    public Set<Player> keySet() {
        return this.delegate.keySet().stream().map(Bukkit::getPlayerExact).filter(Objects::nonNull).collect(Collectors.toSet());
    }
    
    public Set<String> nameKeySet() {
        return this.delegate.keySet();
    }
    
    @Override
    public Collection<V> values() {
        return this.delegate.values();
    }
    
    public Set<String> getOfflinePlayers() {
        return this.delegate.keySet().stream().filter(n -> Bukkit.getPlayerExact(n) == null).collect(Collectors.toSet());
    }
    
    public PlayerMap<V> removeOffline() {
        this.getOfflinePlayers().forEach(this.delegate::remove);
        return this;
    }

    public PlayerMap<V> removeOfflineAndApplyAction(final Consumer<Entry<String, V>> action) {
        this.getOfflinePlayers().stream().map(name -> new AbstractMap.SimpleEntry<>(name, this.delegate.remove(name))).filter(e -> e.getValue() != null).forEach(action);
        return this;
    }

    
    public V removeByName(final String name) {
        final Player p = Bukkit.getPlayerExact(name);
        if (p == null) {
            return this.delegate.remove(name);
        }
        return this.remove(p);
    }

    @Override
    public Set<Entry<Player, V>> entrySet() {
        return this.delegate.entrySet().stream().map(e -> new AbstractMap.SimpleEntry<>(Bukkit.getPlayerExact(e.getKey()), e.getValue())).filter(e -> Objects.nonNull(e.getKey())).collect(Collectors.toSet());
    }
    
    public Set<Entry<String, V>> nameEntrySet() {
        return this.delegate.entrySet();
    }
    
    private String getNameFromPlayer(final Object key) {
        Preconditions.checkNotNull(key, "PlayerMap doesn't support null keys");
        if (key instanceof Player) {
            return ((Player)key).getName();
        }
        Preconditions.checkArgument(key instanceof String, "PlayerMap supports only Player or String(name) keys");
        return (String)key;
    }
    
    public static <V> PlayerMap<V> concurrentSingle() {
        return new PlayerMap<V>(new ConcurrentHashMap<String, V>(16, 0.75f, 1));
    }
}
