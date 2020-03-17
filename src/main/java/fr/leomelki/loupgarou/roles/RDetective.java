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

public class RDetective extends Role{
	public RDetective(LGGame game) {
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
		return "§a§lDétective";
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
		return "Tu gagnes avec le §a§lVillage§f. Chaque nuit, tu mènes l'enquête sur deux joueurs pour découvrir s'ils font partie du même camp.";
	}
	@Override
	public String getTask() {
		return "Choisis deux joueurs à étudier.";
	}
	@Override
	public String getBroadcastedTask() {
		return "Le "+getName()+"§9 est sur une enquête...";
	}
	
	@Override
	public int getTimeout() {
		return 15;
	}
	@Override
	protected void onNightTurn(LGPlayer player, Runnable callback) {
		player.showView();
		
		player.choose(new LGChooseCallback() {
			@Override
			public void callback(LGPlayer choosen) {
				if(choosen != null) {
					if(choosen == player) {
						player.sendMessage("§cVous ne pouvez pas vous sélectionner !");
						return;
					}
					if(player.getCache().has("detective_first")) {
						LGPlayer first = player.getCache().remove("detective_first");
						if(first == choosen) {
							player.sendMessage("§cVous ne pouvez pas comparer §7§l"+first.getName()+"§c avec lui même !");
						} else {
							if((first.getRoleType() == RoleType.NEUTRAL || choosen.getRoleType() == RoleType.NEUTRAL) ? first.getRole().getClass() == choosen.getRole().getClass() : first.getRoleType() == choosen.getRoleType())
								player.sendMessage("§7§l"+first.getName()+"§6 et §7§l"+choosen.getName()+"§6 sont §adu même camp.");
							else
								player.sendMessage("§7§l"+first.getName()+"§6 et §7§l"+choosen.getName()+"§6 ne sont §cpas du même camp.");

							player.stopChoosing();
							player.hideView();
							callback.run();
						}
					} else {
						player.getCache().set("detective_first", choosen);
						player.sendMessage("§9Choisis un joueur avec qui tu souhaites comparer le rôle de §7§l"+choosen.getName());
					}
				}
			}
		});
	}

	@Override
	protected void onNightTurnTimeout(LGPlayer player) {
		player.getCache().remove("detective_first");
		player.stopChoosing();
		player.hideView();
		//player.sendTitle("§cVous n'avez mis personne en couple", "§4Vous avez mis trop de temps à vous décider...", 80);
		//player.sendMessage("§9Tu n'as pas créé de couple.");
	}
	
	
}
