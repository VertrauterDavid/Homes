package net.vertrauterdavid.homes;

import lombok.Getter;
import net.vertrauterdavid.homes.command.HomeCommand;
import net.vertrauterdavid.homes.listener.InventoryClickListener;
import net.vertrauterdavid.homes.listener.PlayerJoinListener;
import net.vertrauterdavid.homes.util.*;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

@Getter
public class Homes extends JavaPlugin {

    @Getter
    private static Homes instance;
    private VersionUtil versionUtil;
    private SqlUtil sqlUtil;
    private HomeUtil homeUtil;

    private ItemUtil setItem;
    private ItemUtil unSetItem;
    private ItemUtil noPermissionItem;
    private ItemUtil confirmItem;
    private ItemUtil cancelItem;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        instance = this;
        versionUtil = new VersionUtil();
        sqlUtil = new SqlUtil(getConfig().getString("Sql.Host"), getConfig().getString("Sql.Port"), getConfig().getString("Sql.Database"), getConfig().getString("Sql.Username"), getConfig().getString("Sql.Password"));
        sqlUtil.update("CREATE TABLE IF NOT EXISTS Homes (UUID VARCHAR(100) NOT NULL, Home1 VARCHAR(100) NOT NULL DEFAULT '-', Home2 VARCHAR(100) NOT NULL DEFAULT '-', Home3 VARCHAR(100) NOT NULL DEFAULT '-', Home4 VARCHAR(100) NOT NULL DEFAULT '-', Home5 VARCHAR(100) NOT NULL DEFAULT '-', Home6 VARCHAR(100) NOT NULL DEFAULT '-', Home7 VARCHAR(100) NOT NULL DEFAULT '-');");
        homeUtil = new HomeUtil();

        setItem = getConfigItem("Gui.Items.Set");
        unSetItem = getConfigItem("Gui.Items.UnSet");
        noPermissionItem = getConfigItem("Gui.Items.NoPermission");
        confirmItem = getConfigItem("DeleteGui.Items.Confirm");
        cancelItem = getConfigItem("DeleteGui.Items.Cancel");

        Bukkit.getPluginManager().registerEvents(new InventoryClickListener(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerJoinListener(), this);

        new HomeCommand("homes");
        new HomeCommand("home");
    }

    public void openInventory(Player player) {
        int maxHomes = getConfig().getInt("Settings.MaxHomes", 5);
        Inventory inventory = Bukkit.createInventory(null, getConfig().getInt("Gui.Rows", 3) * 9, ConfigUtil.translateColorCodes(getConfig().getString("Gui.Title", "Homes")));

        for (int i = 1; i <= maxHomes; i++) {
            int slot = i + (maxHomes == 5 ? 1 : 0) + (inventory.getSize() == 27 ? 9 : 18);
            if (getAmount(player) >= i) {
                inventory.setItem(slot, (homeUtil.get(player.getUniqueId(), i) != null ? getSetItem(i) : getUnSetItem(i)));
            } else {
                inventory.setItem(slot, getNoPermissionItem(i));
            }
        }

        player.openInventory(inventory);
        ConfigUtil.playSound(player, "GuiSounds.OpenSound");
    }

    public void openDeleteInventory(Player player, int home) {
        Inventory inventory = Bukkit.createInventory(null, getConfig().getInt("DeleteGui.Rows", 3) * 9, ConfigUtil.translateColorCodes(getConfig().getString("DeleteGui.Title", "Homes")) + " " + home);

        inventory.setItem(getConfig().getInt("DeleteGui.Items.Confirm.Slot", 13), confirmItem.toItemStack());
        inventory.setItem(getConfig().getInt("DeleteGui.Items.Cancel.Slot", 15), cancelItem.toItemStack());

        player.openInventory(inventory);
        ConfigUtil.playSound(player, "GuiSounds.OpenSound");
    }

    private ItemUtil getConfigItem(String path) {
        Material material = Material.valueOf(ConfigUtil.translateColorCodes(getConfig().getString(path + ".Material")));
        String name = ConfigUtil.translateColorCodes(getConfig().getString(path + ".Name"));
        List<String> lore = getConfig().getStringList(path + ".Lore");
        return new ItemUtil(material).setName(name).setLore(lore.stream().map(ConfigUtil::translateColorCodes).toArray(String[]::new));
    }

    private ItemStack getSetItem(int home) {
        return new ItemUtil(setItem).setName(setItem.getItemMeta().getDisplayName().replaceAll("%home%", String.valueOf(home))).toItemStack();
    }

    private ItemStack getUnSetItem(int home) {
        return new ItemUtil(unSetItem).setName(unSetItem.getItemMeta().getDisplayName().replaceAll("%home%", String.valueOf(home))).toItemStack();
    }

    private ItemStack getNoPermissionItem(int home) {
        return new ItemUtil(noPermissionItem).setName(noPermissionItem.getItemMeta().getDisplayName().replaceAll("%home%", String.valueOf(home))).toItemStack();
    }

    public int getAmount(Player player) {
        if (player.hasPermission("homes.7")) {
            return 7;
        }
        if (player.hasPermission("homes.6")) {
            return 6;
        }
        if (player.hasPermission("homes.5")) {
            return 5;
        }
        if (player.hasPermission("homes.4")) {
            return 4;
        }
        if (player.hasPermission("homes.3")) {
            return 3;
        }
        if (player.hasPermission("homes.2")) {
            return 2;
        }
        return 1;
    }

}
