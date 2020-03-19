package fr.leomelki.loupgarou.classes;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.StringJoiner;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_15_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftPlayer;
import org.bukkit.entity.EntityType;
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
import fr.leomelki.loupgarou.classes.LGGame.TextGenerator;
import fr.leomelki.loupgarou.classes.LGPlayer.LGChooseCallback;
import fr.leomelki.loupgarou.events.LGVoteLeaderChange;
import fr.leomelki.loupgarou.utils.VariousUtils;
import lombok.Getter;
import net.minecraft.server.v1_15_R1.DataWatcher;
import net.minecraft.server.v1_15_R1.DataWatcherObject;
import net.minecraft.server.v1_15_R1.DataWatcherRegistry;
import net.minecraft.server.v1_15_R1.Entity;
import net.minecraft.server.v1_15_R1.EntityArmorStand;
import net.minecraft.server.v1_15_R1.IChatBaseComponent;
import net.minecraft.server.v1_15_R1.PacketPlayOutEntityMetadata;

public class LGVote {
	@Getter LGPlayer choosen;
	private int timeout, initialTimeout, littleTimeout;
	private Runnable callback;
	private final LGGame game;
	@Getter private List<LGPlayer> participants, viewers;
	private final TextGenerator generator;
	@Getter private final HashMap<LGPlayer, List<LGPlayer>> votes = new HashMap<LGPlayer, List<LGPlayer>>();
	private int votesSize = 0;
	private LGPlayer mayor;
	private ArrayList<LGPlayer> latestTop = new ArrayList<LGPlayer>();
	private final boolean positiveVote, randomIfEqual;
	@Getter private boolean mayorVote;
    private boolean ended;
	public LGVote(int timeout, int littleTimeout, LGGame game, boolean positiveVote, boolean randomIfEqual, TextGenerator generator) {
		this.littleTimeout = littleTimeout;
		this.initialTimeout = timeout;
		this.timeout = timeout;
		this.game = game;
		this.generator = generator;
		this.positiveVote = positiveVote;
		this.randomIfEqual = randomIfEqual;
	}
	public void start(List<LGPlayer> participants, List<LGPlayer> viewers, Runnable callback) {
		this.callback = callback;
		this.participants = participants;
		this.viewers = viewers;
		game.wait(timeout, this::end, generator);
		for(LGPlayer player : participants)
			player.choose(getChooseCallback(player));
	}
	public void start(List<LGPlayer> participants, List<LGPlayer> viewers, Runnable callback, LGPlayer mayor) {
		this.callback = callback;
		this.participants = participants;
		this.viewers = viewers;
		this.mayor = mayor;
		game.wait(timeout, this::end, generator);
		for(LGPlayer player : participants)
			player.choose(getChooseCallback(player));
	}
    private static DataWatcherObject<Optional<IChatBaseComponent>> az;
    private static DataWatcherObject<Boolean> aA;
    private static DataWatcherObject<Byte> T;
    private static final EntityArmorStand eas = new EntityArmorStand(null, 0, 0, 0);
    static {
    	try {
    		Field f = Entity.class.getDeclaredField("az");
    		f.setAccessible(true);
	    	az = (DataWatcherObject<Optional<IChatBaseComponent>>) f.get(null);
	    	f = Entity.class.getDeclaredField("aA");
    		f.setAccessible(true);
	    	aA = (DataWatcherObject<Boolean>) f.get(null);
	    	f = Entity.class.getDeclaredField("T");
    		f.setAccessible(true);
	    	T = (DataWatcherObject<Byte>) f.get(null);
    	}catch(Exception err) {
    		err.printStackTrace();
    	}
    }
	private void end() {
		ended = true;
		for(LGPlayer lgp : viewers)
			showVoting(lgp, null);
		for(LGPlayer lgp : votes.keySet())
			updateVotes(lgp, true);
		int max = 0;
		boolean equal = false;
		for(Entry<LGPlayer, List<LGPlayer>> entry : votes.entrySet())
			if(entry.getValue().size() > max) {
				equal = false;
				max = entry.getValue().size();
				choosen = entry.getKey();
			}else if(entry.getValue().size() == max)
				equal = true;
		for(LGPlayer player : participants) {
			player.getCache().remove("vote");
			player.stopChoosing();
		}
		if(equal)
			choosen = null;
		if(equal && mayor == null && randomIfEqual) {
			ArrayList<LGPlayer> choosable = new ArrayList<LGPlayer>();
			for(Entry<LGPlayer, List<LGPlayer>> entry : votes.entrySet())
				if(entry.getValue().size() == max)
					choosable.add(entry.getKey());
			choosen = choosable.get(game.getRandom().nextInt(choosable.size()));
		}
		
		if(equal && mayor != null && max != 0) {
			for(LGPlayer player : viewers)
				player.sendMessage("§9Égalité, le §5§lCapitaine§9 va départager les votes.");
			mayor.sendMessage("§6Tu dois choisir qui va mourir.");

			ArrayList<LGPlayer> choosable = new ArrayList<LGPlayer>();
			for(Entry<LGPlayer, List<LGPlayer>> entry : votes.entrySet())
				if(entry.getValue().size() == max)
					choosable.add(entry.getKey());
			
			for(int i = 0;i<choosable.size();i++) {
				LGPlayer lgp = choosable.get(i);
				showArrow(mayor, lgp, -mayor.getPlayer().getEntityId()-i);
			}
			
			StringJoiner sj = new StringJoiner(", ");
			for(int i = 0;i<choosable.size()-1;i++)
				sj.add(choosable.get(0).getName());
			//mayor.sendTitle("§6C'est à vous de délibérer", "Faut-il tuer "+sj+" ou "+choosable.get(choosable.size()-1).getName()+" ?", 100);
			ArrayList<LGPlayer> blackListed = new ArrayList<LGPlayer>();
			for(LGPlayer player : participants)
				if(!choosable.contains(player))
					blackListed.add(player);
				else {
					VariousUtils.setWarning(player.getPlayer(), true);
					//player.sendMessage("§4§lVous êtes un des principaux suspects ! Défendez vous !");
					//player.sendTitle("§4§lDéfendez vous !", "§cVous êtes l'un des principaux suspects", 100);
				}
			mayorVote = true;
			game.wait(30, ()->{
				for(LGPlayer player : participants)
					if(choosable.contains(player))
						VariousUtils.setWarning(player.getPlayer(), false);

				for(int i = 0;i<choosable.size();i++) {
					LGPlayer lgp = choosable.get(i);
					showArrow(mayor, null, -mayor.getPlayer().getEntityId()-i);
				}
				//Choix au hasard d'un joueur si personne n'a été désigné
				choosen = choosable.get(game.getRandom().nextInt(choosable.size()));
				callback.run();
			}, (player, secondsLeft)->{
				timeout = secondsLeft;
				return mayor == player ? "§6Il te reste §e"+secondsLeft+" seconde"+(secondsLeft > 1 ? "s" : "")+"§6 pour délibérer" : "§6Le §5§lCapitaine§6 délibère (§e"+secondsLeft+" s§6)";
			});
			mayor.choose(new LGChooseCallback() {
				
				@Override
				public void callback(LGPlayer choosen) {
					if(choosen != null) {
						if(blackListed.contains(choosen))
							mayor.sendMessage("§4§oCe joueur n'est pas concerné par le choix.");
						else {
							for(LGPlayer player : participants)
								if(choosable.contains(player))
									VariousUtils.setWarning(player.getPlayer(), false);

							for(int i = 0;i<choosable.size();i++) {
								LGPlayer lgp = choosable.get(i);
								showArrow(mayor, null, -mayor.getPlayer().getEntityId()-i);
							}
							game.cancelWait();
							LGVote.this.choosen = choosen;
							callback.run();
						}
					}
				}
			});
		} else {
			game.cancelWait();
			callback.run();
		}
		
	}
	public LGChooseCallback getChooseCallback(LGPlayer who) {
		return new LGChooseCallback() {
			
			@Override
			public void callback(LGPlayer choosen) {
				if(choosen != null)
					vote(who, choosen);
			}	
		};
	}
	public void vote(LGPlayer voter, LGPlayer voted) {
		if(voted == voter.getCache().get("vote"))
			voted = null;
		
		if(voted != null && voter.getPlayer() != null)
			votesSize++;
		if(voter.getCache().has("vote"))
			votesSize--;
		
		if(votesSize == participants.size() && timeout > littleTimeout) {
			votesSize = 999;
			game.wait(littleTimeout, initialTimeout, this::end, generator);
		}
		String italic = game.isDay() ? "" : "§o";
		boolean changeVote = false;
		if(voter.getCache().has("vote")) {//On enlève l'ancien vote
			LGPlayer devoted = voter.getCache().get("vote");
			if(votes.containsKey(devoted)) {
				List<LGPlayer> voters = votes.get(devoted);
				if(voters != null) {
					voters.remove(voter);
					if(voters.size() == 0)
						votes.remove(devoted);
				}
			}
			voter.getCache().remove("vote");
			updateVotes(devoted);
			changeVote = true;
		}
		
		if(voted != null) {//Si il vient de voter, on ajoute le nouveau vote
			//voter.sendTitle("", "§7Tu as voté pour §7§l"+voted.getName(), 40);
			if(votes.containsKey(voted))
				votes.get(voted).add(voter);
			else
				votes.put(voted, new ArrayList<LGPlayer>(Arrays.asList(voter)));
			voter.getCache().set("vote", voted);
			updateVotes(voted);
		}
		
		if(voter.getPlayer() != null) {
			showVoting(voter, voted);
			String message;
			if(voted != null) {
				if(changeVote) {
					message = "§7§l"+voter.getName()+"§6 a changé son vote pour §7§l"+voted.getName()+"§6.";
					voter.sendMessage("§6Tu as changé de vote pour §7§l"+voted.getName()+"§6.");
				} else {
					message = "§7§l"+voter.getName()+"§6 a voté pour §7§l"+voted.getName()+"§6.";
					voter.sendMessage("§6Tu as voté pour §7§l"+voted.getName()+"§6.");
				}
			} else {
				message = "§7§l"+voter.getName()+"§6 a annulé son vote.";
				voter.sendMessage("§6Tu as annulé ton vote.");
			}
			
			for(LGPlayer player : viewers)
				if(player != voter)
					player.sendMessage(message);
		}
	}
	
