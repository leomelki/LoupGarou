package fr.leomelki.loupgarou.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.event.weather.WeatherChangeEvent;

import fr.leomelki.loupgarou.classes.LGPlayer;

public class CancelListener implements Listener{
	@EventHandler
	public void onPluie(WeatherChangeEvent e) {
		e.setCancelled(true);
	}
	@EventHandler
	public void onMove(PlayerMoveEvent e) {
		LGPlayer lgp = LGPlayer.thePlayer(e.getPlayer());
		if(lgp.getGame() != null && lgp.getGame().isStarted() && e.getFrom().distanceSquared(e.getTo()) > 0.001)
			e.setTo(e.getFrom());
	}
	@EventHandler
	public void onDamage(EntityDamageEvent e) {
		e.setCancelled(true);
	}
	@EventHandler
	public void onFood(FoodLevelChangeEvent e) {
		e.setFoodLevel(6);
	}
	@EventHandler
	public void onRespawn(PlayerRespawnEvent e) {
		e.setRespawnLocation(e.getPlayer().getLocation());
	}
	@EventHandler
	public void onRespawn(PlayerDeathEvent e) {
		e.setDeathMessage("");
		e.setKeepInventory(true);
	}
/*	@EventHandler
	public void onAchievement(PlayerAchievementAwardedEvent e) {
		e.setCancelled(true);
	}*/
	@EventHandler
	public void onEntitySpawn(EntitySpawnEvent e) {
		e.setCancelled(true);
		//TODO here
	/*	System.out.println("\r\n" + 
				"			//TODO here\r\n" + 
				"			//ERREUR : LE LGN PEUT NE PAS AVOIR SON MENU SI IL A LE CHAT OUVERT PAR EX..AVOIR.\r\n" + 
				"			//SI QQN VOIT PLUS SON PERSO -> sneak");
		*///TODO : REMPLACER LE MUTE / UNNMUTE PAR UN SYSTEME DE CHAT AVEC UNE LISTE DES PARTICIPANTS DU CHAT DEDANS (ET DU COUP DES SUR CLASSES DE CHAT QUI OVERRIDE LA FONCTION QUI FORMATTE LES MESSAGES DU COUP POSSIBILITE DUTILISER CA POUR FAIRE UN SYSTEME SUR DISCORD (ET FAIRE UN TYPE DE SALON NOCHAT QUAND ON PEUT PAS PARLER))
	}
	@EventHandler
	public void onDrop(PlayerDropItemEvent e) {
		e.setCancelled(true);
	}
	@EventHandler
	public void onClickInventory(InventoryClickEvent e) {
		if(LGPlayer.thePlayer((Player)e.getWhoClicked()).getGame() != null)
			e.setCancelled(true);
	}
	@EventHandler
	public void onClickInventory(PlayerSwapHandItemsEvent e) {
		if(LGPlayer.thePlayer(e.getPlayer()).getGame() != null)
			e.setCancelled(true);
	}
}
