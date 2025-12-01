package net.vertrauterdavid.homes.util;

import lombok.experimental.UtilityClass;
import net.vertrauterdavid.homes.Homes;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;

@UtilityClass
public class ItemUtil {

    public ItemStack getSetItem(int home) {
        return Homes.getInstance().getSetItem().clone()
                .name(Objects.requireNonNull(Homes.getInstance().getConfig().getString("Gui.Items.Set.Name")).replace("%home%", String.valueOf(home)))
                .build();
    }

    public ItemStack getUnSetItem(int home) {
        return Homes.getInstance().getUnSetItem().clone()
                .name(Objects.requireNonNull(Homes.getInstance().getConfig().getString("Gui.Items.UnSet.Name")).replace("%home%", String.valueOf(home)))
                .build();
    }

    public ItemStack getNoPermissionItem(int home) {
        return Homes.getInstance().getNoPermissionItem().clone()
                .name(Objects.requireNonNull(Homes.getInstance().getConfig().getString("Gui.Items.NoPermission.Name")).replace("%home%", String.valueOf(home)))
                .build();
    }

    public ItemStack getBedItem(int home) {
        return Homes.getInstance().getBedItem().clone()
                .name(Objects.requireNonNull(Homes.getInstance().getConfig().getString("DeleteGui.Items.Bed.Name")).replace("%home%", String.valueOf(home)))
                .build();
    }

}
