package fr.lefilou.structurespawner;

import org.bukkit.plugin.java.JavaPlugin;

public final class StructureSpawnerPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        Structures structures = new Structures();
        Placer placer = new Placer(this);
        MenuManager menus = new MenuManager(this, structures, placer);
        getServer().getPluginManager().registerEvents(menus, this);

        StructureCommand cmd = new StructureCommand(structures, placer, menus);
        getCommand("structure").setExecutor(cmd);
        getCommand("structure").setTabCompleter(cmd);

        getLogger().info(structures.all().size() + " structures chargées.");
    }
}
