package net.vertrauterdavid.homes.command;

import dev.jorel.commandapi.CommandTree;
import dev.jorel.commandapi.arguments.IntegerArgument;
import dev.jorel.commandapi.arguments.LiteralArgument;
import net.vertrauterdavid.homes.Homes;
import net.vertrauterdavid.homes.menu.HomesMenu;
import net.vertrauterdavid.homes.util.ConfigUtil;
import net.vertrauterdavid.homes.util.TeleportUtil;

public class HomeCommand {

    public static void register() {
        new CommandTree("home")
                .withAliases("homes")
                .executesPlayer((player, args) -> {
                    HomesMenu.show(player);
                })

                .then(new IntegerArgument("home", 1, Homes.getInstance().getConfig().getInt("Settings.MaxHomes", 5))
                        .executesPlayer((player, args) -> {
                            int home = (int) args.get("home");

                            if (Homes.getInstance().getHomeManager().get(player.getUniqueId(), home) != null) {
                                TeleportUtil.teleport(player, Homes.getInstance().getHomeManager().get(player.getUniqueId(), home));
                                return;
                            }
                            player.sendMessage(ConfigUtil.getPrefix().append(ConfigUtil.getMessage("Messages.NotYet")));
                        }))

                .then(new LiteralArgument("set")
                        .then(new IntegerArgument("home", 1, Homes.getInstance().getConfig().getInt("Settings.MaxHomes", 5))
                                .executesPlayer((player, args) -> {
                                    int home = (int) args.get("home");

                                    if (Homes.getInstance().getAmount(player) < home) {
                                        player.sendMessage(ConfigUtil.getPrefix().append(ConfigUtil.getMessage("Messages.NoPermission")));
                                        return;
                                    }

                                    Homes.getInstance().getHomeManager().set(player.getUniqueId(), home, player.getLocation());
                                    player.sendMessage(ConfigUtil.getPrefix().append(ConfigUtil.getMessage("Messages.Set")));
                                })))

                .then(new LiteralArgument("delete")
                        .then(new IntegerArgument("home", 1, Homes.getInstance().getConfig().getInt("Settings.MaxHomes", 5))
                                .executesPlayer((player, args) -> {
                                    int home = (int) args.get("home");

                                    if (Homes.getInstance().getHomeManager().get(player.getUniqueId(), home) != null) {
                                        Homes.getInstance().getHomeManager().delete(player.getUniqueId(), home);
                                        player.sendMessage(ConfigUtil.getPrefix().append(ConfigUtil.getMessage("Messages.Delete")));
                                        return;
                                    }

                                    player.sendMessage(ConfigUtil.getPrefix().append(ConfigUtil.getMessage("Messages.NotYet")));
                                })))

                .then(new LiteralArgument("remove")
                        .then(new IntegerArgument("home", 1, Homes.getInstance().getConfig().getInt("Settings.MaxHomes", 5))
                                .executesPlayer((player, args) -> {
                                    int home = (int) args.get("home");

                                    if (Homes.getInstance().getHomeManager().get(player.getUniqueId(), home) != null) {
                                        Homes.getInstance().getHomeManager().delete(player.getUniqueId(), home);
                                        player.sendMessage(ConfigUtil.getPrefix().append(ConfigUtil.getMessage("Messages.Delete")));
                                        return;
                                    }

                                    player.sendMessage(ConfigUtil.getPrefix().append(ConfigUtil.getMessage("Messages.NotYet")));
                                })))

                .register();
    }
}
