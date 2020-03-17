package fr.leomelki.loupgarou.roles;

import java.util.Random;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import fr.leomelki.loupgarou.classes.LGGame;
import fr.leomelki.loupgarou.classes.LGPlayer;
import fr.leomelki.loupgarou.classes.LGPlayer.LGChooseCallback;
import fr.leomelki.loupgarou.events.LGPlayerKilledEvent;
import fr.leomelki.loupgarou.events.LGPlayerKilledEvent.Reason;

public class REnfantSauvage extends Role{
	public REnfantSauvage(LGGame game) {
		super(game);
	}

	@Override
	public String getName() {
		return "§a§lEnfant-Sauvage";
	}

	@Override
	public String getFriendlyName() {
		return "de l'"+getName();
	}

	@Override
	public String getShortDescription() {
		return "Tu gagnes avec le §a§lVillage";
	}

	@Override
	public String getDescription() {
		return "Tu gagnes avec le §a§lVillage§f. Au début de la première nuit, tu dois choisir un joueur comme modèle. S'il meurt au cours de la partie, tu deviendras un §c§lLoup-Garou§f.";
	}

	@Override
	public String getTask() {
		return "Qui veux-tu prendre comme modèle ?";
	}

	@Override
	public String getBroadcastedTask() {
		return "L'"+getName()+"§9 cherche ses marques...";
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
	
	@Override
	protected void onNightTurn(LGPlayer player, Runnable callback) {
		player.showView();
		player.sendMessage("§6Choisissez votre modèle.");
		player.choose(new LGChooseCallback() {
			
			@Override
			public void callback(LGPlayer choosen) {
				if(choosen != null) {
					player.stopChoosing();
					player.sendMessage("§6Si §7§l"+choosen.getName()+"§6 meurt, tu deviendras §c§lLoup-Garou§6.");
					player.sendActionBarMessage("§7§l"+choosen.getName()+"§6 est ton modèle");
					player.getCache().set("enfant_svg", choosen);
					choosen.getCache().set("enfant_svg_d", player);
					getPlayers().remove(player);//Pour éviter qu'il puisse avoir plusieurs modèles
					player.hideView();
					callback.run();
				}
			}
		}, player);
	}
	private static Random random = new Random();
	@Override
	protected void onNightTurnTimeout(LGPlayer player) {
		player.stopChoosing();
		player.hideView();
		LGPlayer choosen = null;
		while(choosen == null || choosen == player)
			choosen = getGame().getAlive().get(random.nextInt(getGame().getAlive().size()));
		player.sendMessage("§6Si §7§l"+choosen.getName()+"§6 meurt, tu deviendras §c§lLoup-Garou§6.");
		player.sendActionBarMessage("§7§l"+choosen.getName()+"§6 est ton modèle");
		player.getCache().set("enfant_svg", choosen);
		choosen.getCache().set("enfant_svg_d", player);
		getPlayers().remove(player);
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerKilled(LGPlayerKilledEvent e) {
		if(e.getGame() == getGame())
			if(e.getKilled().getCache().has("enfant_svg_d")) {
				LGPlayer enfant = e.getKilled().getCache().remove("enfant_svg_d");
				if(!enfant.isDead() && enfant.getCache().remove("enfant_svg") == e.getKilled()) {
					enfant.sendMessage("§7§l"+e.getKilled().getName()+"§6 est mort, tu deviens un §c§lLoup-Garou§6.");
					REnfantSauvageLG lgEnfantSvg = null;
					for(Role role : getGame().getRoles())
						if(role instanceof REnfantSauvageLG)
							lgEnfantSvg = (REnfantSauvageLG)role;
					
					if(lgEnfantSvg == null)
						getGame().getRoles().add(lgEnfantSvg = new REnfantSauvageLG(getGame()));
					
					lgEnfantSvg.join(enfant, false);
				}
			}
	}
	
}
