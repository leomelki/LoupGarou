package fr.leomelki.loupgarou.roles;

import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_15_R1.inventory.CraftInventoryCustom;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.scheduler.BukkitRunnable;

import fr.leomelki.com.comphenix.packetwrapper.WrapperPlayServerHeldItemSlot;
import fr.leomelki.loupgarou.MainLg;
import fr.leomelki.loupgarou.classes.LGGame;
import fr.leomelki.loupgarou.classes.LGPlayer;
import fr.leomelki.loupgarou.events.LGPlayerKilledEvent.Reason;

public class RSorciere extends Role{
	private static ItemStack[] items = new ItemStack[4];
	private static ItemStack cancel;
	static {
		items[0] = new ItemStack(Material.PURPLE_DYE, 1);
		ItemMeta meta = items[0].getItemMeta();
		meta.setDisplayName("§a§lPotion de vie");
		meta.setLore(Arrays.asList("§2Sauve la cible des §c§lLoups§2."));
		items[0].setItemMeta(meta);
		items[1] = new ItemStack(Material.IRON_NUGGET);
		meta = items[1].getItemMeta();
		meta.setDisplayName("§7§lNe rien faire");
		items[1].setItemMeta(meta);
		items[2] = new ItemStack(Material.LIGHT_BLUE_DYE, 1);
		meta = items[2].getItemMeta();
		meta.setDisplayName("§c§lPotion de mort");
		meta.setLore(Arrays.asList("§cTue la personne de ton choix."));
		items[2].setItemMeta(meta);
		cancel = new ItemStack(Material.IRON_NUGGET);
		meta = cancel.getItemMeta();
		meta.setDisplayName("§c§lRevenir au choix des potions");
		cancel.setItemMeta(meta);
	}
	
	
	public RSorciere(LGGame game) {
		super(game);
	}
	@Override
	public RoleType getType() {
		return RoleType.VILLAGER;
	}
	@Override
	public RoleWinType getWinType() {
		return RoleWinType.VILLAGE;
	}
	@Override
	public String getName() {
		return "§a§lSorcière";
	}
	@Override
	public String getFriendlyName() {
		return "de la "+getName();
	}
	@Override
	public String getShortDescription() {
		return "Tu gagnes avec le §a§lVillage";
	}
	@Override
	public String getDescription() {
		return "Tu gagnes avec le §a§lVillage§f. Tu disposes de deux potions : une §e§o§lpotion de vie§f pour sauver la victime des §c§lLoups§f, et une §e§o§lpotion de mort§f pour assassiner quelqu'un.";
	}
	@Override
	public String getTask() {
		return "Que veux-tu faire cette nuit ?";
	}
	@Override
	public String getBroadcastedTask() {
		return "La "+getName()+"§9 est en train de concocter un nouvel élixir.";
	}
	@Override
	public int getTimeout() {
		return 30;
	}
	
	private LGPlayer sauver;
	private Runnable callback;
	
	@Override
	protected void onNightTurn(LGPlayer player, Runnable callback) {
		player.showView();
		this.callback = callback;
		sauver = getGame().getDeaths().get(Reason.LOUP_GAROU);
		if(sauver == null)
			sauver = getGame().getDeaths().get(Reason.DONT_DIE);
		
		openInventory(player);
	}
	@Override
	protected void onNightTurnTimeout(LGPlayer player) {
		player.getPlayer().getInventory().setItem(8, null);
		player.stopChoosing();
		closeInventory(player.getPlayer());
		player.getPlayer().updateInventory();
		player.hideView();
		//player.sendTitle("§cVous n'avez utilisé aucune potion", "§4Vous avez mis trop de temps à vous décider...", 80);
		//player.sendMessage("§6Tu n'as rien fait cette nuit.");
	}
	private void openInventory(LGPlayer player) {
		inMenu = true;
		Inventory inventory = Bukkit.createInventory(null, InventoryType.BREWING, sauver == null ? "§7Personne n'a été ciblé" : "§7§l"+sauver.getName()+" §7est ciblé");
		inventory.setContents(items.clone());//clone au cas où Bukkit prenne directement la liste pour éviter de la modifier avec setItem (jsp)
		if(sauver == null || player.getCache().getBoolean("witch_used_life"))
			inventory.setItem(0, null);
		
		if(sauver != null) {
			ItemStack head = new ItemStack(Material.ARROW);
			ItemMeta meta = head.getItemMeta();
			meta.setDisplayName("§7§l"+sauver.getName()+"§c est ciblé");
			head.setItemMeta(meta);
			inventory.setItem(4, head);
		}
		if(player.getCache().getBoolean("witch_used_death"))
			inventory.setItem(2, null);
		player.getPlayer().closeInventory();
		player.getPlayer().openInventory(inventory);
	}
	boolean inMenu = false;
	
