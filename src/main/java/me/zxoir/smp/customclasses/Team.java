package me.zxoir.smp.customclasses;

import lombok.Getter;
import me.zxoir.smp.SMP;
import me.zxoir.smp.managers.TeamManager;
import me.zxoir.smp.managers.UserManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * MIT License Copyright (c) 2023 Zxoir
 *
 * @author Zxoir
 * @since 7/20/2023
 */

@Getter
public class Team {
    String name;
    String tag;
    String color;
    UUID leader;
    List<String> members;

    public Team(String name, String tag, String color, UUID leader, List<String> members) {
        this.name = name;
        this.tag = tag;
        this.color = color;
        this.leader = leader;
        this.members = members;
    }

    public Team(@NotNull String name, UUID leader) {
        this.name = name;
        this.leader = leader;
        this.color = "&f";

        members = new ArrayList<>();

        User user = UserManager.getUser(leader);
        user.getCache().setTeamName(name.toLowerCase());
    }

    public void addMember(UUID uuid) {
        if (members.size() >= 3)
            return;

        User user = UserManager.getUser(uuid);
        user.getCache().setTeamName(name);

        members.add(uuid.toString());
        saveMembers();

        if (user.getPlayer() != null)
            user.getPlayer().sendMessage("You have joined Team " + name);

        Player leader = Bukkit.getPlayer(getLeader());
        if (leader != null)
            leader.sendMessage("Team >> " + user.getOfflinePlayer().getName() + " has joined the Team!");

        for (String member : members) {
            Player player = Bukkit.getPlayer(UUID.fromString(member));
            if (player == null || player.getUniqueId().equals(uuid))
                continue;

            player.sendMessage("Team >> " + user.getOfflinePlayer().getName() + " has joined the Team!");
        }
    }

    public void removeMember(UUID uuid) {
        User user = UserManager.getUser(uuid);
        user.getCache().setTeamName(null);

        members.remove(uuid.toString());
        saveMembers();

        if (members.isEmpty())
            return;

        Player leader = Bukkit.getPlayer(getLeader());
        if (leader != null)
            leader.sendMessage("Team >> " + user.getOfflinePlayer().getName() + " has left the Team!");

        for (String member : members) {
            Player player = Bukkit.getPlayer(UUID.fromString(member));
            if (player == null)
                continue;

            player.sendMessage("Team >> " + user.getOfflinePlayer().getName() + " has left the Team!");
        }
    }

    public void setTag(String tag) {
        this.tag = tag;

        SMP.getDataFile().getConfig().set("Team." + name.toLowerCase() + ".Tag", tag);
        SMP.getDataFile().saveConfig();
    }

    public void setColor(String color) {
        this.color = color;

        SMP.getDataFile().getConfig().set("Team." + name.toLowerCase() + ".Color", color);
        SMP.getDataFile().saveConfig();
    }

    public void disband() {
        TeamManager.getCachedTeams().asMap().remove(name.toLowerCase());
        SMP.getDataFile().getConfig().set("Team." + name.toLowerCase(), null);
        SMP.getDataFile().saveConfig();

        User user = UserManager.getUser(leader);
        user.getCache().setTeamName(null);

        if (members.isEmpty())
            return;

        for (String uuid : members) {
            User member = UserManager.getUser(UUID.fromString(uuid));
            member.getCache().setTeamName(null);

            if (member.getPlayer() != null)
                member.getPlayer().sendMessage("Team " + name + " has been disbanded!");
        }
    }

    private void saveMembers() {
        SMP.getDataFile().getConfig().set("Team." + name.toLowerCase() + ".Members", members);
        SMP.getDataFile().saveConfig();
    }
}
