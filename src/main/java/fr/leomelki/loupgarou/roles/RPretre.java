package fr.leomelki.loupgarou.roles;

import java.util.ArrayList;
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
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import com.comphenix.protocol.wrappers.WrappedDataWatcher.WrappedDataWatcherObject;
import com.comphenix.protocol.wrappers.WrappedWatchableObject;

import fr.leomelki.com.comphenix.packetwrapper.WrapperPlayServerEntityMetadata;
import fr.leomelki.com.comphenix.packetwrapper.WrapperPlayServerHeldItemSlot;
import fr.leomelki.loupgarou.MainLg;
import fr.leomelki.loupgarou.classes.LGCustomItems;
import fr.leomelki.loupgarou.classes.LGGame;
import fr.leomelki.loupgarou.classes.LGPlayer;
import fr.leomelki.loupgarou.classes.LGPlayer.LGChooseCallback;
import fr.leomelki.loupgarou.events.LGPreDayStartEvent;
import fr.leomelki.loupgarou.utils.VariousUtils;

public class RPretre extends Role{
	static ItemStack[] items = new ItemStack[9];
	static {
		items[3] = new ItemStack(Material.IRON_NUGGET);
		ItemMeta meta = items[3].getItemMeta();
		meta.setDisplayName("§7§lNe rien faire");
		meta.setLore(Arrays.asList("§8Passez votre tour"));
		items[3].setItemMeta(meta);
		items[5] = new ItemStack(Material.ROTTEN_FLESH);
		meta = items[5].getItemMeta();
		meta.setDisplayName("§2§lRessuciter");
		meta.setLore(Arrays.asList(
				"§8Tu peux ressusciter un §a§lVillageois",
				"§8mort précédemment pendant la partie."));
		items[5].setItemMeta(meta);
	}

	public RPretre(LGGame game) {
		super(game);
	}

	@Override
	public String getName() {
		return "§a§lPrêtre";
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
		return "Tu gagnes avec le §a§lVillage§f. Une fois dans la partie, tu peux ressusciter parmi les morts un membre du §a§lVillage§f, qui reviendra à la vie sans ses pouvoirs.";
	}

	@Override
	public String getTask() {
		return "Veux-tu ressusciter un allié défunt ?";
	}

	@Override
	public String getBroadcastedTask() {
		return "Le "+getName()+"§9 récite ses ouvrages...";
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
		return 30;
	}
	@Override
	public boolean hasPlayersLeft() {
		for(LGPlayer pretre : getPlayers())
			for(LGPlayer lgp : getGame().getInGame())
				if(lgp.isDead() && (lgp.getRoleType() == RoleType.VILLAGER || lgp.getRoleType() == pretre.getRoleType()))
					return super.hasPlayersLeft();
		return false;
	}
	
	Runnable callback;
	
	public void openInventory(Player player) {
		inMenu = true;
		Inventory inventory = Bukkit.createInventory(null, 9, "§7Veux-tu réssuciter quelqu'un ?");
		inventory.setContents(items.clone());
		player.closeInventory();
		player.openInventory(inventory);
	}
	WrappedDataWatcherObject invisible = new WrappedDataWatcherObject(0, WrappedDataWatcher.Registry.get(Byte.class));
	@SuppressWarnings("deprecation")
	@Override
	protected void onNightTurn(LGPlayer player, Runnable callback) {
		player.showView();
		for(LGPlayer lgp : getGame().getInGame())
			if(lgp.isDead() && (lgp.getRoleType() == RoleType.VILLAGER || lgp.getRoleType() == player.getRoleType())){
				if(lgp.getPlayer() != null) {
					player.getPlayer().showPlayer(lgp.getPlayer());
					WrapperPlayServerEntityMetadata meta = new WrapperPlayServerEntityMetadata();
					meta.setEntityID(lgp.getPlayer().getEntityId());
					meta.setMetadata(Arrays.asList(new WrappedWatchableObject(invisible, (byte)0)));
					meta.sendPacket(player.getPlayer());
				}
			}else
				player.getPlayer().hidePlayer(lgp.getPlayer());
		this.callback = callback;
		openInventory(player.getPlayer());
	}
	@Override
	protected void onNightTurnTimeout(LGPlayer player) {
		player.getPlayer().getInventory().setItem(8, null);
		player.stopChoosing();
		closeInventory(player.getPlayer());
		player.canSelectDead = false;
		player.getPlayer().updateInventory();
		hidePlayers(player);
		//player.sendTitle("§cVous n'infectez personne", "§4Vous avez mis trop de temps à vous décider...", 80);
		player.sendMessage("§6Tu n'as rien fait cette nuit.");
	}

