/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.google.common.io.Files
 *  com.google.gson.Gson
 *  com.google.gson.reflect.TypeToken
 *  org.bukkit.Bukkit
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.scheduler.BukkitTask
 */
package ru.den_abr.commonlib.cooldown;

import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;
import ru.den_abr.commonlib.utility.GsonUtil;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

public class FileCooldownManager
extends CooldownManager {
    private File file;
    private Gson gson = new Gson();
    private boolean dirty = false;
    private int savePeriod;
    private BukkitTask saveTask;


    @Override
    public void init() {
        this.cooldowns.clear();
        if (this.file.exists()) {
            Map<String, Map<String, Long>> raw = this.gson.fromJson(GsonUtil.readFile(this.file), (new TypeToken<Map<String, Map<String, Long>>>() {}).getType());
            raw.values().stream().map(Map::entrySet).forEach((e) -> e.removeIf((entry) -> entry.getValue() < System.currentTimeMillis()));
            raw.entrySet().forEach((entry) -> (entry.getValue()).entrySet().forEach((cd) -> this.cooldowns.put(entry.getKey(), cd.getKey(), cd.getValue())));
            this.cancelTask();
            this.saveTask = Bukkit.getScheduler().runTaskTimerAsynchronously(this.holder, this::save, (this.savePeriod * 20), (this.savePeriod * 20));
        }
    }

    private void cancelTask() {
        Optional.ofNullable(this.saveTask).ifPresent(BukkitTask::cancel);
    }

    @Override
    public void setCooldown(String player, String action, long time, TimeUnit unit) {
        super.setCooldown(player, action, time, unit);
        this.dirty = true;
    }

    @Override
    public void removeCooldown(String player, String action) {
        super.removeCooldown(player, action);
        this.dirty = true;
    }

    @Override
    public void removeAllCooldowns(String player) {
        super.removeAllCooldowns(player);
        this.dirty = true;
    }

    public void save() {
        if (!this.dirty) {
            return;
        }
        try {
            Files.write((CharSequence)this.gson.toJson((Object)this.cooldowns.rowMap()), (File)this.file, (Charset)StandardCharsets.UTF_8);
            this.dirty = false;
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}

