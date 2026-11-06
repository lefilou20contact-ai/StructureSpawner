package fr.lefilou.structurespawner;

import org.bukkit.Material;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.function.Supplier;

import static org.bukkit.Material.*;
import static org.bukkit.block.BlockFace.*;

/** Catalogue des structures. Toutes sont générées par code : aucun fichier à fournir. */
public final class Structures {

    private final Map<String, StructureDef> map = new LinkedHashMap<>();

    public Collection<StructureDef> all() { return map.values(); }
    public Set<String> ids() { return map.keySet(); }
    public StructureDef get(String id) { return map.get(id.toLowerCase()); }

    private void add(String id, String name, String cat, Material icon, Supplier<Blueprint> b, String... desc) {
        map.put(id, new StructureDef(id, name, cat, icon, false, b, desc));
    }

    private void addRandom(String id, String name, String cat, Material icon, Supplier<Blueprint> b, String... desc) {
        map.put(id, new StructureDef(id, name, cat, icon, true, b, desc));
    }

    public Structures() {
        // ----- Habitations -----
        add("maison_bois", "Maison en bois", "Habitations", OAK_DOOR, Structures::maisonBois,
                "Petite maison cosy en chêne :", "porte, fenêtres, toit en pente,", "table de craft, four et coffre.");
        add("villa", "Villa moderne", "Habitations", QUARTZ_BLOCK, Structures::villa,
                "Grande villa de 2 étages en béton", "blanc, baies vitrées, escalier", "intérieur et toit plat noir.");
        add("cabane_arbre", "Cabane dans les arbres", "Habitations", OAK_LOG, Structures::cabaneArbre,
                "Immense arbre avec plateforme,", "cabane en haut et échelle.", "Parfait pour une base cachée.");

        // ----- Médiéval -----
        add("chateau", "Château fort", "Médiéval", STONE_BRICKS, Structures::chateau,
                "Remparts, 4 tours à toit pointu,", "porte avec herse, donjon central", "avec échelle et drapeau rouge.");
        add("tour_guet", "Tour de guet", "Médiéval", SPYGLASS, Structures::tourGuet,
                "Tour de pierre de 17 blocs avec", "échelle intérieure et plateforme", "à créneaux tout en haut.");
        add("tour_sorcier", "Tour de sorcier", "Médiéval", ENCHANTING_TABLE, Structures::tourSorcier,
                "Tour violette de 3 étages, salle", "d'enchantement au rez-de-chaussée,", "toit en pointe d'améthyste.");
        add("arene", "Arène de combat", "Médiéval", IRON_SWORD, Structures::arene,
                "Colisée circulaire avec gradins,", "grand mur à créneaux et entrée.", "Idéal pour les duels entre potes.");

        // ----- Monuments -----
        add("tour_eiffel", "Tour Eiffel", "Monuments", IRON_BARS, Structures::eiffel,
                "Version stylisée de 66 blocs de haut,", "4 pieds, 2 plateformes et antenne.", "Pour ton projet Paris !");
        add("pyramide", "Pyramide", "Monuments", SANDSTONE, Structures::pyramide,
                "Pyramide de 31 blocs de large avec", "couloir secret, chambre au trésor", "et sommet en or.");
        add("temple_grec", "Temple grec", "Monuments", QUARTZ_PILLAR, Structures::temple,
                "Temple en quartz avec colonnes,", "toit à fronton et autel", "avec un feu sacré.");
        add("phare", "Phare", "Monuments", LANTERN, Structures::phare,
                "Phare rouge et blanc de 24 blocs,", "échelle intérieure, lanterne", "en haut et paratonnerre.");
        add("fontaine", "Fontaine", "Monuments", WATER_BUCKET, Structures::fontaine,
                "Fontaine à 2 bassins avec", "vraie eau, pilier central et", "lumière au sommet.");

        // ----- Fun / YouTube -----
        add("creeper", "Statue de Creeper", "Fun", CREEPER_HEAD, Structures::creeper,
                "Creeper géant de 26 blocs de haut", "qui te regarde droit dans les yeux.", "Parfait pour les vidéos !");
        add("fusee", "Fusée", "Fun", FIREWORK_ROCKET, Structures::fusee,
                "Fusée blanche et rouge de 30 blocs,", "avec hublot, ailerons et moteur.");
        add("bateau_pirate", "Bateau de pirates", "Fun", OAK_BOAT, Structures::bateau,
                "Navire à 2 mâts, voiles blanches,", "drapeau noir et cabine arrière.");
        addRandom("labyrinthe", "Labyrinthe", "Fun", MOSSY_COBBLESTONE, Structures::labyrinthe,
                "Labyrinthe de haies 21×21.", "Chemin différent à chaque fois :", "cache-cache ou course entre potes !");
        addRandom("parkour", "Parcours de parkour", "Fun", LEATHER_BOOTS, Structures::parkour,
                "25 plateformes colorées dans les airs,", "des checkpoints lumineux et une", "arrivée en or. Aléatoire à chaque fois.");

        // ----- Aventure -----
        add("portail_nether", "Portail du Nether", "Aventure", OBSIDIAN, Structures::portail,
                "Portail déjà allumé, sur une", "plateforme de blackstone avec", "lanternes d'âmes.");
        add("bunker", "Bunker souterrain", "Aventure", IRON_DOOR, Structures::bunker,
                "Salle secrète sous terre avec", "trappe, échelle, bibliothèque,", "coffres et établi.");
        add("biodome", "Biodôme", "Aventure", GLASS, Structures::biodome,
                "Grand dôme de verre avec herbe,", "arbres, fleurs et un étang.");
        add("ferme", "Ferme", "Aventure", HAY_BLOCK, Structures::ferme,
                "Champ clôturé 9×9 avec canal d'eau,", "blé, carottes et pommes de terre", "déjà mûrs, portillon et lanternes.");
        add("hub", "Plateforme de spawn", "Aventure", SEA_LANTERN, Structures::hub,
                "Grande plateforme ronde en quartz", "avec emblème en or et 4 piliers", "lumineux. Pour un hub de serveur.");
    }

