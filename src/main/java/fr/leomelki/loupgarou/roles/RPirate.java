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
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import fr.leomelki.com.comphenix.packetwrapper.WrapperPlayServerHeldItemSlot;
import fr.leomelki.loupgarou.MainLg;
import fr.leomelki.loupgarou.classes.LGGame;
import fr.leomelki.loupgarou.classes.LGPlayer;
import fr.leomelki.loupgarou.classes.LGPlayer.LGChooseCallback;
import fr.leomelki.loupgarou.events.LGPlayerKilledEvent;
import fr.leomelki.loupgarou.events.LGPlayerKilledEvent.Reason;

public class RPirate extends Role{
	static ItemStack[] items = new ItemStack[9];
	static {
		items[3] = new ItemStack(Material.IRON_NUGGET);
		ItemMeta meta = items[3].getItemMeta();
		meta.setDisplayName("§7§lNe rien faire");
		meta.setLore(Arrays.asList("§8Passez votre tour"));
		items[3].setItemMeta(meta);
		items[5] = new ItemStack(Material.ROTTEN_FLESH);
		meta = items[5].getItemMeta();
		meta.setDisplayName("§6§lPrendre un otage");
		meta.setLore(Arrays.asList(
				"§8Tu peux prendre un joueur en otage",
				"§8Si tu meurs du vote, il mourra à ta place."));
		items[5].setItemMeta(meta);
	}

	public RPirate(LGGame game) {
		super(game);
	}

	@Override
	public String getName() {
		return "§a§lPirate";
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
		return "Tu gagnes avec le §a§lVillage§f. Une fois dans la partie, tu peux prendre en otage un autre joueur. Si tu es désigné à l'issue du vote de jour, ton §lotage§f mourra à ta place et ton rôle sera dévoilé au reste du village";
	}

	@Override
	public String getTask() {
		return "Veux-tu prendre quelqu'un en otage ?";
	}

	@Override
	public String getBroadcastedTask() {
		return "Le "+getName()+"§9 aiguise son crochet...";
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
	
	Runnable callback;
	
	public void openInventory(Player player) {
		inMenu = true;
		Inventory inventory = Bukkit.createInventory(null, 9, "§7Veux-tu prendre un otage ?");
		inventory.setContents(items.clone());
		player.closeInventory();
		player.openInventory(inventory);
	}
	@Override
	protected void onNightTurn(LGPlayer player, Runnable callback) {
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
		}else if(item.getItemMeta().getDisplayName().equals(items[5].getItemMeta().getDisplayName())) {
			e.setCancelled(true);
			closeInventory(player);
			player.getInventory().setItem(8, items[3]);
			player.updateInventory();
			//Pour éviter les missclick
			WrapperPlayServerHeldItemSlot held = new WrapperPlayServerHeldItemSlot();
			held.setSlot(0);
			held.sendPacket(player);
			lgp.sendMessage("§6Choisissez votre otage.");
			lgp.choose(new LGChooseCallback() {
				
				@Override
				public void callback(LGPlayer choosen) {
					if(choosen != null) {
						player.getInventory().setItem(8, null);
						player.updateInventory();
						lgp.stopChoosing();
						lgp.sendMessage("§6Tu as pris §7§l"+choosen.getName()+"§6 en otage.");
						lgp.sendActionBarMessage("§7§l"+choosen.getName()+"§6 est ton otage");
						lgp.getCache().set("pirate_otage", choosen);
						choosen.getCache().set("pirate_otage_d", lgp);
						getPlayers().remove(lgp);//Pour éviter qu'il puisse prendre plusieurs otages
						choosen.sendMessage("§7§l"+lgp.getName()+"§6 t'a pris en otage, il est "+getName()+"§6.");
						lgp.hideView();
						callback.run();
					}
				}
			}, lgp);
		}
	}
	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerKilled(LGPlayerKilledEvent e) {
		if(e.getGame() == getGame() && e.getReason() == Reason.VOTE)
			if(e.getKilled().getCache().has("pirate_otage")) {
				LGPlayer otage = e.getKilled().getCache().remove("pirate_otage");
				if(!otage.isDead() && otage.getCache().get("pirate_otage_d") == e.getKilled()) {
					getGame().broadcastMessage("§7§l"+e.getKilled().getName()+"§6 est "+getName()+"§6, c'est son otage qui va mourir.");
					e.setKilled(otage);
					e.setReason(Reason.PIRATE);
				}
			}
	}
	
	@EventHandler
	public void onClick(PlayerInteractEvent e) {
		Player player = e.getPlayer();
		LGPlayer lgp = LGPlayer.thePlayer(player);
		if(lgp.getRole() == this) {
			if(e.getItem() != null && e.getItem().hasItemMeta() && e.getItem().getItemMeta().getDisplayName().equals(items[3].getItemMeta().getDisplayName())) {
				e.setCancelled(true);
				player.getInventory().setItem(8, null);
				player.updateInventory();
				lgp.stopChoosing();
				lgp.sendMessage("§6Tu n'as rien fait cette nuit.");
				lgp.hideView();
				callback.run();
			}
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
