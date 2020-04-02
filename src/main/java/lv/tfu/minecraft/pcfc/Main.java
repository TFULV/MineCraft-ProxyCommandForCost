package lv.tfu.minecraft.pcfc;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;

@SuppressWarnings("unused")
public class Main extends JavaPlugin implements Listener {
    private Economy economy;
    private static ArrayList<ProxyCommand> proxyCommands = new ArrayList<>();

    public void onEnable() {
        getConfig().options().copyDefaults(true);
        saveConfig();

        loadingCommands();

        try {
            if (Bukkit.getPluginManager().getPlugin("Vault") == null) {
                throw new Exception();
            }
            RegisteredServiceProvider<Economy> registeredServiceProvider = Bukkit.getServicesManager().getRegistration(Economy.class);
            economy = registeredServiceProvider.getProvider();
            getLogger().info("Vault plugin connected - economy rewards enabled");
        } catch (Exception e) {
            getLogger().warning("Economy plugin not installed!");
        }

        getServer().getPluginManager().registerEvents(this, this);

        getLogger().info("Enabled!");
    }

    public void onDisable() {
        getLogger().info("Disabled!");
    }

    public boolean onCommand(CommandSender commandSender, Command cmd, String label, String[] args) {
        if (commandSender instanceof Player) {
            Player player = ((Player) commandSender).getPlayer();
            if (player == null) {
                getLogger().warning("Something called a command " + cmd.getName());
                return true;
            }

            if (cmd.getName().equalsIgnoreCase("proxyCommands")) {
                if (args.length > 0 && args[0].equalsIgnoreCase("reload")) {
                    proxyCommands.clear();
                    reloadConfig();
                    loadingCommands();
                }

                StringBuilder builderMessage = new StringBuilder(getLanguage("listOfCommands"));
                for (ProxyCommand proxyCommand : proxyCommands) {
                    builderMessage
                            .append("\n")
                            .append(
                                    getLanguage("commandInList")
                                            .replaceAll("%command%", proxyCommand.getAlias())
                                            .replaceAll("%cost%", proxyCommand.getCostToString())
                            );
                }
                player.sendMessage(builderMessage.toString());
            }
        } else {
            getLogger().warning("Some called plugin command");
        }
        return true;
    }

    @SuppressWarnings("unused")
    @EventHandler(priority = EventPriority.HIGHEST)
    public void playerCommandChat(PlayerCommandPreprocessEvent event) {
        for (ProxyCommand proxyCommand : proxyCommands) {
            if (event.getMessage().equalsIgnoreCase(proxyCommand.getAlias())) {
                makeMagic(event.getPlayer(), proxyCommand);
                event.setCancelled(true);
            }
        }
    }

    private void loadingCommands() {
        for (String commandIndex : getConfig().getConfigurationSection("list").getKeys(false)) {
            ConfigurationSection commandSection = getConfig().getConfigurationSection("list." + commandIndex);
            proxyCommands.add(new ProxyCommand(
                    commandSection.getString("alias"),
                    commandSection.getDouble("cost"),
                    commandSection.getBoolean("giveOp"),
                    commandSection.getString("command")
            ));
        }
        getLogger().info("Loaded " + proxyCommands.size() + " commands!");
    }

    private String getLanguage(String key) {
        return getConfig().getString("language." + key);
    }

    private void makeMagic(Player player, ProxyCommand proxyCommand) {
        if (economy.getBalance(player) < proxyCommand.getCost()) {
            player.sendMessage(
                    getLanguage("declineMessage").replaceAll("%cost%", proxyCommand.getCostToString())
            );
            return;
        }

        boolean currentOp = !player.isOp();
        try {
            if (proxyCommand.isGiveOp() && currentOp) {
                player.setOp(true);
            }
            player.sendMessage(
                    getLanguage("payMessage").replaceAll("%cost%", proxyCommand.getCostToString())
            );
            getServer().dispatchCommand(player, proxyCommand.getServerCommand());
            getLogger().info("Command for pay: " + proxyCommand.getCommand());
            economy.withdrawPlayer(player, proxyCommand.getCost());
        } finally {
            if (proxyCommand.isGiveOp() && currentOp) {
                player.setOp(false);
            }
        }
    }
}