package io.github.sawors.simplesit;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Axis;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandException;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class SitCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(args.length == 0){
            if(sender instanceof Player p){
                SittingManager.sitPlayer(p);
            } else {
                sender.sendMessage(Component.text("You must be a player to use this command without any arguments !\nIf you want to sit a specific player please use /sit <playername>.").color(NamedTextColor.YELLOW));
            }
        } else if(sender.hasPermission(SimpleSit.FORCE_SIT_PERMISSION)) {
            for(String playerName : args){
                Player p = Bukkit.getPlayer(playerName);
                if(p != null && p.isOnline()){
                    SittingManager.sitPlayer(p);
                }
            }
        } else {
            sender.sendMessage(Component.text("You do not have the permission to forcefully sit other players !").color(NamedTextColor.RED));
        }
        return false;
    }
}
