package me.zxoir.smp.commands;

import me.zxoir.smp.SMP;
import me.zxoir.smp.customclasses.Team;
import me.zxoir.smp.customclasses.User;
import me.zxoir.smp.managers.TeamManager;
import me.zxoir.smp.managers.UserManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.*;

import static me.zxoir.smp.utilities.Colors.colorize;

/**
 * MIT License Copyright (c) 2023 Zxoir
 *
 * @author Zxoir
 * @since 7/20/2023
 */
public class TeamCommand implements CommandExecutor {
    int MAX_TEAM_MEMBERS = 3;
    HashMap<UUID, List<String>> teamRequest = new HashMap<>();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] args) {

        if (!(sender instanceof Player player))
            return true;

        if (args.length == 1) {

            if (args[0].equalsIgnoreCase("info")) {
                User user = UserManager.getUser(player.getUniqueId());

                if (user.getCache().getTeamName() == null || user.getCache().getTeamName().isEmpty()) {
                    player.sendMessage("You are not in a Team!");
                    return true;
                }

                Team team = TeamManager.getCachedTeams().getIfPresent(user.getCache().getTeamName());
                if (team == null) {
                    player.sendMessage("This team can not be found?!");
                    return true;
                }
                List<String> members = team.getMembers();
                StringBuilder memberList = new StringBuilder();

                memberList.append(ChatColor.GOLD).append("Team Name:").append(ChatColor.WHITE).append(team.getName());
                OfflinePlayer leader = Bukkit.getOfflinePlayer(team.getLeader());
                ChatColor statusLeaderColor = (leader.isOnline()) ? ChatColor.GREEN : ChatColor.RED;
                memberList.append(ChatColor.GOLD).append("Team Leader: ").append(statusLeaderColor).append(leader.getName());

                for (String uuid : members) {
                    OfflinePlayer member = Bukkit.getOfflinePlayer(UUID.fromString(uuid));
                    ChatColor statusColor = (member.isOnline()) ? ChatColor.GREEN : ChatColor.RED;
                    memberList.append(statusColor).append(member.getName()).append(ChatColor.WHITE).append(", ");
                }

                if (memberList.length() > 0) {
                    // Remove the trailing comma and space
                    memberList.setLength(memberList.length() - 2);
                    player.sendMessage(ChatColor.GOLD + "Members: " + memberList);
                } else {
                    player.sendMessage(ChatColor.GOLD + "No members in the team.");
                }
            }

            if (args[0].equalsIgnoreCase("color")) {
                User user = UserManager.getUser(player.getUniqueId());

                if (user.getCache().getTeamName() == null || user.getCache().getTeamName().isEmpty()) {
                    player.sendMessage("You are not in a Team!");
                    return true;
                }

                Team team = TeamManager.getCachedTeams().getIfPresent(user.getCache().getTeamName());
                if (team == null) {
                    player.sendMessage("This team can not be found?!");
                    return true;
                }

                if (!team.getLeader().equals(player.getUniqueId())) {
                    player.sendMessage("You must be the Team Leader to change the team color");
                    return true;
                }

                Random random = new Random();
                String hexColor = String.format("#%06x", random.nextInt(0xffffff + 1));
                team.setColor(hexColor);
                player.sendMessage(colorize(hexColor + "This is your new Team Color"));
            }

        } else if (args.length == 2) {

            if (args[0].equalsIgnoreCase("create")) {
                String teamName = args[1];

                if (SMP.getDataFile().getConfig().isConfigurationSection("Team." + teamName.toLowerCase())) {
                    player.sendMessage("There is already a team with that name!");
                    return true;
                }

                User user = UserManager.getUser(player.getUniqueId());
                player.sendMessage(String.valueOf(user.getCache().getTeamName() != null));
                if (user.getCache().getTeamName() != null) {
                    player.sendMessage(String.valueOf(!user.getCache().getTeamName().isEmpty()));
                    player.sendMessage(String.valueOf(!user.getCache().getTeamName().isBlank()));
                }
                if (user.getCache().getTeamName() != null && !user.getCache().getTeamName().isEmpty()) {
                    player.sendMessage("You are already in a team!");
                    return true;
                }

                user.getCache().setTeamName(teamName.toLowerCase());

                Team team = new Team(teamName, player.getUniqueId());
                TeamManager.registerTeam(team);
                player.sendMessage("Team " + team.getName() + " has been created!");
            }

            if (args[0].equalsIgnoreCase("invite")) {
                User user = UserManager.getUser(player.getUniqueId());

                if (user.getCache().getTeamName() == null || user.getCache().getTeamName().isEmpty()) {
                    player.sendMessage("You are not in a Team!");
                    return true;
                }

                Team team = TeamManager.getCachedTeams().getIfPresent(user.getCache().getTeamName());
                if (team == null) {
                    player.sendMessage("This team can not be found?!");
                    return true;
                }

                if (!team.getLeader().equals(player.getUniqueId())) {
                    player.sendMessage("You must be the Team Leader to invite other players!");
                    return true;
                }

                if (team.getMembers().size() >= MAX_TEAM_MEMBERS) {
                    player.sendMessage("This team has already reached the maximum member size!");
                    return true;
                }

                Player target = Bukkit.getPlayer(args[1]);
                if (target == null) {
                    player.sendMessage("This player is offline");
                    return true;
                }

                User targetUser = UserManager.getUser(target.getUniqueId());
                if (targetUser.getCache().getTeamName() != null && !targetUser.getCache().getTeamName().isEmpty()) {
                    player.sendMessage("That player is already in a team!");
                    return true;
                }

                List<String> requests = new ArrayList<>();

                if (teamRequest.containsKey(target.getUniqueId()))
                    requests = teamRequest.get(target.getUniqueId());

                requests.add(team.getName().toLowerCase());
                teamRequest.put(target.getUniqueId(), requests);

                Component component = Component.text(colorize("&eTeam request received from &9" + team.getName() + " "));
                Component accept = Component.text(colorize("&a&l[ACCEPT] ")).clickEvent(ClickEvent.runCommand("/team accept " + team.getName())).hoverEvent(HoverEvent.showText(Component.text(colorize("&aClick to accept"))));
                Component deny = Component.text(colorize("&c&l[DENY] ")).clickEvent(ClickEvent.runCommand("/team deny " + team.getName())).hoverEvent(HoverEvent.showText(Component.text(colorize("&cClick to decline"))));
                component = component.append(accept).append(deny);
                target.sendMessage(component);

                player.sendMessage("You have invited " + target.getName() + " to join your Team");

                Bukkit.getScheduler().runTaskLater(SMP.getInstance(), () -> {

                    if (!teamRequest.containsKey(target.getUniqueId()))
                        return;

                    List<String> requestsLater = teamRequest.get(target.getUniqueId());

                    if (requestsLater.contains(team.getName().toLowerCase())) {
                        requestsLater.remove(team.getName().toLowerCase());
                        if (requestsLater.isEmpty())
                            teamRequest.remove(target.getUniqueId());
                        else
                            teamRequest.put(target.getUniqueId(), requestsLater);

                        if (player.isOnline())
                            player.sendMessage(target.getName() + "'s invitation to join your team has expired.");
                        if (target.isOnline())
                            target.sendMessage("Your team invitation from " + team.getName() + " has expired");
                    }
                }, 1200);
            }

            if (args[0].equalsIgnoreCase("tag")) {
                User user = UserManager.getUser(player.getUniqueId());

                if (user.getCache().getTeamName() == null || user.getCache().getTeamName().isEmpty()) {
                    player.sendMessage("You are not in a Team!");
                    return true;
                }

                Team team = TeamManager.getCachedTeams().getIfPresent(user.getCache().getTeamName());
                if (team == null) {
                    player.sendMessage("This team can not be found?!");
                    return true;
                }

                if (!team.getLeader().equals(player.getUniqueId())) {
                    player.sendMessage("You must be the Team Leader to change the team color");
                    return true;
                }

                if (!isValidTag(args[1])) {
                    player.sendMessage("Make sure the tag is up to 4 letters long and without symbols");
                    return true;
                }

                team.setTag(args[1]);
                player.sendMessage("Your team tag has been set! You should be able to see it in chat now :)");
            }

            if (args[0].equalsIgnoreCase("color")) {
                User user = UserManager.getUser(player.getUniqueId());

                if (user.getCache().getTeamName() == null || user.getCache().getTeamName().isEmpty()) {
                    player.sendMessage("You are not in a Team!");
                    return true;
                }

                Team team = TeamManager.getCachedTeams().getIfPresent(user.getCache().getTeamName());
                if (team == null) {
                    player.sendMessage("This team can not be found?!");
                    return true;
                }

                if (!team.getLeader().equals(player.getUniqueId())) {
                    player.sendMessage("You must be the Team Leader to change the team color");
                    return true;
                }

                String input = args[1];

                if (!isValidHexColor(input) && !isValidColorCode(input)) {
                    player.sendMessage("You must enter a valid hex color/color code!");
                    return true;
                }

                team.setColor(input);
                player.sendMessage(colorize(input + "Your new team color has been set."));
            }

            if (args[0].equalsIgnoreCase("accept")) {
                String teamName = args[1].toLowerCase();

                if (!teamRequest.containsKey(player.getUniqueId())) {
                    player.sendMessage("You don't have any team requests!");
                    return true;
                }

                List<String> requests = teamRequest.get(player.getUniqueId());
                if (requests.isEmpty()) {
                    teamRequest.remove(player.getUniqueId());
                    player.sendMessage("You don't have any team requests!");
                    return true;
                }

                if (!requests.contains(teamName)) {
                    player.sendMessage("You don't have a invite from this Team!");
                    return true;
                }

                Team team = TeamManager.getCachedTeams().getIfPresent(teamName);
                if (team == null) {
                    player.sendMessage("Team not found?!");
                    return true;
                }

                if (team.getMembers().size() >= MAX_TEAM_MEMBERS) {
                    player.sendMessage("This team has already reached the maximum member size!");
                    return true;
                }

                User user = UserManager.getUser(player.getUniqueId());
                if (user.getCache().getTeamName() != null && !user.getCache().getTeamName().isEmpty()) {
                    player.sendMessage("You are already in a team!");
                    return true;
                }

                requests.remove(team.getName().toLowerCase());
                if (requests.isEmpty())
                    teamRequest.remove(player.getUniqueId());
                else
                    teamRequest.put(player.getUniqueId(), requests);

                team.addMember(player.getUniqueId());
            }

            if (args[0].equalsIgnoreCase("deny")) {
                String teamName = args[1].toLowerCase();

                if (!teamRequest.containsKey(player.getUniqueId())) {
                    player.sendMessage("You don't have any team requests!");
                    return true;
                }

                List<String> requests = teamRequest.get(player.getUniqueId());
                if (requests.isEmpty()) {
                    teamRequest.remove(player.getUniqueId());
                    player.sendMessage("You don't have any team requests!");
                    return true;
                }

                if (!requests.contains(teamName)) {
                    player.sendMessage("You don't have a invite from this Team!");
                    return true;
                }

                requests.remove(teamName);
                if (requests.isEmpty())
                    teamRequest.remove(player.getUniqueId());
                else
                    teamRequest.put(player.getUniqueId(), requests);

                player.sendMessage("You have declined the Team request from " + teamName);
            }

            if (args[0].equalsIgnoreCase("disband") && args[1].equalsIgnoreCase("confirm")) {
                User user = UserManager.getUser(player.getUniqueId());

                if (user.getCache().getTeamName() == null || user.getCache().getTeamName().isEmpty()) {
                    player.sendMessage("You are not in a Team!");
                    return true;
                }

                Team team = TeamManager.getCachedTeams().getIfPresent(user.getCache().getTeamName());
                if (team == null) {
                    player.sendMessage("This team can not be found?!");
                    return true;
                }

                if (!team.getLeader().equals(player.getUniqueId())) {
                    player.sendMessage("You must be the Team Leader to disband the Team!");
                    return true;
                }

                team.disband();
                player.sendMessage("Team disbanded");
            }

            if (args[0].equalsIgnoreCase("leave") && args[1].equalsIgnoreCase("confirm")) {
                User user = UserManager.getUser(player.getUniqueId());

                if (user.getCache().getTeamName() == null || user.getCache().getTeamName().isEmpty()) {
                    player.sendMessage("You are not in a Team!");
                    return true;
                }

                Team team = TeamManager.getCachedTeams().getIfPresent(user.getCache().getTeamName());
                if (team == null) {
                    player.sendMessage("This team can not be found?!");
                    return true;
                }

                if (team.getLeader().equals(player.getUniqueId())) {
                    player.sendMessage("You must disband the team");
                    return true;
                }

                team.removeMember(player.getUniqueId());
                player.sendMessage("You have left the team");
            }

        } else
            sender.sendMessage("""
                    -/Team Create [Name]

                    -/Team Invite [Player Name]

                    -/Team Accept [Team Name]

                    -/Team deny [Team Name]

                    -/Team Tag [2-5Letters] ← It’s Just A Suggest :P

                    -/Team Color ← Random Color

                    -/Team Color [HEX]

                    -/Team Challange [Other Team] ← TP The teams to an Area with their items :P

                    -/Team Disband

                    -/Team Leave

                    -/Team Info
                                        
                    -/Team""");

        return true;
    }

    @Contract(pure = true)
    private boolean isValidHexColor(@NotNull String input) {
        if (!input.startsWith("#"))
            return false;

        return input.matches("^#?([0-9a-fA-F]{3}|[0-9a-fA-F]{6})$");
    }

    private boolean isValidTag(@NotNull String input) {
        return input.matches("^[a-zA-Z]{2,4}$");
    }

    @Contract(pure = true)
    private boolean isValidColorCode(@NotNull String colorCode) {
        return colorCode.matches("^&[0-9a-fA-Fk-oK-OrR]$");
    }
}