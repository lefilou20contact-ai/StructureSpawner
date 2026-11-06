package fr.lefilou.structurespawner;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Ageable;
import org.bukkit.block.data.Bisected;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.type.Door;
import org.bukkit.block.data.type.Farmland;
import org.bukkit.block.data.type.Leaves;
import org.bukkit.block.data.type.Slab;
import org.bukkit.block.data.type.Stairs;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Plan de construction : positions relatives (x, y, z) -> bloc.
 * Origine au centre, y = 0 au niveau du sol, façade (porte) côté +z.
 */
public final class Blueprint {

    public record Pos(int x, int y, int z) {}

    private final Map<Pos, BlockData> blocks = new LinkedHashMap<>();
    private boolean foundation = true;

    public Map<Pos, BlockData> blocks() { return blocks; }
    public boolean hasFoundation() { return foundation; }
    public Blueprint noFoundation() { this.foundation = false; return this; }

    public static BlockData leaves(Material m) {
        BlockData d = m.createBlockData();
        if (d instanceof Leaves l) l.setPersistent(true);
        return d;
    }

    // ---------- poser des blocs ----------

    public Blueprint set(int x, int y, int z, BlockData d) {
        blocks.put(new Pos(x, y, z), d);
        return this;
    }

    public Blueprint set(int x, int y, int z, Material m) {
        return set(x, y, z, m.createBlockData());
    }

    public Blueprint set(int x, int y, int z, String data) {
        return set(x, y, z, Bukkit.createBlockData(data));
    }

    public Blueprint fill(int x1, int y1, int z1, int x2, int y2, int z2, BlockData d) {
        for (int x = Math.min(x1, x2); x <= Math.max(x1, x2); x++)
            for (int y = Math.min(y1, y2); y <= Math.max(y1, y2); y++)
                for (int z = Math.min(z1, z2); z <= Math.max(z1, z2); z++)
                    set(x, y, z, d);
        return this;
    }

    public Blueprint fill(int x1, int y1, int z1, int x2, int y2, int z2, Material m) {
        return fill(x1, y1, z1, x2, y2, z2, m.createBlockData());
    }

    /** Boîte creuse : coque en m, intérieur en air (x1<x2, y1<y2, z1<z2, taille >= 3). */
    public Blueprint box(int x1, int y1, int z1, int x2, int y2, int z2, Material m) {
        fill(x1, y1, z1, x2, y2, z2, m);
        fill(x1 + 1, y1 + 1, z1 + 1, x2 - 1, y2 - 1, z2 - 1, Material.AIR);
        return this;
    }

    // ---------- formes rondes ----------

    private static boolean in(int dx, int dz, int r) {
        return dx * dx + dz * dz <= r * r + r;
    }

    private static boolean core(int dx, int dz, int r) {
        return r >= 1 && dx * dx + dz * dz <= (r - 1) * (r - 1) + (r - 1);
    }

    /** Cylindre plein. */
    public Blueprint cylinder(int cx, int cz, int r, int y1, int y2, Material m) {
        BlockData d = m.createBlockData();
        for (int y = y1; y <= y2; y++)
            for (int dx = -r; dx <= r; dx++)
                for (int dz = -r; dz <= r; dz++)
                    if (in(dx, dz, r)) set(cx + dx, y, cz + dz, d);
        return this;
    }

    /** Anneau (mur circulaire d'épaisseur 1), sans toucher à l'intérieur. */
    public Blueprint ring(int cx, int cz, int r, int y1, int y2, Material m) {
        BlockData d = m.createBlockData();
        for (int y = y1; y <= y2; y++)
            for (int dx = -r; dx <= r; dx++)
                for (int dz = -r; dz <= r; dz++)
                    if (in(dx, dz, r) && !core(dx, dz, r)) set(cx + dx, y, cz + dz, d);
        return this;
    }

    /** Tour creuse : mur en m, intérieur vidé. */
    public Blueprint tube(int cx, int cz, int r, int y1, int y2, Material m) {
        BlockData wall = m.createBlockData();
        BlockData air = Material.AIR.createBlockData();
        for (int y = y1; y <= y2; y++)
            for (int dx = -r; dx <= r; dx++)
                for (int dz = -r; dz <= r; dz++) {
                    if (!in(dx, dz, r)) continue;
                    set(cx + dx, y, cz + dz, core(dx, dz, r) ? air : wall);
                }
        return this;
    }

