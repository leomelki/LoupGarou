package fr.leomelki.loupgarou.roles;

import java.util.ArrayList;
import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import com.comphenix.protocol.wrappers.EnumWrappers.ItemSlot;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import com.comphenix.protocol.wrappers.WrappedDataWatcher.WrappedDataWatcherObject;
import com.comphenix.protocol.wrappers.WrappedWatchableObject;

import fr.leomelki.com.comphenix.packetwrapper.WrapperPlayServerEntityDestroy;
import fr.leomelki.com.comphenix.packetwrapper.WrapperPlayServerEntityEquipment;
import fr.leomelki.com.comphenix.packetwrapper.WrapperPlayServerEntityLook;
import fr.leomelki.com.comphenix.packetwrapper.WrapperPlayServerEntityMetadata;
import fr.leomelki.com.comphenix.packetwrapper.WrapperPlayServerSpawnEntityLiving;
import fr.leomelki.loupgarou.MainLg;
import fr.leomelki.loupgarou.classes.LGGame;
import fr.leomelki.loupgarou.classes.LGPlayer;
import fr.leomelki.loupgarou.classes.LGPlayer.LGChooseCallback;
import fr.leomelki.loupgarou.classes.LGWinType;
import fr.leomelki.loupgarou.events.LGEndCheckEvent;
import fr.leomelki.loupgarou.events.LGGameEndEvent;
import fr.leomelki.loupgarou.events.LGPlayerGotKilledEvent;
import fr.leomelki.loupgarou.events.LGPlayerKilledEvent;
import fr.leomelki.loupgarou.events.LGPlayerKilledEvent.Reason;
import fr.leomelki.loupgarou.events.LGUpdatePrefixEvent;

public class RCupidon extends Role{
	public RCupidon(LGGame game) {
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
		return "§a§lCupidon";
	}
	@Override
	public String getFriendlyName() {
		return "de "+getName();
	}
	@Override
	public String getShortDescription() {
		return "Tu gagnes avec le §a§lVillage";
	}
	@Override
	public String getDescription() {
		return "Tu gagnes avec le §a§lVillage§f. Dès le début de la partie, tu dois former un couple de deux joueurs. Leur objectif sera de survivre ensemble, car si l'un d'eux meurt, l'autre se suicidera.";
	}
	@Override
	public String getTask() {
		return "Choisis deux joueurs à mettre en couple.";
	}
	@Override
	public String getBroadcastedTask() {
		return getName()+"§9 choisit deux âmes à unir.";
	}
	
	@Override
	public int getTimeout() {
		return 15;
	}
	@Override
	public boolean hasPlayersLeft() {
		return getGame().getNight() == 1;
	}
	
	@Override
	protected void onNightTurn(LGPlayer player, Runnable callback) {
		player.showView();
		
		player.choose(new LGChooseCallback() {
			@Override
			public void callback(LGPlayer choosen) {
				if(choosen != null) {
					if(player.getCache().has("cupidon_first")) {
						LGPlayer first = player.getCache().remove("cupidon_first");
						if(first == choosen) {
							int entityId = Integer.MAX_VALUE-choosen.getPlayer().getEntityId();
							WrapperPlayServerEntityDestroy destroy = new WrapperPlayServerEntityDestroy();
							destroy.setEntityIds(new int[] {entityId});
							destroy.sendPacket(player.getPlayer());
						} else {
						//	sendHead(player, choosen);
							int entityId = Integer.MAX_VALUE-first.getPlayer().getEntityId();
							WrapperPlayServerEntityDestroy destroy = new WrapperPlayServerEntityDestroy();
							destroy.setEntityIds(new int[] {entityId});
							destroy.sendPacket(player.getPlayer());
							
							setInLove(first, choosen);
							player.sendMessage("§7§l"+first.getName()+"§9 et §7§l"+choosen.getName()+"§9 sont désormais follement amoureux.");
							player.stopChoosing();
							player.hideView();
							callback.run();
						}
					} else {
						sendHead(player, choosen);
						player.getCache().set("cupidon_first", choosen);
					}
				}
			}
		});
	}
	protected void setInLove(LGPlayer player1, LGPlayer player2) {
		player1.getCache().set("inlove", player2);
		player1.sendMessage("§9Tu tombes amoureux de §7§l"+player2.getName()+"§9, il est "+player2.getRole().getName());
		player1.sendMessage("§9§oTu peux lui parler en mettant un §e!§9 devant ton message.");
		
		player2.getCache().set("inlove", player1);
		player2.sendMessage("§9Tu tombes amoureux de §7§l"+player1.getName()+"§9, il est "+player1.getRole().getName());
		player2.sendMessage("§9§oTu peux lui parler en mettant un §e!§9 devant ton message.");
		
	/*	sendHead(player1, player2);
		sendHead(player2, player1);*/
		
		//On peut créer des cheats grâce à ça (qui permettent de savoir qui est en couple)
		player1.updatePrefix();
		player2.updatePrefix();
	}

