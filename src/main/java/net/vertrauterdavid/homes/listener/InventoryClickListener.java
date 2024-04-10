package net.vertrauterdavid.homes.listener;

import net.vertrauterdavid.homes.Homes;
import net.vertrauterdavid.homes.util.MessageUtil;
import net.vertrauterdavid.homes.util.TeleportUtil;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;

public class InventoryClickListener implements Listener {

    @EventHandler
    public void handle(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;

        if (event.getCurrentItem() == null) return;
        if (event.getCurrentItem().getItemMeta() == null) return;

        Inventory inventory = event.getInventory();
        InventoryView view = event.getView();

        if (view.getTitle().equalsIgnoreCase(MessageUtil.translateColorCodes(Homes.getInstance().getConfig().getString("Gui.Title", "Homes")))) {
            event.setCancelled(true);

            int maxHomes = Homes.getInstance().getConfig().getInt("Settings.MaxHomes", 5);
            for (int i = 1; i <= maxHomes; i++) {
                int slot = i + (maxHomes == 5 ? 1 : 0) + (inventory.getSize() == 27 ? 9 : 18);
                if (event.getRawSlot() == slot) {
                    if (event.getClick() == ClickType.LEFT) {
                        if (Homes.getInstance().getAmount(player) < i) {
                            player.sendMessage(MessageUtil.getPrefix() + MessageUtil.get("Messages.NoPermission"));
                            player.playSound(player.getLocation(), Sound.valueOf(Homes.getInstance().getConfig().getString("GuiSounds.ErrorSound")), 5, 5);
                        } else if (Homes.getInstance().getHomeUtil().get(player.getUniqueId(), i) == null) {
                            Homes.getInstance().getHomeUtil().set(player.getUniqueId(), i, player.getLocation());
                            Homes.getInstance().openInventory(player);
                            player.sendMessage(MessageUtil.getPrefix() + MessageUtil.get("Messages.Set"));
                            player.playSound(player.getLocation(), Sound.valueOf(Homes.getInstance().getConfig().getString("GuiSounds.SuccessSound")), 5, 5);
                        } else {
                            TeleportUtil.teleport(player, Homes.getInstance().getHomeUtil().get(player.getUniqueId(), i));
                        }
                    } else {
                        if (Homes.getInstance().getHomeUtil().get(player.getUniqueId(), i) != null) {
                            if (Homes.getInstance().getConfig().getBoolean("DeleteGui.Enabled")) {
                                Homes.getInstance().openDeleteInventory(player, i);
                            } else {
                                Homes.getInstance().getHomeUtil().delete(player.getUniqueId(), i);
                                Homes.getInstance().openInventory(player);
                                player.sendMessage(MessageUtil.getPrefix() + MessageUtil.get("Messages.Delete"));
                                player.playSound(player.getLocation(), Sound.valueOf(Homes.getInstance().getConfig().getString("GuiSounds.SuccessSound")), 5, 5);
                            }
                        }
                    }
                }
            }
        }

        if (view.getTitle().startsWith(MessageUtil.translateColorCodes(Homes.getInstance().getConfig().getString("DeleteGui.Title", "Homes")))) {
            event.setCancelled(true);

            try {
                int home = Integer.parseInt(view.getTitle().substring(view.getTitle().length() - 1));
                if (event.getRawSlot() == 3 + (inventory.getSize() == 27 ? 9 : 18)) {
                    Homes.getInstance().getHomeUtil().delete(player.getUniqueId(), home);
                    Homes.getInstance().openInventory(player);
                    player.sendMessage(MessageUtil.getPrefix() + MessageUtil.get("Messages.Delete"));
                    player.playSound(player.getLocation(), Sound.valueOf(Homes.getInstance().getConfig().getString("GuiSounds.SuccessSound")), 5, 5);
                } else if (event.getRawSlot() == 5 + (inventory.getSize() == 27 ? 9 : 18)) {
                    Homes.getInstance().openInventory(player);
                }
            } catch (NumberFormatException ignored) { }
        }
    }

}
