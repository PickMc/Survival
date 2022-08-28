package me.zxoir.smp.utilities;

import me.zxoir.smp.SMP;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * MIT License Copyright (c) 2022 Zxoir
 *
 * @author Zxoir
 * @since 8/6/2022
 */
public class BasicUtilities {

    public static boolean isInteger(String key) {
        try {
            Integer.parseInt(key);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isDouble(String key) {
        try {
            Double.parseDouble(key);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static @NotNull List<String> smartComplete(String @NotNull [] args, @NotNull List<String> list) {
        String arg = args[args.length - 1];
        List<String> temp = new ArrayList<>();

        for (String item : list) {
            if (item.toUpperCase().startsWith(arg.toUpperCase())) {
                temp.add(item);
            }
        }

        return temp;
    }

    public static @NotNull BukkitTask runTaskSync(Task task) {
        return new BukkitRunnable() {
            @Override
            public void run() {
                task.execute();
            }
        }.runTask(SMP.getPlugin(SMP.class));
    }

    public static @NotNull BukkitTask runTaskAsync(Task task) {
        return new BukkitRunnable() {
            @Override
            public void run() {
                task.execute();
            }
        }.runTaskAsynchronously(SMP.getPlugin(SMP.class));
    }

    public interface Task {
        void execute();
    }
}
