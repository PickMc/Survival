package me.zxoir.smp.managers;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import me.zxoir.smp.SMP;
import me.zxoir.smp.customclasses.Team;
import me.zxoir.smp.customclasses.User;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * MIT License Copyright (c) 2023 Zxoir
 *
 * @author Zxoir
 * @since 7/20/2023
 */

public class TeamManager {
    private static final Cache<String, Team> cachedTeams = CacheBuilder.newBuilder().build();

    public static void registerTeam(@NotNull Team team) {
        SMP.getDataFile().getConfig().set("Team." + team.getName().toLowerCase() + ".Name", team.getName());
        SMP.getDataFile().getConfig().set("Team." + team.getName().toLowerCase() + ".Tag", team.getTag());
        SMP.getDataFile().getConfig().set("Team." + team.getName().toLowerCase() + ".Color", team.getColor());
        SMP.getDataFile().getConfig().set("Team." + team.getName().toLowerCase() + ".Leader", team.getLeader().toString());
        SMP.getDataFile().getConfig().set("Team." + team.getName().toLowerCase() + ".Members", team.getMembers());
        SMP.getDataFile().saveConfig();

        cachedTeams.put(team.getName().toLowerCase(), team);
    }

    public static void cacheMembers() {
        if (cachedTeams.size() <= 0)
            return;

        for (Team team : cachedTeams.asMap().values()) {
            User leader = UserManager.getUser(team.getLeader());
            leader.getCache().setTeamName(team.getName().toLowerCase());

            if (team.getMembers().isEmpty())
                continue;

            for (String uuidString : team.getMembers()) {
                User member = UserManager.getUser(UUID.fromString(uuidString));
                member.getCache().setTeamName(team.getName().toLowerCase());
            }
        }
    }

    public static Cache<String, Team> getCachedTeams() {
        return cachedTeams;
    }
}
