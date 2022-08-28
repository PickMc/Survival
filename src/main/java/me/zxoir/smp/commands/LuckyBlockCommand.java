package me.zxoir.smp.commands;

import me.zxoir.smp.SMP;
import me.zxoir.smp.customclasses.LuckyBlock;
import me.zxoir.smp.menusystem.MenuUtility;
import me.zxoir.smp.menusystem.menus.LuckyBlockItemsMenu;
import me.zxoir.smp.utilities.BasicUtilities;
import me.zxoir.smp.utilities.Colors;
import me.zxoir.smp.utilities.GlobalCache;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * MIT License Copyright (c) 2022 Zxoir
 *
 * @author Zxoir
 * @since 8/17/2022
 */
public class LuckyBlockCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player))
            return true;

        if (!player.hasPermission(GlobalCache.STAFFPERMISSION)) {
            player.sendMessage(GlobalCache.NOPERMISSION);
            return true;
        }

        if (args.length == 1) {
            // LuckyBlock setLuckyBlock
            if (args[0].equalsIgnoreCase("setLuckyBlock")) {
                ItemStack itemInMainHand = player.getInventory().getItemInMainHand();

                if (itemInMainHand.getType().equals(Material.AIR)) {
                    player.sendMessage(Colors.error + "You must be holding an item on your Main Hand!");
                    return true;
                }

                LuckyBlock.setLuckyBlock(itemInMainHand.clone());
                player.sendMessage(Colors.primary + "LuckyBlock Item has been set.");

                SMP.getDataFile().getConfig().set("LuckyBlockItem", LuckyBlock.getLuckyBlock());
                SMP.getDataFile().saveConfig();
                return true;
            }

            // LuckyBlock getLuckyBlock
            if (args[0].equalsIgnoreCase("getLuckyBlock")) {

                if (LuckyBlock.getLuckyBlock() == null) {
                    player.sendMessage(Colors.error + "There isn't a set LuckyBlock Item");
                    return true;
                }

                player.getInventory().addItem(LuckyBlock.getLuckyBlock());
                player.sendMessage(Colors.primary + "LuckyBlock Item has been given.");
                return true;
            }

            // LuckyBlock items
            if (args[0].equalsIgnoreCase("items")) {

                if (LuckyBlock.getRandomItems().isEmpty()) {
                    player.sendMessage(Colors.error + "There are no LuckyBlock items");
                    return true;
                }

                MenuUtility menuUtility = new MenuUtility(player);
                new LuckyBlockItemsMenu(menuUtility).open();
                return true;
            }
        }

        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("additem")) {
                if (!BasicUtilities.isDouble(args[1])) {
                    player.sendMessage(Colors.error + "Chance must be a number");
                    return true;
                }

                double chance = Double.parseDouble(args[1]);
                if (chance <= 0) {
                    player.sendMessage(Colors.error + "Chance must be more than 0");
                    return true;
                }

                ItemStack itemInMainHand = player.getInventory().getItemInMainHand();

                if (itemInMainHand.getType().equals(Material.AIR)) {
                    player.sendMessage(Colors.error + "You must be holding an item on your Main Hand!");
                    return true;
                }

                if (LuckyBlock.getRandomItems().getLogged().containsKey(itemInMainHand)) {
                    player.sendMessage(Colors.error + "This item is already in the LuckyBlock");
                    return true;
                }

                LuckyBlock.getRandomItems().add(chance, itemInMainHand);
                player.sendMessage(Colors.primary + "Item has been added to LuckyBlock");
                return true;
            }

        }

        player.sendMessage("\n" + Colors.colorize("#E5FF65LuckyBlock " + "&lList of Commands\n" +
                Colors.primary + "/LuckyBlock getLuckyBlock " + Colors.secondary + "Receive a LuckyBlock\n" +
                Colors.primary + "/LuckyBlock setLuckyBlock " + Colors.secondary + "Set the LuckyBlock\n" +
                Colors.primary + "/LuckyBlock addItem <Chance> " + Colors.secondary + "Add item to LuckyBlock\n" +
                Colors.primary + "/LuckyBlock Items " + Colors.secondary + "View LuckyBlock Items\n&a"));

        return true;
    }

}