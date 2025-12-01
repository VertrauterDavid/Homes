package net.vertrauterdavid.homes.menu;

import net.vertrauterdavid.homes.Homes;
import net.vertrauterdavid.homes.util.ConfigUtil;
import net.vertrauterdavid.homes.util.ItemUtil;
import net.vertrauterdavid.homes.util.TeleportUtil;
import net.vertrauterdavid.homes.util.inventory.InventoryBuilder;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class HomesMenu {

    public static void show(final @NotNull Player player) {
        final int maxHomes = Homes.getInstance().getConfig().getInt("Settings.MaxHomes", 5);
        final int rows = Homes.getInstance().getConfig().getInt("Gui.Rows", 3);
        final String title = Homes.getInstance().getConfig().getString("Gui.Title", "Homes");

        final InventoryBuilder inventory = new InventoryBuilder(rows * 9, title, true);

        for (int i = 1; i <= maxHomes; i++) {
            int slot = i + (maxHomes == 5 ? 1 : 0) + (rows * 9 == 27 ? 9 : 18);
            int homeNumber = i;

            if (Homes.getInstance().getAmount(player) >= i) {
                if (Homes.getInstance().getHomeManager().get(player.getUniqueId(), i) != null) {
                    inventory.setItem(slot, ItemUtil.getSetItem(i), event -> {
                        if (event.getClick().isLeftClick()) {
                            TeleportUtil.teleport(player, Homes.getInstance().getHomeManager().get(player.getUniqueId(), homeNumber));
                        } else {
                            if (Homes.getInstance().getConfig().getBoolean("DeleteGui.Enabled")) {
                                DeleteMenu.show(player, homeNumber, deletedHome -> {
                                    Homes.getInstance().getHomeManager().delete(player.getUniqueId(), deletedHome);
                                    show(player);
                                    player.sendMessage(ConfigUtil.getPrefix().append(ConfigUtil.getMessage("Messages.Delete")));
                                    ConfigUtil.playSound(player, "GuiSounds.SuccessSound");
                                });
                            } else {
                                Homes.getInstance().getHomeManager().delete(player.getUniqueId(), homeNumber);
                                show(player);
                                player.sendMessage(ConfigUtil.getPrefix().append(ConfigUtil.getMessage("Messages.Delete")));
                                ConfigUtil.playSound(player, "GuiSounds.SuccessSound");
                            }
                        }
                    });
                } else {
                    inventory.setItem(slot, ItemUtil.getUnSetItem(i), event -> {
                        if (event.getClick().isLeftClick()) {
                            Homes.getInstance().getHomeManager().set(player.getUniqueId(), homeNumber, player.getLocation());
                            show(player);
                            player.sendMessage(ConfigUtil.getPrefix().append(ConfigUtil.getMessage("Messages.Set")));
                            ConfigUtil.playSound(player, "GuiSounds.SuccessSound");
                        }
                    });
                }
            } else {
                inventory.setItem(slot, ItemUtil.getNoPermissionItem(i), event -> {
                    player.sendMessage(ConfigUtil.getPrefix().append(ConfigUtil.getMessage("Messages.NoPermission")));
                    ConfigUtil.playSound(player, "GuiSounds.ErrorSound");
                });
            }
        }

        ConfigUtil.playSound(player, "GuiSounds.OpenSound");
        inventory.open(player);
    }
}