    // =====================================================================
    //  HABITATIONS
    // =====================================================================

    private static Blueprint maisonBois() {
        Blueprint b = new Blueprint();
        b.fill(-3, 0, -3, 3, 0, 3, COBBLESTONE);
        b.box(-3, 1, -3, 3, 4, 3, OAK_PLANKS);
        for (int sx : new int[]{-3, 3})
            for (int sz : new int[]{-3, 3}) b.fill(sx, 1, sz, sx, 4, sz, OAK_LOG);
        b.door(0, 2, 3, OAK_DOOR, SOUTH);
        b.set(-2, 3, 3, GLASS_PANE).set(2, 3, 3, GLASS_PANE);
        b.set(-3, 3, 0, GLASS_PANE).set(3, 3, 0, GLASS_PANE).set(0, 3, -3, GLASS_PANE);
        b.set(-1, 3, 4, "minecraft:wall_torch[facing=south]").set(1, 3, 4, "minecraft:wall_torch[facing=south]");
        // toit
        for (int k = 0; k < 4; k++) {
            int y = 5 + k;
            int e = 3 - k;
            if (e == 0) {
                b.fill(-4, y, 0, 4, y, 0, OAK_PLANKS);
                continue;
            }
            for (int x = -4; x <= 4; x++) {
                b.stair(x, y, -e, OAK_STAIRS, SOUTH, false);
                b.stair(x, y, e, OAK_STAIRS, NORTH, false);
            }
            b.fill(-3, y, -(e - 1), -3, y, e - 1, OAK_PLANKS);
            b.fill(3, y, -(e - 1), 3, y, e - 1, OAK_PLANKS);
        }
        // intérieur
        b.set(-2, 2, -2, CRAFTING_TABLE);
        b.dir(-1, 2, -2, FURNACE, SOUTH);
        b.dir(2, 2, -2, CHEST, WEST);
        b.set(0, 3, 0, "minecraft:lantern[hanging=true]");
        return b;
    }

