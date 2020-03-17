package fr.leomelki.loupgarou.roles;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Listener;

import fr.leomelki.loupgarou.MainLg;
import fr.leomelki.loupgarou.classes.LGCustomItems;
import fr.leomelki.loupgarou.classes.LGGame;
import fr.leomelki.loupgarou.classes.LGPlayer;
import lombok.Getter;
import lombok.Setter;

public abstract class Role implements Listener{
	@Getter @Setter private int waitedPlayers;
	@Getter private ArrayList<LGPlayer> players = new ArrayList<LGPlayer>();
	@Getter private final LGGame game;
	
	public Role(LGGame game) {
		this.game = game;
		Bukkit.getPluginManager().registerEvents(this, MainLg.getInstance());
		FileConfiguration config = MainLg.getInstance().getConfig();
		String roleConfigName = "role."+getClass().getSimpleName().substring(1);
		if(config.contains(roleConfigName))
			waitedPlayers = config.getInt(roleConfigName);
	}
	

	public abstract String getName();
	public abstract String getFriendlyName();
	public abstract String getShortDescription();
	public abstract String getDescription();
	public abstract String getTask();
	public abstract String getBroadcastedTask();
	public RoleType getType(LGPlayer lgp) {
		return getType();
	}
	public RoleWinType getWinType(LGPlayer lgp) {
		return getWinType();
	}
	public RoleType getType() {return null;}
	public RoleWinType getWinType() {return null;}
	/**
	 * @return Timeout in second for this role
	 */
	public abstract int getTimeout();
	
	public void onNightTurn(Runnable callback) {
		 ArrayList<LGPlayer> players = (ArrayList<LGPlayer>) getPlayers().clone();
		 new Runnable() {
			
			@Override
			public void run() {
				getGame().cancelWait();
				if(players.size() == 0) {
					onTurnFinish(callback);
					return;
				}
				LGPlayer player = players.remove(0);
				getGame().wait(getTimeout(), ()->{
					try {
						Role.this.onNightTurnTimeout(player);
					}catch(Exception err) {
						System.out.println("Error when timeout role");
						err.printStackTrace();
					}
					this.run();
				}, (currentPlayer, secondsLeft)->{
					return currentPlayer == player ? "§9§lC'est à ton tour !" : "§6C'est au tour "+getFriendlyName()+" §6(§e"+secondsLeft+" s§6)";
				});
				player.sendMessage("§6"+getTask());
			//	player.sendTitle("§6C'est à vous de jouer", "§a"+getTask(), 100);
				onNightTurn(player, this);
			}
		}.run();
	}
	public void join(LGPlayer player, boolean sendMessage) {
		System.out.println(player.getName()+" est "+getName());
		players.add(player);
		if(player.getRole() == null)
			player.setRole(this);
		waitedPlayers--;
		if(sendMessage) {
			player.sendTitle("§6Tu es "+getName(), "§e"+getShortDescription(), 200);
			player.sendMessage("§6Tu es "+getName()+"§6.");
			player.sendMessage("§6Description : §f"+getDescription());
		}
	}
	public void join(LGPlayer player) {
		join(player, !getGame().isStarted());
		LGCustomItems.updateItem(player);
	}
	public boolean hasPlayersLeft() {
		return getPlayers().size() > 0;
	}
	protected void onNightTurnTimeout(LGPlayer player) {}
	protected void onNightTurn(LGPlayer player, Runnable callback) {}
	protected void onTurnFinish(Runnable callback) {
		callback.run();
	}
	public int getTurnOrder() {
		try {
			RoleSort role = RoleSort.valueOf(getClass().getSimpleName().substring(1));
			return role == null ? -1 : role.ordinal();
		}catch(Throwable e) {
			return -1;
		}
	}//En combientième ce rôle doit être appellé
}
