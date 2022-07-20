/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.comphenix.protocol.PacketType$Play$Server
 *  com.comphenix.protocol.events.PacketContainer
 *  com.comphenix.protocol.reflect.accessors.Accessors
 *  com.comphenix.protocol.wrappers.WrappedChatComponent
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.BiMap
 *  com.google.common.collect.ImmutableBiMap
 *  com.google.common.collect.ImmutableBiMap$Builder
 *  com.google.common.collect.Iterables
 *  com.google.common.collect.Iterators
 *  com.google.common.collect.UnmodifiableIterator
 *  org.bukkit.Material
 *  org.bukkit.entity.Player
 *  org.bukkit.inventory.Inventory
 *  org.bukkit.inventory.ItemStack
 */
package ru.den_abr.commonlib.menu;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.reflect.accessors.Accessors;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.google.common.base.Preconditions;
import com.google.common.collect.*;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import ru.den_abr.commonlib.menu.event.ClickEvent;
import ru.den_abr.commonlib.placeholders.PlaceholderManager;
import ru.den_abr.commonlib.utility.ItemLore;
import ru.den_abr.commonlib.utility.UtilityMethods;

import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.BiFunction;
import java.util.stream.IntStream;

public class PaginatedMenu
extends BorderMenu {
    private List<Icon> items;
    private int pageSize;
    private int backSlot;
    private int forwardSlot;
    private transient BiMap<Integer, Integer> slotMappings;
    private transient BiFunction<Player, Integer, ItemLore> backProducer = NULL_PRODUCER;
    private transient BiFunction<Player, Integer, ItemLore> forwardProducer = NULL_PRODUCER;
    private transient BiFunction<Player, Integer, String> titleProducer = (player, page) -> super.getTitle((Player)player);

    public PaginatedMenu(int pageSize, int backSlot, int forwardSlot) {
        this(pageSize, backSlot, forwardSlot, -1, -1);
    }

    public PaginatedMenu(int pageSize, int backSlot, int forwardSlot, int verticalOffset, int horisontalOffset) {
        super(verticalOffset, horisontalOffset);
        this.pageSize = pageSize;
        this.forwardSlot = forwardSlot;
        this.backSlot = backSlot;
        this.setItems(new CopyOnWriteArrayList<Icon>());
        this.mapSlots();
    }

    @Override
    protected void mapSlots() {
        super.mapSlots();
        ImmutableBiMap.Builder builder = ImmutableBiMap.builder();
        UnmodifiableIterator indexes = Iterators.forArray((Object[])IntStream.range(0, this.pageSize).boxed().toArray(Integer[]::new));
        this.contentSlots.stream().filter(arg_0 -> PaginatedMenu.lambda$mapSlots$2(indexes, arg_0)).forEachOrdered(arg_0 -> PaginatedMenu.lambda$mapSlots$3(builder, indexes, arg_0));
        this.slotMappings = builder.build();
    }

    public String getTitle(Player player, int page) {
        return this.titleProducer.apply(player, page);
    }

    @Override
    public String getTitle(Player player) {
        return this.getTitle(player, this.getPlayerPage(player));
    }

    public PaginatedMenu titleMaker(BiFunction<Player, Integer, String> producer) {
        this.titleProducer = Preconditions.checkNotNull(producer);
        return this;
    }

    public PaginatedMenu backMaker(BiFunction<Player, Integer, ItemLore> producer) {
        this.backProducer = Preconditions.checkNotNull(producer);
        return this;
    }

    public PaginatedMenu forwardMaker(BiFunction<Player, Integer, ItemLore> producer) {
        this.forwardProducer = Preconditions.checkNotNull(producer);
        return this;
    }

    public List<Icon> getItems() {
        return this.items;
    }

    public void setItems(List<Icon> items) {
        Preconditions.checkNotNull(items);
        this.items = items;
    }

    public int getPageSize() {
        return this.pageSize;
    }

    public void setPageSize(int pageSize) {
        if (this.pageSize == pageSize) {
            return;
        }
        this.pageSize = pageSize;
    }

    public int getForwardSlot() {
        return this.forwardSlot;
    }

    public void setForwardSlot(int forwardSlot) {
        this.forwardSlot = forwardSlot;
    }

    public int getBackSlot() {
        return this.backSlot;
    }

    public void setBackSlot(int backSlot) {
        this.backSlot = backSlot;
    }

    public ItemLore getPageForward(Player player, int page) {
        return this.forwardProducer.apply(player, page);
    }

    public ItemLore getPageBack(Player player, int page) {
        return this.backProducer.apply(player, page);
    }

    public int getPages() {
        int contentSize = this.items.size();
        if (contentSize % this.pageSize == 0) {
            return contentSize / this.pageSize;
        }
        return contentSize / this.pageSize + 1;
    }

    public int getPlayerPage(Player player) {
        PagedHolder holder = (PagedHolder)this.getPlayerHolder(player);
        if (holder != null) {
            return holder.getPage();
        }
        return -1;
    }

    @Override
    public PagedHolder createHolder(Player player) {
        return new PagedHolder(this, player);
    }

    @Override
    public void fill(Player player, Inventory inv) {
        ItemLore item;
        super.fill(player, inv);
        PagedHolder holder = (PagedHolder)this.getPlayerHolder(player);
        int page = holder.getPage();
        int pages = this.getPages();
        if (page >= pages) {
            holder.setPage(pages - 1);
            page = holder.getPage();
        }
        ItemStack[] contents = inv.getContents();
        Iterator<Icon> pageIcons = UtilityMethods.getPage(this.items, page, this.pageSize).iterator();
        this.contentSlots.stream().filter(i -> pageIcons.hasNext()).forEach(slot -> {
            contents[slot.intValue()] = this.processItem(pageIcons.next(), player);
        });
        if (this.forwardSlot >= 0 && page + 1 < pages && (item = this.getPageForward(player, page + 1)) != null) {
            contents[this.forwardSlot] = UtilityMethods.color(item.toItem());
        }
        if (this.backSlot >= 0 && page > 0 && (item = this.getPageBack(player, page - 1)) != null) {
            contents[this.backSlot] = UtilityMethods.color(item.toItem());
        }
        inv.setContents(contents);
        String title = this.getTitle(player, page);
        if (!Objects.equals(title, inv.getTitle())) {
            Object handle = Accessors.getMethodAccessor(inv.getClass(), "getInventory", new Class[0]).invoke(inv);
            Accessors.getFieldAccessor(handle.getClass(), "title", true).set(handle, title);
            PacketContainer pc = new PacketContainer(PacketType.Play.Server.OPEN_WINDOW);
            pc.getIntegers().write(0, holder.getOpenId());
            pc.getStrings().write(0, "minecraft:chest");
            WrappedChatComponent wcc = WrappedChatComponent.fromText(title);
            pc.getChatComponents().write(0, wcc);
            pc.getIntegers().write(1, inv.getSize());
            UtilityMethods.sendPacket(player, pc);
        }
    }

    @Override
    public void onPress(ClickEvent event) {
        List<Icon> pageItems;
        int index;
        Player player = event.getPlayer();
        int slot = event.getSlot();
        int page = this.getPlayerPage(player);
        int pages = this.getPages();
        boolean shift = event.isShiftClick();
        if (slot == this.backSlot) {
            if (page > 0) {
                this.setPage(player, shift ? 0 : page - 1);
            }
            return;
        }
        if (slot == this.forwardSlot) {
            if (page + 1 < pages) {
                this.setPage(player, shift ? pages - 1 : page + 1);
            }
            return;
        }
        if (this.slotMappings.containsKey(slot) && page >= 0 && (index = this.slotMappings.get(slot).intValue()) < (pageItems = UtilityMethods.getPage(this.items, page, this.pageSize)).size()) {
            Icon icon = pageItems.get(index);
            event.setIcon(icon);
            icon.onPress(event);
            return;
        }
        super.onPress(event);
    }

    public void open(Player player, int page) {
        page = Math.min(this.getPages() - 1, page);
        PagedHolder holder = (PagedHolder)this.getPlayerHolder(player);
        if (holder == null) {
            holder = this.createHolder(player);
            holder.setPage(page);
            this.prepareHolder(holder);
        } else {
            holder.setPage(page);
        }
        super.open(player);
    }

    public void setPage(Player player, int page) {
        Preconditions.checkArgument((page >= 0 ? 1 : 0) != 0, "invalid page: " + page);
        PagedHolder holder = (PagedHolder)this.getPlayerHolder(player);
        if (holder == null) {
            return;
        }
        int pages = this.getPages();
        page = Math.min(page, Math.max(pages - 1, 0));
        if (holder.getPage() != page) {
            holder.setPage(page);
            this.refreshContents(player);
        }
    }

    @Override
    public void refreshIcons(Player p, int ... slots) {
        PagedHolder holder = (PagedHolder)this.getPlayerHolder(p);
        if (holder == null || slots.length == 0) {
            return;
        }
        Inventory inv = holder.getInventory();
        for (int slot : slots) {
            if (this.isInside(slot)) {
                int index = this.slotToIndex(slot, holder.getPage());
                ItemStack is = Optional.ofNullable(Iterables.get(this.items, index, null)).map(i -> i.getItemStack(p)).map(i -> {
                    if (this.isProccessPlaceholders()) {
                        return PlaceholderManager.proccessItemStack(p, i, true, this.isSkullPlaceholders());
                    }
                    return i;
                }).orElseGet(() -> new ItemStack(Material.AIR));
                inv.setItem(slot, is);
                this.notifySlot(p, slot, is, holder.getOpenId());
                continue;
            }
            super.refreshIcons(p, slot);
        }
    }

    public int getContentPage(Icon icon) {
        int index = this.getContentIndex(icon);
        if (index == -1) {
            return -1;
        }
        return index / this.pageSize;
    }

    public int getContentIndex(Icon icon) {
        return this.items.indexOf(icon);
    }

    public int getContentSlot(Icon icon) {
        int index = this.getContentIndex(icon);
        if (index == -1) {
            return -1;
        }
        int page = index / this.pageSize;
        return this.indexToSlot(index, page);
    }

    public int slotToIndex(int slot, int page) {
        int localIndex = this.slotMappings.get(slot);
        int startIndex = this.pageSize * page;
        return startIndex + localIndex;
    }

    public int indexToSlot(int index, int page) {
        int startIndex = this.pageSize * page;
        return this.slotMappings.inverse().get(index - startIndex);
    }

    private static /* synthetic */ void lambda$mapSlots$3(ImmutableBiMap.Builder builder, Iterator indexes, Integer i) {
        builder.put(i, indexes.next());
    }

    private static /* synthetic */ boolean lambda$mapSlots$2(Iterator indexes, Integer i) {
        return indexes.hasNext();
    }

    public class PagedHolder
    extends MenuHolder {
        private int page;

        public PagedHolder(Menu menu, Player holder) {
            super(menu, holder);
            this.page = 0;
        }

        public int getPage() {
            return this.page;
        }

        public void setPage(int page) {
            this.page = Math.max(0, page);
        }
    }
}

