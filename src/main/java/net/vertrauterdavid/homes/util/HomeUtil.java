package net.vertrauterdavid.homes.util;

import lombok.Getter;
import net.vertrauterdavid.homes.Homes;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.sql.ResultSet;
import java.util.HashMap;
import java.util.UUID;

@Getter
public class HomeUtil {

    private final HashMap<String, String> localStorage = new HashMap<>();

    public HomeUtil() {
        ResultSet resultSet = Homes.getInstance().getSqlUtil().getResult("SELECT * FROM Homes");
        try {
            while (resultSet.next()) {
                localStorage.put(resultSet.getString("UUID") + "-1", resultSet.getString("Home1"));
                localStorage.put(resultSet.getString("UUID") + "-2", resultSet.getString("Home2"));
                localStorage.put(resultSet.getString("UUID") + "-3", resultSet.getString("Home3"));
                localStorage.put(resultSet.getString("UUID") + "-4", resultSet.getString("Home4"));
                localStorage.put(resultSet.getString("UUID") + "-5", resultSet.getString("Home5"));
                localStorage.put(resultSet.getString("UUID") + "-6", resultSet.getString("Home6"));
                localStorage.put(resultSet.getString("UUID") + "-7", resultSet.getString("Home7"));
            }
        } catch (Exception ignored) { }
    }

    public void loadLocal(UUID uuid) {
        ResultSet resultSet = Homes.getInstance().getSqlUtil().getResult("SELECT * FROM Homes WHERE UUID='" + uuid.toString() + "'");
        try {
            while (resultSet.next()) {
                localStorage.put(uuid + "-1", resultSet.getString("Home1"));
                localStorage.put(uuid + "-2", resultSet.getString("Home2"));
                localStorage.put(uuid + "-3", resultSet.getString("Home3"));
                localStorage.put(uuid + "-4", resultSet.getString("Home4"));
                localStorage.put(uuid + "-5", resultSet.getString("Home5"));
                localStorage.put(uuid + "-6", resultSet.getString("Home6"));
                localStorage.put(uuid + "-7", resultSet.getString("Home7"));
            }
        } catch (Exception ignored) { }
    }

    public void set(UUID uuid, int number, Location location) {
        String s = location.getWorld().getName() + "/" + (location.getBlockX() + 0.5) + "/" + location.getBlockY() + "/" + (location.getBlockZ() + 0.5) + "/" + (Math.round(location.getYaw() / 45) * 45);
        Homes.getInstance().getSqlUtil().update("UPDATE Homes SET Home" + number + "='" + s + "' WHERE UUID='" + uuid.toString() + "'");
        localStorage.put(uuid + "-" + number, s);
    }

    public void delete(UUID uuid, int number) {
        Homes.getInstance().getSqlUtil().update("UPDATE Homes SET Home" + number + "='-' WHERE UUID='" + uuid.toString() + "'");
        localStorage.put(uuid + "-" + number, "-");
    }

    public Location get(UUID uuid, int number) {
        String s;
        if (localStorage.containsKey(uuid + "-" + number)) {
            s = localStorage.get(uuid + "-" + number);
        } else {
            s = Homes.getInstance().getSqlUtil().get("Homes", "Home" + number, "UUID='" + uuid.toString() + "'");
            localStorage.put(uuid + "-" + number, s);
        }

        if (s.equalsIgnoreCase("-")) {
            return null;
        }

        String[] locationData = s.split("/");
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
