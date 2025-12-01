
package net.vertrauterdavid.homes.util.inventory;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.kyori.adventure.text.Component;
import net.vertrauterdavid.homes.Homes;
import net.vertrauterdavid.homes.util.ConfigUtil;
import net.vertrauterdavid.homes.util.Scheduler;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.IntStream;

@Getter
@Accessors(fluent = true)
@SuppressWarnings("unused")
public class InventoryBuilder implements InventoryHolder {

    // Credits @ yyuh </3

    private final @NotNull Map<Integer, Consumer<InventoryClickEvent>> itemHandlers = new HashMap<>();
    private final @NotNull List<Consumer<InventoryOpenEvent>> openHandlers = new ArrayList<>();
    private final @NotNull List<Consumer<InventoryCloseEvent>> closeHandlers = new ArrayList<>();
    private final @NotNull List<Consumer<InventoryClickEvent>> clickHandlers = new ArrayList<>();
    private final @NotNull Map<Integer, Integer> multiItemIndices = new HashMap<>();

    private final @NotNull Inventory inventory;

    @Setter private boolean isSafe = false;
    @Setter private boolean isEditor = false;
    @Setter private Predicate<Player> closeFilter;

    public InventoryBuilder(final int size) {
        this(owner -> Bukkit.createInventory(owner, size));
    }

    public InventoryBuilder(final int size, final @NotNull Component title, final boolean safe) {
        this(owner -> Bukkit.createInventory(owner, size, title));
        this.isSafe = safe;
    }

    public InventoryBuilder(final int size, final @NotNull String title, final boolean safe) {
        this(owner -> Bukkit.createInventory(owner, size, ConfigUtil.translateColorCodes(title)));
        this.isSafe = safe;
    }

    public InventoryBuilder(final int size, final @NotNull Component title) {
        this(owner -> Bukkit.createInventory(owner, size, title));
    }

    public InventoryBuilder(final int size, final @NotNull String title) {
        this(owner -> Bukkit.createInventory(owner, size, ConfigUtil.translateColorCodes(title)));
    }

    public InventoryBuilder(final @NotNull String title, final @NotNull InventoryType type){
        this(owner -> Bukkit.createInventory(owner, type));
    }

    public InventoryBuilder(final @NotNull InventoryType type) {
        this(owner -> Bukkit.createInventory(owner, type));
    }

    public InventoryBuilder(final @NotNull InventoryType type, final @NotNull Component title) {
        this(owner -> Bukkit.createInventory(owner, type, title));
    }

    public InventoryBuilder(final @NotNull InventoryType type, final @NotNull String title) {
        this(owner -> Bukkit.createInventory(owner, type, ConfigUtil.translateColorCodes(title)));
    }

    public InventoryBuilder(final @NonNull Function<InventoryHolder, Inventory> inventoryFunction) {
        final Inventory inv = Objects.requireNonNull(inventoryFunction, "inventoryFunction").apply(this);

        if (inv.getHolder() != this) {
            throw new IllegalStateException("Inventory holder is not InventoryBuilder, found: " + inv.getHolder());
        }

        this.inventory = inv;
    }

    protected void onOpen(final @NotNull InventoryOpenEvent event) { }
    protected void onClick(final @NotNull InventoryClickEvent event) { }
    protected void onClose(final @NotNull InventoryCloseEvent event) { }

    public void addItem(final @NotNull ItemStack item) {
        addItem(item, null);
    }

    public void addItem(final @NotNull ItemStack item, final Consumer<InventoryClickEvent> handler) {
        final int slot = this.inventory.firstEmpty();
        if (slot >= 0) {
            setItem(slot, item, handler);
        }
    }

    public void setAnimatedItem(final int slot, final @NotNull ItemStack[] stacks, final int delay,
                                final Consumer<InventoryClickEvent> handler) {
        Scheduler.timer(() -> {
            final int frame = (int) (System.currentTimeMillis() / (delay * 50)) % stacks.length;
            setItem(slot, stacks[frame], handler);
        }, 0, delay);
    }

