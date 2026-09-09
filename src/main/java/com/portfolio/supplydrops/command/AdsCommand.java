package com.portfolio.supplydrops.command;

import com.portfolio.supplydrops.SupplyDropsPlugin;
import com.portfolio.supplydrops.config.MessageManager;
import com.portfolio.supplydrops.gui.CreateDropGui;
import com.portfolio.supplydrops.model.SupplyDrop;
import com.portfolio.supplydrops.service.DropService;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public final class AdsCommand implements CommandExecutor {

    private final SupplyDropsPlugin plugin;
    private final DropService dropService;
    private final MessageManager messageManager;

    public AdsCommand(SupplyDropsPlugin plugin, DropService dropService, MessageManager messageManager) {
        this.plugin = plugin;
        this.dropService = dropService;
        this.messageManager = messageManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Players only.");
            return true;
        }
        if (!player.hasPermission("supplydrops.admin")) {
            messageManager.send(player, "no-permission");
            return true;
        }
        if (args.length == 0 || args[0].equalsIgnoreCase("create")) {
            new CreateDropGui(plugin, dropService, player).open();
            return true;
        }
        String sub = args[0].toLowerCase();
        switch (sub) {
            case "list" -> {
                for (SupplyDrop d : dropService.getAllDrops()) {
                    player.sendMessage(d.getId() + " - " + d.getState());
                }
            }
            case "info" -> {
                if (args.length < 2) return true;
                SupplyDrop d = dropService.getDrop(args[1]);
                if (d != null) {
                    player.sendMessage(d.getId() + " " + d.getState() + " " + d.getLocation());
                }
            }
            case "cancel" -> {
                if (args.length < 2) return true;
                dropService.cancelDrop(args[1]);
            }
            case "remove" -> {
                if (args.length < 2) return true;
                dropService.removeDrop(args[1]);
            }
            case "reload" -> {
                plugin.getConfigManager().reload();
                messageManager.reload();
                messageManager.send(player, "reload-success");
            }
            default -> player.sendMessage("Unknown.");
        }
        return true;
    }
}
