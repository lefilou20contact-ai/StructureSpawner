package fr.lefilou.structurespawner;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public final class StructureCommand implements CommandExecutor, TabCompleter {

    private final Structures structures;
    private final Placer placer;
    private final MenuManager menus;

    public StructureCommand(Structures structures, Placer placer, MenuManager menus) {
        this.structures = structures;
        this.placer = placer;
        this.menus = menus;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player p)) {
            sender.sendMessage("Cette commande est réservée aux joueurs.");
            return true;
        }
        if (!p.hasPermission("structurespawner.use")) {
            p.sendMessage(ChatColor.RED + "Tu n'as pas la permission d'utiliser cette commande.");
            return true;
        }
        if (args.length == 0) {
            menus.openList(p, 0, placer.targetFor(p));
            return true;
        }
        switch (args[0].toLowerCase()) {
            case "list" -> {
                p.sendMessage(ChatColor.GOLD + "Structures disponibles (" + structures.all().size() + ") :");
                for (StructureDef d : structures.all()) {
                    p.sendMessage(ChatColor.YELLOW + " - " + d.id() + ChatColor.GRAY + " : " + d.name());
                }
            }
            case "spawn" -> {
                if (args.length < 2) {
                    p.sendMessage(ChatColor.RED + "Usage : /structure spawn <id>");
                    return true;
                }
                StructureDef def = structures.get(args[1]);
                if (def == null) {
                    p.sendMessage(ChatColor.RED + "Structure inconnue. Fais /structure list.");
                    return true;
                }
                menus.openConfirm(p, def, placer.targetFor(p), 0);
            }
            case "undo" -> {
                if (placer.undo(p)) {
                    p.sendMessage(ChatColor.GREEN + "Annulation de ta dernière construction en cours...");
                } else {
                    p.sendMessage(ChatColor.RED + "Rien à annuler (ou une construction est en cours).");
                }
            }
            default -> {
                p.sendMessage(ChatColor.GOLD + "/structure" + ChatColor.GRAY + " : ouvre le menu");
                p.sendMessage(ChatColor.GOLD + "/structure spawn <id>" + ChatColor.GRAY + " : confirmation directe");
                p.sendMessage(ChatColor.GOLD + "/structure list" + ChatColor.GRAY + " : liste des structures");
                p.sendMessage(ChatColor.GOLD + "/structure undo" + ChatColor.GRAY + " : annule la dernière construction");
            }
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args) {
        if (args.length == 1) return filter(List.of("list", "spawn", "undo"), args[0]);
        if (args.length == 2 && args[0].equalsIgnoreCase("spawn")) {
            return filter(new ArrayList<>(structures.ids()), args[1]);
        }
        return List.of();
    }

    private static List<String> filter(List<String> src, String prefix) {
        String p = prefix.toLowerCase();
        List<String> out = new ArrayList<>();
        for (String s : src) if (s.startsWith(p)) out.add(s);
        return out;
    }
}
