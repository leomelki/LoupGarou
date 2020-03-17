package fr.leomelki.loupgarou.roles;

import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_15_R1.inventory.CraftInventoryCustom;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import fr.leomelki.loupgarou.MainLg;
import fr.leomelki.loupgarou.classes.LGCustomItems;
import fr.leomelki.loupgarou.classes.LGCustomItems.LGCustomItemsConstraints;
import fr.leomelki.loupgarou.classes.LGGame;
import fr.leomelki.loupgarou.classes.LGPlayer;
import fr.leomelki.loupgarou.classes.LGVote;
import fr.leomelki.loupgarou.events.LGCustomItemChangeEvent;
import fr.leomelki.loupgarou.events.LGNightEndEvent;
import fr.leomelki.loupgarou.events.LGPlayerKilledEvent.Reason;

public class RLoupGarouNoir extends Role{
	static ItemStack[] items = new ItemStack[9];
	static ItemStack[] skip = new ItemStack[9];
	static {
		items[3] = new ItemStack(Material.IRON_NUGGET);
		ItemMeta meta = items[3].getItemMeta();
		meta.setDisplayName("§7§lNe rien faire");
		meta.setLore(Arrays.asList("§8Passez votre tour"));
		items[3].setItemMeta(meta);
		items[5] = new ItemStack(Material.ROTTEN_FLESH);
		meta = items[5].getItemMeta();
		meta.setDisplayName("§c§lInfecter");
		meta.setLore(Arrays.asList(
				"§8Tu peux infecter la cible du vote.",
				"§8Le joueur tiendra avec les Loups."));
		items[5].setItemMeta(meta);
		skip[4] = items[3];
	}

	public RLoupGarouNoir(LGGame game) {
		super(game);
	}

	@Override
	public String getName() {
		return "§c§lLoup Noir";
	}

	@Override
	public String getFriendlyName() {
		return "du "+getName();
	}

	@Override
	public String getShortDescription() {
		return "Tu gagnes avec les §c§lLoups-Garous";
	}

	@Override
	public String getDescription() {
		return "Tu gagnes avec les §c§lLoups-Garous§f. Chaque nuit, tu te réunis avec tes compères pour décider d'une victime à éliminer... Une fois dans la partie, tu peux transformer la personne désignée en §c§lLoup§f. L'infecté conserve ses pouvoirs mais gagne désormais avec les §c§lLoups§f.";
	}

	@Override
	public String getTask() {
		return "Veux-tu infecter la cible du vote ?";
	}

	@Override
	public String getBroadcastedTask() {
		return "Le "+getName()+"§9 décide s'il veut infecter sa cible.";
	}
	@Override
	public RoleType getType() {
		return RoleType.LOUP_GAROU;
	}
	@Override
	public RoleWinType getWinType() {
		return RoleWinType.LOUP_GAROU;
	}

	@Override
	public int getTimeout() {
		return 15;
	}
	
	@Override
	public boolean hasPlayersLeft() {
		return super.hasPlayersLeft() && getGame().getDeaths().containsKey(Reason.LOUP_GAROU);
	}
	
	LGVote vote;
	Runnable callback;
	LGPlayer toInfect;
	
	public void openInventory(Player player) {
		inMenu = true;
		Inventory inventory = Bukkit.createInventory(null, 9, "§7Infecter "+toInfect.getName()+" ?");
		inventory.setContents(toInfect == null ? skip.clone() : items.clone());
		player.closeInventory();
		player.openInventory(inventory);
	}
	@Override
	protected void onNightTurn(LGPlayer player, Runnable callback) {
		toInfect = getGame().getDeaths().get(Reason.LOUP_GAROU);
		if(toInfect.getRoleType() == RoleType.LOUP_GAROU)
			toInfect = null;
		player.showView();
		this.callback = callback;
		openInventory(player.getPlayer());
	}
	@Override
	protected void onNightTurnTimeout(LGPlayer player) {
		player.getPlayer().getInventory().setItem(8, null);
		player.stopChoosing();
		closeInventory(player.getPlayer());
		player.getPlayer().updateInventory();
		player.hideView();
		//player.sendTitle("§cVous n'infectez personne", "§4Vous avez mis trop de temps à vous décider...", 80);
		player.sendMessage("§6Tu n'as rien fait cette nuit.");
	}

