package me.zxoir.smp.utilities;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.zxoir.smp.customclasses.Team;
import me.zxoir.smp.customclasses.User;
import me.zxoir.smp.managers.TeamManager;
import me.zxoir.smp.managers.UserManager;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;

import static me.zxoir.smp.utilities.Colors.colorize;

public class CustomPlaceholders extends PlaceholderExpansion {

    @Override
    public @NotNull String getIdentifier() {
        return "smp";
    }

    @Override
    public @NotNull String getAuthor() {
        return "Zxoir";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0.0";
    }

    @Override
    public String onPlaceholderRequest(Player player, @NotNull String params) {
        if (params.equalsIgnoreCase("name")) {
            return player == null ? null : player.getName(); // "name" requires the player to be valid
        }

        if (params.contains("_")) {
            String[] param = params.split("_");

            if (param.length < 2)
                return null;

            if (param[0].equalsIgnoreCase("team")) {
                User user = UserManager.getUsers().get(player.getUniqueId());

                if (user == null)
                    return null;

                if (param[1].equalsIgnoreCase("tag")) {
                    String teamName = user.getCache().getTeamName();

                    if (teamName == null)
                        return "";

                    Team team = TeamManager.getCachedTeams().getIfPresent(teamName);

                    if (teamName.isBlank() || team == null)
                        return "";

                    if (team.getTag() == null || team.getTag().isBlank())
                        return "";

                    return colorize("&8[ " + team.getColor() + team.getTag() + " &8] &r");
                }
            }

            if (param[0].equalsIgnoreCase("stats")) {
                User user = UserManager.getUsers().get(player.getUniqueId());

                if (user == null)
                    return null;

                if (param[1].equalsIgnoreCase("deaths") || param[1].equalsIgnoreCase("death"))
                    return String.valueOf(user.getStats().getDeaths());

                if (param[1].equalsIgnoreCase("playtime"))
                    return TimeManager.formatTimeShort(UserManager.getTotalPlaytime(user).getSeconds());

                if (param[1].equalsIgnoreCase("level"))
                    return String.valueOf(user.getStats().getLevel());

                if (param[1].equalsIgnoreCase("experience"))
                    return String.valueOf(user.getStats().getExperience());

                if (param[1].equalsIgnoreCase("levelupxp"))
                    return String.valueOf(user.getStats().getLevelUpXp());

                if (param[1].equalsIgnoreCase("exptolevel"))
                    return String.valueOf(new BigDecimal(user.getStats().getLevelUpXp()).subtract(user.getStats().getExperience().get()));

                if (param[1].equalsIgnoreCase("afk"))
                    return user.getCache().isAfk() ? "true" : "false";

            }

        }


        return null; // Placeholder is unknown by the Expansion
    }
}
