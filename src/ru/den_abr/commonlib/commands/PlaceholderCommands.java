/*
 * Decompiled with CFR 0.150.
 *
 * Could not load the following classes:
 *  com.google.common.base.Joiner
 *  org.apache.commons.io.FilenameUtils
 *  org.bukkit.ChatColor
 *  org.bukkit.plugin.Plugin
 */
package ru.den_abr.commonlib.commands;

import com.google.common.base.Joiner;
import commonlib.com.minnymin.command.Command;
import commonlib.com.minnymin.command.CommandArgs;
import commonlib.com.minnymin.command.CommandException;
import org.apache.commons.io.FilenameUtils;
import org.bukkit.ChatColor;
import org.bukkit.plugin.Plugin;
import ru.den_abr.commonlib.CommonLib;
import ru.den_abr.commonlib.placeholders.ClassPlaceholderInfo;
import ru.den_abr.commonlib.placeholders.PlaceholderInfo;
import ru.den_abr.commonlib.placeholders.PlaceholderManager;
import ru.den_abr.commonlib.placeholders.custom.LoadablePlaceholder;
import ru.den_abr.commonlib.utility.AutoCatch;

import java.io.File;
import java.util.Arrays;
import java.util.stream.Collectors;

public class PlaceholderCommands {
    @Command(name="clpl", permission="commonlib.placeholders")
    public void help(CommandArgs args) {
        args.sendMessage(ChatColor.GOLD + "/clpl list (PLUGIN) - " + ChatColor.GREEN + "показать список плагинов с плейсхолдерами или плейсхолдеры PLUGIN");
        args.sendMessage(ChatColor.GOLD + "/clpl info [ID] (PLUGIN) - " + ChatColor.GREEN + "показать информацию по плейсхолдеру ID");
        args.sendMessage(ChatColor.GOLD + "/clpl parse [TEXT] - " + ChatColor.GREEN + "преобразовать все плейсхолдеры в TEXT и показать результат");
        args.sendMessage(ChatColor.GOLD + "/clpl load [FILE] - " + ChatColor.GREEN + "загрузить плейсхолдер из FILE");
        args.sendMessage(ChatColor.GOLD + "/clpl unload [ID] - " + ChatColor.GREEN + "выгрузить плейсхолдер ID");
    }

    @Command(name="clpl.list", permission="commonlib.placeholders.list")
    public void placeholders(CommandArgs args) throws CommandException {
        if (args.length() == 0) {
            args.sendMessage(ChatColor.GREEN + "Плагины, зарегистрировавшие свои плейсхолдеры: " + ChatColor.GOLD + Joiner.on(", ").join(PlaceholderManager.getAllPlaceholders().keySet().stream().map(Plugin::getName).collect(Collectors.toList())));
        } else {
            Plugin p = PlaceholderManager.getAllPlaceholders().keySet().stream().filter(pl -> pl.getName().equalsIgnoreCase(args.getArgs(0))).findAny().orElse(null);
            if (p == null) {
                args.halt(ChatColor.RED + "На этот плагин не зарегистрирован ни один плейсхолдер");
            }
            args.sendMessage(ChatColor.GREEN + "Плейсхолдеры " + ChatColor.YELLOW + p.getName() + ChatColor.GREEN + ": " + ChatColor.GOLD + Joiner.on(", ").join(PlaceholderManager.getRegisteredPlaceholders(p).stream().map(PlaceholderInfo::getId).collect(Collectors.toList())));
        }
    }

    @Command(name="clpl.info", permission="commonlib.placeholders.info")
    public void info(CommandArgs args) throws CommandException {
        if (args.length() == 0) {
            args.halt(ChatColor.RED + "Укажи ID плейсхолдера");
        }
        PlaceholderInfo info = null;
        if (args.length() == 1) {
            info = PlaceholderManager.getAllPlaceholders().values().stream().filter(pl -> pl.getId().equalsIgnoreCase(args.getArgs(0))).findAny().orElse(null);
        } else {
            Plugin p = PlaceholderManager.getAllPlaceholders().keySet().stream().filter(pl -> pl.getName().equalsIgnoreCase(args.getArgs(1))).findAny().orElse(null);
            if (p == null) {
                args.halt(ChatColor.RED + "На этот плагин не зарегистрирован ни один плейсхолдер");
            }
            info = PlaceholderManager.getRegisteredPlaceholders(p).stream().filter(pl -> pl.getId().equalsIgnoreCase(args.getArgs(0))).findAny().orElse(null);
        }
        if (info == null) {
            args.halt(ChatColor.RED + "Плейсхолдер не найден");
        }
        args.sendMessage(ChatColor.GREEN + "ID: " + ChatColor.GOLD + info.getId());
        args.sendMessage(ChatColor.GREEN + "Плагин: " + ChatColor.GOLD + info.getHolder().getName());
        if (info instanceof ClassPlaceholderInfo) {
            ClassPlaceholderInfo clpl = (ClassPlaceholderInfo)info;
            LoadablePlaceholder lpl = clpl.getHandlingPlaceholder();
            args.sendMessage(ChatColor.GREEN + "Версия: " + ChatColor.GOLD + lpl.getVersion());
            args.sendMessage(ChatColor.GREEN + "Автор: " + ChatColor.GOLD + lpl.getAuthor());
            args.sendMessage(ChatColor.GREEN + "Описание: " + ChatColor.GOLD + lpl.getDescription());
            args.sendMessage(ChatColor.GREEN + "Зависимости: " + ChatColor.GOLD + Joiner.on(", ").join(lpl.getDependencies()));
            args.sendMessage(ChatColor.GREEN + "P: " + ChatColor.GOLD + lpl.requiresPlayer());
        }
        args.sendMessage(ChatColor.GREEN + "A: " + ChatColor.GOLD + info.mayCallAsync());
        if (info.getDescription() != null) {
            args.sendMessage(ChatColor.GREEN + "Подробнее: ");
            args.sendMessage(info.getDescription());
        }
    }

