package net.vertrauterdavid.homes.util;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import net.vertrauterdavid.homes.Homes;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

public class TeleportUtil {

    public static void teleport(Player player, Location location) {
        player.closeInventory();

        if (Homes.getInstance().getConfig().getInt("Teleport.CoolDown") < 1) {
            player.teleport(location);
            player.playSound(player.getLocation(), Sound.valueOf(Homes.getInstance().getConfig().getString("Teleport.TeleportSound")), 5, 5);
            return;
        }

        final int[] seconds = {6};
        UUID uuid = player.getUniqueId();
        new BukkitRunnable() {
            @Override
            public void run() {
                seconds[0]--;

                if (Bukkit.getPlayer(uuid) == null) {
                    this.cancel();
                    return;
                }

                switch (seconds[0]) {
                    case 5, 4, 3, 2, 1 -> {
                        if (!(Homes.getInstance().getConfig().getString("Teleport.Message").equalsIgnoreCase(""))) {
                            player.sendMessage(MessageUtil.getPrefix() + MessageUtil.get("Teleport.Message").replaceAll("%seconds%", String.valueOf(seconds[0])));
                        }
                        if (!(Homes.getInstance().getConfig().getString("Teleport.Actionbar").equalsIgnoreCase(""))) {
                            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(MessageUtil.get("Teleport.Actionbar").replaceAll("%seconds%", String.valueOf(seconds[0]))));
                        }
                        if (!(Homes.getInstance().getConfig().getString("Teleport.Title.Title").equalsIgnoreCase("")) || !(Homes.getInstance().getConfig().getString("Teleport.Title.SubTitle").equalsIgnoreCase(""))) {
                            player.sendTitle(MessageUtil.get("Teleport.Title.Title").replaceAll("%seconds%", String.valueOf(seconds[0])), MessageUtil.get("Teleport.Title.SubTitle").replaceAll("%seconds%", String.valueOf(seconds[0])));
                        }
                        player.playSound(player.getLocation(), Sound.valueOf(Homes.getInstance().getConfig().getString("Teleport.CoolDownSound")), 5, 5);
                    }
                    case 0 -> {
                        player.teleport(location);
                        player.playSound(player.getLocation(), Sound.valueOf(Homes.getInstance().getConfig().getString("Teleport.CoolDownSound")), 5, 5);
                        this.cancel();
                    }
                }
            }
        }.runTaskTimer(Homes.getInstance(), 0, 20);
    }

}