	public List<LGPlayer> getVotes(LGPlayer voted){
		return votes.containsKey(voted) ? votes.get(voted) : new ArrayList<LGPlayer>(0);
	}
	
	private void updateVotes(LGPlayer voted) {
		updateVotes(voted, false);
	}
	private void updateVotes(LGPlayer voted, boolean kill) {
		int entityId = Integer.MIN_VALUE+voted.getPlayer().getEntityId();
		WrapperPlayServerEntityDestroy destroy = new WrapperPlayServerEntityDestroy();
		destroy.setEntityIds(new int[] {entityId});
		for(LGPlayer lgp : viewers)
			destroy.sendPacket(lgp.getPlayer());
		
		if(!kill) {
			int max = 0;
			for(Entry<LGPlayer, List<LGPlayer>> entry : votes.entrySet())
				if(entry.getValue().size() > max)
					max = entry.getValue().size();
			ArrayList<LGPlayer> last = latestTop;
			latestTop = new ArrayList<LGPlayer>();
			for(Entry<LGPlayer, List<LGPlayer>> entry : votes.entrySet())
				if(entry.getValue().size() == max)
					latestTop.add(entry.getKey());
			Bukkit.getPluginManager().callEvent(new LGVoteLeaderChange(game, this, last, latestTop));
		}
		
		if(votes.containsKey(voted) && !kill) {
			Location loc = voted.getPlayer().getLocation();

			WrapperPlayServerSpawnEntityLiving spawn = new WrapperPlayServerSpawnEntityLiving();
			spawn.setEntityID(entityId);
			spawn.setType(EntityType.DROPPED_ITEM);
			//spawn.setMetadata(new WrappedDataWatcher(Arrays.asList(new WrappedWatchableObject(0, (byte)0x20), new WrappedWatchableObject(5, true))));
			spawn.setX(loc.getX());
			spawn.setY(loc.getY()+0.3);
			spawn.setZ(loc.getZ());
			

			int votesNbr = votes.get(voted).size();
			/*WrapperPlayServerEntityMetadata meta = new WrapperPlayServerEntityMetadata();
			meta.setEntityID(entityId);
			meta.setMetadata(Arrays.asList(new WrappedWatchableObject(invisible, (byte)0x20), new WrappedWatchableObject(noGravity, true), new WrappedWatchableObject(customNameVisible, true), new WrappedWatchableObject(customName, IChatBaseComponent.ChatSerializer.b("§6§l"+votesNbr+"§e vote"+(votesNbr > 1 ? "s" : "")))));
			*/
			DataWatcher datawatcher = new DataWatcher(eas);
			datawatcher.register(T, (byte)0x20);
	        datawatcher.register(az, Optional.ofNullable(IChatBaseComponent.ChatSerializer.a("{\"text\":\"§6§l"+votesNbr+"§e vote"+(votesNbr > 1 ? "s" : "")+"\"}")));
	        datawatcher.register(aA, true);
			PacketPlayOutEntityMetadata meta = new PacketPlayOutEntityMetadata(entityId, datawatcher, true);
			
			for(LGPlayer lgp : viewers) {
				spawn.sendPacket(lgp.getPlayer());
				((CraftPlayer)lgp.getPlayer()).getHandle().playerConnection.sendPacket(meta);
			}
			
			
		/*	EntityArmorStand ea = new EntityArmorStand(((CraftWorld)loc.getWorld()).getHandle(), loc.getX(), loc.getY()+0.3, loc.getZ());
			ea.setPosition(loc.getX(), loc.getY()+0.3, loc.getZ());
			ea.setInvisible(true);
			ea.setCustomNameVisible(true);
			int votesNbr = votes.get(voted).size();
			ea.setCustomName((IChatBaseComponent) WrappedChatComponent.fromText("§6§l"+votesNbr+"§e vote"+(votesNbr > 1 ? "s" : "")).getHandle());
			
			PacketPlayOutSpawnEntityLiving spawn = new PacketPlayOutSpawnEntityLiving(ea);
			try {
				Field field = spawn.getClass().getDeclaredField("a");
				field.setAccessible(true);
				field.set(spawn, entityId);
			} catch (Exception e) {
				e.printStackTrace();
			}*/
		/*	WrapperPlayServerSpawnEntityLiving spawn = new WrapperPlayServerSpawnEntityLiving();
			spawn.setEntityID(entityId);
			spawn.setType(EntityType.ARMOR_STAND);
			WrappedDataWatcher meta = new WrappedDataWatcher();
			meta.setObject(0, (byte)0x20);
			meta.setObject(2, "§6§l"+votes.get(voted)+"§e votes");
		//	meta.setObject(3, true);
			spawn.setMetadata(meta);
			Location loc = voted.getPlayer().getLocation();
			spawn.setX(loc.getX());
			spawn.setY(loc.getY()+0.3);
			spawn.setZ(loc.getZ());*/
		/*	for(LGPlayer lgp : viewers)
				((CraftPlayer)lgp.getPlayer()).getHandle().playerConnection.sendPacket(spawn);*/
			//	spawn.sendPacket(lgp.getPlayer());
		}
	}
	WrappedDataWatcherObject invisible = new WrappedDataWatcherObject(0, WrappedDataWatcher.Registry.get(Byte.class)),
							 noGravity = new WrappedDataWatcherObject(5, WrappedDataWatcher.Registry.get(Boolean.class)),
							 customNameVisible = new WrappedDataWatcherObject(3, WrappedDataWatcher.Registry.get(Boolean.class)),
							 customName = new WrappedDataWatcherObject(2, WrappedDataWatcher.Registry.get(IChatBaseComponent.class)),
							 item = new WrappedDataWatcherObject(7, WrappedDataWatcher.Registry.get(net.minecraft.server.v1_15_R1.ItemStack.class));
	private void showVoting(LGPlayer to, LGPlayer ofWho) {
		int entityId = -to.getPlayer().getEntityId();
		WrapperPlayServerEntityDestroy destroy = new WrapperPlayServerEntityDestroy();
		destroy.setEntityIds(new int[] {entityId});
		destroy.sendPacket(to.getPlayer());
		if(ofWho != null) {
			WrapperPlayServerSpawnEntityLiving spawn = new WrapperPlayServerSpawnEntityLiving();
			spawn.setEntityID(entityId);
			spawn.setType(EntityType.DROPPED_ITEM);
			//spawn.setMetadata(new WrappedDataWatcher(Arrays.asList(new WrappedWatchableObject(0, (byte)0x20), new WrappedWatchableObject(5, true))));
			Location loc = ofWho.getPlayer().getLocation();
			spawn.setX(loc.getX());
			spawn.setY(loc.getY()+1.3);
			spawn.setZ(loc.getZ());
			spawn.setHeadPitch(0);
			Location toLoc = to.getPlayer().getLocation();
			double diffX = loc.getX()-toLoc.getX(),
				   diffZ = loc.getZ()-toLoc.getZ();
			float yaw = 180-((float) Math.toDegrees(Math.atan2(diffX, diffZ)));
			
			spawn.setYaw(yaw);
			spawn.sendPacket(to.getPlayer());
			
			WrapperPlayServerEntityMetadata meta = new WrapperPlayServerEntityMetadata();
			meta.setEntityID(entityId);
			meta.setMetadata(Arrays.asList(new WrappedWatchableObject(invisible, (byte)0x20), new WrappedWatchableObject(noGravity, true)));
			meta.sendPacket(to.getPlayer());
			
			WrapperPlayServerEntityLook look = new WrapperPlayServerEntityLook();
			look.setEntityID(entityId);
			look.setPitch(0);
			look.setYaw(yaw);
			look.sendPacket(to.getPlayer());
			
			new BukkitRunnable() {
				
				@Override
				public void run() {
					WrapperPlayServerEntityEquipment equip = new WrapperPlayServerEntityEquipment();
					equip.setEntityID(entityId);
					equip.setSlot(ItemSlot.HEAD);
			        ItemStack skull = new ItemStack(Material.EMERALD);
					equip.setItem(skull);
					equip.sendPacket(to.getPlayer());
				}
			}.runTaskLater(MainLg.getInstance(), 2);
		}
	}
	