    @Command(name="clpl.parse", permission="commonlib.plparse")
    public void plparse(CommandArgs args) {
        args.sendMessage(PlaceholderManager.proccessString(args.getPlayer(), Joiner.on(' ').join(args.getArgs()), true));
    }

    @Command(name="clpl.unload", permission="commonlib.placeholders.loadunload")
    public void unload(CommandArgs args) throws CommandException {
        PlaceholderInfo pl;
        if (args.length() == 0) {
            args.halt(ChatColor.RED + "Укажи ID плейсхолдера");
        }
        if (!((pl = PlaceholderManager.getPlaceholder(CommonLib.INSTANCE, args.getArgs(0))) instanceof ClassPlaceholderInfo)) {
            args.halt(ChatColor.RED + "Плейсхолдер не найден");
        }
        PlaceholderManager.unloadClassPlaceholder((ClassPlaceholderInfo)pl);
        args.sendMessage(ChatColor.GREEN + "Плейсхолдер выгружен");
    }

    @Command(name="clpl.unload.all", permission="commonlib.placeholders.loadunload")
    public void unloadAll(CommandArgs args) throws CommandException {
        PlaceholderManager.unloadFromClasses();
        args.sendMessage(ChatColor.GREEN + "Все плейсхолдеры были выгружены");
    }

    @Command(name="clpl.reload", permission="commonlib.placeholders.loadunload")
    public void reload(CommandArgs args) throws CommandException {
        PlaceholderInfo pl;
        if (args.length() == 0) {
            args.halt(ChatColor.RED + "Укажи ID плейсхолдера");
        }
        if (!((pl = PlaceholderManager.getPlaceholder(CommonLib.INSTANCE, args.getArgs(0))) instanceof ClassPlaceholderInfo)) {
            args.halt(ChatColor.RED + "Плейсхолдер не найден");
        }
        ClassPlaceholderInfo cpl = (ClassPlaceholderInfo)pl;
        PlaceholderManager.unloadClassPlaceholder(cpl);
        File file = cpl.getHandlingPlaceholder().getFile();
        if (!file.exists()) {
            args.halt(ChatColor.RED + "Перезагружать не из чего.");
        }
        LoadablePlaceholder lpl = PlaceholderManager.loadPlaceholderFromFile(file);
        PlaceholderManager.registerClassPlaceholder(lpl);
        args.sendMessage(ChatColor.GREEN + "Плейсхолдер перезагружен.");
        args.sendMessage(ChatColor.GOLD + pl.getId() + " v" + lpl.getVersion() + " by " + ChatColor.LIGHT_PURPLE + lpl.getAuthor());
    }

    @Command(name="clpl.reload.all", permission="commonlib.placeholders.loadunload")
    public void reloadAll(CommandArgs args) throws CommandException {
        PlaceholderManager.unloadFromClasses();
        PlaceholderManager.loadFromClasses();
        args.sendMessage(ChatColor.GREEN + "Все плейсхолдеры были перезагружены");
    }

    @Command(name="clpl.load", permission="commonlib.placeholders.loadunload")
    public void loadPlaceholder(CommandArgs args) throws CommandException {
        LoadablePlaceholder pl;
        String fileName;
        File file;
        if (args.length() == 0) {
            args.halt(ChatColor.RED + "Укажи имя файла");
        }
        if ((file = this.findFile(fileName = args.getArgs(0))) == null) {
            args.halt(ChatColor.RED + "Файл не найден");
        }
        if ((pl = PlaceholderManager.loadPlaceholderFromFile(file)) != null) {
            PlaceholderInfo current = PlaceholderManager.getPlaceholder(CommonLib.INSTANCE, pl.getId());
            if (current != null) {
                String version = current instanceof ClassPlaceholderInfo ? "v" + ((ClassPlaceholderInfo)current).getHandlingPlaceholder().getVersion() : "N/A";
                args.halt(ChatColor.RED + "Плейсхолдер с таким ID(Старый: " + ChatColor.YELLOW + version + ChatColor.RED + ", новый " + ChatColor.YELLOW + "v" + pl.getVersion() + ChatColor.RED + ") уже загружен");
                AutoCatch.suppress(pl.getLoader()::close);
            }
            PlaceholderManager.registerClassPlaceholder(pl);
            args.sendMessage(ChatColor.GREEN + "Плейсхолдер загружен.");
            args.sendMessage(ChatColor.GOLD + pl.getId() + " v" + pl.getVersion() + " by " + ChatColor.LIGHT_PURPLE + pl.getAuthor());
        } else {
            args.halt(ChatColor.RED + "Плейсхолдер не загружен. Подробности в консоли.");
        }
    }

    private File findFile(String name) {
        return Arrays.stream(PlaceholderManager.getPlaceholdersFolder().listFiles()).filter(f -> FilenameUtils.getBaseName(f.getName()).equalsIgnoreCase(name)).findAny().orElse(null);
    }
}

