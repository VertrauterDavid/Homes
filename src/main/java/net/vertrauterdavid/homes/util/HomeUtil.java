package net.vertrauterdavid.homes.util;

import net.vertrauterdavid.homes.Homes;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.UUID;

public class HomeUtil {

    public void set(UUID uuid, int number, Location location) {
        Homes.getInstance().getSqlUtil().update("UPDATE Homes SET Home" + number + "='" + location.getWorld().getName() + "/" + (location.getBlockX() + 0.5) + "/" + location.getBlockY() + "/" + (location.getBlockZ() + 0.5) + "/" + (Math.round(location.getYaw() / 45) * 45) + "' WHERE UUID='" + uuid.toString() + "'");
    }

    public void delete(UUID uuid, int number) {
        Homes.getInstance().getSqlUtil().update("UPDATE Homes SET Home" + number + "='-' WHERE UUID='" + uuid.toString() + "'");
    }

    public Location get(UUID uuid, int number) {
        String config = Homes.getInstance().getSqlUtil().get("Homes", "Home" + number, "UUID='" + uuid.toString() + "'");
        if (config.equalsIgnoreCase("-")) {
            return null;
        }

        String[] locationData = config.split("/");
        return new Location(
                Bukkit.getWorld(locationData[0]),
                Double.parseDouble(locationData[1]),
                Double.parseDouble(locationData[2]),
                Double.parseDouble(locationData[3]),
                (float) Double.parseDouble(locationData[4]),
                0
        );
    }

}
