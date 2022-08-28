package me.zxoir.smp.runnables;

import me.zxoir.smp.customclasses.ChatEvents;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * MIT License Copyright (c) 2022 Zxoir
 *
 * @author Zxoir
 * @since 8/7/2022
 */
public class ChatEventEndRunnable extends BukkitRunnable {

    @Override
    public void run() {
        ChatEvents.endEvent(null);
    }

}
