/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.HashBasedTable
 *  com.google.common.collect.Table
 */
package ru.den_abr.commonlib.cooldown;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import ru.den_abr.commonlib.CommonPlugin;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

public class CooldownManager {
    protected Table<String, String, Long> cooldowns = HashBasedTable.create();
    protected CommonPlugin holder;
    protected TimeUnit defaultUnit;
    protected long defaultCooldown;
    protected String biasPrefix;

    public void init() {
    }

    public boolean isCooldown(String player, String action) {
        return this.getCooldown(player, action).isPresent();
    }

    public Optional<Long> getCooldown(String player, String action) {
        return Optional.ofNullable(this.cooldowns.get(player.toLowerCase(), action.toLowerCase())).filter(t -> {
            if (System.currentTimeMillis() > t) {
                return true;
            }
            this.removeCooldown(player, action);
            return false;
        });
    }

    public void removeCooldown(String player, String action) {
        this.cooldowns.remove(player.toLowerCase(), action.toLowerCase());
    }

    public void clear() {
        this.cooldowns.clear();
    }

    public void removeAllCooldowns(String player) {
        this.cooldowns.row(player.toLowerCase()).clear();
    }

    public void setCooldown(String player, String action) {
        this.setCooldown(player, action, this.defaultCooldown);
    }

    public void setCooldown(String player, String action, long time) {
        this.setCooldown(player, action, time, this.defaultUnit);
    }

    public void setCooldown(String player, String action, long time, TimeUnit unit) {
        this.cooldowns.put(player.toLowerCase(), action.toLowerCase(), (System.currentTimeMillis() + unit.toMillis(time)));
    }
}

