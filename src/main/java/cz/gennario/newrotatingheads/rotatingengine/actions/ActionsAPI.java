package cz.gennario.newrotatingheads.rotatingengine.actions;

import com.cryptomorin.xseries.XSound;
import com.cryptomorin.xseries.messages.ActionBar;
import com.cryptomorin.xseries.messages.Titles;
import cz.gennario.newrotatingheads.Main;
import cz.gennario.newrotatingheads.utils.BungeeUtils;
import cz.gennario.newrotatingheads.utils.Utils;
import cz.gennario.newrotatingheads.utils.replacement.Replacement;
import dev.dejvokep.boostedyaml.block.implementation.Section;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;

public class ActionsAPI {

    private final Map<String, ActionResponse> actions;
    private final Replacement replacement;

    public ActionsAPI() {
        this.actions = new HashMap<>();
        this.replacement = new Replacement((player, string) -> string.replace("%player%", player.getName()));

        loadDefaults();
    }

    public void loadDefaults() {
        /* Connect to server action */
        addAction("connect", (player, identifier, data, replacement1) -> {
            if (data.isExist("value")) {
                BungeeUtils.connect(player, replacement.replace(player, data.getString("value")));
            }
        });

        /* Console and player command */
        addAction("console-cmd", (player, identifier, data, replacement) -> {
            if (data.isExist("value")) {
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), replacement.replace(player, data.getString("value")));
                    }
                }.runTask(Main.getInstance());
            } else if (data.isExist("values")) {
                for (String s : data.getListString("values")) {
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), replacement.replace(player, s));
                        }
                    }.runTask(Main.getInstance());
                }
            } else {
                System.out.println("Some console-cmd action are missing correct data");
            }
        });

        /* Console and player command */
        addAction("message", (player, identifier, data, replacement) -> {
            if (data.isExist("value")) {
                player.sendMessage(Utils.colorize(player, replacement.replace(player, data.getString("value"))));
            } else if (data.isExist("values")) {
                for (String s : data.getListString("values")) {
                    player.sendMessage(Utils.colorize(player, replacement.replace(player, s)));
                }
            } else {
                System.out.println("Some console-cmd action are missing correct data");
            }
        });

        /* Console and player command */
        addAction("broadcast", (player, identifier, data, replacement) -> {
            if (data.isExist("value")) {
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        Bukkit.broadcastMessage(Utils.colorize(player, replacement.replace(player, data.getString("value"))));
                    }
                }.runTask(Main.getInstance());
            } else if (data.isExist("values")) {
                for (String s : data.getListString("values")) {
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            Bukkit.broadcastMessage(Utils.colorize(player, replacement.replace(player, s)));
                        }
                    }.runTask(Main.getInstance());
                }
            } else {
                System.out.println("Some console-cmd action are missing correct data");
            }
        });

        addAction("player-cmd", (player, identifier, data, replacement) -> {
            if (data.isExist("value")) {
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        player.chat("/" + replacement.replace(player, data.getString("value")));
                    }
                }.runTask(Main.getInstance());
            } else if (data.isExist("values")) {
                for (String s : data.getListString("values")) {
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            player.chat("/" + replacement.replace(player, s));
                        }
                    }.runTask(Main.getInstance());
                }
            } else {
                System.out.println("Some player-cmd action are missing correct data");
            }
        });

        /* Close inventory action */
        addAction("close-inv", (player, identifier, data, replacement) -> {
            player.closeInventory();
        });

        /* Actionbar action */
        addAction("actionbar", (player, identifier, data, replacement) -> {
            if (data.isExist("value")) {
                ActionBar.sendActionBar(player, Utils.colorize(player, replacement.replace(player, data.getString("value"))));
            } else {
                System.out.println("Some actionbar action are missing correct data");
            }
        });
        addAction("actionbar-all", (player, identifier, data, replacement) -> {
            if (data.isExist("value")) {
                for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                    ActionBar.sendActionBar(onlinePlayer, Utils.colorize(player, replacement.replace(onlinePlayer, data.getString("value"))));
                }
            } else {
                System.out.println("Some actionbar-all action are missing correct data");
            }
        });

        /* Title action */
        addAction("title", (player, identifier, data, replacement) -> {
            if (data.isExist("title") && data.isExist("subtitle")) {
                String title = data.getString("title", "");
                String subtitle = data.getString("subtitle", "");
                int fadeIn = data.getInt("fade-in", 20);
                int stay = data.getInt("stay", 60);
                int fadeOut = data.getInt("fade-out", 20);
                Titles.sendTitle(player, fadeIn, stay, fadeOut, Utils.colorize(player, replacement.replace(player, title)), Utils.colorize(player, replacement.replace(player, subtitle)));
            } else {
                System.out.println("Some title action are missing correct data");
            }
        });
        addAction("title-all", (player, identifier, data, replacement) -> {
            if (data.isExist("title") && data.isExist("subtitle")) {
                String title = data.getString("title", "");
                String subtitle = data.getString("subtitle", "");
                int fadeIn = data.getInt("fade-in", 20);
                int stay = data.getInt("stay", 60);
                int fadeOut = data.getInt("fade-out", 20);
                for (Player op : Bukkit.getOnlinePlayers()) {
                    Titles.sendTitle(op, fadeIn, stay, fadeOut, Utils.colorize(player, replacement.replace(player, title)), Utils.colorize(player, replacement.replace(player, subtitle)));
                }
            } else {
                System.out.println("Some title action are missing correct data");
            }
        });

        /* Sound action */
        addAction("sound", ((player, identifier, data, replacement1) -> {
            if (data.isExist("value")) {
                float volume = 60;
                float pitch = 20;
                if (data.isExist("volume")) volume = data.getFloat("volume");
                if (data.isExist("pitch")) pitch = data.getFloat("pitch");
                Sound sound = XSound.valueOf(data.getString("value")).parseSound();
                if (sound != null) {
                    player.playSound(player.getLocation(), sound, volume, pitch);
                }
            }
        }));
        addAction("sound-all", ((player, identifier, data, replacement1) -> {
            if (data.isExist("value")) {
                float volume = 60;
                float pitch = 20;
                if (data.isExist("volume")) volume = data.getFloat("volume");
                if (data.isExist("pitch")) pitch = data.getFloat("pitch");
                Sound sound = XSound.valueOf(data.getString("value")).parseSound();
                if (sound != null) {
                    for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                        onlinePlayer.playSound(player.getLocation(), sound, volume, pitch);
                    }
                }
            }
        }));

        /* Gamemode action */
        addAction("gamemode", ((player, identifier, data, replacement1) -> {
            if (data.isExist("value")) {
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        player.setGameMode(GameMode.valueOf(replacement.replace(player, data.getString("value")).toUpperCase()));
                    }
                }.runTask(Main.getInstance());
            }
        }));
        addAction("gamemode-all", ((player, identifier, data, replacement1) -> {
            if (data.isExist("value")) {
                for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            onlinePlayer.setGameMode(GameMode.valueOf(replacement.replace(onlinePlayer, data.getString("value")).toUpperCase()));
                        }
                    }.runTask(Main.getInstance());
                }
            }
        }));

        /* Fly action */
        addAction("fly-toggle", (player, identifier, data, replacement1) -> {
            player.setAllowFlight(!player.getAllowFlight());
        });
        addAction("fly-enabled", (player, identifier, data, replacement1) -> {
            player.setAllowFlight(true);
        });
        addAction("fly-disabled", (player, identifier, data, replacement1) -> {
            player.setAllowFlight(false);
        });
        addAction("fly-toggle-all", (player, identifier, data, replacement1) -> {
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                onlinePlayer.setAllowFlight(!onlinePlayer.getAllowFlight());
            }
        });
    }

    public void addAction(String identifier, ActionResponse response) {
        actions.put(identifier, response);
    }


    public void useAction(Player player, Section... actionConfigurations) {
        for (Section section : actionConfigurations) {
            if (section.contains("conditions")) {
                boolean allow = true;
                for (String s : section.getSection("conditions").getRoutesAsStrings(false)) {
                    if (!Main.getInstance().getConditionsAPI().check(player, section.getSection("conditions." + s), replacement))
                        allow = false;
                }

                if (!allow) {
                    continue;
                }
            }

            String type = section.getString("type");
            if (!actions.containsKey(type)) {
                System.out.println("Action " + type + " doesn't exist! Please try something else...");
                return;
            }
            ActionData data = new ActionData(section);
            actions.get(type).action(player, type, data, replacement);
        }
    }

    public void useAction(Player player, Replacement replacement, Section... actionConfigurations) {
        for (Section section : actionConfigurations) {
            if (section.contains("conditions")) {
                boolean allow = true;
                for (String s : section.getSection("conditions").getRoutesAsStrings(false)) {
                    if (!Main.getInstance().getConditionsAPI().check(player, section.getSection("conditions." + s), replacement))
                        allow = false;
                }
                if (!allow) {
                    continue;
                }
            }

            String type = section.getString("type");
            if (!actions.containsKey(type)) {
                System.out.println("Action " + type + " doesn't exist! Please try something else...");
                return;
            }
            ActionData data = new ActionData(section);
            actions.get(type).action(player, type, data, replacement);
        }
    }

}
