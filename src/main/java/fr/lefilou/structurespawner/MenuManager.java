package fr.lefilou.structurespawner;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public final class MenuManager implements Listener {

    private static final int PAGE_SIZE = 45;

    private static final class Holder implements InventoryHolder {
        final boolean confirm;
        final int page;
        final StructureDef def;
        final Placer.Target target;
        Inventory inv;

        Holder(boolean confirm, int page, StructureDef def, Placer.Target target) {
            this.confirm = confirm;
            this.page = page;
            this.def = def;
            this.target = target;
        }

        @Override
        public Inventory getInventory() {
            return inv;
        }
    }

    private final JavaPlugin plugin;
    private final Structures structures;
    private final Placer placer;

    public MenuManager(JavaPlugin plugin, Structures structures, Placer placer) {
        this.plugin = plugin;
        this.structures = structures;
        this.placer = placer;
    }

    private static String c(String s) {
        return ChatColor.translateAlternateColorCodes('&', s);
    }

    private void later(Runnable r) {
        Bukkit.getScheduler().runTask(plugin, r);
    }

    private static ItemStack item(Material m, String name, String... lore) {
        ItemStack it = new ItemStack(m);
        ItemMeta meta = it.getItemMeta();
        meta.setDisplayName(c(name));
        if (lore.length > 0) {
            List<String> l = new ArrayList<>();
            for (String s : lore) l.add(c(s));
            meta.setLore(l);
        }
        it.setItemMeta(meta);
        return it;
    }

    private ItemStack icon(StructureDef d, boolean clickHint) {
        ItemStack it = new ItemStack(d.icon());
        ItemMeta meta = it.getItemMeta();
        meta.setDisplayName(c("&a&l" + d.name()));
        List<String> lore = new ArrayList<>();
        lore.add(c("&8" + d.category()));
        lore.add("");
        for (String s : d.description()) lore.add(c("&7" + s));
        lore.add("");
        Blueprint bp = d.sample();
        int[] s = bp.size();
        if (d.regenerate()) lore.add(c("&bForme différente à chaque fois"));
        lore.add(c("&eTaille : &f" + s[0] + " × " + s[1] + " × " + s[2]));
        lore.add(c("&eBlocs : &f" + bp.count()));
        if (clickHint) {
            lore.add("");
            lore.add(c("&a▶ Clique pour choisir"));
        }
        meta.setLore(lore);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        it.setItemMeta(meta);
        return it;
    }

    // ---------- menus ----------

    public void openList(Player p, int page, Placer.Target target) {
        List<StructureDef> all = new ArrayList<>(structures.all());
        int pages = Math.max(1, (all.size() + PAGE_SIZE - 1) / PAGE_SIZE);
        int pg = Math.max(0, Math.min(page, pages - 1));

        Holder h = new Holder(false, pg, null, target);
        Inventory inv = Bukkit.createInventory(h, 54, c("&8Structures &7(" + (pg + 1) + "/" + pages + ")"));
        h.inv = inv;

        for (int i = 0; i < PAGE_SIZE; i++) {
            int idx = pg * PAGE_SIZE + i;
            if (idx >= all.size()) break;
            inv.setItem(i, icon(all.get(idx), true));
        }
        ItemStack pane = item(Material.GRAY_STAINED_GLASS_PANE, " ");
        for (int i = PAGE_SIZE; i < 54; i++) inv.setItem(i, pane);
        if (pg > 0) inv.setItem(45, item(Material.ARROW, "&e« Page précédente"));
        if (pg < pages - 1) inv.setItem(53, item(Material.ARROW, "&ePage suivante »"));
        inv.setItem(49, item(Material.BARRIER, "&cFermer"));
        p.openInventory(inv);
    }

    public void openConfirm(Player p, StructureDef def, Placer.Target target, int page) {
        Holder h = new Holder(true, page, def, target);
        Inventory inv = Bukkit.createInventory(h, 27, c("&8Confirmer la construction"));
        h.inv = inv;

        ItemStack pane = item(Material.GRAY_STAINED_GLASS_PANE, " ");
        for (int i = 0; i < 27; i++) inv.setItem(i, pane);

        Location l = target.base();
        inv.setItem(11, item(Material.LIME_CONCRETE, "&a&l✔ CONFIRMER",
                "&7Construit &f" + def.name(),
                "&7En &f" + l.getBlockX() + ", " + l.getBlockY() + ", " + l.getBlockZ(),
                "&7La façade est tournée vers toi.",
                "",
                "&7Tu pourras annuler avec &f/structure undo"));
        inv.setItem(13, icon(def, false));
        inv.setItem(15, item(Material.RED_CONCRETE, "&c&l✘ ANNULER", "&7Ne rien construire."));
        inv.setItem(22, item(Material.ARROW, "&e« Retour à la liste"));
        p.openInventory(inv);
    }

    // ---------- clics ----------

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (!(e.getInventory().getHolder() instanceof Holder h)) return;
        e.setCancelled(true);
        if (!(e.getWhoClicked() instanceof Player p)) return;
        if (e.getClickedInventory() != e.getInventory()) return;
        int slot = e.getSlot();

        if (!h.confirm) {
            if (slot < PAGE_SIZE) {
                List<StructureDef> all = new ArrayList<>(structures.all());
                int idx = h.page * PAGE_SIZE + slot;
                if (idx < all.size()) later(() -> openConfirm(p, all.get(idx), h.target, h.page));
            } else if (slot == 45 && e.getCurrentItem() != null && e.getCurrentItem().getType() == Material.ARROW) {
                later(() -> openList(p, h.page - 1, h.target));
            } else if (slot == 53 && e.getCurrentItem() != null && e.getCurrentItem().getType() == Material.ARROW) {
                later(() -> openList(p, h.page + 1, h.target));
            } else if (slot == 49) {
                later(p::closeInventory);
            }
        } else {
            if (slot == 11) {
                later(() -> {
                    p.closeInventory();
                    if (!p.hasPermission("structurespawner.use")) return;
                    placer.spawn(p, h.def, h.target);
                });
            } else if (slot == 15) {
                later(() -> {
                    p.closeInventory();
                    p.sendMessage(c("&7Construction annulée."));
                });
            } else if (slot == 22) {
                later(() -> openList(p, h.page, h.target));
            }
        }
    }

    @EventHandler
    public void onDrag(InventoryDragEvent e) {
        if (e.getInventory().getHolder() instanceof Holder) e.setCancelled(true);
    }
}