    private static Blueprint villa() {
        Blueprint b = new Blueprint();
        b.fill(-8, 0, -6, 8, 0, 6, SMOOTH_STONE);
        b.box(-7, 1, -5, 7, 9, 5, WHITE_CONCRETE);
        b.fill(-6, 5, -4, 6, 5, 4, SMOOTH_QUARTZ);          // plancher de l'étage
        b.fill(-5, 5, -4, -3, 5, -4, AIR);                  // trou de l'escalier
        for (int i = 0; i < 4; i++) b.stair(-5 + i, 2 + i, -4, QUARTZ_STAIRS, EAST, false);
        for (int z : new int[]{-5, 5}) {
            b.fill(-5, 2, z, 5, 3, z, GLASS);
            b.fill(-5, 6, z, 5, 7, z, GLASS);
        }
        for (int x : new int[]{-7, 7}) {
            b.fill(x, 2, -3, x, 3, 3, GLASS);
            b.fill(x, 6, -3, x, 7, 3, GLASS);
        }
        for (int x : new int[]{-7, 7})
            for (int z : new int[]{-5, 5}) b.fill(x, 1, z, x, 9, z, QUARTZ_PILLAR);
        b.door(0, 2, 5, DARK_OAK_DOOR, SOUTH);
        b.fill(-8, 10, -6, 8, 10, 6, BLACK_CONCRETE);
        b.set(0, 4, 0, "minecraft:lantern[hanging=true]").set(0, 8, 0, "minecraft:lantern[hanging=true]");
        return b;
    }

    private static Blueprint cabaneArbre() {
        Blueprint b = new Blueprint();
        b.sphere(0, 15, 0, 6, Blueprint.leaves(OAK_LEAVES), false, false);
        b.fill(-3, 7, -3, 4, 7, 4, SPRUCE_PLANKS);                 // plateforme
        for (int i = -3; i <= 4; i++) {
            b.set(i, 8, -3, OAK_FENCE).set(i, 8, 4, OAK_FENCE).set(-3, 8, i, OAK_FENCE).set(4, 8, i, OAK_FENCE);
        }
        b.box(-2, 8, -2, 3, 12, 3, SPRUCE_PLANKS);                  // cabane
        b.set(3, 10, 0, GLASS_PANE).set(3, 10, 1, GLASS_PANE).set(-2, 10, 0, GLASS_PANE).set(-2, 10, 1, GLASS_PANE);
        b.fill(0, 0, 0, 1, 14, 1, OAK_LOG);                         // tronc 2x2
        b.set(-1, 7, 0, AIR);
        for (int y = 1; y <= 8; y++) b.dir(-1, y, 0, LADDER, WEST);
        b.set(2, 11, 2, "minecraft:lantern[hanging=true]");
        return b;
    }

    // =====================================================================
    //  MEDIEVAL
    // =====================================================================

    private static Blueprint chateau() {
        Blueprint b = new Blueprint();
        b.fill(-12, 0, -12, 12, 0, 12, COBBLESTONE);
        b.fill(-12, 1, -12, 12, 8, -11, STONE_BRICKS);
        b.fill(-12, 1, 11, 12, 8, 12, STONE_BRICKS);
        b.fill(-12, 1, -12, -11, 8, 12, STONE_BRICKS);
        b.fill(11, 1, -12, 12, 8, 12, STONE_BRICKS);
        for (int i = -12; i <= 12; i += 2) {
            b.set(i, 9, -12, STONE_BRICKS).set(i, 9, 12, STONE_BRICKS);
            b.set(-12, 9, i, STONE_BRICKS).set(12, 9, i, STONE_BRICKS);
        }
        // tours d'angle
        for (int cx : new int[]{-12, 12}) {
            for (int cz : new int[]{-12, 12}) {
                b.tube(cx, cz, 3, 1, 14, STONE_BRICKS);
                for (int k = 0; k <= 4; k++) b.cylinder(cx, cz, 4 - k, 15 + k, 15 + k, SPRUCE_PLANKS);
            }
        }
        // porte
        b.fill(-2, 1, 11, 2, 4, 12, AIR);
        b.fill(-2, 4, 12, 2, 4, 12, IRON_BARS);
        b.set(-3, 4, 13, "minecraft:wall_torch[facing=south]").set(3, 4, 13, "minecraft:wall_torch[facing=south]");
        // donjon
        b.box(-4, 1, -4, 4, 13, 4, STONE_BRICKS);
        b.fill(-3, 7, -3, 3, 7, 3, OAK_PLANKS);
        b.set(-3, 13, 0, AIR);
        for (int y = 2; y <= 13; y++) b.dir(-3, y, 0, LADDER, EAST);
        b.door(0, 2, 4, OAK_DOOR, SOUTH);
        for (int i = -4; i <= 4; i += 2) {
            b.set(i, 14, -4, STONE_BRICKS).set(i, 14, 4, STONE_BRICKS);
            b.set(-4, 14, i, STONE_BRICKS).set(4, 14, i, STONE_BRICKS);
        }
        b.set(0, 6, 0, "minecraft:lantern[hanging=true]").set(0, 12, 0, "minecraft:lantern[hanging=true]");
        b.fill(0, 14, 0, 0, 18, 0, OAK_FENCE);
        b.fill(1, 17, 0, 3, 18, 0, RED_WOOL);
        return b;
    }