	private void showArrow(LGPlayer to, LGPlayer ofWho, int entityId) {
		WrapperPlayServerEntityDestroy destroy = new WrapperPlayServerEntityDestroy();
		destroy.setEntityIds(new int[] {entityId});
		destroy.sendPacket(to.getPlayer());
		if(ofWho != null) {
			WrapperPlayServerSpawnEntityLiving spawn = new WrapperPlayServerSpawnEntityLiving();
			spawn.setEntityID(entityId);
			spawn.setType(EntityType.DROPPED_ITEM);
			//spawn.setMetadata(new WrappedDataWatcher());
			Location loc = ofWho.getPlayer().getLocation();
			spawn.setX(loc.getX());
			spawn.setY(loc.getY()+1.3);
			spawn.setZ(loc.getZ());
			spawn.setHeadPitch(0);
			Location toLoc = to.getPlayer().getLocation();
			double diffX = loc.getX()-toLoc.getX(),
				   diffZ = loc.getZ()-toLoc.getZ();
			float yaw = 180-((float) Math.toDegrees(Math.atan2(diffX, diffZ)));
			
			spawn.setYaw(yaw);
			spawn.sendPacket(to.getPlayer());
			
			WrapperPlayServerEntityMetadata meta = new WrapperPlayServerEntityMetadata();
			meta.setEntityID(entityId);
			meta.setMetadata(Arrays.asList(new WrappedWatchableObject(invisible, (byte)0x20), new WrappedWatchableObject(noGravity, true)));
			meta.sendPacket(to.getPlayer());
			
			WrapperPlayServerEntityLook look = new WrapperPlayServerEntityLook();
			look.setEntityID(entityId);
			look.setPitch(0);
			look.setYaw(yaw);
			look.sendPacket(to.getPlayer());
			
			new BukkitRunnable() {
				
				@Override
				public void run() {
					WrapperPlayServerEntityEquipment equip = new WrapperPlayServerEntityEquipment();
					equip.setEntityID(entityId);
					equip.setSlot(ItemSlot.HEAD);
			        ItemStack skull = new ItemStack(Material.EMERALD);
					equip.setItem(skull);
					equip.sendPacket(to.getPlayer());
				}
			}.runTaskLater(MainLg.getInstance(), 2);
		}
	}
	public void remove(LGPlayer killed) {
		participants.remove(killed);
		if(!ended) {
			votes.remove(killed);
			latestTop.remove(killed);
		}
	}
}