    public void setItem(final int slot, final @NotNull ItemStack item) {
        setItem(slot, item, null);
    }

    public void setItem(final int slot, final @NotNull ItemStack item, final Consumer<InventoryClickEvent> handler) {
        this.inventory.setItem(slot, item);
        if (handler != null) {
            this.itemHandlers.put(slot, handler);
        } else {
            this.itemHandlers.remove(slot);
        }
    }

    public void setItems(final int slotFrom, final int slotTo, final @NotNull ItemStack item) {
        setItems(slotFrom, slotTo, item, null);
    }

    public void setItems(final int slotFrom, final int slotTo, final @NotNull ItemStack item, final Consumer<InventoryClickEvent> handler) {
        for (int i = slotFrom; i <= slotTo; i++) {
            setItem(i, item, handler);
        }
    }

    public void setItems(final int[] slots, final @NotNull ItemStack item) {
        setItems(slots, item, null);
    }

    public void setItems(final int @NotNull [] slots, final @NotNull ItemStack item, final Consumer<InventoryClickEvent> handler) {
        for (final int slot : slots) {
            setItem(slot, item, handler);
        }
    }

    public void setMultiItem(final int slot, final @NotNull List<ItemStack> stacks, final Consumer<InventoryClickEvent> handler) {
        if (stacks.isEmpty()) {
            throw new IllegalArgumentException("Item list cannot be empty.");
        }

        multiItemIndices.putIfAbsent(slot, 0);
        final int currentIndex = multiItemIndices.get(slot);

        setItem(slot, stacks.get(currentIndex), event -> {
            int idx = multiItemIndices.get(slot);
            final int delta = event.getClick().isRightClick() ? -1 : 1;
            idx = (idx + delta + stacks.size()) % stacks.size();

            multiItemIndices.put(slot, idx);
            setItem(slot, stacks.get(idx), itemHandlers.get(slot));

            if (handler != null) {
                handler.accept(event);
            }
        });
    }

    public void setSwapDisplay(final int slot, final @NotNull ItemStack trueItem, final @NotNull ItemStack falseItem, final boolean swapItemBool, final @NotNull Consumer<InventoryClickEvent> action) {
        final Consumer<InventoryClickEvent> handler = event -> {
            event.setCancelled(true);
            action.accept(event);
        };
        if (swapItemBool) {
            setItem(slot, trueItem, event -> event.setCancelled(true));
        } else {
            setItem(slot, falseItem, event -> {
                setItem(slot, trueItem, handler);
                handler.accept(event);
            });
        }
    }

    public void setSwapItem(final int slot, final @NotNull ItemStack trueItem, final @NotNull ItemStack falseItem, final boolean swapItemBool, final @NotNull Consumer<InventoryClickEvent> trueHandler, final @NotNull Consumer<InventoryClickEvent> falseHandler) {
        final Consumer<InventoryClickEvent> handler = event -> {
            event.setCancelled(true);
            setSwapItem(slot, trueItem, falseItem, !swapItemBool, trueHandler, falseHandler);
        };

        if (swapItemBool) {
            setItem(slot, trueItem, event -> {
                handler.accept(event);
                falseHandler.accept(event);
            });
        } else {
            setItem(slot, falseItem, event -> {
                handler.accept(event);
                trueHandler.accept(event);
            });
        }
    }

    public void setPlaceholders(final int @NotNull ... slot){
        for(final int x : slot){
            setItem(x, ItemBuilder.item(Material.GRAY_STAINED_GLASS_PANE).tooltip(false).build(),
                    event -> event.setCancelled(true));
        }
    }

    public void setPlaceholder(final int slot, final @NotNull Material item){
        setItem(slot, ItemBuilder.item(item).tooltip(false).build(),
                event -> event.setCancelled(true));
    }

    public void setPlaceholder(final int slot){
        setItem(slot, ItemBuilder.item(Material.GRAY_STAINED_GLASS_PANE).tooltip(false).build(),
                event -> event.setCancelled(true));
    }