    private static Blueprint tourGuet() {
        Blueprint b = new Blueprint();
        b.cylinder(0, 0, 4, 0, 0, COBBLESTONE);
        b.tube(0, 0, 3, 1, 16, STONE_BRICKS);
        b.cylinder(0, 0, 4, 17, 17, STONE_BRICKS);
        b.crenel(0, 0, 4, 18, STONE_BRICKS);
        b.door(0, 1, 3, OAK_DOOR, SOUTH);
        for (int y : new int[]{6, 11, 15}) {
            b.set(3, y, 0, GLASS_PANE).set(-3, y, 0, GLASS_PANE).set(0, y, 3, GLASS_PANE);
        }
        b.set(0, 17, -2, AIR);
        for (int y = 1; y <= 17; y++) b.dir(0, y, -2, LADDER, SOUTH);
        b.set(0, 16, 0, "minecraft:lantern[hanging=true]");
        return b;
    }

    private static Blueprint tourSorcier() {
        Blueprint b = new Blueprint();
        b.cylinder(0, 0, 4, 0, 0, DEEPSLATE_BRICKS);
        b.tube(0, 0, 3, 1, 20, PURPUR_BLOCK);
        for (int fy : new int[]{7, 14}) b.cylinder(0, 0, 2, fy, fy, OAK_PLANKS);
        b.cylinder(0, 0, 4, 21, 21, PURPLE_CONCRETE);
        int[] radii = {3, 3, 2, 2, 1, 1, 0};
        for (int k = 0; k < radii.length; k++) b.cylinder(0, 0, radii[k], 22 + k, 22 + k, PURPLE_CONCRETE);
        b.set(0, 29, 0, AMETHYST_BLOCK).set(0, 30, 0, LIGHTNING_ROD);
        b.door(0, 1, 3, DARK_OAK_DOOR, SOUTH);
        for (int y : new int[]{4, 11, 18}) {
            b.set(3, y, 0, GLASS_PANE).set(-3, y, 0, GLASS_PANE).set(0, y, 3, GLASS_PANE);
        }
        b.set(0, 1, 0, ENCHANTING_TABLE);
        b.fill(2, 1, 0, 2, 2, 0, BOOKSHELF).fill(-2, 1, 0, -2, 2, 0, BOOKSHELF);
        for (int y = 1; y <= 20; y++) b.dir(0, y, -2, LADDER, SOUTH);
        for (int y : new int[]{6, 13, 20}) b.set(0, y, 0, "minecraft:lantern[hanging=true]");
        return b;
    }

    private static Blueprint arene() {
        Blueprint b = new Blueprint();
        b.cylinder(0, 0, 14, 0, 0, SMOOTH_SANDSTONE);
        b.ring(0, 0, 8, 0, 0, RED_CONCRETE);
        b.cylinder(0, 0, 2, 0, 0, GOLD_BLOCK);
        for (int i = 0; i < 5; i++) b.ring(0, 0, 15 + i, 1, 1 + i, STONE_BRICKS);
        b.ring(0, 0, 20, 1, 9, STONE_BRICKS);
        b.crenel(0, 0, 20, 10, STONE_BRICKS);
        b.fill(-2, 1, 14, 2, 5, 21, AIR);
        b.set(-3, 4, 22, "minecraft:wall_torch[facing=south]").set(3, 4, 22, "minecraft:wall_torch[facing=south]");
        return b;
    }

    // =====================================================================
    //  MONUMENTS
    // =====================================================================

    private static int eiffelWidth(int y, int h) {
        return (int) Math.round(1 + 9 * Math.pow(1 - y / (double) h, 2.2));
    }

