package fr.leomelki.loupgarou.roles;

import org.bukkit.event.EventHandler;
import org.bukkit.scheduler.BukkitRunnable;

import fr.leomelki.loupgarou.MainLg;
import fr.leomelki.loupgarou.classes.LGGame;
import fr.leomelki.loupgarou.classes.LGPlayer;
import fr.leomelki.loupgarou.classes.LGPlayer.LGChooseCallback;
import fr.leomelki.loupgarou.events.LGDayEndEvent;
import fr.leomelki.loupgarou.events.LGNightStart;
import fr.leomelki.loupgarou.events.LGVoteEvent;

public class RCorbeau extends Role{
	public RCorbeau(LGGame game) {
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
		return "§a§lCorbeau";
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
		return "Tu gagnes avec le §a§lVillage§f. Chaque nuit, tu peux désigner un joueur qui se retrouvera le lendemain avec deux voix contre lui au vote.";
	}
	@Override
	public String getTask() {
		return "Tu peux choisir un joueur qui aura deux votes contre lui.";
	}
	@Override
	public String getBroadcastedTask() {
		return "Le "+getName()+"§9 s'apprête à diffamer quelqu'un...";
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
				if(choosen != null && choosen != player) {
					//player.sendTitle("§6Vous avez regardé un rôle", "§e§l"+choosen.getName()+"§6§l est §e§l"+choosen.getRole().getName(), 5*20);
					
					choosen.getCache().set("corbeau_selected", true);
					
					player.sendActionBarMessage("§e§l"+choosen.getName()+"§6 aura deux votes contre lui");
					player.sendMessage("§6Tu nuis à la réputation de §7§l"+choosen.getName()+"§6.");
					player.stopChoosing();
					player.hideView();
					callback.run();
				}
			}
		});
	}
	
	@EventHandler
	public void onNightStart(LGDayEndEvent e) {
		if(e.getGame() == getGame())
			for(LGPlayer lgp : getGame().getAlive())
				lgp.getCache().remove("corbeau_selected");
	}
	
	@EventHandler
	public void onVoteStart(LGVoteEvent e) {
		if(e.getGame() == getGame())
			for(LGPlayer lgp : getGame().getAlive())
				if(lgp.getCache().getBoolean("corbeau_selected")) {
					lgp.getCache().remove("corbeau_selected");
					LGPlayer lg = lgp;
					new BukkitRunnable() {
						
						@Override
						public void run() {
							getGame().getVote().vote(new LGPlayer("§a§lLe corbeau"), lg);
							getGame().getVote().vote(new LGPlayer("§a§lLe corbeau"), lg);//fix
							getGame().broadcastMessage("§7§l"+lg.getName()+"§6 a reçu la visite du "+getName()+"§6.");
						}
					}.runTask(MainLg.getInstance());
					
				}
	}
	
	@Override
	protected void onNightTurnTimeout(LGPlayer player) {
		player.stopChoosing();
		player.hideView();
		//player.sendTitle("§cVous n'avez regardé aucun rôle", "§4Vous avez mis trop de temps à vous décider...", 80);
		//player.sendMessage("§cVous n'avez pas utilisé votre pouvoir cette nuit.");
	}
}
