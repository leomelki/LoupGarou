package fr.leomelki.loupgarou.roles;

import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_15_R1.inventory.CraftInventoryCustom;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import fr.leomelki.loupgarou.MainLg;
import fr.leomelki.loupgarou.classes.LGGame;
import fr.leomelki.loupgarou.classes.LGPlayer;

public class RChienLoup extends Role{
	static ItemStack[] items = new ItemStack[9];
	static {
		items[3] = new ItemStack(Material.GOLDEN_APPLE);
		ItemMeta meta = items[3].getItemMeta();
		meta.setDisplayName("§2Devenir Villageois");
		meta.setLore(Arrays.asList("§7§lVous n'aurez aucun pouvoir mais",
									"§7§lresterez dans le camp du §a§lVillage§7§l."));
		items[3].setItemMeta(meta);
		items[5] = new ItemStack(Material.ROTTEN_FLESH);
		meta = items[5].getItemMeta();
		meta.setDisplayName("§cDevenir Loup-Garou");
		meta.setLore(Arrays.asList("§cVous rejoindrez le camp des §c§lLoups"));
		items[5].setItemMeta(meta);
	}

	public RChienLoup(LGGame game) {
		super(game);
	}

	@Override
	public String getName() {
		return "§a§lChien-Loup";
	}

	@Override
	public String getFriendlyName() {
		return "du "+getName();
	}

	@Override
	public String getShortDescription() {
		return "Tu gagnes avec le §a§lVillage";
	}

	@Override
	public String getDescription() {
		return "Tu gagnes avec le §a§lVillage§f. Au début de la première nuit, tu peux choisir entre rester fidèle aux §a§lVillageois§f ou alors rejoindre le clan des §c§lLoups-Garous§f.";
	}

	@Override
	public String getTask() {
		return "Souhaites-tu devenir un §c§lLoup-Garou§6 ?";
	}

	@Override
	public String getBroadcastedTask() {
		return "Le "+getName()+"§9 pourrait trouver de nouveaux amis...";
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
	public int getTimeout() {
		return 15;
	}
	
	@Override
	public boolean hasPlayersLeft() {
		return super.hasPlayersLeft() && !already;
	}
	
	Runnable callback;
	boolean already;
	
	public void openInventory(Player player) {
		inMenu = true;
		Inventory inventory = Bukkit.createInventory(null, 9, "§7Choisis ton camp.");
		inventory.setContents(items.clone());
		player.closeInventory();
		player.openInventory(inventory);
	}
	@Override
	protected void onNightTurn(LGPlayer player, Runnable callback) {
		already = true;
		player.showView();
		this.callback = callback;
		openInventory(player.getPlayer());
	}
	@Override
	protected void onNightTurnTimeout(LGPlayer player) {
		closeInventory(player.getPlayer());
		player.hideView();
		//player.sendTitle("§cVous n'infectez personne", "§4Vous avez mis trop de temps à vous décider...", 80);
		player.sendActionBarMessage("§6Tu rejoins le §a§lVillage.");
		player.sendMessage("§6Tu rejoins le §a§lVillage.");
	}

	boolean inMenu;
	
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
			lgp.sendActionBarMessage("§6Tu resteras fidèle au §a§lVillage§6.");
			lgp.sendMessage("§6Tu resteras fidèle au §a§lVillage§6.");
			lgp.hideView();
			callback.run();
		}else if(item.getItemMeta().getDisplayName().equals(items[5].getItemMeta().getDisplayName())) {
			e.setCancelled(true);
			closeInventory(player);

			lgp.sendActionBarMessage("§6Tu as changé de camp.");
			lgp.sendMessage("§6Tu as changé de camp.");
			
			//On le fait aussi rejoindre le camp des loups pour le tour pendant la nuit.
			RChienLoupLG lgChienLoup = null;
			for(Role role : getGame().getRoles())
				if(role instanceof RChienLoupLG)
					lgChienLoup = (RChienLoupLG)role;
			
			if(lgChienLoup == null)
				getGame().getRoles().add(lgChienLoup = new RChienLoupLG(getGame()));
			
			lgChienLoup.join(lgp, false);
			lgp.updateOwnSkin();
			
			lgp.hideView();
			callback.run();
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
	
}
