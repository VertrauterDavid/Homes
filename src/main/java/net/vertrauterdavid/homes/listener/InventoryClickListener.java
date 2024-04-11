package net.vertrauterdavid.homes.listener;

import net.vertrauterdavid.homes.Homes;
import net.vertrauterdavid.homes.util.ConfigUtil;
import net.vertrauterdavid.homes.util.TeleportUtil;
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

        if (view.getTitle().equalsIgnoreCase(ConfigUtil.translateColorCodes(Homes.getInstance().getConfig().getString("Gui.Title", "Homes")))) {
            event.setCancelled(true);

            int maxHomes = Homes.getInstance().getConfig().getInt("Settings.MaxHomes", 5);
            for (int i = 1; i <= maxHomes; i++) {
                int slot = i + (maxHomes == 5 ? 1 : 0) + (inventory.getSize() == 27 ? 9 : 18);
                if (event.getRawSlot() == slot) {
                    if (event.getClick() == ClickType.LEFT) {
                        if (Homes.getInstance().getAmount(player) < i) {
                            player.sendMessage(ConfigUtil.getPrefix() + ConfigUtil.getMessage("Messages.NoPermission"));
                            ConfigUtil.playSound(player, "GuiSounds.ErrorSound");
                        } else if (Homes.getInstance().getHomeUtil().get(player.getUniqueId(), i) == null) {
                            Homes.getInstance().getHomeUtil().set(player.getUniqueId(), i, player.getLocation());
                            Homes.getInstance().openInventory(player);
                            player.sendMessage(ConfigUtil.getPrefix() + ConfigUtil.getMessage("Messages.Set"));
                            ConfigUtil.playSound(player, "GuiSounds.SuccessSound");
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
                                player.sendMessage(ConfigUtil.getPrefix() + ConfigUtil.getMessage("Messages.Delete"));
                                ConfigUtil.playSound(player, "GuiSounds.SuccessSound");
                            }
                        }
                    }
                }
            }
        }

        if (view.getTitle().startsWith(ConfigUtil.translateColorCodes(Homes.getInstance().getConfig().getString("DeleteGui.Title", "Homes")))) {
            event.setCancelled(true);

            try {
                int home = Integer.parseInt(view.getTitle().substring(view.getTitle().length() - 1));
                if (event.getRawSlot() == Homes.getInstance().getConfig().getInt("DeleteGui.Items.Confirm.Slot", 13)) {
                    Homes.getInstance().getHomeUtil().delete(player.getUniqueId(), home);
                    Homes.getInstance().openInventory(player);
                    player.sendMessage(ConfigUtil.getPrefix() + ConfigUtil.getMessage("Messages.Delete"));
                    ConfigUtil.playSound(player, "GuiSounds.SuccessSound");
                } else if (event.getRawSlot() == Homes.getInstance().getConfig().getInt("DeleteGui.Items.Cancel.Slot", 15)) {
                    Homes.getInstance().openInventory(player);
                }
            } catch (NumberFormatException ignored) { }
        }
    }

}