    public void removeItem(final int slot) {
        this.inventory.clear(slot);
        this.itemHandlers.remove(slot);
    }

    public void removeItems(final int @NotNull ... slots) {
        for (final int slot : slots) {
            removeItem(slot);
        }
    }

    public @NotNull InventoryBuilder fill(final @NotNull ItemStack filler, final @NotNull String @NotNull ... pattern) {
        final int rows = inventory.getSize() / 9;
        for (int row = 0; row < pattern.length && row < rows; row++) {
            final String[] rowChars = pattern[row].split(" ");
            for (int col = 0; col < Math.min(rowChars.length, 9); col++) {
                if ("X".equals(rowChars[col])) {
                    final int slot = row * 9 + col;
                    setItem(slot, filler, e -> e.setCancelled(true));
                }
            }
        }
        return this;
    }

    public void fill(final @NotNull List<AbstractItem> stacks, final @NotNull String @NotNull ... pattern) {
        final int rows = inventory.getSize() / 9;
        int itemIndex = 0;

        for (int row = 0; row < pattern.length && row < rows; row++) {
            final String[] rowChars = pattern[row].split(" ");
            for (int col = 0; col < Math.min(rowChars.length, 9); col++) {
                if ("X".equals(rowChars[col]) && itemIndex < stacks.size()) {
                    final AbstractItem item = stacks.get(itemIndex++);
                    if (item != null) {
                        final int slot = row * 9 + col;
                        setItem(slot, item.getStack(), item.getHandler());
                    }
                }
            }
        }
    }

    public void setSafety(final boolean b){
        isSafe = b;
    }

    public void addOpenHandler(final @NotNull Consumer<InventoryOpenEvent> openHandler) {
        this.openHandlers.add(openHandler);
    }

    public void addCloseHandler(final @NotNull Consumer<InventoryCloseEvent> closeHandler) {
        this.closeHandlers.add(closeHandler);
    }

    public void addClickHandler(final @NotNull Consumer<InventoryClickEvent> clickHandler) {
        this.clickHandlers.add(clickHandler);
    }

    public void open(final @NotNull Player player) {
        Scheduler.runSync(() -> player.openInventory(this.inventory));
    }

    public int[] getBorders() {
        final int size = this.inventory.getSize();
        return IntStream.range(0, size)
                .filter(i -> size < 27 || i < 9
                        || i % 9 == 0 || (i - 8) % 9 == 0
                        || i > size - 9)
                .toArray();
    }

    public int[] getCorners() {
        final int size = this.inventory.getSize();
        return IntStream.range(0, size)
                .filter(i -> i < 2 || (i > 6 && i < 10)
                        || i == 17 || i == size - 18
                        || (i > size - 11 && i < size - 7)
                        || i > size - 3)
                .toArray();
    }

    @Override
    public @NotNull Inventory getInventory() {
        return this.inventory;
    }

    public @NotNull ItemStack getItem(final int index){
        final ItemStack item = getInventory().getItem(index);
        return item == null ? new ItemStack(Material.AIR) : item;
    }

    void handleOpen(final @NotNull InventoryOpenEvent event) {
        onOpen(event);
        this.openHandlers.forEach(c -> c.accept(event));
    }

    boolean handleClose(final @NotNull InventoryCloseEvent event) {
        onClose(event);
        this.closeHandlers.forEach(c -> c.accept(event));
        return this.closeFilter != null && this.closeFilter.test((Player) event.getPlayer());
    }

    boolean isSafe(){
        return isSafe;
    }

    boolean isEditor() {
        return isEditor;
    }

    void handleClick(final @NotNull InventoryClickEvent event) {
        onClick(event);
        this.clickHandlers.forEach(c -> c.accept(event));
        final Consumer<InventoryClickEvent> clickConsumer = this.itemHandlers.get(event.getRawSlot());
        if (clickConsumer != null) {
            clickConsumer.accept(event);
        }
    }
}