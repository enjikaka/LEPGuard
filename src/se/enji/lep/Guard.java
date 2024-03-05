package se.enji.lep;

import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockIgniteEvent.IgniteCause;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class Guard extends JavaPlugin implements Listener {
	FileConfiguration config;
	Logger log = Bukkit.getLogger();
	
	public void onEnable() {
		getServer().getPluginManager().registerEvents(this, this);
		config=getConfig();
		config.options().copyDefaults(true);
		saveConfig();
	}
	
	@EventHandler
	public void onBlockIgnite(BlockIgniteEvent event) {
		boolean fireAllowed = config.getBoolean("fire-allowed");
		if (event.getCause() == IgniteCause.FLINT_AND_STEEL) {
			Player p = event.getPlayer();
			if (!fireAllowed) {
				if (p.hasPermission("pico.guard.fire-allowed")) return;
				else {
					p.sendMessage(config.getString("messages.fire-disabled"));
					event.setCancelled(true);
				}
			}
			else return;
		}
		else if (!config.getBoolean("fire.allowed")) event.setCancelled(true);
	}
	
	@EventHandler(priority = EventPriority.HIGH)
	public void onEntityExplode(EntityExplodeEvent e) {
		if (!config.getBoolean("creeper.harmBlocks")) {
			Entity c = e.getEntity();
			if (c.getType() == EntityType.CREEPER) {
				c.remove();
				e.setCancelled(true);
			}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGH)
	public void onEntityDamage(EntityDamageEvent e) {
		DamageCause dc = e.getCause();
		if (!config.getBoolean("creeper.harmPlayers")) if (dc==DamageCause.ENTITY_EXPLOSION) e.setCancelled(true);
		if (e.getEntityType() == EntityType.PLAYER) {
			if (!config.getBoolean("player.harmed-by.lava")) if (dc==DamageCause.LAVA) e.setCancelled(true);
			if (!config.getBoolean("player.harmed-by.TNT")) if (dc==DamageCause.BLOCK_EXPLOSION) e.setCancelled(true);
			if (!config.getBoolean("player.harmed-by.falling")) if (dc==DamageCause.FALL) e.setCancelled(true);
			if (!config.getBoolean("player.harmed-by.fire")) if (dc==DamageCause.FIRE) e.setCancelled(true);
			if (!config.getBoolean("player.harmed-by.fire")) if (dc==DamageCause.FIRE) e.setCancelled(true);
			if (!config.getBoolean("player.harmed-by.starve")) if (dc==DamageCause.STARVATION) e.setCancelled(true);
		}
	}
}
