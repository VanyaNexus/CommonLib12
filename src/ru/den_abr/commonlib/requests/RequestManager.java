package ru.den_abr.commonlib.requests;

import com.google.common.base.Supplier;
import com.google.common.collect.Table;
import com.google.common.collect.Tables;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import ru.den_abr.commonlib.CommonLib;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class RequestManager {
    private final Table<Player, Class<? extends Request>, BukkitTask> requests = RequestManager.newConcurrentTable();

    public void doRequest(Player p, Request req, Runnable onSuccess, Consumer<Throwable> onException) {
        if (this.requests.contains(p, req.getClass())) {
            return;
        }
        this.requests.put(p, req.getClass(), Bukkit.getScheduler().runTaskAsynchronously(CommonLib.INSTANCE, () -> {
            try {
                req.run();
                onSuccess.run();
            }
            catch (Throwable e) {
                onException.accept(e);
            }
            finally {
                this.requests.remove(p, req.getClass());
            }
        }));
    }

    public void doRequest(Player p, Request req) {
        this.doRequest(p, req, () -> {}, t -> t.printStackTrace());
    }

    public void cancelRequests(Player p) {
        this.requests.row(p).forEach((req, task) -> {
            task.cancel();
            this.requests.remove(p, req);
        });
    }

    public void cancelAll() {
        this.requests.values().forEach(BukkitTask::cancel);
        this.requests.clear();
    }

    public static <R, C, V> Table<R, C, V> newConcurrentTable() {
        return Tables.newCustomTable(new ConcurrentHashMap(), new Supplier<Map<C, V>>(){

            public Map<C, V> get() {
                return new ConcurrentHashMap();
            }
        });
    }

}