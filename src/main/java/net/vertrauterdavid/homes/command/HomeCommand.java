package net.vertrauterdavid.homes.command;

import net.vertrauterdavid.homes.Homes;
import net.vertrauterdavid.homes.util.ConfigUtil;
import net.vertrauterdavid.homes.util.TeleportUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class HomeCommand implements CommandExecutor, TabCompleter {

    public HomeCommand(String name) {
        Homes.getInstance().getCommand(name).setExecutor(this);
        Homes.getInstance().getCommand(name).setTabCompleter(this);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(commandSender instanceof Player player)) return false;

        if (args.length == 0) {
            Homes.getInstance().openInventory(player);
            return false;
        }

        if (args.length == 1) {
            try {
                int home = Integer.parseInt(args[0]);
                if (home >= 1 && home <= Homes.getInstance().getConfig().getInt("Settings.MaxHomes", 5)) {
                    if (Homes.getInstance().getHomeUtil().get(player.getUniqueId(), home) != null) {
                        TeleportUtil.teleport(player, Homes.getInstance().getHomeUtil().get(player.getUniqueId(), home));
                        return false;
                    }

                    player.sendMessage(ConfigUtil.getPrefix() + ConfigUtil.getMessage("Messages.NotYet"));
                    return false;
                }
            } catch (NumberFormatException ignored) { }
        }

        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("set")) {
                try {
                    int home = Integer.parseInt(args[1]);
                    if (home >= 1 && home <= Homes.getInstance().getConfig().getInt("Settings.MaxHomes", 5)) {
                        if (Homes.getInstance().getAmount(player) < home) {
                            player.sendMessage(ConfigUtil.getPrefix() + ConfigUtil.getMessage("Messages.NoPermission"));
                            return false;
                        }

                        Homes.getInstance().getHomeUtil().set(player.getUniqueId(), home, player.getLocation());
                        player.sendMessage(ConfigUtil.getPrefix() + ConfigUtil.getMessage("Messages.Set"));
                        return false;
                    }
                } catch (NumberFormatException ignored) { }
            }

            if (args[0].equalsIgnoreCase("delete") || args[0].equalsIgnoreCase("remove")) {
                try {
                    int home = Integer.parseInt(args[1]);
                    if (Homes.getInstance().getHomeUtil().get(player.getUniqueId(), home) != null) {
                        Homes.getInstance().getHomeUtil().delete(player.getUniqueId(), home);
                        player.sendMessage(ConfigUtil.getPrefix() + ConfigUtil.getMessage("Messages.Delete"));
                        return false;
                    }

                    player.sendMessage(ConfigUtil.getPrefix() + ConfigUtil.getMessage("Messages.NotYet"));
                    return false;
                } catch (NumberFormatException ignored) { }
            }
        }

        if (player.hasPermission("home.admin") && (args.length == 2 || args.length == 3)) {
            UUID target = Bukkit.getPlayerUniqueId(args[0]);
            if (target == null) {
                player.sendMessage(ConfigUtil.getPrefix() + "§cPlayer was not found!");
                return false;
            }

            if (args.length == 2) {
                try {
                    int home = Integer.parseInt(args[1]);
                    if (home >= 1 && home <= Homes.getInstance().getConfig().getInt("Settings.MaxHomes", 5)) {
                        if (Homes.getInstance().getHomeUtil().get(player.getUniqueId(), home) != null) {
                            player.teleport(Homes.getInstance().getHomeUtil().get(target, home));
                            return false;
                        }

                        player.sendMessage(ConfigUtil.getPrefix() + ConfigUtil.getMessage("Messages.NotYet"));
                        return false;
                    }
                } catch (NumberFormatException ignored) { }
            }

            if (args.length == 3) {
                if (args[1].equalsIgnoreCase("set")) {
                    try {
                        int home = Integer.parseInt(args[2]);
                        if (home >= 1 && home <= Homes.getInstance().getConfig().getInt("Settings.MaxHomes", 5)) {
                            Homes.getInstance().getHomeUtil().set(target, home, player.getLocation());
                            player.sendMessage(ConfigUtil.getPrefix() + ConfigUtil.getMessage("Messages.Set"));
                            return false;
                        }
                    } catch (NumberFormatException ignored) { }
                }

                if (args[1].equalsIgnoreCase("delete") || args[1].equalsIgnoreCase("remove")) {
                    try {
                        int home = Integer.parseInt(args[2]);
                        if (Homes.getInstance().getHomeUtil().get(target, home) != null) {
                            Homes.getInstance().getHomeUtil().delete(target, home);
                            player.sendMessage(ConfigUtil.getPrefix() + ConfigUtil.getMessage("Messages.Delete"));
                            return false;
                        }

                        player.sendMessage(ConfigUtil.getPrefix() + ConfigUtil.getMessage("Messages.NotYet"));
                        return false;
                    } catch (NumberFormatException ignored) { }
                }
            }
        }

        player.sendMessage(" ");
        player.sendMessage(ConfigUtil.getPrefix() + "Usage: §c/home");
        player.sendMessage(ConfigUtil.getPrefix() + "Usage: §c/home <1-" + Homes.getInstance().getAmount(player) + ">");
        player.sendMessage(ConfigUtil.getPrefix() + "Usage: §c/home set <1-" + Homes.getInstance().getAmount(player) + ">");
        player.sendMessage(ConfigUtil.getPrefix() + "Usage: §c/home delete <1-" + Homes.getInstance().getAmount(player) + ">");
        player.sendMessage(ConfigUtil.getPrefix() + "Usage: §c/home remove <1-" + Homes.getInstance().getAmount(player) + ">");
        if (player.hasPermission("home.admin")) {
            player.sendMessage(" ");
            player.sendMessage(ConfigUtil.getPrefix() + "Usage: §c/home <player> <1-" + Homes.getInstance().getConfig().getInt("Settings.MaxHomes", 5) + ">");
            player.sendMessage(ConfigUtil.getPrefix() + "Usage: §c/home <player> set <1-" + Homes.getInstance().getConfig().getInt("Settings.MaxHomes", 5) + ">");
            player.sendMessage(ConfigUtil.getPrefix() + "Usage: §c/home <player> delete <1-" + Homes.getInstance().getConfig().getInt("Settings.MaxHomes", 5) + ">");
            player.sendMessage(ConfigUtil.getPrefix() + "Usage: §c/home <player> remove <1-" + Homes.getInstance().getConfig().getInt("Settings.MaxHomes", 5) + ">");
        }
        player.sendMessage(" ");
        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(commandSender instanceof Player player)) return new ArrayList<>();

        List<String> list = new ArrayList<>();

        if (args.length == 3) {
            UUID target = Bukkit.getPlayerUniqueId(args[0]);
            if (target != null) {
                if (args[1].equalsIgnoreCase("set")) {
                    for (int i = 1; i <= Homes.getInstance().getConfig().getInt("Settings.MaxHomes", 5); i++) {
                        list.add(i + "");
                    }
                } else if (args[1].equalsIgnoreCase("delete") || args[1].equalsIgnoreCase("remove")) {
                    for (int i = 1; i <= Homes.getInstance().getConfig().getInt("Settings.MaxHomes", 5); i++) {
                        if (Homes.getInstance().getHomeUtil().get(target, i) != null) {
                            list.add(i + "");
                        }
                    }
                }
            }
        }

        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("set")) {
                for (int i = 1; i <= Homes.getInstance().getAmount(player); i++) {
                    list.add(i + "");
                }
            } else if (args[0].equalsIgnoreCase("delete") || args[0].equalsIgnoreCase("remove")) {
                for (int i = 1; i <= Homes.getInstance().getAmount(player); i++) {
                    if (Homes.getInstance().getHomeUtil().get(player.getUniqueId(), i) != null) {
                        list.add(i + "");
                    }
                }
            } else if (player.hasPermission("home.admin")) {
                list.add("set");
                list.add("delete");
                list.add("remove");
            }
        }

        if (args.length == 1) {
            list.add("set");
            list.add("delete");
            list.add("remove");

            if (player.hasPermission("home.admin")) {
                list.addAll(Bukkit.getOnlinePlayers().stream().map(Player::getName).toList());
            }

            for (int i = 1; i <= Homes.getInstance().getAmount(player); i++) {
                if (Homes.getInstance().getHomeUtil().get(player.getUniqueId(), i) != null) {
                    list.add(i + "");
                }
            }
        }

        return list.stream().filter(content -> content.toLowerCase().startsWith(args[args.length - 1].toLowerCase())).sorted().toList();
    }

}