    private static Blueprint eiffel() {
        Blueprint b = new Blueprint();
        final int h = 56;
        int prev = -1;
        for (int y = 0; y <= h; y++) {
            int w = eiffelWidth(y, h);
            for (int sx : new int[]{-1, 1}) {
                for (int sz : new int[]{-1, 1}) {
                    b.set(sx * w, y, sz * w, IRON_BLOCK);
                    if (prev != -1 && prev != w) {
                        b.set(sx * prev, y, sz * prev, IRON_BLOCK);
                        b.set(sx * w, y, sz * prev, IRON_BLOCK);
                    }
                }
            }
            if (y % 4 == 0 && w > 1) {
                for (int i = -w + 1; i <= w - 1; i++) {
                    b.set(i, y, -w, IRON_BARS).set(i, y, w, IRON_BARS);
                    b.set(-w, y, i, IRON_BARS).set(w, y, i, IRON_BARS);
                }
            }
            prev = w;
        }
        for (int py : new int[]{18, 34}) {
            int w = eiffelWidth(py, h);
            b.fill(-w, py, -w, w, py, w, POLISHED_ANDESITE);
            for (int i = -w; i <= w; i++) {
                b.set(i, py + 1, -w, IRON_BARS).set(i, py + 1, w, IRON_BARS);
                b.set(-w, py + 1, i, IRON_BARS).set(w, py + 1, i, IRON_BARS);
            }
            b.set(-w, py + 1, -w, SEA_LANTERN).set(w, py + 1, -w, SEA_LANTERN);
            b.set(-w, py + 1, w, SEA_LANTERN).set(w, py + 1, w, SEA_LANTERN);
        }
        b.fill(0, 34, 0, 0, h + 8, 0, IRON_BLOCK);
        b.set(0, h + 9, 0, LIGHTNING_ROD);
        return b;
    }

    private static Blueprint pyramide() {
        Blueprint b = new Blueprint();
        for (int k = 0; k <= 15; k++) {
            int h = 15 - k;
            b.fill(-h, k, -h, h, k, h, (k % 4 == 3) ? CUT_SANDSTONE : SANDSTONE);
        }
        b.set(0, 15, 0, GOLD_BLOCK);
        b.fill(-4, 1, -4, 4, 5, 4, AIR);
        b.fill(-1, 1, 5, 1, 3, 16, AIR);
        b.set(0, 1, 0, CHISELED_SANDSTONE).set(0, 2, 0, GOLD_BLOCK);
        b.dir(-3, 1, -3, CHEST, EAST).dir(3, 1, -3, CHEST, WEST);
        b.dir(-3, 1, 3, CHEST, EAST).dir(3, 1, 3, CHEST, WEST);
        b.set(-3, 1, 0, LANTERN).set(3, 1, 0, LANTERN).set(0, 1, -3, LANTERN).set(0, 1, 3, LANTERN);
        return b;
    }

    private static Blueprint temple() {
        Blueprint b = new Blueprint();
        b.fill(-11, 0, -8, 11, 0, 8, SMOOTH_QUARTZ);
        b.fill(-10, 1, -7, 10, 1, 7, SMOOTH_QUARTZ);
        for (int x = -8; x <= 8; x += 4)
            for (int z : new int[]{-5, 5}) b.fill(x, 2, z, x, 8, z, QUARTZ_PILLAR);
        for (int x : new int[]{-8, 8}) b.fill(x, 2, 0, x, 8, 0, QUARTZ_PILLAR);
        b.fill(-9, 9, -6, 9, 9, 6, QUARTZ_BLOCK);
        for (int k = 1; k <= 6; k++) b.fill(-9, 9 + k, -6 + k, 9, 9 + k, 6 - k, QUARTZ_BLOCK);
        b.set(0, 2, 0, CHISELED_QUARTZ_BLOCK).set(0, 3, 0, CAMPFIRE);
        return b;
    }

    private static Blueprint phare() {
        Blueprint b = new Blueprint();
        b.cylinder(0, 0, 4, 0, 0, STONE_BRICKS);
        for (int y = 1; y <= 16; y++) b.tube(0, 0, 3, y, y, (y / 2) % 2 == 0 ? WHITE_CONCRETE : RED_CONCRETE);
        b.cylinder(0, 0, 4, 17, 17, SMOOTH_STONE);
        b.set(0, 17, -2, AIR);
        for (int y = 1; y <= 17; y++) b.dir(0, y, -2, LADDER, SOUTH);
        b.ring(0, 0, 4, 18, 18, IRON_BARS);
        b.tube(0, 0, 1, 18, 20, GLASS);
        b.fill(0, 18, 0, 0, 20, 0, SEA_LANTERN);
        b.cylinder(0, 0, 2, 21, 21, RED_CONCRETE).cylinder(0, 0, 1, 22, 22, RED_CONCRETE);
        b.set(0, 23, 0, RED_CONCRETE).set(0, 24, 0, LIGHTNING_ROD);
        b.door(0, 1, 3, IRON_DOOR, SOUTH);
        return b;
    }

