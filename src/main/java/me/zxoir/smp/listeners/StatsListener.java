package me.zxoir.smp.listeners;

import me.zxoir.smp.customclasses.User;
import me.zxoir.smp.managers.UserManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.jetbrains.annotations.NotNull;

public class StatsListener implements Listener {

    @EventHandler
    public void onDeath(@NotNull PlayerDeathEvent event) {
        User user = UserManager.getUser(event.getPlayer().getUniqueId());
        user.getStats().setDeaths(user.getStats().getDeaths() + 1);
    }


}