	private void hidePlayers(LGPlayer player) {
		if(player.getPlayer() != null) {
			for(LGPlayer lgp : getGame().getInGame())
				if(lgp.getPlayer() != null && lgp != player)
					player.getPlayer().hidePlayer(lgp.getPlayer());
		}
	}

	boolean inMenu = false;
	ArrayList<LGPlayer> ressucited = new ArrayList<LGPlayer>();
	
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
			hidePlayers(lgp);
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
			lgp.sendMessage("§6Choisissez qui réssuciter.");
			lgp.canSelectDead = true;
			lgp.choose(new LGChooseCallback() {
				
				@Override
				public void callback(LGPlayer choosen) {
					if(choosen != null) {
						if(!choosen.isDead())
							lgp.sendMessage("§7§l"+choosen.getName()+"§c n'est pas mort.");
						else if(lgp.getRoleType() == RoleType.LOUP_GAROU && choosen.getRoleType() == RoleType.NEUTRAL) {
							lgp.sendMessage("§7§l"+choosen.getName()+"§c ne faisait ni partie du §a§lVillage§6 ni des §c§lLoups§6.");
						}else if(lgp.getRoleType() != RoleType.LOUP_GAROU && choosen.getRoleType() != RoleType.VILLAGER) {
							lgp.sendMessage("§7§l"+choosen.getName()+"§c ne faisait pas partie du §a§lVillage§6.");
						} else {
							player.getInventory().setItem(8, null);
							player.updateInventory();
							lgp.stopChoosing();
							lgp.canSelectDead = false;
							lgp.sendMessage("§6Tu as ramené §7§l"+choosen.getName()+"§6 à la vie.");
							lgp.sendActionBarMessage("§7§l"+choosen.getName()+"§6 sera réssucité");
							
							
							ressucited.add(choosen);
							getPlayers().remove(lgp);//Pour éviter qu'il puisse sauver plusieurs personnes.
							choosen.sendMessage("§6Tu vas être réssucité en tant que §a§lVillageois§6.");
							hidePlayers(lgp);
							lgp.hideView();
							callback.run();
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
			if(e.getItem() != null && e.getItem().hasItemMeta() && e.getItem().getItemMeta().getDisplayName().equals(items[3].getItemMeta().getDisplayName())) {
				e.setCancelled(true);
				player.getInventory().setItem(8, null);
				player.updateInventory();
				lgp.stopChoosing();
				lgp.sendMessage("§6Tu n'as rien fait cette nuit.");
				lgp.canSelectDead = false;
				hidePlayers(lgp);
				callback.run();
			}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onDayStart(LGPreDayStartEvent e) {
		if(e.getGame() == getGame())
			if(ressucited.size() > 0) {
				for(LGPlayer lgp : ressucited) {
					if(lgp.getPlayer() == null || !lgp.isDead())
						continue;
					lgp.setDead(false);
					lgp.getCache().reset();
					RVillageois villagers = null;
					for(Role role : getGame().getRoles())
						if(role instanceof RVillageois)
							villagers = (RVillageois)role;
					if(villagers == null)
						getGame().getRoles().add(villagers = new RVillageois(getGame()));
					villagers.join(lgp, false);//Le joueur réssucité rejoint les villageois.
					lgp.setRole(villagers);
					lgp.getPlayer().removePotionEffect(PotionEffectType.INVISIBILITY);
					lgp.getPlayer().getInventory().setHelmet(null);
					lgp.getPlayer().updateInventory();
					LGCustomItems.updateItem(lgp);

					lgp.joinChat(getGame().getDayChat());//Pour qu'il ne parle plus dans le chat des morts (et ne le voit plus) et qu'il parle dans le chat des vivants
					VariousUtils.setWarning(lgp.getPlayer(), true);
					
					getGame().updateRoleScoreboard();
					
					getGame().broadcastMessage("§7§l"+lgp.getName()+"§6 a été ressuscité cette nuit.");

					for(LGPlayer player : getGame().getInGame())
						if(player.getPlayer() != null && player != lgp) {
							player.getPlayer().showPlayer(lgp.getPlayer());
						}
				}
				ressucited.clear();
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
