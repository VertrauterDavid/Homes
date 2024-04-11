package net.vertrauterdavid.homes.listener;

import net.vertrauterdavid.homes.Homes;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {

    @EventHandler
    public void handle(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        if (!(Homes.getInstance().getSqlUtil().get("Homes", "UUID", "UUID='" + player.getUniqueId().toString() + "'").equalsIgnoreCase(player.getUniqueId().toString()))) {
            Homes.getInstance().getSqlUtil().update("INSERT INTO Homes (UUID) VALUES ('" + player.getUniqueId().toString() + "')");
        }

        Bukkit.getScheduler().runTaskAsynchronously(Homes.getInstance(), () -> Homes.getInstance().getHomeUtil().loadLocal(player.getUniqueId()));
    }

}
