package me.zxoir.smp.commands;

import me.zxoir.smp.SMP;
import me.zxoir.smp.customclasses.User;
import me.zxoir.smp.listeners.FirstJoinKitListener;
import me.zxoir.smp.managers.ConfigManager;
import me.zxoir.smp.managers.UserManager;
import me.zxoir.smp.menusystem.MenuUtility;
import me.zxoir.smp.menusystem.menus.SettingsMenu;
import me.zxoir.smp.utilities.Colors;
import me.zxoir.smp.utilities.GlobalCache;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.time.Instant;

/**
 * MIT License Copyright (c) 2022 Zxoir
 *
 * @author Zxoir
 * @since 8/7/2022
 */
public class MainCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String @NotNull [] args) {
        boolean isPlayer = sender instanceof Player;

        if (!sender.hasPermission(GlobalCache.STAFFPERMISSION) && isPlayer) {
            sender.sendMessage(GlobalCache.NOPERMISSION);
            return true;
        }

        if (args.length == 1) {

            // SMP Test
            if (args[0].equalsIgnoreCase("test")) {
                if (!isPlayer)
                    return true;

                Player player = (Player) sender;
                User user = UserManager.getUser(player.getUniqueId());
                player.sendMessage("Calculated Current Session Playtime: " + (user.getCache().getSessionStart() != null ? Duration.between(user.getCache().getSessionStart(), Instant.now()).getSeconds() : "NULL/AFK") + " seconds");
                player.sendMessage("Calculated Total Sessions Playtime: " + (user.getCache().getSessionStart() != null ? user.getPlaytime().plus(Duration.between(user.getCache().getSessionStart(), Instant.now())).getSeconds() : "NULL/AFK") + " seconds");
                player.sendMessage("Cached Playtime: " + user.getPlaytime().getSeconds() + " seconds");
                player.sendMessage("Playtime Serialized: " + UserManager.getTotalPlaytime(user).toString());

                return true;
            }

            // SMP reloadConfig
            if (args[0].equalsIgnoreCase("reload")) {
                ConfigManager.reloadConfig();
                sender.sendMessage(Colors.primary + "Config reloaded");
                return true;
            }

            // SMP SetFirstJoinKit
            if (args[0].equalsIgnoreCase("setFirstJoinKit")) {
                if (!isPlayer)
                    return true;

                Player player = (Player) sender;
                FirstJoinKitListener.setKitItems(player.getInventory().getContents().clone());
                player.sendMessage(Colors.primary + "First Join Kit has been set as your Inventory");

                SMP.getDataFile().getConfig().set("FirstJoinKit", FirstJoinKitListener.getKitItems());
                SMP.getDataFile().saveConfig();
                return true;
            }

            if (args[0].equalsIgnoreCase("getFirstJoinKit")) {
                if (!isPlayer)
                    return true;

                Player player = (Player) sender;
                if (FirstJoinKitListener.getKitItems() == null) {
                    player.sendMessage(Colors.error + "There isn't a set First Join Kit");
                    return true;
                }

                player.getInventory().setContents(FirstJoinKitListener.getKitItems());
                player.sendMessage(Colors.primary + "First Join Kit given");
                return true;
            }

            // SMP Settings
            if (args[0].equalsIgnoreCase("settings")) {
                if (!isPlayer)
                    return true;

                Player player = (Player) sender;

                MenuUtility menuUtility = new MenuUtility(player);
                new SettingsMenu(menuUtility).open();
                return true;
            }

            // SMP AddRareItem
            if (args[0].equalsIgnoreCase("addrareitem")) {
                if (!isPlayer)
                    return true;

                Player player = (Player) sender;
                ItemStack itemInMainHand = player.getInventory().getItemInMainHand();

                if (itemInMainHand.getType().equals(Material.AIR)) {
                    player.sendMessage(Colors.error + "You must be holding an item on your Main Hand!");
                    return true;
                }

                if (SMP.getRareItems().contains(itemInMainHand.getType())) {
                    player.sendMessage(Colors.error + "This material is already listed in the Rare Items list.");
                    return true;
                }

                SMP.getRareItems().add(player.getInventory().getItemInMainHand().getType());
                player.sendMessage(Colors.primary + "Added " + player.getInventory().getItemInMainHand().getType().name());
                return true;
            }

            // SMP AddCraftableItem
            if (args[0].equalsIgnoreCase("addcraftableitem")) {
                if (!isPlayer)
                    return true;

                Player player = (Player) sender;
                ItemStack itemInMainHand = player.getInventory().getItemInMainHand();

                if (itemInMainHand.getType().equals(Material.AIR)) {
                    player.sendMessage(Colors.error + "You must be holding an item on your Main Hand!");
                    return true;
                }

                if (SMP.getCraftableItems().contains(itemInMainHand.getType())) {
                    player.sendMessage(Colors.error + "This material is already listed in the Craftable Items list.");
                    return true;
                }

                if (Bukkit.getServer().getRecipesFor(itemInMainHand).isEmpty()) {
                    player.sendMessage(Colors.error + "&cThis material is not craftable.");
                    return true;
                }

                SMP.getCraftableItems().add(player.getInventory().getItemInMainHand().getType());
                player.sendMessage(Colors.primary + "Added " + player.getInventory().getItemInMainHand().getType().name());
                return true;
            }

        }

        sender.sendMessage("\n" + Colors.primary + "#E5FF65SMP " + ChatColor.BOLD + "List of Commands\n" +
                Colors.primary + "/SMP Test " + Colors.secondary + "Test Command\n" +
                Colors.primary + "/SMP setFirstJoinKit " + Colors.secondary + "Set First Join Kit\n" +
                Colors.primary + "/SMP getFirstJoinKit " + Colors.secondary + "Get First Join Kit\n" +
                Colors.primary + "/SMP addRareItem " + Colors.secondary + "Register Rare Item\n" +
                Colors.primary + "/SMP addCraftableItem " + Colors.secondary + "Register Craftable Item\n" +
                Colors.primary + "/SMP Settings " + Colors.secondary + "Open Server Settings\n" +
                Colors.primary + "/SMP Items " + Colors.secondary + "View LuckyBlock Items\n&a");

        return true;
    }
}