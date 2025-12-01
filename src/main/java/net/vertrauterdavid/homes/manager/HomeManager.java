package net.vertrauterdavid.homes.manager;

import lombok.SneakyThrows;
import net.vertrauterdavid.homes.Homes;
import net.vertrauterdavid.homes.util.Scheduler;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.ResultSet;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class HomeManager {

    private final ConcurrentHashMap<String, String> cache = new ConcurrentHashMap<>();

    @SneakyThrows
    public HomeManager() {
        final ResultSet resultSet = Homes.getInstance().getSqlConnection().getResult("SELECT * FROM Homes");

        while (resultSet.next()) {
            cache.put(resultSet.getString("UUID") + "-1", resultSet.getString("Home1"));
            cache.put(resultSet.getString("UUID") + "-2", resultSet.getString("Home2"));
            cache.put(resultSet.getString("UUID") + "-3", resultSet.getString("Home3"));
            cache.put(resultSet.getString("UUID") + "-4", resultSet.getString("Home4"));
            cache.put(resultSet.getString("UUID") + "-5", resultSet.getString("Home5"));
            cache.put(resultSet.getString("UUID") + "-6", resultSet.getString("Home6"));
            cache.put(resultSet.getString("UUID") + "-7", resultSet.getString("Home7"));
        }
    }

    @SneakyThrows
    public void loadLocal(final @NotNull UUID uuid) {
        final ResultSet resultSet = Homes.getInstance().getSqlConnection().getResult("SELECT * FROM Homes WHERE UUID='" + uuid + "'");
        while (resultSet.next()) {
            cache.put(uuid + "-1", resultSet.getString("Home1"));
            cache.put(uuid + "-2", resultSet.getString("Home2"));
            cache.put(uuid + "-3", resultSet.getString("Home3"));
            cache.put(uuid + "-4", resultSet.getString("Home4"));
            cache.put(uuid + "-5", resultSet.getString("Home5"));
            cache.put(uuid + "-6", resultSet.getString("Home6"));
            cache.put(uuid + "-7", resultSet.getString("Home7"));
        }
    }

    public void set(final @NotNull UUID uuid, int index, Location location) {
        final String string = location.getWorld().getName() + "/" + (location.getBlockX() + 0.5) + "/" + location.getBlockY() + "/" + (location.getBlockZ() + 0.5) + "/" + (Math.round(location.getYaw() / 45) * 45);

        cache.put(uuid + "-" + index, string);

        Scheduler.timerAsync(() -> Homes.getInstance().getSqlConnection().update("UPDATE Homes SET Home" + index + "='" + string + "' WHERE UUID='" + uuid + "'"), 1, 1);
    }


    public void delete(final @NotNull UUID uuid, int index) {
        cache.put(uuid + "-" + index, "-");

        Scheduler.timerAsync(() -> Homes.getInstance().getSqlConnection().update("UPDATE Homes SET Home" + index + "='-' WHERE UUID='" + uuid + "'"), 1, 1);
    }


    public @Nullable Location get(final @NotNull UUID uuid, int index) {
        String string;
        if (cache.containsKey(uuid + "-" + index)) {
            string = cache.get(uuid + "-" + index);
        } else {
            string = Homes.getInstance().getSqlConnection().get("Homes", "Home" + index, "UUID='" + uuid + "'");
            cache.put(uuid + "-" + index, string);
        }

        if (string.equalsIgnoreCase("-")) {
            return null;
        }

        String[] locationData = string.split("/");
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