	private void closeInventory(Player p) {
		inMenu = false;
		p.closeInventory();
	}
	@EventHandler
	public void onInventoryClick(InventoryClickEvent e) {
		ItemStack item = e.getCurrentItem();
		Player player = (Player) e.getWhoClicked();
		LGPlayer lgp = LGPlayer.thePlayer(player);

		if (lgp.getRole() != this || item == null || item.getItemMeta() == null)
			return;

		if (item.getItemMeta().getDisplayName().equals(items[0].getItemMeta().getDisplayName()) && sauver != null) {// Potion de vie
			e.setCancelled(true);
			closeInventory(player);
			saveLife(lgp);
		} else if (item.getItemMeta().getDisplayName().equals(items[1].getItemMeta().getDisplayName())) {// Cancel
			e.setCancelled(true);
			closeInventory(player);
			lgp.sendMessage("§6Tu n'as rien fait cette nuit.");
			lgp.hideView();
			callback.run();
		} else if (item.getItemMeta().getDisplayName().equals(items[2].getItemMeta().getDisplayName())) {// Potion de mort
			e.setCancelled(true);
			player.getInventory().setItem(8, cancel);
			player.updateInventory();
			
			//On le met sur le slot 0 pour éviter un missclick sur la croix
			WrapperPlayServerHeldItemSlot hold = new WrapperPlayServerHeldItemSlot();
			hold.setSlot(0);
			hold.sendPacket(lgp.getPlayer());
			
			closeInventory(player);
			lgp.choose((choosen) -> {
				if (choosen != null) {
					lgp.stopChoosing();
					kill(choosen, lgp);
				}
			}/*, sauver*/);//On peut tuer la personne qui a été tué par les loups (bien que cela ne serve à rien)
		}
	}
	
	@EventHandler
	public void onClick(PlayerInteractEvent e) {
		Player p = e.getPlayer();
		LGPlayer player = LGPlayer.thePlayer(p);
		if(e.getItem() != null && e.getItem().getType() == Material.IRON_NUGGET && player.getRole() == this) {
			player.stopChoosing();
			p.getInventory().setItem(8, null);
			p.updateInventory();

			openInventory(player);
		}
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
	
	private void kill(LGPlayer choosen, LGPlayer player) {
		player.getPlayer().getInventory().setItem(8, null);
		player.getPlayer().updateInventory();
		player.getCache().set("witch_used_death", true);
		getGame().kill(choosen, Reason.SORCIERE);
		player.sendMessage("§6Tu as décidé d'assassiner §7§l"+choosen.getName()+"§6.");
		player.sendActionBarMessage("§7§l"+choosen.getName()+"§9 a été tué.");
		player.hideView();
		callback.run();
	}
	private void saveLife(LGPlayer player) {
		player.getCache().set("witch_used_life", true);
		getGame().getDeaths().remove(Reason.LOUP_GAROU, sauver);
		player.sendMessage("§6Tu as décidé de sauver §7§l"+sauver.getName()+"§6.");
		player.sendActionBarMessage("§7§l"+sauver.getName()+"§9 a été sauvé.");
		player.hideView();
		callback.run();
	}
}