    private static Blueprint fontaine() {
        Blueprint b = new Blueprint();
        b.cylinder(0, 0, 7, 0, 0, STONE_BRICKS);
        b.ring(0, 0, 6, 1, 2, STONE_BRICKS);
        b.cylinder(0, 0, 5, 1, 1, WATER);
        b.fill(0, 1, 0, 0, 4, 0, STONE_BRICKS);
        b.cylinder(0, 0, 3, 5, 5, STONE_BRICKS);
        b.ring(0, 0, 3, 6, 6, STONE_BRICKS);
        b.cylinder(0, 0, 2, 6, 6, WATER);
        b.fill(0, 6, 0, 0, 8, 0, STONE_BRICKS);
        b.set(0, 9, 0, SEA_LANTERN);
        return b;
    }

    // =====================================================================
    //  FUN / YOUTUBE
    // =====================================================================

    private static void scatter(Blueprint b, int x1, int y1, int z1, int x2, int y2, int z2, Material[] pool, Random r) {
        for (int x = x1; x <= x2; x++)
            for (int y = y1; y <= y2; y++)
                for (int z = z1; z <= z2; z++) b.set(x, y, z, pool[r.nextInt(pool.length)]);
    }

    private static Blueprint creeper() {
        Blueprint b = new Blueprint();
        Random rnd = new Random(7);
        Material[] g = {LIME_CONCRETE, GREEN_CONCRETE, LIME_CONCRETE, MOSS_BLOCK, GREEN_CONCRETE};
        for (int lx : new int[]{-4, 0})
            for (int lz : new int[]{-4, 0}) scatter(b, lx, 0, lz, lx + 3, 5, lz + 3, g, rnd);
        scatter(b, -4, 6, -2, 3, 17, 1, g, rnd);
        scatter(b, -4, 18, -4, 3, 25, 3, g, rnd);
        String[] face = {
                "........",
                "........",
                ".XX..XX.",
                ".XX..XX.",
                "...XX...",
                "..XXXX..",
                "..XXXX..",
                "..X..X.."
        };
        for (int row = 0; row < 8; row++)
            for (int col = 0; col < 8; col++)
                if (face[row].charAt(col) == 'X') b.set(-4 + col, 25 - row, 3, BLACK_CONCRETE);
        return b;
    }

