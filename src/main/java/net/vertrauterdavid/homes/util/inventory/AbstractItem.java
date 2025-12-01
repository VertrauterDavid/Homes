package net.vertrauterdavid.homes.util.inventory;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.function.Consumer;

@Getter
@RequiredArgsConstructor
public class AbstractItem {

    private final ItemStack stack;
    private final Consumer<InventoryClickEvent> handler;
}
