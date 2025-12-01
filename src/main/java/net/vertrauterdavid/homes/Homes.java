package net.vertrauterdavid.homes;

import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPIBukkitConfig;
import dev.jorel.commandapi.CommandAPILogger;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.vertrauterdavid.homes.command.HomeCommand;
import net.vertrauterdavid.homes.database.SqlConnection;
import net.vertrauterdavid.homes.listener.PlayerJoinListener;
import net.vertrauterdavid.homes.manager.HomeManager;
import net.vertrauterdavid.homes.util.ConfigUtil;
import net.vertrauterdavid.homes.util.bstats.Metrics;
import net.vertrauterdavid.homes.util.inventory.InventoryManager;
import net.vertrauterdavid.homes.util.inventory.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@Getter
public class Homes extends JavaPlugin {

    private ItemBuilder setItem;
    private ItemBuilder unSetItem;
    private ItemBuilder noPermissionItem;
    private ItemBuilder confirmItem;
    private ItemBuilder bedItem;
    private ItemBuilder cancelItem;

    @Getter
    private static Homes instance;
    private SqlConnection sqlConnection;
    private HomeManager homeManager;

    @Override
    public void onLoad() {
        CommandAPI.setLogger(CommandAPILogger.fromJavaLogger(getLogger()));
        CommandAPIBukkitConfig config = new CommandAPIBukkitConfig(this);
        config.setNamespace("homes");
        CommandAPI.onLoad(config);
    }

    @Override
    public void onEnable() {
        saveDefaultConfig();
        instance = this;

        CommandAPI.onEnable();

        sqlConnection = new SqlConnection();
        sqlConnection.setup();
        sqlConnection.createTables();

        InventoryManager.register(instance);
        homeManager = new HomeManager();

        setItem = getConfigItem("Gui.Items.Set");
        unSetItem = getConfigItem("Gui.Items.UnSet");
        noPermissionItem = getConfigItem("Gui.Items.NoPermission");
        confirmItem = getConfigItem("DeleteGui.Items.Confirm");
        bedItem = getConfigItem("DeleteGui.Items.Bed");
        cancelItem = getConfigItem("DeleteGui.Items.Cancel");

        Bukkit.getPluginManager().registerEvents(new PlayerJoinListener(), instance);

        HomeCommand.register();

        int id = 28182;
        new Metrics(instance, id);
    }

    @Override
    public void onDisable() {
        CommandAPI.onDisable();
        sqlConnection.close();
    }

    private ItemBuilder getConfigItem(String path) {
        Material material = Material.valueOf(getConfig().getString(path + ".Material"));
        String name = getConfig().getString(path + ".Name");
        List<Component> lore = ConfigUtil.getComponentList(path + ".Lore");
        return new ItemBuilder(material).name(name).lore(lore);
    }

    public int getAmount(final @NotNull Player player) {
        for (int i = 7; i >= 1; i--) {
            if (player.hasPermission("homes." + i)) {
                return i;
            }
        }
        return 1;
    }
}