	boolean inMenu = false;
	
	private void closeInventory(Player p) {
		inMenu = false;
		p.closeInventory();
	}
	@EventHandler
	public void onInventoryClick(InventoryClickEvent e) {
		ItemStack item = e.getCurrentItem();
		Player player = (Player)e.getWhoClicked();
		LGPlayer lgp = LGPlayer.thePlayer(player);
			
		if(lgp.getRole() != this || item == null || item.getItemMeta() == null)return;

		if(item.getItemMeta().getDisplayName().equals(items[3].getItemMeta().getDisplayName())) {
			e.setCancelled(true);
			closeInventory(player);
			lgp.sendMessage("§6Tu n'as rien fait cette nuit.");
			lgp.hideView();
			callback.run();
		}else if(item.getItemMeta().getDisplayName().equals(items[5].getItemMeta().getDisplayName()) && toInfect != null) {
			e.setCancelled(true);
			closeInventory(player);
			player.updateInventory();
			closeInventory(player);
			
			lgp.getCache().set("has_infected", true);
			toInfect.getCache().set("infected", true);
			getPlayers().remove(lgp);
			toInfect.getCache().set("just_infected", true);
			lgp.sendActionBarMessage("§9§lVous infectez §9"+toInfect.getName());
			lgp.sendMessage("§6Tu as infecté §7§l"+toInfect.getName()+"§6.");
			lgp.stopChoosing();
			getGame().getDeaths().remove(Reason.LOUP_GAROU, toInfect);
			lgp.hideView();
			callback.run();
		}
	}
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onDayStart(LGNightEndEvent e) {
		if(e.getGame() == getGame())
			for(LGPlayer player : getGame().getAlive()) {
				if(player.getCache().getBoolean("just_infected")) {
					player.getCache().remove("just_infected");
					player.sendMessage("§6Tu as été infecté pendant la nuit.");
					player.sendMessage("§6§oTu gagnes désormais avec les §c§l§oLoups-Garous§6§o.");
					for(Role role : getGame().getRoles())
						if(role instanceof RLoupGarou)
							if(!player.isDead()) {//Si il n'a pas été tué je ne sais comment
								role.join(player, false);
								player.getPlayer().getInventory().setItemInOffHand(new ItemStack(LGCustomItems.getItem(player)));
							}
					
					for(LGPlayer lgp : getGame().getInGame()) {
						if(lgp.getRoleType() == RoleType.LOUP_GAROU)
							lgp.sendMessage("§7§l"+player.getName()+"§6 s'est fait infecter pendant la nuit.");
						else
							lgp.sendMessage("§6Un joueur a été §c§linfecté§6 pendant la nuit.");
					}
					
					if(getGame().checkEndGame())
						e.setCancelled(true);
				}
			}
	}
	
	@Override
	public void join(LGPlayer player, boolean sendMessage) {
		super.join(player, sendMessage);
		for(Role role : getGame().getRoles())
			if(role instanceof RLoupGarou)
				role.join(player, false);
	}

	@EventHandler
	public void onQuitInventory(InventoryCloseEvent e) {
		if(e.getInventory() instanceof CraftInventoryCustom) {
			LGPlayer player = LGPlayer.thePlayer((Player)e.getPlayer());
			if(player.getRole() == this && inMenu) {
				new BukkitRunnable() {
					
					@Override
					public void run() {
						e.getPlayer().openInventory(e.getInventory());
					}
				}.runTaskLater(MainLg.getInstance(), 1);
			}
		}
	}
	
	@EventHandler
	public void onCustomItemChange(LGCustomItemChangeEvent e) {
		if(e.getGame() == getGame())
			if(e.getPlayer().getCache().getBoolean("infected"))
				e.getConstraints().add(LGCustomItemsConstraints.INFECTED.getName());
	}
	
}
