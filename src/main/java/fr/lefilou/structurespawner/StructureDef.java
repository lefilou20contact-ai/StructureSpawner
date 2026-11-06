package fr.lefilou.structurespawner;

import org.bukkit.Material;

import java.util.List;
import java.util.function.Supplier;

public final class StructureDef {
    private final String id;
    private final String name;
    private final String category;
    private final Material icon;
    private final boolean regenerate;
    private final Supplier<Blueprint> builder;
    private final List<String> description;
    private Blueprint cache;

    public StructureDef(String id, String name, String category, Material icon, boolean regenerate,
                        Supplier<Blueprint> builder, String... description) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.icon = icon;
        this.regenerate = regenerate;
        this.builder = builder;
        this.description = List.of(description);
    }

    public String id() { return id; }
    public String name() { return name; }
    public String category() { return category; }
    public Material icon() { return icon; }
    public boolean regenerate() { return regenerate; }
    public List<String> description() { return description; }

    /** Plan utilisé pour l'affichage dans le menu (construit une seule fois). */
    public Blueprint sample() {
        if (cache == null) cache = builder.get();
        return cache;
    }

    /** Plan utilisé pour la construction (régénéré à chaque fois si la structure est aléatoire). */
    public Blueprint blueprint() {
        if (regenerate || cache == null) cache = builder.get();
        return cache;
    }
}