    private static Blueprint fusee() {
        Blueprint b = new Blueprint();
        for (int y = 3; y <= 23; y++) {
            boolean red = (y >= 8 && y <= 9) || (y >= 18 && y <= 19);
            b.cylinder(0, 0, 3, y, y, red ? RED_CONCRETE : WHITE_CONCRETE);
        }
        int[] nose = {2, 2, 1, 1, 0};
        for (int k = 0; k < nose.length; k++) b.cylinder(0, 0, nose[k], 24 + k, 24 + k, RED_CONCRETE);
        b.fill(-1, 13, 3, 1, 14, 3, GLASS);
        b.cylinder(0, 0, 2, 1, 2, BLACK_CONCRETE);
        int[] fh = {6, 4, 2, 1};
        int[][] dirs = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};
        for (int[] d : dirs)
            for (int k = 0; k < fh.length; k++)
                for (int y = 0; y < fh[k]; y++) b.set(d[0] * (4 + k), y, d[1] * (4 + k), RED_CONCRETE);
        return b;
    }

    private static Blueprint bateau() {
        Blueprint b = new Blueprint().noFoundation();
        for (int z = -11; z <= 11; z++) {
            double f = 1 - Math.pow(z / 12.0, 2);
            int hw = (int) Math.round(5 * Math.sqrt(f));
            int w0 = Math.max(0, hw - 2);
            int w1 = Math.max(0, hw - 1);
            b.fill(-w0, 0, z, w0, 0, z, DARK_OAK_PLANKS);
            b.fill(-w1, 1, z, w1, 1, z, DARK_OAK_PLANKS);
            b.fill(-hw, 2, z, hw, 2, z, SPRUCE_PLANKS);
            b.fill(-hw, 3, z, hw, 3, z, OAK_PLANKS);
            b.set(-hw, 4, z, OAK_FENCE).set(hw, 4, z, OAK_FENCE);
        }
        b.fill(0, 4, -4, 0, 17, -4, SPRUCE_LOG);
        b.fill(0, 4, 4, 0, 13, 4, SPRUCE_LOG);
        b.fill(-4, 8, -3, 4, 15, -3, WHITE_WOOL);
        b.fill(-3, 7, 5, 3, 12, 5, WHITE_WOOL);
        b.fill(1, 16, -4, 3, 17, -4, BLACK_WOOL);
        b.set(0, 4, -12, OAK_FENCE).set(0, 5, -13, OAK_FENCE);
        b.box(-3, 4, 6, 3, 8, 10, DARK_OAK_PLANKS);
        b.door(0, 5, 6, SPRUCE_DOOR, NORTH);
        b.set(-3, 6, 8, GLASS_PANE).set(3, 6, 8, GLASS_PANE);
        b.set(0, 7, 8, "minecraft:lantern[hanging=true]");
        return b;
    }

    private static Blueprint labyrinthe() {
        Blueprint b = new Blueprint();
        int n = 10;
        int s = 2 * n + 1;
        boolean[][] open = new boolean[s][s];
        Random r = new Random();
        Deque<int[]> stack = new ArrayDeque<>();
        open[1][1] = true;
        stack.push(new int[]{1, 1});
        int[][] dirs = {{2, 0}, {-2, 0}, {0, 2}, {0, -2}};
        while (!stack.isEmpty()) {
            int[] cur = stack.peek();
            List<int[]> opts = new ArrayList<>();
            for (int[] d : dirs) {
                int nx = cur[0] + d[0];
                int nz = cur[1] + d[1];
                if (nx > 0 && nz > 0 && nx < s - 1 && nz < s - 1 && !open[nx][nz]) opts.add(d);
            }
            if (opts.isEmpty()) {
                stack.pop();
                continue;
            }
            int[] d = opts.get(r.nextInt(opts.size()));
            open[cur[0] + d[0] / 2][cur[1] + d[1] / 2] = true;
            open[cur[0] + d[0]][cur[1] + d[1]] = true;
            stack.push(new int[]{cur[0] + d[0], cur[1] + d[1]});
        }
        open[1][0] = true;          // entrée (nord-ouest)
        open[s - 2][s - 1] = true;  // sortie (sud-est)
        org.bukkit.block.data.BlockData hedge = Blueprint.leaves(OAK_LEAVES);
        for (int ix = 0; ix < s; ix++) {
            for (int iz = 0; iz < s; iz++) {
                int x = ix - n;
                int z = iz - n;
                b.set(x, 0, z, DIRT_PATH);
                if (!open[ix][iz]) b.fill(x, 1, z, x, 3, z, hedge);
            }
        }
        b.set(0, 0, 0, GOLD_BLOCK);
        return b;
    }

    private static Blueprint parkour() {
        Blueprint b = new Blueprint().noFoundation();
        Random r = new Random();
        Material[] cols = {LIGHT_BLUE_CONCRETE, LIME_CONCRETE, YELLOW_CONCRETE, ORANGE_CONCRETE,
                MAGENTA_CONCRETE, CYAN_CONCRETE, PINK_CONCRETE};
        b.fill(-1, 0, -1, 1, 0, 1, EMERALD_BLOCK);
        int x = 0;
        int y = 0;
        int z = -1;
        for (int i = 1; i <= 25; i++) {
            int dz = 2 + r.nextInt(3);
            int dx = r.nextInt(3) - 1;
            int dy = r.nextInt(3) - 1;
            if (dz == 4 && dy > 0) dy = 0;
            z -= dz;
            x += dx;
            y += dy;
            if (i % 8 == 0) b.fill(x - 1, y, z - 1, x + 1, y, z + 1, SEA_LANTERN);
            else b.set(x, y, z, cols[r.nextInt(cols.length)]);
        }
        z -= 3;
        b.fill(x - 1, y, z - 1, x + 1, y, z + 1, GOLD_BLOCK);
        return b;
    }

    // =====================================================================
    //  AVENTURE
    // =====================================================================

    private static Blueprint portail() {
        Blueprint b = new Blueprint();
        b.fill(-4, 0, -3, 5, 0, 3, BLACKSTONE);
        b.fill(-1, 0, 0, 2, 4, 0, OBSIDIAN);
        b.fill(0, 1, 0, 1, 3, 0, NETHER_PORTAL);
        b.set(-1, 2, 0, CRYING_OBSIDIAN).set(2, 3, 0, CRYING_OBSIDIAN);
        for (int x : new int[]{-3, 4}) {
            for (int z : new int[]{-2, 2}) {
                b.fill(x, 1, z, x, 3, z, BLACKSTONE);
                b.set(x, 4, z, SOUL_LANTERN);
            }
        }
        return b;
    }

    private static Blueprint bunker() {
        Blueprint b = new Blueprint().noFoundation();
        b.box(-5, -7, -5, 5, -1, 5, STONE_BRICKS);
        b.fill(-2, -6, -2, 2, -6, 2, RED_CARPET);
        b.set(-4, -6, -4, CRAFTING_TABLE);
        b.dir(-3, -6, -4, FURNACE, SOUTH);
        b.dir(4, -6, -4, CHEST, WEST).dir(3, -6, -4, CHEST, WEST);
        b.set(2, -6, -4, BARREL);
        b.fill(-4, -6, 0, -4, -5, 3, BOOKSHELF);
        for (int x : new int[]{-3, 3})
            for (int z : new int[]{-3, 3}) b.set(x, -2, z, "minecraft:lantern[hanging=true]");
        for (int y = -6; y <= -2; y++) b.dir(0, y, 4, LADDER, NORTH);
        b.set(0, -1, 4, "minecraft:iron_trapdoor[half=bottom]");
        return b;
    }

    private static void tree(Blueprint b, int x, int y, int z, int h) {
        b.sphere(x, y + h, z, 2, Blueprint.leaves(OAK_LEAVES), false, false);
        b.fill(x, y, z, x, y + h - 1, z, OAK_LOG);
    }

    private static Blueprint biodome() {
        Blueprint b = new Blueprint();
        b.sphere(0, 0, 0, 12, GLASS.createBlockData(), true, true);
        b.cylinder(0, 0, 12, 0, 0, GRASS_BLOCK);
        b.fill(-1, 1, 11, 1, 2, 12, AIR);
        b.cylinder(5, 5, 3, 0, 0, WATER);
        Random r = new Random(3);
        Material[] flowers = {DANDELION, POPPY, AZURE_BLUET, FERN};
        for (int dx = -9; dx <= 9; dx++) {
            for (int dz = -9; dz <= 9; dz++) {
                if (dx * dx + dz * dz > 81) continue;
                if ((dx - 5) * (dx - 5) + (dz - 5) * (dz - 5) <= 16) continue;
                if (r.nextDouble() < 0.1) b.set(dx, 1, dz, flowers[r.nextInt(flowers.length)]);
            }
        }
        tree(b, -6, 1, -4, 5);
        tree(b, -3, 1, 6, 4);
        tree(b, 6, 1, -6, 6);
        return b;
    }

    private static Blueprint ferme() {
        Blueprint b = new Blueprint();
        b.fill(-5, 0, -5, 5, 0, 5, DIRT);
        for (int x = -4; x <= 4; x++) {
            for (int z = -4; z <= 4; z++) {
                if (x == 0) {
                    b.set(x, 0, z, WATER);
                    continue;
                }
                b.farmland(x, 0, z);
                int ax = Math.abs(x);
                b.crop(x, 1, z, ax <= 2 ? WHEAT : (ax == 3 ? CARROTS : POTATOES));
            }
        }
        for (int i = -5; i <= 5; i++) {
            b.set(i, 1, -5, OAK_FENCE).set(i, 1, 5, OAK_FENCE);
            b.set(-5, 1, i, OAK_FENCE).set(5, 1, i, OAK_FENCE);
        }
        b.dir(0, 1, 5, OAK_FENCE_GATE, SOUTH);
        for (int sx : new int[]{-5, 5})
            for (int sz : new int[]{-5, 5}) b.set(sx, 2, sz, LANTERN);
        return b;
    }

    private static Blueprint hub() {
        Blueprint b = new Blueprint();
        b.cylinder(0, 0, 14, 0, 0, SMOOTH_QUARTZ);
        b.ring(0, 0, 10, 0, 0, LIGHT_BLUE_CONCRETE);
        b.ring(0, 0, 6, 0, 0, CYAN_CONCRETE);
        b.cylinder(0, 0, 3, 0, 0, GOLD_BLOCK);
        b.ring(0, 0, 14, 1, 1, QUARTZ_SLAB);
        for (int sx : new int[]{-7, 7}) {
            for (int sz : new int[]{-7, 7}) {
                b.fill(sx, 1, sz, sx, 6, sz, QUARTZ_PILLAR);
                b.set(sx, 7, sz, SEA_LANTERN);
            }
        }
        return b;
    }
}
