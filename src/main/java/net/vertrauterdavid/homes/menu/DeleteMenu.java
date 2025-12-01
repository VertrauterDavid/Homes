package net.vertrauterdavid.homes.menu;

import net.vertrauterdavid.homes.Homes;
import net.vertrauterdavid.homes.util.ConfigUtil;
import net.vertrauterdavid.homes.util.ItemUtil;
import net.vertrauterdavid.homes.util.inventory.InventoryBuilder;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class DeleteMenu {

    public static void show(final @NotNull Player player, final int home, Consumer<Integer> onConfirm) {
        final int rows = Homes.getInstance().getConfig().getInt("DeleteGui.Rows", 3);
        final String title = Homes.getInstance().getConfig().getString("DeleteGui.Title", "Homes") + " " + home;

        final InventoryBuilder inventory = new InventoryBuilder(rows * 9, title, true);

        int confirmSlot = Homes.getInstance().getConfig().getInt("DeleteGui.Items.Confirm.Slot", 11);
        inventory.setItem(confirmSlot, Homes.getInstance().getConfirmItem().build(), event -> {
            onConfirm.accept(home);
        });

        if (Homes.getInstance().getConfig().getBoolean("DeleteGui.Items.Bed.Enabled", true)) {
            int bedSlot = Homes.getInstance().getConfig().getInt("DeleteGui.Items.Bed.Slot", 13);
            inventory.setItem(bedSlot, ItemUtil.getBedItem(home));
        }

        int cancelSlot = Homes.getInstance().getConfig().getInt("DeleteGui.Items.Cancel.Slot", 15);
        inventory.setItem(cancelSlot, Homes.getInstance().getCancelItem().build(), event -> {
            HomesMenu.show(player);
        });

        ConfigUtil.playSound(player, "GuiSounds.OpenSound");
        inventory.open(player);
    }
}