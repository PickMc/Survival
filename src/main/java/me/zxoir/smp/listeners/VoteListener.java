package me.zxoir.smp.listeners;

import com.vexsoftware.votifier.model.Vote;
import com.vexsoftware.votifier.model.VotifierEvent;
import lombok.Getter;
import me.zxoir.smp.customclasses.LuckyBlock;
import me.zxoir.smp.managers.ConfigManager;
import me.zxoir.smp.utilities.BasicUtilities;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.UUID;

/**
 * MIT License Copyright (c) 2022 Zxoir
 *
 * @author Zxoir
 * @since 8/18/2022
 */
public class VoteListener implements Listener {
    @Getter
    private static final HashMap<UUID, Integer> pendingVoteReward = new HashMap<>();

    @EventHandler
    public void onVote(@NotNull VotifierEvent event) {
        Vote vote = event.getVote();

        OfflinePlayer voter = Bukkit.getOfflinePlayer(vote.getUsername());
        if (!voter.hasPlayedBefore())
            return;

        if (voter.isOnline() && voter.getPlayer() != null && LuckyBlock.getLuckyBlock() != null) {
            if (voter.getPlayer().getInventory().addItem(LuckyBlock.getLuckyBlock()).isEmpty())
                voter.getPlayer().sendMessage(ConfigManager.getVoteReceivedMessage());
            else {
                voter.getPlayer().sendMessage(ConfigManager.getVoteInventoryFullMessage());
                voter.getPlayer().getWorld().dropItemNaturally(voter.getPlayer().getLocation(), LuckyBlock.getLuckyBlock());
            }

        } else
            addPendingVoteReward(voter.getUniqueId());

    }

    @EventHandler
    public void onJoin(@NotNull PlayerJoinEvent event) {
        Player player = event.getPlayer();

        if (!pendingVoteReward.containsKey(player.getUniqueId()) || LuckyBlock.getLuckyBlock() == null)
            return;

        int amount = pendingVoteReward.get(player.getUniqueId());
        ItemStack itemStack = LuckyBlock.getLuckyBlock();
        itemStack.setAmount(amount);

        HashMap<Integer, ItemStack> lostItems = player.getInventory().addItem(itemStack);
        if (lostItems.isEmpty())
            player.sendMessage(ConfigManager.getVoteReceivedOfflineMessage());
        else {
            player.sendMessage(ConfigManager.getVoteInventoryFullMessage());

            for (ItemStack item : lostItems.values())
                player.getWorld().dropItemNaturally(player.getLocation(), item);
        }

        pendingVoteReward.remove(player.getUniqueId());
    }

    @EventHandler
    public void onRespawn(@NotNull PlayerRespawnEvent event) {
        Player player = event.getPlayer();

        if (!pendingVoteReward.containsKey(player.getUniqueId()) || LuckyBlock.getLuckyBlock() == null)
            return;

        int amount = pendingVoteReward.get(player.getUniqueId());
        ItemStack itemStack = LuckyBlock.getLuckyBlock();
        itemStack.setAmount(amount);
        BasicUtilities.runTaskSync(() -> player.getInventory().addItem(itemStack));
        pendingVoteReward.remove(player.getUniqueId());
    }

    private void addPendingVoteReward(UUID uuid) {
        if (!pendingVoteReward.containsKey(uuid)) {
            pendingVoteReward.put(uuid, 1);
            return;
        }

        pendingVoteReward.put(uuid, pendingVoteReward.get(uuid) + 1);
    }
}
