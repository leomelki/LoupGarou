package fr.leomelki.loupgarou.roles;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

import org.bukkit.event.EventHandler;
import org.bukkit.scheduler.BukkitRunnable;

import fr.leomelki.loupgarou.MainLg;
import fr.leomelki.loupgarou.classes.LGGame;
import fr.leomelki.loupgarou.classes.LGPlayer;
import fr.leomelki.loupgarou.events.LGGameEndEvent;
import fr.leomelki.loupgarou.events.LGPlayerKilledEvent;
import fr.leomelki.loupgarou.events.LGPlayerKilledEvent.Reason;

public class RBouffon extends Role{
	public RBouffon(LGGame game) {
		super(game);
	}
	@Override
	public RoleType getType() {
		return RoleType.NEUTRAL;
	}
	@Override
	public RoleWinType getWinType() {
		return RoleWinType.NONE;
	}
	@Override
	public String getName() {
		return "§d§lBouffon";
	}
	@Override
	public String getFriendlyName() {
		return "du "+getName();
	}
	@Override
	public String getShortDescription() {
		return "Tu gagnes si tu remplis ton objectif";
	}
	@Override
	public String getDescription() {
		return "Tu es §d§lNeutre§f et tu gagnes si tu remplis ton objectif. Ton objectif est d'être éliminé par le village lors de n’importe quel vote de jour. Si tu réussis, tu gagnes la partie, mais celle-ci continue. Tu pourras tuer l'une des personnes qui t'ont condamné.";
	}
	@Override
	public String getTask() {
		return "Choisis quelqu’un à hanter parmi ceux qui ont voté pour ta mort.";
	}
	@Override
	public String getBroadcastedTask() {
		return "L'esprit vengeur du "+getName()+"§9 rôde sur le village...";
	}
	@Override
	public int getTimeout() {
		return 15;
	}

	public void onNightTurn(Runnable callback) {
		 ArrayList<LGPlayer> players = (ArrayList<LGPlayer>) needToPlay.clone();
		 new Runnable() {
			
			@Override
			public void run() {
				getGame().cancelWait();
				if(players.size() == 0) {
					onTurnFinish(callback);
					return;
				}
				LGPlayer player = players.remove(0);
				getGame().wait(getTimeout(), ()->{RBouffon.this.onNightTurnTimeout(player);this.run();}, (currentPlayer, secondsLeft)->{
					return currentPlayer == player ? "§9§lC'est à ton tour !" : "§6C'est au tour "+getFriendlyName()+" §6(§e"+secondsLeft+" s§6)";
				});
				player.sendMessage("§6"+getTask());
			//	player.sendTitle("§6C'est à vous de jouer", "§a"+getTask(), 100);
				onNightTurn(player, this);
			}
		}.run();
	}
	public boolean hasPlayersLeft() {
		return needToPlay.size() > 0;
	}
	
	
	
	@Override
	protected void onNightTurn(LGPlayer player, Runnable callback) {
		needToPlay.remove(player);
		player.showView();
		player.getCache().set("bouffon_win", true);
		List<LGPlayer> choosable = getGame().getVote().getVotes(player);
		StringJoiner sj = new StringJoiner("§6§o, §6§o§l");
		for(LGPlayer lgp : choosable)
			if(lgp.getPlayer() != null && lgp.getPlayer() != player)
				sj.add(lgp.getName());
		
		player.sendMessage("§6§o§l"+sj+"§6§o "+(sj.length() > 1 ? "ont" : "a")+" voté pour toi.");
				
		player.choose((choosen)->{
			if(choosen != null) {
				if(!choosable.contains(choosen))
					player.sendMessage("§7§l"+choosen.getName()+"§4 n'a pas voté pour vous.");
				else if(choosen.isDead())
					player.sendMessage("§7§l"+choosen.getName()+"§4 est mort.");//fix
				else {
					player.stopChoosing();
					player.sendMessage("§6Ton fantôme va hanter l'esprit de §7§l"+choosen.getName()+"§6.");
					getGame().kill(choosen, Reason.BOUFFON);
					player.hideView();
					callback.run();
				}
			}
		}, player);
	}
	
	@Override
	protected void onNightTurnTimeout(LGPlayer player) {
		player.stopChoosing();
	}
	
	ArrayList<LGPlayer> needToPlay = new ArrayList<LGPlayer>();
	
	@EventHandler
	public void onPlayerKill(LGPlayerKilledEvent e) {
		if(e.getKilled().getRole() == this && e.getReason() == Reason.VOTE) {
			needToPlay.add(e.getKilled());
			getGame().broadcastMessage("§9§oQuelle erreur, le "+getName()+"§9§o aura droit à sa vengeance...");
			e.getKilled().sendMessage("§6Tu as rempli ta mission, l'heure de la vengeance a sonné.");
		}
	}
	
	@EventHandler
	public void onWin(LGGameEndEvent e) {
		if(e.getGame() == getGame())
			for(LGPlayer lgp : getGame().getInGame())
				if(lgp.getRole() == this && lgp.getCache().getBoolean("bouffon_win")) {
					e.getWinners().add(lgp);
					new BukkitRunnable() {
						
						@Override
						public void run() {
							getGame().broadcastMessage("§6§oLe "+getName()+"§6§o a rempli son objectif.");
						}
					}.runTaskAsynchronously(MainLg.getInstance());
				}
	}
}