	WrappedDataWatcherObject invisible = new WrappedDataWatcherObject(0, WrappedDataWatcher.Registry.get(Byte.class)),
							 noGravity = new WrappedDataWatcherObject(5, WrappedDataWatcher.Registry.get(Boolean.class));
	protected void sendHead(LGPlayer to, LGPlayer ofWho) {
		int entityId = Integer.MAX_VALUE-ofWho.getPlayer().getEntityId();
		WrapperPlayServerSpawnEntityLiving spawn = new WrapperPlayServerSpawnEntityLiving();
		spawn.setEntityID(entityId);
		spawn.setType(EntityType.DROPPED_ITEM);
		//spawn.setMetadata(new WrappedDataWatcher(Arrays.asList(new WrappedWatchableObject(invisible, (byte)0x20), new WrappedWatchableObject(noGravity, true))));
		Location loc = ofWho.getPlayer().getLocation();
		spawn.setX(loc.getX());
		spawn.setY(loc.getY()+1.9);
		spawn.setZ(loc.getZ());
		spawn.setHeadPitch(0);
		Location toLoc = to.getPlayer().getLocation();
		double diffX = loc.getX()-toLoc.getX(),
			   diffZ = loc.getZ()-toLoc.getZ();
		float yaw = 180-((float) Math.toDegrees(Math.atan2(diffX, diffZ)));
		
		spawn.setYaw(yaw);
		spawn.sendPacket(to.getPlayer());
		
		WrapperPlayServerEntityLook look = new WrapperPlayServerEntityLook();
		look.setEntityID(entityId);
		look.setPitch(0);
		look.setYaw(yaw);
		look.sendPacket(to.getPlayer());
		
		WrapperPlayServerEntityMetadata meta = new WrapperPlayServerEntityMetadata();
		meta.setEntityID(entityId);
		meta.setMetadata(Arrays.asList(new WrappedWatchableObject(invisible, (byte)0x20), new WrappedWatchableObject(noGravity, true)));
		meta.sendPacket(to.getPlayer());
		
		
		
		new BukkitRunnable() {
			
			@Override
			public void run() {
				WrapperPlayServerEntityEquipment equip = new WrapperPlayServerEntityEquipment();
				equip.setEntityID(entityId);
				equip.setSlot(ItemSlot.HEAD);
		        ItemStack skull = new ItemStack(Material.SUGAR);
				equip.setItem(skull);
				equip.sendPacket(to.getPlayer());
			}
		}.runTaskLater(MainLg.getInstance(), 2);
	}
	
	@Override
	protected void onNightTurnTimeout(LGPlayer player) {
		player.getCache().remove("cupidon_first");
		player.stopChoosing();
		player.hideView();
		//player.sendTitle("§cVous n'avez mis personne en couple", "§4Vous avez mis trop de temps à vous décider...", 80);
		//player.sendMessage("§9Tu n'as pas créé de couple.");
	}
	
	
	@EventHandler
	public void onPlayerKill(LGPlayerGotKilledEvent e) {
		if(e.getGame() == getGame() && e.getKilled().getCache().has("inlove") && !e.getKilled().getCache().<LGPlayer>get("inlove").isDead()) {
			LGPlayer killed = e.getKilled().getCache().get("inlove");
			LGPlayerKilledEvent event = new LGPlayerKilledEvent(getGame(), killed, Reason.LOVE);
			Bukkit.getPluginManager().callEvent(event);
			if(!event.isCancelled())
				getGame().kill(event.getKilled(), event.getReason(), false);
		}
	}
	
