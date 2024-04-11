package net.vertrauterdavid.homes.util;

import net.vertrauterdavid.homes.Homes;
import org.bukkit.Bukkit;

import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;

public class VersionUtil {

    private double newestVersion = -1;
    private double currentVersion = -1;

    public VersionUtil() {
        newestVersion = getNewestVersion();
        currentVersion = getCurrentVersion();

        Bukkit.getScheduler().runTaskLaterAsynchronously(Homes.getInstance(), () -> {
            if (newestVersion == -1 || currentVersion == -1) {
                Homes.getInstance().getLogger().log(Level.WARNING, " ");
                Homes.getInstance().getLogger().log(Level.WARNING, "§cVersion check failed! If you want to report this error you can do so under:");
                Homes.getInstance().getLogger().log(Level.WARNING, "§chttps://github.com/VertrauterDavid");
                Homes.getInstance().getLogger().log(Level.WARNING, " ");
                return;
            }

            if (newestVersion > currentVersion) {
                Homes.getInstance().getLogger().log(Level.WARNING, " ");
                Homes.getInstance().getLogger().log(Level.WARNING, "§aHomes started!");
                Homes.getInstance().getLogger().log(Level.WARNING, " ");
                Homes.getInstance().getLogger().log(Level.WARNING, "§fCurrent version: §c" + currentVersion + " §f| Latest version: §c" + newestVersion);
                Homes.getInstance().getLogger().log(Level.WARNING, " ");
                Homes.getInstance().getLogger().log(Level.WARNING, "§cYou can download the latest version here:");
                Homes.getInstance().getLogger().log(Level.WARNING, "§chttps://github.com/VertrauterDavid");
                Homes.getInstance().getLogger().log(Level.WARNING, " ");
                return;
            }

            Homes.getInstance().getLogger().log(Level.INFO, " ");
            Homes.getInstance().getLogger().log(Level.INFO, "§aHomes successfully started!");
            Homes.getInstance().getLogger().log(Level.INFO, " ");
            Homes.getInstance().getLogger().log(Level.INFO, "§fCurrent version: §a" + currentVersion + " §f| Latest version: §a" + newestVersion);
            Homes.getInstance().getLogger().log(Level.INFO, "§aYou are up to date!");
            Homes.getInstance().getLogger().log(Level.INFO, " ");
        }, 20L);
    }

    public double getNewestVersion() {
        if (this.newestVersion != -1) {
            return this.newestVersion;
        }

        try {
            HttpURLConnection httpURLConnection = (HttpURLConnection) new URL("https://api.vertrauterdavid.net/plugins/checkVersionFree.php").openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoOutput(true);
            byte[] input = ("product=Homes").getBytes(StandardCharsets.UTF_8);
            httpURLConnection.getOutputStream().write(input, 0, input.length);
            httpURLConnection.getResponseCode();
            newestVersion = Double.parseDouble(new String(httpURLConnection.getInputStream().readAllBytes(), StandardCharsets.UTF_8));
            return newestVersion;
        } catch (Exception ignored) { }

        return 0;
    }

    public double getCurrentVersion() {
        if (this.currentVersion != -1) {
            return this.currentVersion;
        }

        double currentVersion = 0;

        try {
            currentVersion = Double.parseDouble(Homes.getInstance().getDescription().getVersion());
        } catch (NumberFormatException ignored) { }

        return currentVersion;
    }

}
