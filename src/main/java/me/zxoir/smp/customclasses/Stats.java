package me.zxoir.smp.customclasses;

import com.google.common.base.Strings;
import lombok.Getter;
import lombok.Setter;
import me.zxoir.smp.managers.UserManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static me.zxoir.smp.utilities.BasicUtilities.runTaskSync;
import static me.zxoir.smp.utilities.Colors.colorize;

/**
 * MIT License Copyright (c) 2022 Zxoir
 *
 * @author Zxoir
 * @since 8/5/2022
 */

@Getter
@Setter
public class Stats {
    final UUID uuid;
    AtomicInteger level = new AtomicInteger(1);
    AtomicInteger deaths = new AtomicInteger(0);
    AtomicReference<BigDecimal> experience = new AtomicReference<>(new BigDecimal(0));

    public Stats(UUID uuid) {
        this.uuid = uuid;
    }

    public Stats(UUID uuid, AtomicInteger level, AtomicInteger deaths, AtomicReference<BigDecimal> experience) {
        this.uuid = uuid;
        this.level = level;
        this.deaths = deaths;
        this.experience = experience;
    }

    public BigDecimal addXp(int minXp, int maxXp) {
        BigDecimal randomXp = BigDecimal.valueOf(ThreadLocalRandom.current().nextDouble(maxXp - minXp) + minXp);

        BigDecimal updatedExperience = experience.get();
        updatedExperience = updatedExperience.add(randomXp);
        experience.set(updatedExperience);

        Player player = Bukkit.getPlayer(uuid);

        if (player != null && player.isOnline() && randomXp.intValue() != 0) {
            String xpAdded = "&e+" + String.format("%.2f", randomXp.doubleValue()) + " XP ";
            String progress = "&8[&7" + String.format("%.2f", updatedExperience.doubleValue()) + " " + getProgressBar(updatedExperience.intValue(), getLevelUpXp()) + " &7" + getLevelUpXp() + "&8]";
            runTaskSync(() -> player.sendActionBar(Component.text(colorize(xpAdded + progress))));
        }

        if (experience.get().subtract(BigDecimal.valueOf(getLevelUpXp())).doubleValue() >= 0)
            levelUp(uuid);

        return randomXp;
    }

    private void levelUp(UUID uuid) {
        Player player = Bukkit.getPlayer(uuid);

        if (player == null || !player.isOnline())
            return;

        int oldLevel = level.get();
        experience.set(experience.get().subtract(BigDecimal.valueOf(getLevelUpXp())));
        level.set(getNewLevel(level.get() + 1));

        int totalLevelUp = level.get() - oldLevel;

        if (LuckyBlock.getLuckyBlock() != null) {
            for (int i = 0; i < totalLevelUp; i++) {
                if (!player.getInventory().addItem(LuckyBlock.getLuckyBlock()).isEmpty())
                    player.getWorld().dropItemNaturally(player.getLocation(), LuckyBlock.getLuckyBlock());
            }
        }

        Title title = Title.title(Component.text(colorize("&aYou have leveled up!")).asComponent(), Component.text(colorize("&b" + oldLevel + " &7-> &b" + this.level)).asComponent());
        runTaskSync(() -> {
            player.showTitle(title);
            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 10, 2);
        });

        UserManager.getUser(uuid).save();
    }

    public int getLevelUpXp() {
        if (level.get() >= 25)
            return 12500;
        else if (level.get() > 5)
            return level.get() * 500;

        return level.get() * 225;
    }

    public int getLevelUpXp(int level) {
        if (level >= 25)
            return 12500;
        else if (level > 5)
            return level * 500;

        return level * 225;
    }

    private int getNewLevel(int level) {
        int requiredExperienceToLevel = getLevelUpXp(level);

        if (experience.get().subtract(BigDecimal.valueOf(requiredExperienceToLevel)).doubleValue() >= 0) {
            experience.set(experience.get().subtract(BigDecimal.valueOf(requiredExperienceToLevel)));
            return getNewLevel(level + 1);
        }

        return level;
    }

    private @NotNull String getProgressBar(int current, int max) {
        if (current > max)
            current = max;

        float percent = (float) current / max;
        int progressBars = (int) (40 * percent);

        return Strings.repeat("" + ChatColor.GREEN + '|', progressBars)
                + Strings.repeat("" + ChatColor.RED + '|', 40 - progressBars);
    }

    public int getDeaths() {
        return deaths.get();
    }

    public void setDeaths(int deaths) {
        this.deaths.set(deaths);
        UserManager.getUser(uuid).save();
    }
}