	@EventHandler(priority = EventPriority.HIGH)
	public void onGameEnd(LGGameEndEvent e) {
		if(e.getGame() == getGame()) {
			WrapperPlayServerEntityDestroy destroy = new WrapperPlayServerEntityDestroy();
			ArrayList<Integer> ids = new ArrayList<Integer>();
			for(LGPlayer lgp : getGame().getInGame())
				ids.add(Integer.MAX_VALUE-lgp.getPlayer().getEntityId());
			int[] intList = new int[ids.size()];
			for(int i = 0;i<ids.size();i++)
				intList[i] = ids.get(i);
			destroy.setEntityIds(intList);
			for(LGPlayer lgp : getGame().getInGame())
				destroy.sendPacket(lgp.getPlayer());
			
			for(LGPlayer lgp : getGame().getInGame())
				if(lgp.getCache().has("inlove")) {
					if(e.getWinType() == LGWinType.COUPLE) {
						if(!e.getWinners().contains(lgp))
							e.getWinners().add(lgp);
					} else {
						LGPlayer player2 = lgp.getCache().<LGPlayer>get("inlove");
						boolean winEnCouple = (lgp.getRoleType() == RoleType.LOUP_GAROU) != (player2.getRoleType() == RoleType.LOUP_GAROU) || lgp.getRoleWinType() == RoleWinType.SEUL || player2.getRoleWinType() == RoleWinType.SEUL;
						if(winEnCouple) {
							System.out.println(lgp.getName()+" ne peut pas gagner car il était en couple !");
							e.getWinners().remove(lgp);
						}
					}
				}
		}
	}
	
	@EventHandler
	public void onEndCheck(LGEndCheckEvent e) {
		if(e.getGame() == getGame()) {
			ArrayList<LGPlayer> winners = new ArrayList<LGPlayer>();
			for(LGPlayer lgp : getGame().getAlive())
				if(lgp.getRoleWinType() != RoleWinType.NONE)
					winners.add(lgp);
			if(winners.size() == 2) {
				LGPlayer player1 = winners.get(0),
						 player2 = winners.get(1);
				boolean winEnCouple = (player1.getRoleType() == RoleType.LOUP_GAROU) != (player2.getRoleType() == RoleType.LOUP_GAROU) || player1.getRoleWinType() == RoleWinType.SEUL || player2.getRoleWinType() == RoleWinType.SEUL;
				if(player1.getCache().get("inlove") == player2 && /*player1.isABadGuy() != player2.isABadGuy()*/ (player1.getRoleType() == RoleType.LOUP_GAROU) != (player2.getRoleType() == RoleType.LOUP_GAROU))
					e.setWinType(LGWinType.COUPLE);
			}
		}
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onChat(AsyncPlayerChatEvent e) {
		LGPlayer player = LGPlayer.thePlayer(e.getPlayer());
		if(player.getGame() == getGame()) {
			if(e.getMessage().startsWith("!") && player.getCache().has("inlove")) {
				player.sendMessage("§d\u2764 "+player.getName()+" §6» §f"+e.getMessage().substring(1));
				player.getCache().<LGPlayer>get("inlove").sendMessage("§d\u2764 "+player.getName()+" §6» §f"+e.getMessage().substring(1));
				e.setCancelled(true);
			}
		}
	}
	@EventHandler(priority = EventPriority.HIGH)
	public void onUpdatePrefix (LGUpdatePrefixEvent e) {
		if(e.getGame() == getGame())
			if(e.getTo().getCache().get("inlove") == e.getPlayer() || ((e.getTo() == e.getPlayer() || e.getTo().getRole() == this) && e.getPlayer().getCache().has("inlove")))
				e.setPrefix("§d\u2764 §f"+e.getPrefix());
	}
	
/*	@EventHandler
	public void onNight(LGDayEndEvent e) {
		if(e.getGame() == getGame())
			for(LGPlayer lgp : getGame().getAlive())
				if(lgp.getCache().has("inlove"))
					lgp.unMute(lgp.getCache().get("inlove"));
	}*/
	
}
