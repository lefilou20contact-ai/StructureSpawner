package fr.lefilou.structurespawner;

import org.bukkit.Axis;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.MultipleFacing;
import org.bukkit.block.data.Orientable;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public final class Placer {

    private static final BlockFace[] ORDER = {BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST};
    private static final int PER_TICK = 2500;

    /** Où construire (bloc visé + 1 au-dessus) et de combien de quarts de tour pivoter. */
    public record Target(Location base, int turns) {}

    private record Snap(World world, int x, int y, int z, BlockData data) {}

    private final JavaPlugin plugin;
    private final Map<UUID, List<Snap>> undo = new HashMap<>();
    private final Set<UUID> busy = new HashSet<>();

    public Placer(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    private static String c(String s) {
        return ChatColor.translateAlternateColorCodes('&', s);
    }

    /** Détermine l'emplacement visé par le joueur ; la façade de la structure te fait face. */
    public Target targetFor(Player p) {
        BlockFace facing = p.getFacing();
        int turns = Math.max(0, Arrays.asList(ORDER).indexOf(facing));
        Block hit = p.getTargetBlockExact(60);
        Location base;
        if (hit != null) {
            base = hit.getLocation().add(0, 1, 0);
        } else {
            Block ahead = p.getLocation().getBlock().getRelative(facing, 10);
            int y = ahead.getWorld().getHighestBlockYAt(ahead.getX(), ahead.getZ()) + 1;
            base = new Location(ahead.getWorld(), ahead.getX(), y, ahead.getZ());
        }
        return new Target(base, turns);
    }

    public void spawn(Player p, StructureDef def, Target t) {
        if (!busy.add(p.getUniqueId())) {
            p.sendMessage(c("&cUne construction est déjà en cours."));
            return;
        }
        Blueprint bp = def.blueprint();
        World w = t.base().getWorld();
        int bx = t.base().getBlockX();
        int by = t.base().getBlockY();
        int bz = t.base().getBlockZ();

        // 1. rotation + position dans le monde
        Map<Blueprint.Pos, BlockData> plan = new LinkedHashMap<>();
        int minY = Integer.MAX_VALUE;
        for (Blueprint.Pos l : bp.blocks().keySet()) minY = Math.min(minY, l.y());
        for (Map.Entry<Blueprint.Pos, BlockData> e : bp.blocks().entrySet()) {
            int x = e.getKey().x();
            int z = e.getKey().z();
            for (int i = 0; i < t.turns(); i++) {
                int nx = -z;
                z = x;
                x = nx;
            }
            plan.put(new Blueprint.Pos(bx + x, by + e.getKey().y(), bz + z),
                    rotate(e.getValue().clone(), t.turns()));
        }

        // 2. fondations + connexion des barrières / vitres
        if (bp.hasFoundation()) addFoundation(plan, w, by + minY);
        connect(plan);

        // 3. construction par lots (évite de faire lagger le serveur)
        List<Map.Entry<Blueprint.Pos, BlockData>> list = new ArrayList<>(plan.entrySet());
        list.sort(Comparator.comparingInt((Map.Entry<Blueprint.Pos, BlockData> e) -> e.getKey().y()));
        List<Snap> snaps = new ArrayList<>(list.size());
        p.sendMessage(c("&eConstruction de &f" + def.name() + " &e(" + list.size() + " blocs)..."));

        new BukkitRunnable() {
            int i = 0;

            @Override
            public void run() {
                int end = Math.min(i + PER_TICK, list.size());
                for (; i < end; i++) {
                    Map.Entry<Blueprint.Pos, BlockData> e = list.get(i);
                    Blueprint.Pos k = e.getKey();
                    if (k.y() < w.getMinHeight() || k.y() >= w.getMaxHeight()) continue;
                    Block b = w.getBlockAt(k.x(), k.y(), k.z());
                    snaps.add(new Snap(w, k.x(), k.y(), k.z(), b.getBlockData()));
                    b.setBlockData(e.getValue(), false);
                }
                if (i >= list.size()) {
                    cancel();
                    undo.put(p.getUniqueId(), snaps);
                    busy.remove(p.getUniqueId());
                    if (p.isOnline()) {
                        p.sendMessage(c("&a✔ Terminé ! &7Fais &f/structure undo &7pour annuler."));
                        p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1f, 1f);
                    }
                }
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }

    public boolean undo(Player p) {
        List<Snap> snaps = undo.get(p.getUniqueId());
        if (snaps == null || snaps.isEmpty()) return false;
        if (!busy.add(p.getUniqueId())) return false;
        undo.remove(p.getUniqueId());
        List<Snap> rev = new ArrayList<>(snaps);
        Collections.reverse(rev);

        new BukkitRunnable() {
            int i = 0;

            @Override
            public void run() {
                int end = Math.min(i + PER_TICK, rev.size());
                for (; i < end; i++) {
                    Snap s = rev.get(i);
                    s.world().getBlockAt(s.x(), s.y(), s.z()).setBlockData(s.data(), false);
                }
                if (i >= rev.size()) {
                    cancel();
                    busy.remove(p.getUniqueId());
                    if (p.isOnline()) p.sendMessage(c("&a✔ Construction annulée."));
                }
            }
        }.runTaskTimer(plugin, 0L, 1L);
        return true;
    }

    // ---------- outils ----------

    /** Prolonge vers le bas (max 8 blocs) la première couche pour que rien ne flotte sur une pente. */
    private void addFoundation(Map<Blueprint.Pos, BlockData> plan, World w, int lowY) {
        List<Map.Entry<Blueprint.Pos, BlockData>> low = new ArrayList<>();
        for (Map.Entry<Blueprint.Pos, BlockData> e : plan.entrySet()) {
            if (e.getKey().y() == lowY && !e.getValue().getMaterial().isAir()) low.add(e);
        }
        for (Map.Entry<Blueprint.Pos, BlockData> e : low) {
            Material m = e.getValue().getMaterial();
            BlockData fill = (m.isSolid() && m.isOccluding() ? m : Material.COBBLESTONE).createBlockData();
            Blueprint.Pos k = e.getKey();
            for (int d = 1; d <= 8; d++) {
                Blueprint.Pos below = new Blueprint.Pos(k.x(), k.y() - d, k.z());
                if (plan.containsKey(below)) break;
                if (w.getBlockAt(below.x(), below.y(), below.z()).getType().isSolid()) break;
                plan.put(below, fill);
            }
        }
    }

    /** Connecte les barrières, vitres et barreaux entre eux (la pose sans physique ne le fait pas). */
    private void connect(Map<Blueprint.Pos, BlockData> plan) {
        for (Map.Entry<Blueprint.Pos, BlockData> e : plan.entrySet()) {
            if (!(e.getValue() instanceof MultipleFacing mf)) continue;
            Blueprint.Pos k = e.getKey();
            for (BlockFace face : mf.getAllowedFaces()) {
                if (face.getModY() != 0) continue;
                BlockData n = plan.get(new Blueprint.Pos(k.x() + face.getModX(), k.y(), k.z() + face.getModZ()));
                boolean link = n != null && (n.getMaterial() == mf.getMaterial()
                        || (n.getMaterial().isSolid() && n.getMaterial().isOccluding()));
                mf.setFace(face, link);
            }
        }
    }

    private BlockData rotate(BlockData d, int turns) {
        if (turns == 0) return d;
        if (d instanceof Directional dir) {
            int idx = Arrays.asList(ORDER).indexOf(dir.getFacing());
            if (idx >= 0) {
                BlockFace nf = ORDER[(idx + turns) % 4];
                if (dir.getFaces().contains(nf)) dir.setFacing(nf);
            }
        }
        if (d instanceof Orientable o && turns % 2 == 1) {
            if (o.getAxis() == Axis.X) o.setAxis(Axis.Z);
            else if (o.getAxis() == Axis.Z) o.setAxis(Axis.X);
        }
        return d;
    }
}
