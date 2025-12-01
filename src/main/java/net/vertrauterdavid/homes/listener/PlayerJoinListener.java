package net.vertrauterdavid.homes.listener;

import net.vertrauterdavid.homes.Homes;
import net.vertrauterdavid.homes.util.Scheduler;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        final Player player = event.getPlayer();

        Scheduler.timerAsync(() -> {
            if (!(Homes.getInstance().getSqlConnection().get("Homes", "UUID", "UUID='" + player.getUniqueId() + "'").equalsIgnoreCase(player.getUniqueId().toString()))) {
                Homes.getInstance().getSqlConnection().update("INSERT INTO Homes (UUID) VALUES ('" + player.getUniqueId() + "')");
            }
            Homes.getInstance().getHomeManager().loadLocal(player.getUniqueId());
        }, 1, 1);
    }
}
