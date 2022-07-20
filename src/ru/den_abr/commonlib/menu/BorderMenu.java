/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  org.bukkit.Material
 *  org.bukkit.entity.Player
 *  org.bukkit.inventory.Inventory
 *  org.bukkit.inventory.ItemStack
 */
package ru.den_abr.commonlib.menu;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import ru.den_abr.commonlib.utility.ItemLore;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class BorderMenu
extends DefaultMenu {
    static BiFunction<Player, Integer, ItemLore> NULL_PRODUCER = (player, page) -> null;
    protected transient List<Integer> contentSlots;
    protected transient List<Integer> borderSlots;
    private int verticalOffset;
    private int horisontalOffset;
    private Function<Player, ItemLore> fillerProducer = player -> null;

    public BorderMenu(int verticalOffset, int horisontalOffset) {
        this.verticalOffset = verticalOffset;
        this.horisontalOffset = horisontalOffset;
    }

    protected void mapSlots() {
        this.contentSlots = this.genContentSlots(true);
        this.borderSlots = this.genContentSlots(false);
    }

    @Override
    public void setRows(int rows) {
        super.setRows(rows);
        this.mapSlots();
    }

    @Override
    public void fill(Player player, Inventory inv) {
        super.fill(player, inv);
        ItemStack[] contents = inv.getContents();
        ItemLore filler = this.fillerProducer.apply(player);
        if (filler != null) {
            ItemStack fillerItem = filler.toItem();
            this.borderSlots.stream().filter(slot -> contents[slot] == null || contents[slot].getType() == Material.AIR).forEach(slot -> {
                contents[slot.intValue()] = fillerItem;
            });
        }
        inv.setContents(contents);
    }

    public int getVerticalOffset() {
        return this.verticalOffset;
    }

    public void setVerticalOffset(int verticalOffset) {
        this.verticalOffset = verticalOffset;
        this.mapSlots();
    }

    public int getHorisontalOffset() {
        return this.horisontalOffset;
    }

    public void setHorisontalOffset(int horisontalOffset) {
        this.horisontalOffset = horisontalOffset;
        this.mapSlots();
    }

    public void fillerMaker(Function<Player, ItemLore> producer) {
        this.fillerProducer = producer;
    }

    public boolean isInside(int slot) {
        if (this.horisontalOffset == -1 && this.verticalOffset == -1) {
            return true;
        }
        int relative = slot % 9;
        int rowNumber = slot / 9;
        boolean insideHor = this.horisontalOffset > -1 ? relative >= this.horisontalOffset && relative <= 8 - this.horisontalOffset : (insideHor = true);
        boolean insideVert = this.verticalOffset > -1 ? rowNumber >= this.verticalOffset && rowNumber <= this.getRows() - 1 - this.verticalOffset : true;
        return insideHor && insideVert;
    }

    public List<Integer> getContentSlots() {
        return this.contentSlots;
    }

    public List<Integer> getBorderSlots() {
        return this.borderSlots;
    }

    private List<Integer> genContentSlots(boolean inside) {
        return IntStream.range(0, this.getRows() * 9).filter(value -> inside == this.isInside(value)).boxed().collect(Collectors.toList());
    }
}

