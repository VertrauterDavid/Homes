package net.vertrauterdavid.homes.util;

import net.md_5.bungee.api.ChatColor;
import net.vertrauterdavid.homes.Homes;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConfigUtil {

    public static String getMessage(String key) {
        return translateColorCodes(Homes.getInstance().getConfig().getString(key, ""));
    }

    public static String getPrefix() {
        return translateColorCodes(Homes.getInstance().getConfig().getString("Messages.Prefix", ""));
    }

    public static void playSound(Player player, String key) {
        String sound = Homes.getInstance().getConfig().getString(key, "");
        if (!sound.equalsIgnoreCase("")) {
            try {
                player.playSound(player.getLocation(), Sound.valueOf(sound), 5, 5);
            } catch (Exception ignored) { }
        }
    }

    public static String translateColorCodes(String message) {
        Pattern pattern = Pattern.compile("&#([A-Fa-f0-9]{6})");
        Matcher matcher = pattern.matcher(message);
        StringBuffer buffer = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(buffer, ChatColor.of("#" + matcher.group(1)).toString());
        }
        matcher.appendTail(buffer);
        return ChatColor.translateAlternateColorCodes('&', buffer.toString());
    }

}
