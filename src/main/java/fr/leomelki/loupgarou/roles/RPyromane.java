package fr.leomelki.loupgarou.roles;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
import fr.leomelki.loupgarou.classes.LGWinType;
import fr.leomelki.loupgarou.classes.LGPlayer.LGChooseCallback;
import fr.leomelki.loupgarou.events.LGEndCheckEvent;
import fr.leomelki.loupgarou.events.LGGameEndEvent;
import fr.leomelki.loupgarou.events.LGPlayerKilledEvent;
import fr.leomelki.loupgarou.events.LGPyromaneGasoilEvent;
import fr.leomelki.loupgarou.events.LGPlayerKilledEvent.Reason;

public class RPyromane extends Role{
	static ItemStack[] items = new ItemStack[9];
	static ItemStack cancel, nothing;
	static {
		cancel = new ItemStack(Material.IRON_NUGGET);
		ItemMeta meta = cancel.getItemMeta();
		meta.setDisplayName("§7§lAnnuler");
		meta.setLore(Arrays.asList("§8Rouvrir le menu"));
		cancel.setItemMeta(meta);
		nothing = new ItemStack(Material.IRON_NUGGET);
		meta = nothing.getItemMeta();
		meta.setDisplayName("§7§lNe rien faire");
		nothing.setItemMeta(meta);
		items[3] = new ItemStack(Material.FLINT_AND_STEEL);
		meta = items[3].getItemMeta();
		meta.setDisplayName("§e§lMettre le feu");
		meta.setLore(Arrays.asList(
				"§8Tuez les joueurs que vous avez",
				"§8Précédemment recouvert de gasoil."));
		items[3].setItemMeta(meta);
		items[5] = new ItemStack(Material.LAVA_BUCKET);
		meta = items[5].getItemMeta();
		meta.setDisplayName("§c§lRecouvrir d'essence");
		meta.setLore(Arrays.asList(
				"§8Recouvres deux joueurs d'essence"));
		items[5].setItemMeta(meta);
	}

	public RPyromane(LGGame game) {
		super(game);
	}

	@Override
	public String getName() {
		return "§6§lPyromane";
	}

	@Override
	public String getFriendlyName() {
		return "du "+getName();
	}

	@Override
	public String getShortDescription() {
		return "Tu gagnes §7§lSEUL";
	}

	@Override
	public String getDescription() {
		return "Tu gagnes §7§lSEUL§f. Chaque nuit, tu peux recouvrir de gasoil deux joueurs au choix, ou immoler tous ceux que tu as précédemment visités. Les joueurs sauront qu'ils ont été recouverts de gasoil.";
	}

	@Override
	public String getTask() {
		return "Que veux-tu faire cette nuit ?";
	}

	@Override
	public String getBroadcastedTask() {
		return "Le "+getName()+"§9 joue avec une allumette...";
	}
	@Override
	public RoleType getType() {
		return RoleType.NEUTRAL;
	}
	@Override
	public RoleWinType getWinType() {
		return RoleWinType.SEUL;
	}
	
	@Override
	public int getTimeout() {
		return 30;
	}
	
	Runnable callback;
	
	public void openInventory(Player player) {
		inMenu = true;
		Inventory inventory = Bukkit.createInventory(null, 9, "§7Que veux-tu faire ?");
		ItemStack[] content = items.clone();
		LGPlayer lgp = LGPlayer.thePlayer(player);
		if(!lgp.getCache().has("pyromane_essence"))
			lgp.getCache().set("pyromane_essence", new ArrayList<>());
		if(lgp.getCache().<List<LGPlayer>>get("pyromane_essence").size() == 0)
			content[3] = nothing;
		inventory.setContents(content);
		player.closeInventory();
		player.openInventory(inventory);
	}
	@Override
	protected void onNightTurn(LGPlayer player, Runnable callback) {
		first = null;
		player.showView();
		this.callback = callback;
		openInventory(player.getPlayer());
	}
	@Override
	protected void onNightTurnTimeout(LGPlayer player) {
		if(first != null) {
			List<LGPlayer> liste = player.getCache().<List<LGPlayer>>get("pyromane_essence");
			LGPyromaneGasoilEvent event = new LGPyromaneGasoilEvent(getGame(), first);
			Bukkit.getPluginManager().callEvent(event);
			if(event.isCancelled())
				player.sendMessage("§7§l"+event.getPlayer().getName()+"§c est immunisé.");
			else {
				event.getPlayer().sendMessage("§6Tu es recouvert de gasoil...");
				liste.add(event.getPlayer());
			}
		}
		player.getPlayer().getInventory().setItem(8, null);
		player.stopChoosing();
		closeInventory(player.getPlayer());
		player.getPlayer().updateInventory();
		player.hideView();
		player.sendMessage("§6Tu n'as rien fait cette nuit.");
	}