    /** Créneaux (damier) sur un anneau. */
    public Blueprint crenel(int cx, int cz, int r, int y, Material m) {
        BlockData d = m.createBlockData();
        for (int dx = -r; dx <= r; dx++)
            for (int dz = -r; dz <= r; dz++)
                if (in(dx, dz, r) && !core(dx, dz, r) && ((dx + dz) & 1) == 0) set(cx + dx, y, cz + dz, d);
        return this;
    }

    public Blueprint sphere(int cx, int cy, int cz, int r, BlockData d, boolean hollow, boolean upperOnly) {
        BlockData air = Material.AIR.createBlockData();
        int outer = r * r + r;
        int inner = (r - 1) * (r - 1) + (r - 1);
        for (int dx = -r; dx <= r; dx++)
            for (int dy = -r; dy <= r; dy++)
                for (int dz = -r; dz <= r; dz++) {
                    if (upperOnly && dy < 0) continue;
                    int s = dx * dx + dy * dy + dz * dz;
                    if (s > outer) continue;
                    set(cx + dx, cy + dy, cz + dz, (hollow && s <= inner) ? air : d);
                }
        return this;
    }

    // ---------- blocs spéciaux ----------

    public Blueprint stair(int x, int y, int z, Material m, BlockFace facing, boolean top) {
        Stairs s = (Stairs) m.createBlockData();
        s.setFacing(facing);
        if (top) s.setHalf(Bisected.Half.TOP);
        return set(x, y, z, s);
    }

    public Blueprint slab(int x, int y, int z, Material m, boolean top) {
        Slab s = (Slab) m.createBlockData();
        s.setType(top ? Slab.Type.TOP : Slab.Type.BOTTOM);
        return set(x, y, z, s);
    }

    public Blueprint door(int x, int y, int z, Material m, BlockFace facing) {
        Door lower = (Door) m.createBlockData();
        lower.setFacing(facing);
        lower.setHalf(Bisected.Half.BOTTOM);
        Door upper = (Door) lower.clone();
        upper.setHalf(Bisected.Half.TOP);
        set(x, y, z, lower);
        return set(x, y + 1, z, upper);
    }

    /** Bloc orientable (échelle, coffre, four, portillon...). */
    public Blueprint dir(int x, int y, int z, Material m, BlockFace facing) {
        BlockData d = m.createBlockData();
        ((Directional) d).setFacing(facing);
        return set(x, y, z, d);
    }

    public Blueprint crop(int x, int y, int z, Material m) {
        Ageable a = (Ageable) m.createBlockData();
        a.setAge(a.getMaximumAge());
        return set(x, y, z, a);
    }

    public Blueprint farmland(int x, int y, int z) {
        Farmland f = (Farmland) Material.FARMLAND.createBlockData();
        f.setMoisture(f.getMaximumMoisture());
        return set(x, y, z, f);
    }

    // ---------- infos ----------

    /** Largeur (x), hauteur (y), profondeur (z). */
    public int[] size() {
        if (blocks.isEmpty()) return new int[]{0, 0, 0};
        int x0 = Integer.MAX_VALUE, y0 = Integer.MAX_VALUE, z0 = Integer.MAX_VALUE;
        int x1 = Integer.MIN_VALUE, y1 = Integer.MIN_VALUE, z1 = Integer.MIN_VALUE;
        for (Pos p : blocks.keySet()) {
            x0 = Math.min(x0, p.x()); x1 = Math.max(x1, p.x());
            y0 = Math.min(y0, p.y()); y1 = Math.max(y1, p.y());
            z0 = Math.min(z0, p.z()); z1 = Math.max(z1, p.z());
        }
        return new int[]{x1 - x0 + 1, y1 - y0 + 1, z1 - z0 + 1};
    }

    public int count() {
        int n = 0;
        for (BlockData d : blocks.values()) if (!d.getMaterial().isAir()) n++;
        return n;
    }
}
