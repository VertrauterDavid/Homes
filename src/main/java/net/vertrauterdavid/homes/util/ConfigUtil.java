package net.vertrauterdavid.homes.util;

import lombok.experimental.UtilityClass;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.vertrauterdavid.homes.Homes;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@UtilityClass
public class ConfigUtil {

    @NotNull
    public Component getMessage(@NotNull String key) {
        return translateColorCodes(Homes.getInstance().getConfig().getString(key, ""));
    }

    @NotNull
    public Component getPrefix() {
        return translateColorCodes(Homes.getInstance().getConfig().getString("Messages.Prefix", ""));
    }

    public void playSound(final @NotNull Player player, @NotNull String key) {
        final String sound = Homes.getInstance().getConfig().getString(key, "");
        if (!sound.equalsIgnoreCase("")) {
            try {
                player.playSound(player.getLocation(), Sound.valueOf(sound), 5, 5);
            } catch (Exception ignored) { }
        }
    }

    @NotNull
    public Component translateColorCodes(@NotNull String message) {
        message = message.replaceAll("&([0-9a-fk-or])", "§$1");
        Pattern pattern = Pattern.compile("&#([A-Fa-f0-9]{6})");
        Matcher matcher = pattern.matcher(message);
        StringBuilder buffer = new StringBuilder();

        while (matcher.find()) {
            matcher.appendReplacement(buffer, "§x"
                    + "§" + matcher.group(1).charAt(0)
                    + "§" + matcher.group(1).charAt(1)
                    + "§" + matcher.group(1).charAt(2)
                    + "§" + matcher.group(1).charAt(3)
                    + "§" + matcher.group(1).charAt(4)
                    + "§" + matcher.group(1).charAt(5));
        }
        matcher.appendTail(buffer);
        return LegacyComponentSerializer.legacySection().deserialize(buffer.toString());
    }

    @NotNull
    public List<Component> getComponentList(@NotNull String path) {
        return Homes.getInstance().getConfig().getStringList(path)
                .stream()
                .map(ConfigUtil::translateColorCodes)
                .toList();
    }


}
