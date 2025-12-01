
package net.vertrauterdavid.homes.util.inventory;

import net.vertrauterdavid.homes.util.Scheduler;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.plugin.Plugin;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

public final class InventoryManager {

    private static final AtomicBoolean REGISTERED = new AtomicBoolean(false);

    private InventoryManager() {
        throw new UnsupportedOperationException();
    }

    public static void register(Plugin plugin) {
        Objects.requireNonNull(plugin, "plugin");

        if (REGISTERED.getAndSet(true)) {
            throw new IllegalStateException("FastInv is already registered");
        }

        Bukkit.getPluginManager().registerEvents(new InventoryListener(plugin), plugin);
    }

    public static void closeAll() {
        Bukkit.getOnlinePlayers().stream()
                .filter(player -> player.getOpenInventory().getTopInventory().getHolder() instanceof InventoryBuilder)
                .forEach(Player::closeInventory);
    }

    public static final class InventoryListener implements Listener {

        private final Plugin plugin;

        public InventoryListener(Plugin plugin) {
            this.plugin = plugin;
        }

        @EventHandler
        public void onInventoryDrag(InventoryDragEvent event) {
            if (event.getInventory().getHolder() instanceof InventoryBuilder inv && inv.isEditor()) {
                event.setCancelled(true);
            }
        }

        @EventHandler
        public void onInventoryClick(InventoryClickEvent event) {
            if (!(event.getInventory().getHolder() instanceof InventoryBuilder inv)) return;

            if (inv.isEditor()) {
                if (event.getClickedInventory() == null || event.getClickedInventory() != inv.getInventory()) {
                    event.setCancelled(true);
                    return;
                }
                switch (event.getAction()) {
                    case MOVE_TO_OTHER_INVENTORY, HOTBAR_SWAP,
                         DROP_ALL_CURSOR, DROP_ALL_SLOT,
                         DROP_ONE_SLOT, DROP_ONE_CURSOR,
                         COLLECT_TO_CURSOR -> {
                        event.setCancelled(true);
                        return;
                    }
                }
            }

            if (inv.isSafe()) {
                event.setCancelled(true);
            }

            if (event.getClickedInventory() == inv.getInventory()) {
                inv.handleClick(event);
            }
        }

        @EventHandler
        public void onInventoryOpen(InventoryOpenEvent event) {
            if (event.getInventory().getHolder() instanceof InventoryBuilder inv) {
                inv.handleOpen(event);
            }
        }

        @EventHandler
        public void onInventoryClose(InventoryCloseEvent event) {
            if (!(event.getInventory().getHolder() instanceof InventoryBuilder inv)) return;

            if (!event.getPlayer().getItemOnCursor().isEmpty()) {
                inv.addItem(event.getPlayer().getItemOnCursor());
                event.getPlayer().getItemOnCursor().setAmount(0);
            }

            if (inv.handleClose(event)) {
                Scheduler.runSync(() -> inv.open((Player) event.getPlayer()));
            }
        }

        @EventHandler
        public void onPluginDisable(PluginDisableEvent event) {
            if (event.getPlugin() != this.plugin) return;

            closeAll();
            REGISTERED.set(false);
        }
    }
}