	boolean inMenu = false;
	LGPlayer first;
	
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
		if(item.getItemMeta().getDisplayName().equals(nothing.getItemMeta().getDisplayName())) {
			lgp.stopChoosing();
			closeInventory(player);
			lgp.hideView();
			lgp.sendMessage("§6Tu n'as rien fait cette nuit.");
			callback.run();
		}else if(item.getItemMeta().getDisplayName().equals(items[3].getItemMeta().getDisplayName())) {
			e.setCancelled(true);
			closeInventory(player);
			if(lgp.getCache().<List<LGPlayer>>get("pyromane_essence").size() != 0) {
				List<LGPlayer> liste = lgp.getCache().<List<LGPlayer>>get("pyromane_essence");
				for(LGPlayer scndPlayer : liste) {
					if(!scndPlayer.isDead() && scndPlayer.getPlayer() != null) {
						getGame().kill(scndPlayer, Reason.PYROMANE);
					}
				}
				liste.clear();
				lgp.sendMessage("§6§lTu as décidé de brûler tes victimes ce soir.");
				lgp.sendActionBarMessage("§6Tes victimes brûleront ce soir.");
			}else
				lgp.sendMessage("§6§lPersonne n'a pris feu.");
			lgp.hideView();
			callback.run();
		}else if(item.getItemMeta().getDisplayName().equals(items[5].getItemMeta().getDisplayName())) {
			e.setCancelled(true);
			closeInventory(player);
			player.getInventory().setItem(8, cancel);
			player.updateInventory();
			//Pour éviter les missclick
			WrapperPlayServerHeldItemSlot held = new WrapperPlayServerHeldItemSlot();
			held.setSlot(0);
			held.sendPacket(player);
			lgp.sendMessage("§6Choisis deux joueurs à recouvrir de gasoil.");
			lgp.choose(new LGChooseCallback() {
				@Override
				public void callback(LGPlayer choosen) {
					if(choosen != null) {
						if(choosen == first) {
							lgp.sendMessage("§cTu as déjà versé du gasoil sur §7§l"+choosen.getName()+"§6.");
							return;
						}
						List<LGPlayer> liste = lgp.getCache().<List<LGPlayer>>get("pyromane_essence");
						if(liste.contains(choosen)) {
							lgp.sendMessage("§7§l"+choosen.getName()+"§c est déjà recouvert de gasoil.");
							return;
						}
						if(first == choosen) {
							lgp.sendMessage("§cVous avez déjà sélectionné §7§l"+choosen.getName()+"§c.");
							return;
						}
						player.getInventory().setItem(8, null);
						player.updateInventory();
						lgp.sendMessage("§6Tu as versé du gasoil sur §7§l"+choosen.getName()+"§6.");
						lgp.sendActionBarMessage("§6§7§l"+choosen.getName()+"§6 est recouvert de gasoil");
						if(first != null || getGame().getAlive().size() == 2) {
							lgp.hideView();
							lgp.stopChoosing();
							LGPyromaneGasoilEvent event = new LGPyromaneGasoilEvent(getGame(), choosen);
							Bukkit.getPluginManager().callEvent(event);
							if(event.isCancelled())
								lgp.sendMessage("§7§l"+event.getPlayer().getName()+"§c est immunisée.");
							else {
								event.getPlayer().sendMessage("§6Tu es recouvert de gasoil...");
								liste.add(event.getPlayer());
							}
							if(first != null) {
								event = new LGPyromaneGasoilEvent(getGame(), first);
								Bukkit.getPluginManager().callEvent(event);
								if(event.isCancelled())
									lgp.sendMessage("§7§l"+event.getPlayer().getName()+"§c est immunisée.");
								else {
									event.getPlayer().sendMessage("§6Tu es recouvert de gasoil...");
									liste.add(event.getPlayer());
								}
							}
							callback.run();
						} else {
							lgp.sendMessage("§6Choisis un deuxième joueur à recouvrir de gasoil.");
							first = choosen;
						}
					}
				}
			}, lgp);
		}
	}
	@EventHandler
	public void onClick(PlayerInteractEvent e) {
		Player player = e.getPlayer();
		LGPlayer lgp = LGPlayer.thePlayer(player);
		if(lgp.getRole() == this) {
			if(e.getItem() != null && e.getItem().hasItemMeta() && e.getItem().getItemMeta().getDisplayName().equals(cancel.getItemMeta().getDisplayName())) {
				e.setCancelled(true);
				player.getInventory().setItem(8, null);
				player.updateInventory();
				lgp.stopChoosing();
				openInventory(player);
			}
		}
	}
	@EventHandler
	public void onKilled(LGPlayerKilledEvent e) {
		if(e.getGame() == getGame())
			for(LGPlayer lgp : getPlayers())
				if(lgp.getCache().has("pyromane_essence")) {
					List<LGPlayer> liste = lgp.getCache().<List<LGPlayer>>get("pyromane_essence");
					if(liste.contains(e.getKilled()))//Au cas où le mec soit rez
						liste.remove(e.getKilled());
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
	
	//Win condition
	
	@EventHandler
	public void onEndgameCheck(LGEndCheckEvent e) {
		if(e.getGame() == getGame() && e.getWinType() == LGWinType.SOLO) {
			if(getPlayers().size() > 0)
				e.setWinType(LGWinType.PYROMANE);
		}
	}
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onEndGame(LGGameEndEvent e) {
		if(e.getWinType() == LGWinType.PYROMANE) {
			e.getWinners().clear();
			e.getWinners().addAll(getPlayers());
		}
	}
	
}
