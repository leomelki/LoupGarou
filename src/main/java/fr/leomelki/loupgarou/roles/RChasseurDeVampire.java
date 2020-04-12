package fr.leomelki.loupgarou.roles;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import fr.leomelki.loupgarou.classes.LGGame;
import fr.leomelki.loupgarou.classes.LGPlayer;
import fr.leomelki.loupgarou.classes.LGPlayer.LGChooseCallback;
import fr.leomelki.loupgarou.classes.LGWinType;
import fr.leomelki.loupgarou.events.LGEndCheckEvent;
import fr.leomelki.loupgarou.events.LGGameEndEvent;
import fr.leomelki.loupgarou.events.LGNightEndEvent;
import fr.leomelki.loupgarou.events.LGNightPlayerPreKilledEvent;
import fr.leomelki.loupgarou.events.LGPyromaneGasoilEvent;
import fr.leomelki.loupgarou.events.LGPlayerKilledEvent.Reason;
import fr.leomelki.loupgarou.events.LGRoleTurnEndEvent;

public class RChasseurDeVampire extends Role{
	public RChasseurDeVampire(LGGame game) {
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
		return "§a§lChasseur de Vampires";
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
		return "Tu gagnes avec le §a§lVillage§f. Chaque nuit, tu peux traquer un joueur pour découvrir s'il s'agit d'un Vampire. Si c'est le cas, tu le tueras dans son sommeil. Si les §5§lVampires§f te prennent pour cible, tu seras immunisé contre leur attaque, et tu tueras le plus jeune d'entre eux.";
	}
	@Override
	public String getTask() {
		return "Choisis un joueur à pister.";
	}
	@Override
	public String getBroadcastedTask() {
		return "Le "+getName()+"§9 traque ses proies...";
	}
	@Override
	public int getTimeout() {
		return 15;
	}
	@Override
	public boolean hasPlayersLeft() {
		for(LGPlayer lgp : getGame().getAlive())
			if(lgp.getRoleType() == RoleType.VAMPIRE)
				return super.hasPlayersLeft();
		return false;
	}
	
	@Override
	protected void onNightTurn(LGPlayer player, Runnable callback) {
		player.showView();
		
		player.choose(new LGChooseCallback() {
			@Override
			public void callback(LGPlayer choosen) {
				if(choosen != null && choosen != player) {
				//	player.sendMessage("§6Tu as choisi de rendre visite à §7§l"+choosen.getName()+"§6.");
					if(choosen.getCache().getBoolean("vampire") || choosen.getRole() instanceof RVampire) {
						getGame().kill(choosen, Reason.CHASSEUR_DE_VAMPIRE);
						player.sendMessage("§7§l"+choosen.getName()+"§6 est un §5§lVampire§6, à l'attaque.");
						player.sendActionBarMessage("§e§l"+choosen.getName()+"§6 va mourir");
					} else {
						player.sendMessage("§7§l"+choosen.getName()+"§6 n'est pas un §5§lVampire§6...");
						player.sendActionBarMessage("§e§l"+choosen.getName()+"§6 n'est pas un §5§lVampire");
					}
					
					player.stopChoosing();
					player.hideView();
					callback.run();
				}
			}
		});
	}
	
	
	@Override
	protected void onNightTurnTimeout(LGPlayer player) {
		player.stopChoosing();
		player.hideView();
		//player.sendTitle("§cVous n'avez regardé aucun rôle", "§4Vous avez mis trop de temps à vous décider...", 80);
		//player.sendMessage("§cVous n'avez pas utilisé votre pouvoir cette nuit.");
	}
}
